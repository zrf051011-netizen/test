package com.dorm.dao;

import com.dorm.entity.Dormitory;
import com.dorm.util.PageBean;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DormitoryDao extends BaseDao {
    private static final String OCCUPANCY_COUNT =
            "(SELECT COUNT(*) FROM student s WHERE s.dormitory_id = d.id AND s.status = 'IN')";

    private static final String SELECT_DORM =
            "SELECT d.id, d.building_id, d.room_number, d.floor, d.capacity, " + OCCUPANCY_COUNT + " AS current_count, d.status, d.create_time, d.update_time, " +
            "b.building_name, b.building_admin_id, u.real_name AS admin_name FROM dormitory d " +
            "LEFT JOIN building b ON d.building_id = b.id LEFT JOIN `user` u ON b.building_admin_id = u.id";

    public Dormitory findById(int id) {
        return queryBean(SELECT_DORM + " WHERE d.id = ?", Dormitory.class, id);
    }

    public Dormitory findByIdForUpdate(Connection conn, int id) throws SQLException {
        return queryBean(conn, SELECT_DORM + " WHERE d.id = ? FOR UPDATE", Dormitory.class, id);
    }

    public PageBean<Dormitory> findPage(String keyword, Integer buildingId, String status, Integer adminId, int page, int size) {
        QueryParts parts = buildWhere(keyword, buildingId, status, adminId);
        String sql = SELECT_DORM + parts.where + " ORDER BY b.building_name, d.floor, d.room_number";
        String countSql = "SELECT COUNT(*) FROM dormitory d LEFT JOIN building b ON d.building_id = b.id LEFT JOIN `user` u ON b.building_admin_id = u.id" + parts.where;
        return queryPage(sql, countSql, Dormitory.class, page, size, parts.params.toArray());
    }

    public List<Dormitory> findList(String keyword, Integer buildingId, String status, Integer adminId) {
        QueryParts parts = buildWhere(keyword, buildingId, status, adminId);
        return queryList(SELECT_DORM + parts.where + " ORDER BY b.building_name, d.floor, d.room_number", Dormitory.class, parts.params.toArray());
    }

    public List<Dormitory> findAvailable(Integer adminId) {
        if (adminId == null) {
            return queryList(SELECT_DORM + " WHERE d.status <> 'REPAIR' AND " + OCCUPANCY_COUNT + " < d.capacity ORDER BY b.building_name, d.room_number", Dormitory.class);
        }
        return queryList(SELECT_DORM + " WHERE d.status <> 'REPAIR' AND " + OCCUPANCY_COUNT + " < d.capacity AND b.building_admin_id = ? ORDER BY b.building_name, d.room_number", Dormitory.class, adminId);
    }

    private QueryParts buildWhere(String keyword, Integer buildingId, String status, Integer adminId) {
        QueryParts parts = new QueryParts();
        parts.where.append(" WHERE 1=1");
        if (keyword != null && !keyword.isEmpty()) {
            parts.where.append(" AND (d.room_number LIKE ? OR b.building_name LIKE ?)");
            String like = "%" + keyword + "%";
            parts.params.add(like);
            parts.params.add(like);
        }
        if (buildingId != null && buildingId > 0) {
            parts.where.append(" AND d.building_id = ?");
            parts.params.add(buildingId);
        }
        if (status != null && !status.isEmpty()) {
            parts.where.append(" AND d.status = ?");
            parts.params.add(status);
        }
        if (adminId != null && adminId > 0) {
            parts.where.append(" AND b.building_admin_id = ?");
            parts.params.add(adminId);
        }
        return parts;
    }

    public int save(Dormitory dormitory) {
        return insertAndGetKey("INSERT INTO dormitory (building_id, room_number, floor, capacity, current_count, status) VALUES (?, ?, ?, ?, ?, ?)",
                dormitory.getBuildingId(), dormitory.getRoomNumber(), dormitory.getFloor(), dormitory.getCapacity(), dormitory.getCurrentCount(), dormitory.getStatus());
    }

    public int updateDormitory(Dormitory dormitory) {
        return update("UPDATE dormitory SET building_id = ?, room_number = ?, floor = ?, capacity = ?, status = ? WHERE id = ?",
                dormitory.getBuildingId(), dormitory.getRoomNumber(), dormitory.getFloor(), dormitory.getCapacity(), dormitory.getStatus(), dormitory.getId());
    }

    public int updateCountAndStatus(Connection conn, int id, int currentCount, String status) throws SQLException {
        return update(conn, "UPDATE dormitory SET current_count = ?, status = ? WHERE id = ?", currentCount, status, id);
    }

    public int deleteById(int id) {
        return update("DELETE FROM dormitory WHERE id = ?", id);
    }

    public int countAll() {
        return queryNumber("SELECT COUNT(*) FROM dormitory").intValue();
    }

    public int sumCapacity(Integer adminId) {
        if (adminId == null) {
            return queryNumber("SELECT COALESCE(SUM(capacity), 0) FROM dormitory").intValue();
        }
        return queryNumber("SELECT COALESCE(SUM(d.capacity), 0) FROM dormitory d JOIN building b ON d.building_id = b.id WHERE b.building_admin_id = ?", adminId).intValue();
    }

    public int sumCurrentCount(Integer adminId) {
        if (adminId == null) {
            return queryNumber("SELECT COUNT(*) FROM student WHERE status = 'IN' AND dormitory_id IS NOT NULL").intValue();
        }
        return queryNumber("SELECT COUNT(*) FROM student s JOIN dormitory d ON s.dormitory_id = d.id JOIN building b ON d.building_id = b.id WHERE s.status = 'IN' AND b.building_admin_id = ?", adminId).intValue();
    }

    private static class QueryParts {
        private final StringBuilder where = new StringBuilder();
        private final List<Object> params = new ArrayList<Object>();
    }
}
