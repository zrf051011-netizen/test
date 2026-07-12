package com.dorm.service;

import com.dorm.dao.DashboardDao;
import com.dorm.entity.DashboardStats;

public class DashboardService {
    private final DashboardDao dashboardDao = new DashboardDao();

    public DashboardStats getStats(Integer adminId) {
        return dashboardDao.getStats(adminId);
    }
}
