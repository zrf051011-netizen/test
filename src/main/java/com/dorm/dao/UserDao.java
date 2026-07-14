package com.dorm.dao;

import com.dorm.entity.User;
import com.dorm.util.PageBean;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao extends BaseDao {
    private static final String SELECT_USER = "SELECT id, username, password, salt, real_name, phone, role, status, create_time, update_time, last_login_time, last_login_ip FROM `user`";

    public User findByUsername(String username) {
        return queryBean(SELECT_USER + " WHERE username = ?", User.class, username);
    }

    public User findById(int id) {
        return queryBean(SELECT_USER + " WHERE id = ?", User.class, id);
    }

    public List<User> findByRole(String role) {
        return queryList(SELECT_USER + " WHERE role = ? AND status = 1 ORDER BY real_name", User.class, role);
    }

    public PageBean<User> findPage(String keyword, String role, int page, int size) {
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        List<Object> params = new ArrayList<Object>();
        if (keyword != null && !keyword.isEmpty()) {
            where.append(" AND (username LIKE ? OR real_name LIKE ? OR phone LIKE ?)");
            String like = "%" + keyword + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }
        if (role != null && !role.isEmpty()) {
            where.append(" AND role = ?");
            params.add(role);
        }
        String sql = SELECT_USER + where + " ORDER BY create_time DESC";
        String countSql = "SELECT COUNT(*) FROM `user`" + where;
        return queryPage(sql, countSql, User.class, page, size, params.toArray());
    }

    public int save(User user) {
        return insertAndGetKey("INSERT INTO `user` (username, password, salt, real_name, phone, role, status) VALUES (?, ?, ?, ?, ?, ?, ?)",
                user.getUsername(), user.getPassword(), user.getSalt(), user.getRealName(), user.getPhone(), user.getRole(), user.getStatus());
    }

    public int save(Connection conn, User user) throws SQLException {
        return insertAndGetKey(conn, "INSERT INTO `user` (username, password, salt, real_name, phone, role, status) VALUES (?, ?, ?, ?, ?, ?, ?)",
                user.getUsername(), user.getPassword(), user.getSalt(), user.getRealName(), user.getPhone(), user.getRole(), user.getStatus());
    }

    public int updateBasic(User user) {
        return update("UPDATE `user` SET real_name = ?, phone = ?, role = ?, status = ? WHERE id = ?",
                user.getRealName(), user.getPhone(), user.getRole(), user.getStatus(), user.getId());
    }

    public int updateBasic(Connection conn, User user) throws SQLException {
        return update(conn, "UPDATE `user` SET real_name = ?, phone = ?, role = ?, status = ? WHERE id = ?",
                user.getRealName(), user.getPhone(), user.getRole(), user.getStatus(), user.getId());
    }

    public int updatePassword(int userId, String password, String salt) {
        return update("UPDATE `user` SET password = ?, salt = ? WHERE id = ?", password, salt, userId);
    }

    public int updateLastLogin(int userId, String ip) {
        return update("UPDATE `user` SET last_login_time = NOW(), last_login_ip = ? WHERE id = ?", ip, userId);
    }

    public int changeStatus(int id, int status) {
        return update("UPDATE `user` SET status = ? WHERE id = ?", status, id);
    }

    public int deleteById(int id) {
        return update("DELETE FROM `user` WHERE id = ?", id);
    }

    public int countAll() {
        return queryNumber("SELECT COUNT(*) FROM `user`").intValue();
    }
}
