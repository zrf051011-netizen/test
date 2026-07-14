package com.dorm.service;

import com.dorm.dao.StudentDao;
import com.dorm.dao.UserDao;
import com.dorm.entity.Student;
import com.dorm.entity.User;
import com.dorm.exception.BusinessException;
import com.dorm.util.DBUtil;
import com.dorm.util.MD5Util;
import com.dorm.util.PageBean;

import java.sql.Connection;
import java.sql.SQLException;

public class UserService {
    private final UserDao userDao = new UserDao();
    private final StudentDao studentDao = new StudentDao();

    public User login(String username, String password, String ip) {
        if (username == null || username.trim().isEmpty()) {
            throw new BusinessException("请输入用户名");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new BusinessException("请输入密码");
        }
        User user = userDao.findByUsername(username.trim());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (user.getStatus() == null || user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        if (!matches(password, user)) {
            throw new BusinessException("密码错误");
        }
        userDao.updateLastLogin(user.getId(), ip);
        return userDao.findById(user.getId());
    }

    private boolean matches(String password, User user) {
        if (user.getSalt() == null || user.getSalt().isEmpty()) {
            return MD5Util.encryptPlain(password).equalsIgnoreCase(user.getPassword());
        }
        return MD5Util.encrypt(password, user.getSalt()).equalsIgnoreCase(user.getPassword());
    }

    public String homePath(User user) {
        if ("ADMIN".equals(user.getRole())) {
            return "/admin/dashboard";
        }
        if ("BUILDING_ADMIN".equals(user.getRole())) {
            return "/buildingadmin/dashboard";
        }
        return "/student/dashboard";
    }

    public PageBean<User> findPage(String keyword, String role, int page, int size) {
        return userDao.findPage(keyword, role, page, size);
    }

    public User findById(int id) {
        return userDao.findById(id);
    }

    public Student findStudentProfile(int userId) {
        return studentDao.findByUserId(userId);
    }

    public void saveUser(User user, String studentNo) {
        validateUser(user);
        if (user.getId() == null || user.getId() <= 0) {
            createUser(user, studentNo);
            return;
        }
        updateUser(user, studentNo);
    }

    private void createUser(User user, String studentNo) {
        if (userDao.findByUsername(user.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }
        String requestedStudentNo = normalizeStudentNo(studentNo);
        if ("STUDENT".equals(user.getRole()) && !requestedStudentNo.isEmpty()) {
            ensureStudentNoAvailable(requestedStudentNo, null);
        }

        String salt = MD5Util.generateSalt();
        user.setSalt(salt);
        user.setPassword(MD5Util.encrypt("admin123", salt));
        user.setStatus(user.getStatus() == null ? 1 : user.getStatus());

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            int userId = userDao.save(conn, user);
            if ("STUDENT".equals(user.getRole())) {
                String resolvedStudentNo = requestedStudentNo.isEmpty() ? autoStudentNo(userId) : requestedStudentNo;
                Student profile = new Student();
                profile.setUserId(userId);
                profile.setStudentNo(resolvedStudentNo);
                profile.setStatus("OUT");
                studentDao.save(conn, profile);
            }
            conn.commit();
        } catch (SQLException e) {
            rollback(conn);
            throw new BusinessException("保存失败，用户名或学号可能已存在");
        } finally {
            close(conn);
        }
    }

    private void updateUser(User user, String studentNo) {
        User old = userDao.findById(user.getId());
        if (old == null) {
            throw new BusinessException("用户不存在");
        }
        Student profile = studentDao.findByUserId(user.getId());
        if (profile != null && !"STUDENT".equals(user.getRole())) {
            throw new BusinessException("该账号已关联学生档案，不能直接改为其他角色");
        }

        String requestedStudentNo = normalizeStudentNo(studentNo);
        String resolvedStudentNo = "";
        if ("STUDENT".equals(user.getRole())) {
            resolvedStudentNo = requestedStudentNo.isEmpty()
                    ? (profile == null ? autoStudentNo(user.getId()) : profile.getStudentNo())
                    : requestedStudentNo;
            ensureStudentNoAvailable(resolvedStudentNo, profile == null ? null : profile.getId());
        }

        user.setUsername(old.getUsername());
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);
            userDao.updateBasic(conn, user);
            if ("STUDENT".equals(user.getRole())) {
                if (profile == null) {
                    Student newProfile = new Student();
                    newProfile.setUserId(user.getId());
                    newProfile.setStudentNo(resolvedStudentNo);
                    newProfile.setStatus("OUT");
                    studentDao.save(conn, newProfile);
                } else if (!resolvedStudentNo.equals(profile.getStudentNo())) {
                    studentDao.updateStudentNo(conn, user.getId(), resolvedStudentNo);
                }
            }
            conn.commit();
        } catch (SQLException e) {
            rollback(conn);
            throw new BusinessException("保存失败，学号可能已存在");
        } finally {
            close(conn);
        }
    }

    private void validateUser(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new BusinessException("请输入用户名");
        }
        if (user.getRealName() == null || user.getRealName().trim().isEmpty()) {
            throw new BusinessException("请输入真实姓名");
        }
        if (!"ADMIN".equals(user.getRole())
                && !"BUILDING_ADMIN".equals(user.getRole())
                && !"STUDENT".equals(user.getRole())) {
            throw new BusinessException("用户角色不正确");
        }
        if (user.getStatus() == null || user.getStatus() != 0 && user.getStatus() != 1) {
            throw new BusinessException("用户状态不正确");
        }
    }

    private String normalizeStudentNo(String studentNo) {
        String value = studentNo == null ? "" : studentNo.trim();
        if (value.length() > 20) {
            throw new BusinessException("学号不能超过20个字符");
        }
        return value;
    }

    private String autoStudentNo(int userId) {
        return String.format("AUTO%08d", userId);
    }

    private void ensureStudentNoAvailable(String studentNo, Integer currentStudentId) {
        Student sameNo = studentDao.findByStudentNo(studentNo);
        if (sameNo != null && (currentStudentId == null || !currentStudentId.equals(sameNo.getId()))) {
            throw new BusinessException("学号已存在");
        }
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

    public void changePassword(int userId, String oldPassword, String newPassword, String confirmPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            throw new BusinessException("新密码至少6位");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new BusinessException("两次输入的新密码不一致");
        }
        User user = userDao.findById(userId);
        if (user == null || !matches(oldPassword, user)) {
            throw new BusinessException("原密码不正确");
        }
        String salt = MD5Util.generateSalt();
        userDao.updatePassword(userId, MD5Util.encrypt(newPassword, salt), salt);
    }

    public void resetPasswordByPhone(String username, String phone, String newPassword, String confirmPassword) {
        String normalizedUsername = username == null ? "" : username.trim();
        String normalizedPhone = phone == null ? "" : phone.trim();
        if (normalizedUsername.isEmpty() || normalizedPhone.isEmpty()) {
            throw new BusinessException("请填写用户名和绑定手机号");
        }
        if (newPassword == null || newPassword.length() < 8) {
            throw new BusinessException("新密码至少8位");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new BusinessException("两次输入的新密码不一致");
        }

        User user = userDao.findByUsername(normalizedUsername);
        if (user == null || user.getStatus() == null || user.getStatus() == 0
                || user.getPhone() == null || !normalizedPhone.equals(user.getPhone().trim())) {
            throw new BusinessException("账号或手机号不匹配");
        }

        String salt = MD5Util.generateSalt();
        userDao.updatePassword(user.getId(), MD5Util.encrypt(newPassword, salt), salt);
    }

    public void deleteUser(int id) {
        User user = userDao.findById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if ("ADMIN".equals(user.getRole())) {
            throw new BusinessException("不能删除超级管理员");
        }
        userDao.deleteById(id);
    }
}
