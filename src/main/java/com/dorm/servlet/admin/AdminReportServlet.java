package com.dorm.servlet.admin;

import com.dorm.service.OperationsReportService;
import com.dorm.util.WebUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/reports/operations.xlsx")
public class AdminReportServlet extends HttpServlet {
    private final OperationsReportService reportService = new OperationsReportService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String encodedName = WebUtil.enc("宿舍运营报表.xlsx").replace("+", "%20");
        response.reset();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Content-Disposition", "attachment; filename=dormitory-operations.xlsx; filename*=UTF-8''" + encodedName);
        reportService.write(response.getOutputStream());
    }
}
