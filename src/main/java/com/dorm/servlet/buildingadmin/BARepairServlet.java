package com.dorm.servlet.buildingadmin;

import com.dorm.entity.User;
import com.dorm.exception.BusinessException;
import com.dorm.service.RepairService;
import com.dorm.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet({"/buildingadmin/repairs", "/buildingadmin/repairs/*"})
public class BARepairServlet extends HttpServlet {
    private final RepairService repairService = new RepairService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        String keyword = WebUtil.getString(request, "keyword");
        String status = WebUtil.getString(request, "status");
        request.setAttribute("activeMenu", "repairs");
        request.setAttribute("pageTitle", "报修处理");
        request.setAttribute("keyword", keyword);
        request.setAttribute("status", status);
        request.setAttribute("pageBean", repairService.findPage(keyword, status, user.getId(), null, WebUtil.getInt(request, "page", 1), 8));
        request.getRequestDispatcher("/WEB-INF/buildingadmin/repairs.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        try {
            repairService.process(WebUtil.getInt(request, "id", 0), WebUtil.getString(request, "status"), user.getId(), WebUtil.getString(request, "handleRemark"));
            response.sendRedirect(request.getContextPath() + "/buildingadmin/repairs?success=" + WebUtil.enc("处理成功"));
        } catch (BusinessException e) {
            response.sendRedirect(request.getContextPath() + "/buildingadmin/repairs?error=" + WebUtil.enc(e.getMessage()));
        }
    }
}
