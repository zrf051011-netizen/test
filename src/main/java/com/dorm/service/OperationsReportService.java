package com.dorm.service;

import com.dorm.entity.DashboardStats;
import com.dorm.entity.Dormitory;
import com.dorm.entity.Repair;
import com.dorm.entity.Student;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

public class OperationsReportService {
    private final DashboardService dashboardService = new DashboardService();
    private final StudentService studentService = new StudentService();
    private final DormitoryService dormitoryService = new DormitoryService();
    private final RepairService repairService = new RepairService();

    public void write(OutputStream output) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Styles styles = new Styles(workbook);
            writeOverview(workbook, styles);
            writeStudents(workbook, styles, studentService.findAll());
            writeDormitories(workbook, styles, dormitoryService.findList("", null, "", null));
            writeRepairs(workbook, styles, repairService.findList("", "", null, null, null));
            workbook.write(output);
        }
    }

    private void writeOverview(Workbook workbook, Styles styles) {
        DashboardStats stats = dashboardService.getStats(null);
        Sheet sheet = workbook.createSheet("运营概览");
        header(sheet, styles, "指标", "数值");
        row(sheet, styles, "学生总数", stats.getStudentCount());
        row(sheet, styles, "楼栋总数", stats.getBuildingCount());
        row(sheet, styles, "宿舍总数", stats.getDormitoryCount());
        row(sheet, styles, "床位总数", stats.getBedCount());
        row(sheet, styles, "已使用床位", stats.getUsedBedCount());
        row(sheet, styles, "入住率", stats.getOccupancyRate() + "%");
        row(sheet, styles, "待处理报修", stats.getRepairPendingCount());
        row(sheet, styles, "处理中报修", stats.getRepairDoingCount());
        finish(sheet, 2);
    }

    private void writeStudents(Workbook workbook, Styles styles, List<Student> students) {
        Sheet sheet = workbook.createSheet("学生档案");
        header(sheet, styles, "姓名", "学号", "性别", "班级", "专业", "入住状态", "楼栋", "宿舍", "手机号");
        for (Student student : students) {
            row(sheet, styles,
                    student.getRealName(), student.getStudentNo(), student.getGender(), student.getClassName(),
                    student.getMajor(), student.getStatusName(), student.getBuildingName(), student.getRoomNumber(), student.getPhone());
        }
        finish(sheet, 9);
    }

    private void writeDormitories(Workbook workbook, Styles styles, List<Dormitory> dormitories) {
        Sheet sheet = workbook.createSheet("宿舍房态");
        header(sheet, styles, "楼栋", "宿舍", "楼层", "床位数", "已入住", "空闲床位", "状态", "楼管");
        for (Dormitory dormitory : dormitories) {
            row(sheet, styles,
                    dormitory.getBuildingName(), dormitory.getRoomNumber(), dormitory.getFloor(), dormitory.getCapacity(),
                    dormitory.getCurrentCount(), dormitory.getVacancy(), dormitory.getStatusName(), dormitory.getAdminName());
        }
        finish(sheet, 8);
    }

    private void writeRepairs(Workbook workbook, Styles styles, List<Repair> repairs) {
        Sheet sheet = workbook.createSheet("报修记录");
        header(sheet, styles, "提交时间", "楼栋", "宿舍", "报修人", "类型", "描述", "状态", "处理人", "处理时间", "处理备注");
        for (Repair repair : repairs) {
            row(sheet, styles,
                    repair.getReportTime(), repair.getBuildingName(), repair.getRoomNumber(), repair.getReporterName(),
                    repair.getRepairType(), repair.getDescription(), repair.getStatusName(), repair.getHandlerName(),
                    repair.getHandleTime(), repair.getHandleRemark());
        }
        finish(sheet, 10);
    }

    private void header(Sheet sheet, Styles styles, String... values) {
        Row row = sheet.createRow(0);
        row.setHeightInPoints(24);
        for (int i = 0; i < values.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(values[i]);
            cell.setCellStyle(styles.header);
        }
    }

    private void row(Sheet sheet, Styles styles, Object... values) {
        Row row = sheet.createRow(sheet.getLastRowNum() + 1);
        for (int i = 0; i < values.length; i++) {
            Cell cell = row.createCell(i);
            Object value = values[i];
            if (value instanceof Number) {
                cell.setCellValue(((Number) value).doubleValue());
            } else if (value instanceof Date) {
                cell.setCellValue((Date) value);
                cell.setCellStyle(styles.date);
            } else {
                cell.setCellValue(value == null ? "" : String.valueOf(value));
            }
        }
    }

    private void finish(Sheet sheet, int columns) {
        sheet.createFreezePane(0, 1);
        sheet.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(0, Math.max(sheet.getLastRowNum(), 0), 0, columns - 1));
        for (int i = 0; i < columns; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, Math.min(Math.max(sheet.getColumnWidth(i) + 768, 12 * 256), 36 * 256));
        }
    }

    private static class Styles {
        private final CellStyle header;
        private final CellStyle date;

        private Styles(Workbook workbook) {
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            header = workbook.createCellStyle();
            header.setFont(headerFont);
            header.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            header.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            header.setAlignment(HorizontalAlignment.CENTER);

            date = workbook.createCellStyle();
            date.setDataFormat(workbook.createDataFormat().getFormat("yyyy-mm-dd hh:mm"));
        }
    }
}
