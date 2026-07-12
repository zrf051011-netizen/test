package com.dorm.servlet.admin;

import com.dorm.service.BuildingService;
import com.dorm.service.RepairService;
import com.dorm.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/repairs")
public class AdminRepairServlet extends HttpServlet {
    private final RepairService repairService = new RepairService();
    private final BuildingService buildingService = new BuildingService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyword = WebUtil.getString(request, "keyword");
        String status = WebUtil.getString(request, "status");
        int buildingId = WebUtil.getInt(request, "buildingId", 0);

        request.setAttribute("activeMenu", "repairs");
        request.setAttribute("pageTitle", "报修监管");
        request.setAttribute("keyword", keyword);
        request.setAttribute("status", status);
        request.setAttribute("buildingId", buildingId);
        request.setAttribute("buildings", buildingService.findAllActive());
        request.setAttribute("pageBean", repairService.findPage(
                keyword,
                status,
                buildingId == 0 ? null : buildingId,
                null,
                null,
                WebUtil.getInt(request, "page", 1),
                10
        ));
        request.getRequestDispatcher("/WEB-INF/admin/repairs.jsp").forward(request, response);
    }
}
