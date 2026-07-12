package com.dorm.servlet.buildingadmin;

import com.dorm.entity.User;
import com.dorm.exception.BusinessException;
import com.dorm.service.CheckService;
import com.dorm.service.DormitoryService;
import com.dorm.service.StudentService;
import com.dorm.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet({"/buildingadmin/check", "/buildingadmin/check/*"})
public class CheckServlet extends HttpServlet {
    private final CheckService checkService = new CheckService();
    private final StudentService studentService = new StudentService();
    private final DormitoryService dormitoryService = new DormitoryService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        request.setAttribute("activeMenu", "check");
        request.setAttribute("pageTitle", "入住办理");
        request.setAttribute("outStudents", studentService.findOutStudents());
        request.setAttribute("inStudents", studentService.findPage("", "IN", user.getId(), 1, 200).getList());
        request.setAttribute("availableDorms", dormitoryService.findAvailable(user.getId()));
        request.getRequestDispatcher("/WEB-INF/buildingadmin/check.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        String action = WebUtil.getString(request, "action");
        try {
            if ("in".equals(action)) {
                checkService.checkIn(WebUtil.getInt(request, "studentId", 0), WebUtil.getInt(request, "dormitoryId", 0), user.getId(), WebUtil.getString(request, "remark"));
            } else if ("out".equals(action)) {
                checkService.checkOut(WebUtil.getInt(request, "studentId", 0), user.getId(), WebUtil.getString(request, "remark"));
            } else if ("change".equals(action)) {
                checkService.changeDorm(WebUtil.getInt(request, "studentId", 0), WebUtil.getInt(request, "dormitoryId", 0), user.getId(), WebUtil.getString(request, "remark"));
            } else {
                throw new BusinessException("办理类型不正确");
            }
            response.sendRedirect(request.getContextPath() + "/buildingadmin/check?success=" + WebUtil.enc("办理成功"));
        } catch (BusinessException e) {
            response.sendRedirect(request.getContextPath() + "/buildingadmin/check?error=" + WebUtil.enc(e.getMessage()));
        } catch (RuntimeException e) {
            response.sendRedirect(request.getContextPath() + "/buildingadmin/check?error=" + WebUtil.enc("办理失败：" + e.getMessage()));
        }
    }
}
