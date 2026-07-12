package com.dorm.dao;

import com.dorm.entity.Building;
import com.dorm.util.PageBean;

import java.util.ArrayList;
import java.util.List;

public class BuildingDao extends BaseDao {
    private static final String SELECT_BUILDING =
            "SELECT b.id, b.building_name, b.floor_count, b.address, b.building_admin_id, b.status, b.create_time, b.update_time, u.real_name AS admin_name " +
            "FROM building b LEFT JOIN `user` u ON b.building_admin_id = u.id";

    private static final String SELECT_BUILDING_STATS =
            "SELECT b.id, b.building_name, b.floor_count, b.address, b.building_admin_id, b.status, b.create_time, b.update_time, u.real_name AS admin_name, " +
            "COUNT(d.id) AS room_count, COALESCE(SUM(d.capacity), 0) AS bed_count, COALESCE(SUM(occupancy.used_count), 0) AS used_count " +
            "FROM building b LEFT JOIN `user` u ON b.building_admin_id = u.id LEFT JOIN dormitory d ON b.id = d.building_id " +
            "LEFT JOIN (SELECT dormitory_id, COUNT(*) AS used_count FROM student WHERE status = 'IN' GROUP BY dormitory_id) occupancy ON occupancy.dormitory_id = d.id";

    public Building findById(int id) {
        return queryBean(SELECT_BUILDING + " WHERE b.id = ?", Building.class, id);
    }

    public List<Building> findAllActive() {
        return queryList(SELECT_BUILDING + " WHERE b.status = 1 ORDER BY b.building_name", Building.class);
    }

    public List<Building> findByAdminId(int adminId) {
        return queryList(SELECT_BUILDING + " WHERE b.building_admin_id = ? AND b.status = 1 ORDER BY b.building_name", Building.class, adminId);
    }

    public PageBean<Building> findPage(String keyword, int page, int size) {
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        List<Object> params = new ArrayList<Object>();
        if (keyword != null && !keyword.isEmpty()) {
            where.append(" AND (b.building_name LIKE ? OR b.address LIKE ? OR u.real_name LIKE ?)");
            String like = "%" + keyword + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }
        String sql = SELECT_BUILDING + where + " ORDER BY b.create_time DESC";
        String countSql = "SELECT COUNT(*) FROM building b LEFT JOIN `user` u ON b.building_admin_id = u.id" + where;
        return queryPage(sql, countSql, Building.class, page, size, params.toArray());
    }

    public List<Building> findStats(Integer adminId) {
        String where = adminId == null ? "" : " WHERE b.building_admin_id = ?";
        String group = " GROUP BY b.id, b.building_name, b.floor_count, b.address, b.building_admin_id, b.status, b.create_time, b.update_time, u.real_name ORDER BY b.building_name";
        if (adminId == null) {
            return queryList(SELECT_BUILDING_STATS + group, Building.class);
        }
        return queryList(SELECT_BUILDING_STATS + where + group, Building.class, adminId);
    }

    public int save(Building building) {
        return insertAndGetKey("INSERT INTO building (building_name, floor_count, address, building_admin_id, status) VALUES (?, ?, ?, ?, ?)",
                building.getBuildingName(), building.getFloorCount(), building.getAddress(), building.getBuildingAdminId(), building.getStatus());
    }

    public int updateBuilding(Building building) {
        return update("UPDATE building SET building_name = ?, floor_count = ?, address = ?, building_admin_id = ?, status = ? WHERE id = ?",
                building.getBuildingName(), building.getFloorCount(), building.getAddress(), building.getBuildingAdminId(), building.getStatus(), building.getId());
    }

    public int deleteById(int id) {
        return update("DELETE FROM building WHERE id = ?", id);
    }

    public int countAll() {
        return queryNumber("SELECT COUNT(*) FROM building").intValue();
    }
}
