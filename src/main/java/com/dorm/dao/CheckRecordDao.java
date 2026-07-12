package com.dorm.dao;

import com.dorm.entity.CheckRecord;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CheckRecordDao extends BaseDao {
    private static final String SELECT_RECORD =
            "SELECT cr.id, cr.student_id, cr.from_dormitory_id, cr.to_dormitory_id, cr.type, cr.check_date, cr.operator_id, cr.remark, cr.create_time, " +
            "su.real_name AS student_name, ou.real_name AS operator_name, " +
            "CONCAT(fb.building_name, '-', fd.room_number) AS from_room, CONCAT(tb.building_name, '-', td.room_number) AS to_room " +
            "FROM check_record cr JOIN student s ON cr.student_id = s.id JOIN `user` su ON s.user_id = su.id " +
            "LEFT JOIN `user` ou ON cr.operator_id = ou.id " +
            "LEFT JOIN dormitory fd ON cr.from_dormitory_id = fd.id LEFT JOIN building fb ON fd.building_id = fb.id " +
            "LEFT JOIN dormitory td ON cr.to_dormitory_id = td.id LEFT JOIN building tb ON td.building_id = tb.id";

    public int save(Connection conn, CheckRecord record) throws SQLException {
        return insertAndGetKey(conn, "INSERT INTO check_record (student_id, from_dormitory_id, to_dormitory_id, type, operator_id, remark) VALUES (?, ?, ?, ?, ?, ?)",
                record.getStudentId(), record.getFromDormitoryId(), record.getToDormitoryId(), record.getType(), record.getOperatorId(), record.getRemark());
    }

    public List<CheckRecord> findByStudentId(int studentId) {
        return queryList(SELECT_RECORD + " WHERE cr.student_id = ? ORDER BY cr.check_date DESC", CheckRecord.class, studentId);
    }

    public List<CheckRecord> latest(Integer adminId, int size) {
        if (adminId == null) {
            return queryList(SELECT_RECORD + " ORDER BY cr.check_date DESC LIMIT ?", CheckRecord.class, size);
        }
        return queryList(SELECT_RECORD + " JOIN dormitory cd ON s.dormitory_id = cd.id JOIN building cb ON cd.building_id = cb.id WHERE cb.building_admin_id = ? ORDER BY cr.check_date DESC LIMIT ?",
                CheckRecord.class, adminId, size);
    }
}
