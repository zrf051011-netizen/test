package com.dorm.servlet.admin;

import com.dorm.entity.Building;
import com.dorm.exception.BusinessException;
import com.dorm.service.BuildingService;
import com.dorm.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet({"/admin/buildings", "/admin/buildings/*"})
public class BuildingManageServlet extends HttpServlet {
    private final BuildingService buildingService = new BuildingService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        if ("/edit".equals(path)) {
            int id = WebUtil.getInt(request, "id", 0);
            if (id > 0) {
                request.setAttribute("item", buildingService.findById(id));
            }
            prepare(request);
            request.getRequestDispatcher("/WEB-INF/admin/building_form.jsp").forward(request, response);
            return;
        }
        if ("/delete".equals(path)) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        prepare(request);
        request.setAttribute("keyword", WebUtil.getString(request, "keyword"));
        request.setAttribute("pageBean", buildingService.findPage(WebUtil.getString(request, "keyword"), WebUtil.getInt(request, "page", 1), 8));
        request.getRequestDispatcher("/WEB-INF/admin/buildings.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if ("/delete".equals(request.getPathInfo())) {
            try {
                buildingService.delete(WebUtil.getInt(request, "id", 0));
                response.sendRedirect(request.getContextPath() + "/admin/buildings?success=" + WebUtil.enc("删除成功"));
            } catch (Exception e) {
                response.sendRedirect(request.getContextPath() + "/admin/buildings?error=" + WebUtil.enc(e.getMessage()));
            }
            return;
        }
        Building building = new Building();
        building.setId(WebUtil.getInt(request, "id", 0));
        building.setBuildingName(WebUtil.getString(request, "buildingName"));
        building.setFloorCount(WebUtil.getInt(request, "floorCount", 6));
        building.setAddress(WebUtil.getString(request, "address"));
        int adminId = WebUtil.getInt(request, "buildingAdminId", 0);
        building.setBuildingAdminId(adminId == 0 ? null : adminId);
        building.setStatus(WebUtil.getInt(request, "status", 1));
        try {
            buildingService.save(building);
            response.sendRedirect(request.getContextPath() + "/admin/buildings?success=" + WebUtil.enc("保存成功"));
        } catch (BusinessException e) {
            response.sendRedirect(request.getContextPath() + "/admin/buildings?error=" + WebUtil.enc(e.getMessage()));
        }
    }

    private void prepare(HttpServletRequest request) {
        request.setAttribute("activeMenu", "buildings");
        request.setAttribute("pageTitle", "楼栋管理");
        request.setAttribute("admins", buildingService.findBuildingAdmins());
    }
}
