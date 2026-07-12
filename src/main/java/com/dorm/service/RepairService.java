package com.dorm.service;

import com.dorm.dao.RepairDao;
import com.dorm.dao.StudentDao;
import com.dorm.entity.Repair;
import com.dorm.entity.Student;
import com.dorm.exception.BusinessException;
import com.dorm.util.PageBean;

import java.util.List;

public class RepairService {
    private final RepairDao repairDao = new RepairDao();
    private final StudentDao studentDao = new StudentDao();

    public PageBean<Repair> findPage(String keyword, String status, Integer adminId, Integer reporterId, int page, int size) {
        return repairDao.findPage(keyword, status, adminId, reporterId, page, size);
    }

    public PageBean<Repair> findPage(String keyword, String status, Integer buildingId, Integer adminId, Integer reporterId, int page, int size) {
        return repairDao.findPage(keyword, status, buildingId, adminId, reporterId, page, size);
    }

    public List<Repair> findList(String keyword, String status, Integer buildingId, Integer adminId, Integer reporterId) {
        return repairDao.findList(keyword, status, buildingId, adminId, reporterId);
    }

    public List<Repair> latest(Integer adminId, int size) {
        return repairDao.latest(adminId, size);
    }

    public List<Repair> latestOpen(Integer adminId, Integer reporterId, boolean includeDoing, int size) {
        return repairDao.latestOpen(adminId, reporterId, includeDoing, size);
    }

    public int countByStatus(String status, Integer adminId) {
        return repairDao.countByStatus(status, adminId);
    }

    public int countOpenByReporter(int reporterId) {
        return repairDao.countOpenByReporter(reporterId);
    }

    public void submit(int userId, String repairType, String description) {
        Student student = studentDao.findByUserId(userId);
        if (student == null || !"IN".equals(student.getStatus()) || student.getDormitoryId() == null) {
            throw new BusinessException("未入住学生不能提交报修");
        }
        if (repairType == null || repairType.trim().isEmpty()) {
            throw new BusinessException("请选择报修类型");
        }
        Repair repair = new Repair();
        repair.setReporterId(student.getId());
        repair.setDormitoryId(student.getDormitoryId());
        repair.setRepairType(repairType);
        repair.setDescription(description);
        repairDao.save(repair);
    }

    public void process(int repairId, String status, int handlerId, String remark) {
        Repair repair = repairDao.findById(repairId);
        if (repair == null) {
            throw new BusinessException("报修记录不存在");
        }
        if (repair.getBuildingAdminId() == null || !repair.getBuildingAdminId().equals(handlerId)) {
            throw new BusinessException("无权处理其他楼栋的报修");
        }
        String currentStatus = repair.getStatus();
        boolean validTransition = "PENDING".equals(currentStatus) && "DOING".equals(status)
                || "DOING".equals(currentStatus) && "DONE".equals(status);
        if (!validTransition) {
            throw new BusinessException("报修状态已变化，请刷新后按流程处理");
        }
        int updated = repairDao.updateStatusForBuildingAdmin(repairId, currentStatus, status, handlerId, remark);
        if (updated == 0) {
            throw new BusinessException("报修状态已变化或当前账号无权处理");
        }
    }
}
