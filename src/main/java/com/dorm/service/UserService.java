package com.dorm.service;

import com.dorm.dao.UserDao;
import com.dorm.entity.User;
import com.dorm.exception.BusinessException;
import com.dorm.util.MD5Util;
import com.dorm.util.PageBean;

public class UserService {
    private final UserDao userDao = new UserDao();

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

    public void saveUser(User user) {
        if (user.getId() == null || user.getId() <= 0) {
            if (userDao.findByUsername(user.getUsername()) != null) {
                throw new BusinessException("用户名已存在");
            }
            String salt = MD5Util.generateSalt();
            user.setSalt(salt);
            user.setPassword(MD5Util.encrypt("admin123", salt));
            user.setStatus(user.getStatus() == null ? 1 : user.getStatus());
            userDao.save(user);
            return;
        }
        User old = userDao.findById(user.getId());
        if (old == null) {
            throw new BusinessException("用户不存在");
        }
        user.setUsername(old.getUsername());
        userDao.updateBasic(user);
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
