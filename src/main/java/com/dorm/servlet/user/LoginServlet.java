package com.dorm.servlet.user;

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

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("loginUser");
        if (user != null) {
            response.sendRedirect(request.getContextPath() + userService.homePath(user));
            return;
        }
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = WebUtil.getString(request, "username");
        String password = WebUtil.getString(request, "password");
        try {
            User user = userService.login(username, password, request.getRemoteAddr());
            request.changeSessionId();
            request.getSession().setAttribute("loginUser", user);
            response.sendRedirect(request.getContextPath() + userService.homePath(user));
        } catch (BusinessException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("username", username);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}
