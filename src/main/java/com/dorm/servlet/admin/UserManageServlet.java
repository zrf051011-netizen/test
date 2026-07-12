package com.dorm.servlet.admin;

import com.dorm.entity.User;
import com.dorm.exception.BusinessException;
import com.dorm.service.UserService;
import com.dorm.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet({"/admin/users", "/admin/users/*"})
public class UserManageServlet extends HttpServlet {
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        if ("/edit".equals(path)) {
            int id = WebUtil.getInt(request, "id", 0);
            if (id > 0) {
                request.setAttribute("item", userService.findById(id));
            }
            list(request, response, "/WEB-INF/admin/user_form.jsp");
            return;
        }
        if ("/delete".equals(path)) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        list(request, response, "/WEB-INF/admin/users.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if ("/delete".equals(request.getPathInfo())) {
            try {
                userService.deleteUser(WebUtil.getInt(request, "id", 0));
                response.sendRedirect(request.getContextPath() + "/admin/users?success=" + WebUtil.enc("删除成功"));
            } catch (BusinessException e) {
                response.sendRedirect(request.getContextPath() + "/admin/users?error=" + WebUtil.enc(e.getMessage()));
            }
            return;
        }
        User user = new User();
        user.setId(WebUtil.getInt(request, "id", 0));
        user.setUsername(WebUtil.getString(request, "username"));
        user.setRealName(WebUtil.getString(request, "realName"));
        user.setPhone(WebUtil.getString(request, "phone"));
        user.setRole(WebUtil.getString(request, "role"));
        user.setStatus(WebUtil.getInt(request, "status", 1));
        try {
            userService.saveUser(user);
            response.sendRedirect(request.getContextPath() + "/admin/users?success=" + WebUtil.enc("保存成功"));
        } catch (BusinessException e) {
            response.sendRedirect(request.getContextPath() + "/admin/users?error=" + WebUtil.enc(e.getMessage()));
        }
    }

    private void list(HttpServletRequest request, HttpServletResponse response, String jsp) throws ServletException, IOException {
        String keyword = WebUtil.getString(request, "keyword");
        String role = WebUtil.getString(request, "role");
        request.setAttribute("activeMenu", "users");
        request.setAttribute("pageTitle", "用户管理");
        request.setAttribute("keyword", keyword);
        request.setAttribute("role", role);
        request.setAttribute("pageBean", userService.findPage(keyword, role, WebUtil.getInt(request, "page", 1), 8));
        request.getRequestDispatcher(jsp).forward(request, response);
    }
}
