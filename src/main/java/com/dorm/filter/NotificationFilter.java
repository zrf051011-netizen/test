package com.dorm.filter;

import com.dorm.entity.Repair;
import com.dorm.entity.User;
import com.dorm.service.RepairService;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@WebFilter("/*")
public class NotificationFilter implements Filter {
    private final RepairService repairService = new RepairService();

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("loginUser");

        if (user != null && isPageRequest(request)) {
            loadNotifications(request, user);
        }
        chain.doFilter(req, resp);
    }

    private void loadNotifications(HttpServletRequest request, User user) {
        boolean enabled = false;
        int count = 0;
        List<Repair> repairs = Collections.emptyList();
        String link = "/";
        String title = "报修待办";

        if ("BUILDING_ADMIN".equals(user.getRole())) {
            enabled = true;
            count = repairService.countActionableByBuildingAdmin(user.getId());
            repairs = repairService.latestActionableByBuildingAdmin(user.getId(), 5);
            link = "/buildingadmin/repairs";
            title = "本楼栋待处理与处理中报修";
        }

        request.setAttribute("noticeEnabled", enabled);
        request.setAttribute("noticeCount", count);
        request.setAttribute("noticeRepairs", repairs);
        request.setAttribute("noticeLink", link);
        request.setAttribute("noticeTitle", title);
    }

    private boolean isPageRequest(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return "GET".equalsIgnoreCase(request.getMethod())
                && !path.startsWith("/css/")
                && !path.startsWith("/js/")
                && !path.startsWith("/images/")
                && !path.startsWith("/static/")
                && !path.startsWith("/favicon");
    }
}
