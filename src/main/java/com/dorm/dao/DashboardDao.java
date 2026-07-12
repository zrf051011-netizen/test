package com.dorm.dao;

import com.dorm.entity.DashboardStats;

public class DashboardDao extends BaseDao {
    public DashboardStats getStats(Integer adminId) {
        DashboardStats stats = new DashboardStats();
        if (adminId == null) {
            stats.setUserCount(queryNumber("SELECT COUNT(*) FROM `user`").intValue());
            stats.setStudentCount(queryNumber("SELECT COUNT(*) FROM student").intValue());
            stats.setBuildingCount(queryNumber("SELECT COUNT(*) FROM building").intValue());
            stats.setDormitoryCount(queryNumber("SELECT COUNT(*) FROM dormitory").intValue());
            stats.setBedCount(queryNumber("SELECT COALESCE(SUM(capacity), 0) FROM dormitory").intValue());
            stats.setUsedBedCount(queryNumber("SELECT COUNT(*) FROM student WHERE status = 'IN' AND dormitory_id IS NOT NULL").intValue());
            stats.setRepairPendingCount(queryNumber("SELECT COUNT(*) FROM repair WHERE status = 'PENDING'").intValue());
            stats.setRepairDoingCount(queryNumber("SELECT COUNT(*) FROM repair WHERE status = 'DOING'").intValue());
            return stats;
        }

        stats.setUserCount(queryNumber("SELECT COUNT(*) FROM student s JOIN dormitory d ON s.dormitory_id = d.id JOIN building b ON d.building_id = b.id WHERE b.building_admin_id = ?", adminId).intValue());
        stats.setStudentCount(stats.getUserCount());
        stats.setBuildingCount(queryNumber("SELECT COUNT(*) FROM building WHERE building_admin_id = ?", adminId).intValue());
        stats.setDormitoryCount(queryNumber("SELECT COUNT(*) FROM dormitory d JOIN building b ON d.building_id = b.id WHERE b.building_admin_id = ?", adminId).intValue());
        stats.setBedCount(queryNumber("SELECT COALESCE(SUM(d.capacity), 0) FROM dormitory d JOIN building b ON d.building_id = b.id WHERE b.building_admin_id = ?", adminId).intValue());
        stats.setUsedBedCount(queryNumber("SELECT COUNT(*) FROM student s JOIN dormitory d ON s.dormitory_id = d.id JOIN building b ON d.building_id = b.id WHERE s.status = 'IN' AND b.building_admin_id = ?", adminId).intValue());
        stats.setRepairPendingCount(queryNumber("SELECT COUNT(*) FROM repair r JOIN dormitory d ON r.dormitory_id = d.id JOIN building b ON d.building_id = b.id WHERE r.status = 'PENDING' AND b.building_admin_id = ?", adminId).intValue());
        stats.setRepairDoingCount(queryNumber("SELECT COUNT(*) FROM repair r JOIN dormitory d ON r.dormitory_id = d.id JOIN building b ON d.building_id = b.id WHERE r.status = 'DOING' AND b.building_admin_id = ?", adminId).intValue());
        return stats;
    }
}
