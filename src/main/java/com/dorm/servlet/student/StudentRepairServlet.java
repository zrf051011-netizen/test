package com.dorm.servlet.student;

import com.dorm.entity.Student;
import com.dorm.entity.User;
import com.dorm.exception.BusinessException;
import com.dorm.service.RepairService;
import com.dorm.service.StudentService;
import com.dorm.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet({"/student/repairs", "/student/repairs/*"})
public class StudentRepairServlet extends HttpServlet {
    private final RepairService repairService = new RepairService();
    private final StudentService studentService = new StudentService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        Student student = studentService.findByUserId(user.getId());
        request.setAttribute("activeMenu", "repairs");
        request.setAttribute("pageTitle", "我的报修");
        request.setAttribute("student", student);
        request.setAttribute("pageBean", repairService.findPage("", WebUtil.getString(request, "status"), null, student == null ? 0 : student.getId(), WebUtil.getInt(request, "page", 1), 8));
        request.getRequestDispatcher("/WEB-INF/student/repairs.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        try {
            repairService.submit(user.getId(), WebUtil.getString(request, "repairType"), WebUtil.getString(request, "description"));
            response.sendRedirect(request.getContextPath() + "/student/repairs?success=" + WebUtil.enc("报修已提交"));
        } catch (BusinessException e) {
            response.sendRedirect(request.getContextPath() + "/student/repairs?error=" + WebUtil.enc(e.getMessage()));
        }
    }
}
