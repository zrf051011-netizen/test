package com.dorm.dao;

import com.dorm.entity.Repair;
import com.dorm.util.PageBean;

import java.util.ArrayList;
import java.util.List;

public class RepairDao extends BaseDao {
    private static final String SELECT_REPAIR =
            "SELECT r.id, r.dormitory_id, r.reporter_id, r.repair_type, r.description, r.report_time, r.status, r.handler_id, r.handle_time, r.handle_remark, r.create_time, r.update_time, " +
            "ru.real_name AS reporter_name, hu.real_name AS handler_name, b.building_name, b.building_admin_id, d.room_number " +
            "FROM repair r JOIN student s ON r.reporter_id = s.id JOIN `user` ru ON s.user_id = ru.id " +
            "JOIN dormitory d ON r.dormitory_id = d.id JOIN building b ON d.building_id = b.id LEFT JOIN `user` hu ON r.handler_id = hu.id";

    public Repair findById(int id) {
        return queryBean(SELECT_REPAIR + " WHERE r.id = ?", Repair.class, id);
    }

    public PageBean<Repair> findPage(String keyword, String status, Integer adminId, Integer reporterId, int page, int size) {
        return findPage(keyword, status, null, adminId, reporterId, page, size);
    }

    public PageBean<Repair> findPage(String keyword, String status, Integer buildingId, Integer adminId, Integer reporterId, int page, int size) {
        QueryParts parts = buildWhere(keyword, status, buildingId, adminId, reporterId);
        String sql = SELECT_REPAIR + parts.where + " ORDER BY r.report_time DESC";
        String countSql = "SELECT COUNT(*) FROM repair r JOIN student s ON r.reporter_id = s.id JOIN `user` ru ON s.user_id = ru.id JOIN dormitory d ON r.dormitory_id = d.id JOIN building b ON d.building_id = b.id LEFT JOIN `user` hu ON r.handler_id = hu.id" + parts.where;
        return queryPage(sql, countSql, Repair.class, page, size, parts.params.toArray());
    }

    public List<Repair> findList(String keyword, String status, Integer buildingId, Integer adminId, Integer reporterId) {
        QueryParts parts = buildWhere(keyword, status, buildingId, adminId, reporterId);
        return queryList(SELECT_REPAIR + parts.where + " ORDER BY r.report_time DESC", Repair.class, parts.params.toArray());
    }

    public List<Repair> latest(Integer adminId, int size) {
        if (adminId == null) {
            return queryList(SELECT_REPAIR + " ORDER BY r.report_time DESC LIMIT ?", Repair.class, size);
        }
        return queryList(SELECT_REPAIR + " WHERE b.building_admin_id = ? ORDER BY r.report_time DESC LIMIT ?", Repair.class, adminId, size);
    }

    public List<Repair> latestOpen(Integer adminId, Integer reporterId, boolean includeDoing, int size) {
        QueryParts parts = buildWhere("", "", null, adminId, reporterId);
        parts.where.append(includeDoing ? " AND r.status <> 'DONE'" : " AND r.status = 'PENDING'");
        return queryList(SELECT_REPAIR + parts.where + " ORDER BY r.report_time DESC LIMIT ?",
                Repair.class, append(parts.params, size));
    }

    private QueryParts buildWhere(String keyword, String status, Integer buildingId, Integer adminId, Integer reporterId) {
        QueryParts parts = new QueryParts();
        parts.where.append(" WHERE 1=1");
        if (keyword != null && !keyword.isEmpty()) {
            parts.where.append(" AND (ru.real_name LIKE ? OR b.building_name LIKE ? OR d.room_number LIKE ? OR r.repair_type LIKE ?)");
            String like = "%" + keyword + "%";
            parts.params.add(like);
            parts.params.add(like);
            parts.params.add(like);
            parts.params.add(like);
        }
        if (status != null && !status.isEmpty()) {
            parts.where.append(" AND r.status = ?");
            parts.params.add(status);
        }
        if (buildingId != null && buildingId > 0) {
            parts.where.append(" AND d.building_id = ?");
            parts.params.add(buildingId);
        }
        if (adminId != null && adminId > 0) {
            parts.where.append(" AND b.building_admin_id = ?");
            parts.params.add(adminId);
        }
        if (reporterId != null && reporterId > 0) {
            parts.where.append(" AND r.reporter_id = ?");
            parts.params.add(reporterId);
        }
        return parts;
    }

    public int save(Repair repair) {
        return insertAndGetKey("INSERT INTO repair (dormitory_id, reporter_id, repair_type, description, status) VALUES (?, ?, ?, ?, 'PENDING')",
                repair.getDormitoryId(), repair.getReporterId(), repair.getRepairType(), repair.getDescription());
    }

    public int updateStatusForBuildingAdmin(int id, String currentStatus, String nextStatus, int handlerId, String remark) {
        String base = "UPDATE repair r JOIN dormitory d ON r.dormitory_id = d.id " +
                "JOIN building b ON d.building_id = b.id SET r.status = ?, r.handler_id = ?, r.handle_remark = ?";
        if ("DONE".equals(nextStatus)) {
            base += ", r.handle_time = NOW()";
        }
        return update(base + " WHERE r.id = ? AND r.status = ? AND b.building_admin_id = ?",
                nextStatus, handlerId, remark, id, currentStatus, handlerId);
    }

    public boolean hasUnfinishedByStudent(int studentId) {
        return queryNumber("SELECT COUNT(*) FROM repair WHERE reporter_id = ? AND status <> 'DONE'", studentId).intValue() > 0;
    }

    public int countByStatus(String status, Integer adminId) {
        if (adminId == null) {
            return queryNumber("SELECT COUNT(*) FROM repair WHERE status = ?", status).intValue();
        }
        return queryNumber("SELECT COUNT(*) FROM repair r JOIN dormitory d ON r.dormitory_id = d.id JOIN building b ON d.building_id = b.id WHERE r.status = ? AND b.building_admin_id = ?", status, adminId).intValue();
    }

    public int countOpenByReporter(int reporterId) {
        return queryNumber("SELECT COUNT(*) FROM repair WHERE reporter_id = ? AND status <> 'DONE'", reporterId).intValue();
    }

    private Object[] append(List<Object> params, Object value) {
        List<Object> values = new ArrayList<Object>(params);
        values.add(value);
        return values.toArray();
    }

    private static class QueryParts {
        private final StringBuilder where = new StringBuilder();
        private final List<Object> params = new ArrayList<Object>();
    }
}
