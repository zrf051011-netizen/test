package com.dorm.servlet.admin;

import com.dorm.entity.Student;
import com.dorm.entity.StudentImportRow;
import com.dorm.exception.BusinessException;
import com.dorm.service.StudentExcelService;
import com.dorm.service.StudentService;
import com.dorm.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

@WebServlet({"/admin/students", "/admin/students/*"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5 * 1024 * 1024, maxRequestSize = 6 * 1024 * 1024)
public class StudentManageServlet extends HttpServlet {
    private final StudentService studentService = new StudentService();
    private final StudentExcelService studentExcelService = new StudentExcelService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        if ("/template".equals(path)) {
            downloadTemplate(response);
            return;
        }
        if ("/edit".equals(path)) {
            int id = WebUtil.getInt(request, "id", 0);
            if (id > 0) {
                request.setAttribute("item", studentService.findById(id));
            }
            prepare(request);
            request.getRequestDispatcher("/WEB-INF/admin/student_form.jsp").forward(request, response);
            return;
        }
        if ("/delete".equals(path)) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        prepare(request);
        String keyword = WebUtil.getString(request, "keyword");
        String status = WebUtil.getString(request, "status");
        request.setAttribute("keyword", keyword);
        request.setAttribute("status", status);
        request.setAttribute("pageBean", studentService.findPage(keyword, status, null, WebUtil.getInt(request, "page", 1), 8));
        request.getRequestDispatcher("/WEB-INF/admin/students.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if ("/delete".equals(request.getPathInfo())) {
            try {
                studentService.delete(WebUtil.getInt(request, "id", 0));
                response.sendRedirect(request.getContextPath() + "/admin/students?success=" + WebUtil.enc("删除成功"));
            } catch (Exception e) {
                response.sendRedirect(request.getContextPath() + "/admin/students?error=" + WebUtil.enc(e.getMessage()));
            }
            return;
        }
        if ("/import".equals(request.getPathInfo())) {
            importExcel(request, response);
            return;
        }
        Student student = new Student();
        student.setId(WebUtil.getInt(request, "id", 0));
        student.setStudentNo(WebUtil.getString(request, "studentNo"));
        student.setGender(WebUtil.getString(request, "gender"));
        student.setClassName(WebUtil.getString(request, "className"));
        student.setMajor(WebUtil.getString(request, "major"));
        student.setEmergencyContact(WebUtil.getString(request, "emergencyContact"));
        student.setEmergencyPhone(WebUtil.getString(request, "emergencyPhone"));
        try {
            studentService.saveStudent(student,
                    WebUtil.getString(request, "username"),
                    WebUtil.getString(request, "realName"),
                    WebUtil.getString(request, "phone"));
            response.sendRedirect(request.getContextPath() + "/admin/students?success=" + WebUtil.enc("保存成功"));
        } catch (BusinessException e) {
            response.sendRedirect(request.getContextPath() + "/admin/students?error=" + WebUtil.enc(e.getMessage()));
        }
    }

    private void downloadTemplate(HttpServletResponse response) throws IOException {
        String encodedName = WebUtil.enc("学生批量导入模板.xlsx").replace("+", "%20");
        response.reset();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Content-Disposition", "attachment; filename=student-import-template.xlsx; filename*=UTF-8''" + encodedName);
        studentExcelService.writeTemplate(response.getOutputStream());
    }

    private void importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Part file = request.getPart("excelFile");
            if (file == null || file.getSize() == 0) {
                throw new BusinessException("请选择要导入的 Excel 文件");
            }
            String filename = file.getSubmittedFileName() == null ? "" : file.getSubmittedFileName();
            String lowerName = filename.toLowerCase(Locale.ROOT);
            if (!lowerName.endsWith(".xlsx") && !lowerName.endsWith(".xls")) {
                throw new BusinessException("仅支持 .xlsx 或 .xls 文件");
            }
            List<StudentImportRow> rows;
            try (InputStream input = file.getInputStream()) {
                rows = studentExcelService.read(input);
            }
            int count = studentService.importStudents(rows);
            response.sendRedirect(request.getContextPath() + "/admin/students?success=" +
                    WebUtil.enc("成功导入 " + count + " 名学生，默认密码为 admin123"));
        } catch (IllegalStateException e) {
            response.sendRedirect(request.getContextPath() + "/admin/students?error=" + WebUtil.enc("Excel 文件不能超过 5MB"));
        } catch (BusinessException e) {
            response.sendRedirect(request.getContextPath() + "/admin/students?error=" + WebUtil.enc(e.getMessage()));
        } catch (Exception e) {
            getServletContext().log("Student Excel import failed", e);
            response.sendRedirect(request.getContextPath() + "/admin/students?error=" + WebUtil.enc("导入失败，请检查文件格式后重试"));
        }
    }

    private void prepare(HttpServletRequest request) {
        request.setAttribute("activeMenu", "students");
        request.setAttribute("pageTitle", "学生管理");
    }
}
