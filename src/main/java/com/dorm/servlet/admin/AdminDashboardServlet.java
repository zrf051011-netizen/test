package com.dorm.servlet.admin;

import com.dorm.service.BuildingService;
import com.dorm.service.CheckRecordService;
import com.dorm.service.DashboardService;
import com.dorm.service.RepairService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {
    private final DashboardService dashboardService = new DashboardService();
    private final BuildingService buildingService = new BuildingService();
    private final RepairService repairService = new RepairService();
    private final CheckRecordService checkRecordService = new CheckRecordService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("activeMenu", "dashboard");
        request.setAttribute("pageTitle", "总览");
        request.setAttribute("stats", dashboardService.getStats(null));
        request.setAttribute("buildingStats", buildingService.findStats(null));
        request.setAttribute("latestRepairs", repairService.latest(null, 5));
        request.setAttribute("latestRecords", checkRecordService.latest(null, 5));
        request.getRequestDispatcher("/WEB-INF/admin/dashboard.jsp").forward(request, response);
    }
}
