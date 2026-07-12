package com.dorm.servlet.buildingadmin;

import com.dorm.entity.User;
import com.dorm.service.BuildingService;
import com.dorm.service.CheckRecordService;
import com.dorm.service.DashboardService;
import com.dorm.service.DormitoryService;
import com.dorm.service.RepairService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/buildingadmin/dashboard")
public class BuildingAdminDashboardServlet extends HttpServlet {
    private final DashboardService dashboardService = new DashboardService();
    private final BuildingService buildingService = new BuildingService();
    private final DormitoryService dormitoryService = new DormitoryService();
    private final RepairService repairService = new RepairService();
    private final CheckRecordService checkRecordService = new CheckRecordService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        Integer adminId = user.getId();
        request.setAttribute("activeMenu", "dashboard");
        request.setAttribute("pageTitle", "楼栋总览");
        request.setAttribute("stats", dashboardService.getStats(adminId));
        request.setAttribute("buildingStats", buildingService.findStats(adminId));
        request.setAttribute("roomCards", dormitoryService.findList("", null, "", adminId));
        request.setAttribute("latestRepairs", repairService.latest(adminId, 5));
        request.setAttribute("latestRecords", checkRecordService.latest(adminId, 5));
        request.getRequestDispatcher("/WEB-INF/buildingadmin/dashboard.jsp").forward(request, response);
    }
}
