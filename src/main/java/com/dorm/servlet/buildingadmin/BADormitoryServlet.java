package com.dorm.servlet.buildingadmin;

import com.dorm.entity.User;
import com.dorm.service.BuildingService;
import com.dorm.service.DormitoryService;
import com.dorm.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/buildingadmin/dormitories")
public class BADormitoryServlet extends HttpServlet {
    private final DormitoryService dormitoryService = new DormitoryService();
    private final BuildingService buildingService = new BuildingService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        String keyword = WebUtil.getString(request, "keyword");
        String status = WebUtil.getString(request, "status");
        request.setAttribute("activeMenu", "dormitories");
        request.setAttribute("pageTitle", "本楼栋宿舍");
        request.setAttribute("keyword", keyword);
        request.setAttribute("status", status);
        request.setAttribute("buildings", buildingService.findByAdminId(user.getId()));
        request.setAttribute("roomCards", dormitoryService.findList(keyword, null, status, user.getId()));
        request.setAttribute("pageBean", dormitoryService.findPage(keyword, null, status, user.getId(), WebUtil.getInt(request, "page", 1), 8));
        request.getRequestDispatcher("/WEB-INF/buildingadmin/dormitories.jsp").forward(request, response);
    }
}
