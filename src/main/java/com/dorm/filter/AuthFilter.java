package com.dorm.filter;

import com.dorm.entity.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String contextPath = request.getContextPath();
        String uri = request.getRequestURI();
        String path = uri.substring(contextPath.length());

        if (isWhite(path)) {
            chain.doFilter(req, resp);
            return;
        }

        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("loginUser");
        if (user == null) {
            response.sendRedirect(contextPath + "/login.jsp");
            return;
        }

        if (!hasPermission(path, user.getRole())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            request.setAttribute("forbiddenMessage", "当前账号没有访问这个页面的权限");
            request.getRequestDispatcher("/403.jsp").forward(request, response);
            return;
        }

        chain.doFilter(req, resp);
    }

    private boolean isWhite(String path) {
        return path.equals("/")
                || path.equals("/index.jsp")
                || path.equals("/login")
                || path.equals("/login.jsp")
                || path.equals("/forgot-password")
                || path.equals("/403.jsp")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.startsWith("/static/")
                || path.startsWith("/favicon");
    }

    private boolean hasPermission(String path, String role) {
        if (path.startsWith("/admin/")) {
            return "ADMIN".equals(role);
        }
        if (path.startsWith("/buildingadmin/")) {
            return "BUILDING_ADMIN".equals(role);
        }
        if (path.startsWith("/student/")) {
            return "STUDENT".equals(role);
        }
        return true;
    }
}
