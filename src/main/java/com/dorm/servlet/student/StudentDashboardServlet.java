package com.dorm.servlet.student;

import com.dorm.entity.Student;
import com.dorm.entity.User;
import com.dorm.service.CheckRecordService;
import com.dorm.service.RepairService;
import com.dorm.service.StudentService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/student/dashboard")
public class StudentDashboardServlet extends HttpServlet {
    private final StudentService studentService = new StudentService();
    private final RepairService repairService = new RepairService();
    private final CheckRecordService checkRecordService = new CheckRecordService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        Student student = studentService.findByUserId(user.getId());
        request.setAttribute("activeMenu", "dashboard");
        request.setAttribute("pageTitle", "我的宿舍");
        request.setAttribute("student", student);
        if (student != null && student.getDormitoryId() != null) {
            request.setAttribute("roommates", studentService.findRoommates(student.getDormitoryId(), student.getId()));
        }
        if (student != null) {
            request.setAttribute("records", checkRecordService.findByStudentId(student.getId()));
            request.setAttribute("pageBean", repairService.findPage("", "", null, student.getId(), 1, 5));
        }
        request.getRequestDispatcher("/WEB-INF/student/dashboard.jsp").forward(request, response);
    }
}
