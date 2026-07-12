package com.dorm.servlet.buildingadmin;

import com.dorm.entity.User;
import com.dorm.service.StudentService;
import com.dorm.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/buildingadmin/students")
public class BAStudentServlet extends HttpServlet {
    private final StudentService studentService = new StudentService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        String keyword = WebUtil.getString(request, "keyword");
        String status = WebUtil.getString(request, "status");
        request.setAttribute("activeMenu", "students");
        request.setAttribute("pageTitle", "本楼栋学生");
        request.setAttribute("keyword", keyword);
        request.setAttribute("status", status);
        request.setAttribute("pageBean", studentService.findPage(keyword, status, user.getId(), WebUtil.getInt(request, "page", 1), 8));
        request.getRequestDispatcher("/WEB-INF/buildingadmin/students.jsp").forward(request, response);
    }
}
