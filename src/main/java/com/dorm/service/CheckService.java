package com.dorm.service;

import com.dorm.dao.CheckRecordDao;
import com.dorm.dao.DormitoryDao;
import com.dorm.dao.RepairDao;
import com.dorm.dao.StudentDao;
import com.dorm.entity.CheckRecord;
import com.dorm.entity.Dormitory;
import com.dorm.entity.Student;
import com.dorm.exception.BusinessException;
import com.dorm.util.DBUtil;

import java.sql.Connection;
import java.sql.SQLException;

public class CheckService {
    private final StudentDao studentDao = new StudentDao();
    private final DormitoryDao dormitoryDao = new DormitoryDao();
    private final CheckRecordDao checkRecordDao = new CheckRecordDao();
    private final RepairDao repairDao = new RepairDao();

    public void checkIn(int studentId, int dormitoryId, int operatorId, String remark) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            Student student = studentDao.findByIdForUpdate(conn, studentId);
            Dormitory dormitory = dormitoryDao.findByIdForUpdate(conn, dormitoryId);
            if (student == null) {
                throw new BusinessException("学生不存在");
            }
            if ("IN".equals(student.getStatus())) {
                throw new BusinessException("该学生已经入住");
            }
            ensureManagedBy(dormitory, operatorId);
            ensureAvailable(dormitory);

            int newCount = dormitory.getCurrentCount() + 1;
            studentDao.updateDorm(conn, studentId, dormitoryId, "IN");
            dormitoryDao.updateCountAndStatus(conn, dormitoryId, newCount, statusFor(dormitory, newCount));
            checkRecordDao.save(conn, record(studentId, null, dormitoryId, "IN", operatorId, remark));
            conn.commit();
        } catch (BusinessException e) {
            rollback(conn);
            throw e;
        } catch (Exception e) {
            rollback(conn);
            throw new RuntimeException(e);
        } finally {
            close(conn);
        }
    }

    public void checkOut(int studentId, int operatorId, String remark) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            Student student = studentDao.findByIdForUpdate(conn, studentId);
            if (student == null || !"IN".equals(student.getStatus()) || student.getDormitoryId() == null) {
                throw new BusinessException("学生未入住，不能退宿");
            }
            Dormitory oldDorm = dormitoryDao.findByIdForUpdate(conn, student.getDormitoryId());
            ensureManagedBy(oldDorm, operatorId);
            if (repairDao.hasUnfinishedByStudent(studentId)) {
                throw new BusinessException("该学生有未完成报修，暂不能退宿");
            }

            int newCount = Math.max(oldDorm.getCurrentCount() - 1, 0);
            studentDao.updateDorm(conn, studentId, null, "OUT");
            dormitoryDao.updateCountAndStatus(conn, oldDorm.getId(), newCount, statusFor(oldDorm, newCount));
            checkRecordDao.save(conn, record(studentId, oldDorm.getId(), null, "OUT", operatorId, remark));
            conn.commit();
        } catch (BusinessException e) {
            rollback(conn);
            throw e;
        } catch (Exception e) {
            rollback(conn);
            throw new RuntimeException(e);
        } finally {
            close(conn);
        }
    }

    public void changeDorm(int studentId, int targetDormitoryId, int operatorId, String remark) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            Student student = studentDao.findByIdForUpdate(conn, studentId);
            if (student == null || !"IN".equals(student.getStatus()) || student.getDormitoryId() == null) {
                throw new BusinessException("学生未入住，不能换宿");
            }
            if (student.getDormitoryId().equals(targetDormitoryId)) {
                throw new BusinessException("目标宿舍不能与当前宿舍相同");
            }

            int oldDormitoryId = student.getDormitoryId();
            Dormitory first = dormitoryDao.findByIdForUpdate(conn, Math.min(oldDormitoryId, targetDormitoryId));
            Dormitory second = dormitoryDao.findByIdForUpdate(conn, Math.max(oldDormitoryId, targetDormitoryId));
            Dormitory oldDorm = oldDormitoryId <= targetDormitoryId ? first : second;
            Dormitory targetDorm = oldDormitoryId <= targetDormitoryId ? second : first;
            ensureManagedBy(oldDorm, operatorId);
            ensureManagedBy(targetDorm, operatorId);
            ensureAvailable(targetDorm);

            int oldCount = Math.max(oldDorm.getCurrentCount() - 1, 0);
            int targetCount = targetDorm.getCurrentCount() + 1;
            studentDao.updateDorm(conn, studentId, targetDormitoryId, "IN");
            dormitoryDao.updateCountAndStatus(conn, oldDorm.getId(), oldCount, statusFor(oldDorm, oldCount));
            dormitoryDao.updateCountAndStatus(conn, targetDorm.getId(), targetCount, statusFor(targetDorm, targetCount));
            checkRecordDao.save(conn, record(studentId, oldDorm.getId(), targetDorm.getId(), "CHANGE", operatorId, remark));
            conn.commit();
        } catch (BusinessException e) {
            rollback(conn);
            throw e;
        } catch (Exception e) {
            rollback(conn);
            throw new RuntimeException(e);
        } finally {
            close(conn);
        }
    }

    private void ensureManagedBy(Dormitory dormitory, int operatorId) {
        if (dormitory == null) {
            throw new BusinessException("宿舍不存在");
        }
        if (dormitory.getBuildingAdminId() == null || !dormitory.getBuildingAdminId().equals(operatorId)) {
            throw new BusinessException("无权办理其他楼栋的住宿业务");
        }
    }

    private void ensureAvailable(Dormitory dormitory) {
        if (dormitory == null) {
            throw new BusinessException("宿舍不存在");
        }
        if ("REPAIR".equals(dormitory.getStatus())) {
            throw new BusinessException("宿舍维修中，不能入住");
        }
        if (dormitory.getCurrentCount() >= dormitory.getCapacity()) {
            throw new BusinessException("宿舍已满");
        }
    }

    private String statusFor(Dormitory dormitory, int count) {
        if ("REPAIR".equals(dormitory.getStatus())) {
            return "REPAIR";
        }
        return count >= dormitory.getCapacity() ? "FULL" : "AVAILABLE";
    }

    private CheckRecord record(int studentId, Integer fromId, Integer toId, String type, int operatorId, String remark) {
        CheckRecord record = new CheckRecord();
        record.setStudentId(studentId);
        record.setFromDormitoryId(fromId);
        record.setToDormitoryId(toId);
        record.setType(type);
        record.setOperatorId(operatorId);
        record.setRemark(remark);
        return record;
    }

    private void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {
            }
        }
    }

    private void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }
}
