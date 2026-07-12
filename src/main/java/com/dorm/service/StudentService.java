package com.dorm.service;

import com.dorm.dao.StudentDao;
import com.dorm.dao.UserDao;
import com.dorm.entity.Student;
import com.dorm.entity.StudentImportRow;
import com.dorm.entity.User;
import com.dorm.exception.BusinessException;
import com.dorm.util.DBUtil;
import com.dorm.util.MD5Util;
import com.dorm.util.PageBean;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class
StudentService {
    private final StudentDao studentDao = new StudentDao();
    private final UserDao userDao = new UserDao();

    public PageBean<Student> findPage(String keyword, String status, Integer adminId, int page, int size) {
        return studentDao.findPage(keyword, status, adminId, page, size);
    }

    public List<Student> findOutStudents() {
        return studentDao.findOutStudents();
    }

    public List<Student> findAll() {
        return studentDao.findAll();
    }

    public Student findById(int id) {
        return studentDao.findById(id);
    }

    public Student findByUserId(int userId) {
        return studentDao.findByUserId(userId);
    }

    public List<Student> findRoommates(int dormitoryId, int exceptStudentId) {
        return studentDao.findRoommates(dormitoryId, exceptStudentId);
    }

    public void saveStudent(Student student, String username, String realName, String phone) {
        if (student.getStudentNo() == null || student.getStudentNo().trim().isEmpty()) {
            throw new BusinessException("请输入学号");
        }
        if (realName == null || realName.trim().isEmpty()) {
            throw new BusinessException("请输入学生姓名");
        }
        if (student.getId() != null && student.getId() > 0) {
            updateStudent(student, realName, phone);
            return;
        }
        if (username == null || username.trim().isEmpty()) {
            throw new BusinessException("请输入登录用户名");
        }
        if (userDao.findByUsername(username) != null) {
            throw new BusinessException("用户名已存在");
        }
        if (studentDao.findByStudentNo(student.getStudentNo()) != null) {
            throw new BusinessException("学号已存在");
        }

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            createStudent(conn, student, username, realName, phone);
            conn.commit();
        } catch (Exception e) {
            rollback(conn);
            if (e instanceof BusinessException) {
                throw (BusinessException) e;
            }
            throw new RuntimeException(e);
        } finally {
            close(conn);
        }
    }

    public int importStudents(List<StudentImportRow> rows) {
        if (rows == null || rows.isEmpty()) {
            throw new BusinessException("没有可导入的学生数据");
        }
        for (StudentImportRow row : rows) {
            if (userDao.findByUsername(row.getUsername()) != null) {
                throw new BusinessException("第" + row.getRowNumber() + "行：登录用户名已存在：" + row.getUsername());
            }
            if (studentDao.findByStudentNo(row.getStudentNo()) != null) {
                throw new BusinessException("第" + row.getRowNumber() + "行：学号已存在：" + row.getStudentNo());
            }
        }

        Connection conn = null;
        int currentRow = 0;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            for (StudentImportRow row : rows) {
                currentRow = row.getRowNumber();
                Student student = new Student();
                student.setStudentNo(row.getStudentNo());
                student.setGender(row.getGender());
                student.setClassName(row.getClassName());
                student.setMajor(row.getMajor());
                student.setEmergencyContact(row.getEmergencyContact());
                student.setEmergencyPhone(row.getEmergencyPhone());
                createStudent(conn, student, row.getUsername(), row.getRealName(), row.getPhone());
            }
            conn.commit();
            return rows.size();
        } catch (Exception e) {
            rollback(conn);
            if (e instanceof BusinessException) {
                throw (BusinessException) e;
            }
            throw new BusinessException("第" + currentRow + "行导入失败，用户名或学号可能已存在");
        } finally {
            close(conn);
        }
    }

    private void createStudent(Connection conn, Student student, String username, String realName, String phone) throws SQLException {
        String salt = MD5Util.generateSalt();
        User user = new User();
        user.setUsername(username);
        user.setRealName(realName);
        user.setPhone(phone);
        user.setRole("STUDENT");
        user.setStatus(1);
        user.setSalt(salt);
        user.setPassword(MD5Util.encrypt("admin123", salt));
        int userId = userDao.save(conn, user);
        student.setUserId(userId);
        student.setStatus("OUT");
        studentDao.save(conn, student);
    }

    private void updateStudent(Student student, String realName, String phone) {
        Student old = studentDao.findById(student.getId());
        if (old == null) {
            throw new BusinessException("学生不存在");
        }
        Student sameNo = studentDao.findByStudentNo(student.getStudentNo());
        if (sameNo != null && !sameNo.getId().equals(student.getId())) {
            throw new BusinessException("学号已存在");
        }
        User user = userDao.findById(old.getUserId());
        user.setRealName(realName);
        user.setPhone(phone);
        user.setRole("STUDENT");
        user.setStatus(1);
        userDao.updateBasic(user);
        studentDao.updateStudent(student);
    }

    public void delete(int id) {
        Student student = studentDao.findById(id);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        if ("IN".equals(student.getStatus())) {
            throw new BusinessException("学生已入住，请先办理退宿");
        }
        studentDao.deleteById(id);
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
