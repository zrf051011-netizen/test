package com.dorm.dao;

import com.dorm.entity.Student;
import com.dorm.util.PageBean;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDao extends BaseDao {
    private static final String SELECT_STUDENT =
            "SELECT s.id, s.user_id, s.student_no, s.gender, s.class_name, s.major, s.dormitory_id, s.check_in_date, s.status, " +
            "s.emergency_contact, s.emergency_phone, s.create_time, s.update_time, u.username, u.real_name, u.phone, b.building_name, d.room_number " +
            "FROM student s JOIN `user` u ON s.user_id = u.id " +
            "LEFT JOIN dormitory d ON s.dormitory_id = d.id LEFT JOIN building b ON d.building_id = b.id";

    public Student findById(int id) {
        return queryBean(SELECT_STUDENT + " WHERE s.id = ?", Student.class, id);
    }

    public Student findByIdForUpdate(Connection conn, int id) throws SQLException {
        return queryBean(conn, SELECT_STUDENT + " WHERE s.id = ? FOR UPDATE", Student.class, id);
    }

    public Student findByUserId(int userId) {
        return queryBean(SELECT_STUDENT + " WHERE s.user_id = ?", Student.class, userId);
    }

    public Student findByStudentNo(String studentNo) {
        return queryBean(SELECT_STUDENT + " WHERE s.student_no = ?", Student.class, studentNo);
    }

    public PageBean<Student> findPage(String keyword, String status, Integer adminId, int page, int size) {
        QueryParts parts = buildWhere(keyword, status, adminId);
        String sql = SELECT_STUDENT + parts.where + " ORDER BY s.create_time DESC";
        String countSql = "SELECT COUNT(*) FROM student s JOIN `user` u ON s.user_id = u.id LEFT JOIN dormitory d ON s.dormitory_id = d.id LEFT JOIN building b ON d.building_id = b.id" + parts.where;
        return queryPage(sql, countSql, Student.class, page, size, parts.params.toArray());
    }

    public List<Student> findOutStudents() {
        return queryList(SELECT_STUDENT + " WHERE s.status = 'OUT' ORDER BY s.student_no", Student.class);
    }

    public List<Student> findAll() {
        return queryList(SELECT_STUDENT + " ORDER BY s.create_time DESC", Student.class);
    }

    public List<Student> findRoommates(int dormitoryId, int exceptStudentId) {
        return queryList(SELECT_STUDENT + " WHERE s.dormitory_id = ? AND s.id <> ? ORDER BY s.student_no", Student.class, dormitoryId, exceptStudentId);
    }

    private QueryParts buildWhere(String keyword, String status, Integer adminId) {
        QueryParts parts = new QueryParts();
        parts.where.append(" WHERE 1=1");
        if (keyword != null && !keyword.isEmpty()) {
            parts.where.append(" AND (s.student_no LIKE ? OR u.real_name LIKE ? OR u.phone LIKE ? OR s.class_name LIKE ?)");
            String like = "%" + keyword + "%";
            parts.params.add(like);
            parts.params.add(like);
            parts.params.add(like);
            parts.params.add(like);
        }
        if (status != null && !status.isEmpty()) {
            parts.where.append(" AND s.status = ?");
            parts.params.add(status);
        }
        if (adminId != null && adminId > 0) {
            parts.where.append(" AND b.building_admin_id = ?");
            parts.params.add(adminId);
        }
        return parts;
    }

    public int save(Connection conn, Student student) throws SQLException {
        return insertAndGetKey(conn, "INSERT INTO student (user_id, student_no, gender, class_name, major, dormitory_id, status, emergency_contact, emergency_phone) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                student.getUserId(), student.getStudentNo(), student.getGender(), student.getClassName(), student.getMajor(), student.getDormitoryId(), student.getStatus(), student.getEmergencyContact(), student.getEmergencyPhone());
    }

    public int updateStudent(Student student) {
        return update("UPDATE student SET student_no = ?, gender = ?, class_name = ?, major = ?, emergency_contact = ?, emergency_phone = ? WHERE id = ?",
                student.getStudentNo(), student.getGender(), student.getClassName(), student.getMajor(), student.getEmergencyContact(), student.getEmergencyPhone(), student.getId());
    }

    public int updateStudentNo(Connection conn, int userId, String studentNo) throws SQLException {
        return update(conn, "UPDATE student SET student_no = ? WHERE user_id = ?", studentNo, userId);
    }

    public int updateDorm(Connection conn, int studentId, Integer dormitoryId, String status) throws SQLException {
        if (dormitoryId == null) {
            return update(conn, "UPDATE student SET dormitory_id = NULL, check_in_date = NULL, status = ? WHERE id = ?", status, studentId);
        }
        return update(conn, "UPDATE student SET dormitory_id = ?, check_in_date = CURDATE(), status = ? WHERE id = ?", dormitoryId, status, studentId);
    }

    public int deleteById(int id) {
        return update("DELETE FROM student WHERE id = ?", id);
    }

    public int countAll(Integer adminId) {
        if (adminId == null) {
            return queryNumber("SELECT COUNT(*) FROM student").intValue();
        }
        return queryNumber("SELECT COUNT(*) FROM student s JOIN dormitory d ON s.dormitory_id = d.id JOIN building b ON d.building_id = b.id WHERE b.building_admin_id = ?", adminId).intValue();
    }

    private static class QueryParts {
        private final StringBuilder where = new StringBuilder();
        private final List<Object> params = new ArrayList<Object>();
    }
}
