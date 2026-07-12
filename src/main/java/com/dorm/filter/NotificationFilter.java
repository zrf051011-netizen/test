package com.dorm.filter;

import com.dorm.entity.Repair;
import com.dorm.entity.Student;
import com.dorm.entity.User;
import com.dorm.service.RepairService;
import com.dorm.service.StudentService;

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
    private final StudentService studentService = new StudentService();

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
        int count = 0;
        List<Repair> repairs = Collections.emptyList();
        String link = "/";
        String title = "待处理事项";

        if ("ADMIN".equals(user.getRole())) {
            count = repairService.countByStatus("PENDING", null);
            repairs = repairService.latestOpen(null, null, false, 5);
            link = "/admin/repairs?status=PENDING";
            title = "全校待处理报修";
        } else if ("BUILDING_ADMIN".equals(user.getRole())) {
            count = repairService.countByStatus("PENDING", user.getId());
            repairs = repairService.latestOpen(user.getId(), null, false, 5);
            link = "/buildingadmin/repairs?status=PENDING";
            title = "本楼栋待处理报修";
        } else if ("STUDENT".equals(user.getRole())) {
            Student student = studentService.findByUserId(user.getId());
            if (student != null) {
                count = repairService.countOpenByReporter(student.getId());
                repairs = repairService.latestOpen(null, student.getId(), true, 5);
            }
            link = "/student/repairs";
            title = "我的报修进度";
        }

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
