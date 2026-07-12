package com.dorm.servlet.admin;

import com.dorm.entity.Dormitory;
import com.dorm.exception.BusinessException;
import com.dorm.service.BuildingService;
import com.dorm.service.DormitoryService;
import com.dorm.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet({"/admin/dormitories", "/admin/dormitories/*"})
public class DormitoryManageServlet extends HttpServlet {
    private final DormitoryService dormitoryService = new DormitoryService();
    private final BuildingService buildingService = new BuildingService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        if ("/edit".equals(path)) {
            int id = WebUtil.getInt(request, "id", 0);
            if (id > 0) {
                request.setAttribute("item", dormitoryService.findById(id));
            }
            prepare(request);
            request.getRequestDispatcher("/WEB-INF/admin/dormitory_form.jsp").forward(request, response);
            return;
        }
        if ("/delete".equals(path)) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        prepare(request);
        String keyword = WebUtil.getString(request, "keyword");
        String status = WebUtil.getString(request, "status");
        int buildingId = WebUtil.getInt(request, "buildingId", 0);
        request.setAttribute("keyword", keyword);
        request.setAttribute("status", status);
        request.setAttribute("buildingId", buildingId);
        request.setAttribute("pageBean", dormitoryService.findPage(keyword, buildingId == 0 ? null : buildingId, status, null, WebUtil.getInt(request, "page", 1), 8));
        request.getRequestDispatcher("/WEB-INF/admin/dormitories.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if ("/delete".equals(request.getPathInfo())) {
            try {
                dormitoryService.delete(WebUtil.getInt(request, "id", 0));
                response.sendRedirect(request.getContextPath() + "/admin/dormitories?success=" + WebUtil.enc("删除成功"));
            } catch (Exception e) {
                response.sendRedirect(request.getContextPath() + "/admin/dormitories?error=" + WebUtil.enc(e.getMessage()));
            }
            return;
        }
        Dormitory dormitory = new Dormitory();
        dormitory.setId(WebUtil.getInt(request, "id", 0));
        dormitory.setBuildingId(WebUtil.getInt(request, "buildingId", 0));
        dormitory.setRoomNumber(WebUtil.getString(request, "roomNumber"));
        dormitory.setFloor(WebUtil.getInt(request, "floor", 1));
        dormitory.setCapacity(WebUtil.getInt(request, "capacity", 4));
        dormitory.setStatus(WebUtil.getString(request, "status"));
        try {
            dormitoryService.save(dormitory);
            response.sendRedirect(request.getContextPath() + "/admin/dormitories?success=" + WebUtil.enc("保存成功"));
        } catch (BusinessException e) {
            response.sendRedirect(request.getContextPath() + "/admin/dormitories?error=" + WebUtil.enc(e.getMessage()));
        }
    }

    private void prepare(HttpServletRequest request) {
        request.setAttribute("activeMenu", "dormitories");
        request.setAttribute("pageTitle", "宿舍管理");
        request.setAttribute("buildings", buildingService.findAllActive());
    }
}
