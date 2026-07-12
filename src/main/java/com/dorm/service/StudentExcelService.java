package com.dorm.service;

import com.dorm.entity.StudentImportRow;
import com.dorm.exception.BusinessException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class StudentExcelService {
    private static final int MAX_ROWS = 1000;
    private static final String[] HEADERS = {
            "登录用户名", "姓名", "学号", "性别", "班级", "专业", "手机号", "紧急联系人", "紧急联系电话"
    };

    public List<StudentImportRow> read(InputStream input) {
        try (Workbook workbook = WorkbookFactory.create(input)) {
            if (workbook.getNumberOfSheets() == 0) {
                throw new BusinessException("Excel 中没有工作表");
            }
            Sheet sheet = workbook.getSheetAt(0);
            validateHeader(sheet.getRow(0), workbook);
            if (sheet.getLastRowNum() > MAX_ROWS) {
                throw new BusinessException("单次最多导入 " + MAX_ROWS + " 条学生数据");
            }

            DataFormatter formatter = new DataFormatter(Locale.CHINA);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            List<StudentImportRow> rows = new ArrayList<StudentImportRow>();
            Set<String> usernames = new HashSet<String>();
            Set<String> studentNos = new HashSet<String>();

            for (int index = 1; index <= sheet.getLastRowNum(); index++) {
                Row row = sheet.getRow(index);
                if (isBlank(row, formatter, evaluator)) {
                    continue;
                }
                if (rows.size() >= MAX_ROWS) {
                    throw new BusinessException("单次最多导入 " + MAX_ROWS + " 条学生数据");
                }
                StudentImportRow item = parseRow(row, index + 1, formatter, evaluator);
                if (!usernames.add(item.getUsername())) {
                    throw rowError(item.getRowNumber(), "登录用户名在文件中重复：" + item.getUsername());
                }
                if (!studentNos.add(item.getStudentNo())) {
                    throw rowError(item.getRowNumber(), "学号在文件中重复：" + item.getStudentNo());
                }
                rows.add(item);
            }
            if (rows.isEmpty()) {
                throw new BusinessException("Excel 中没有可导入的学生数据");
            }
            return rows;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Excel 文件无法读取，请使用下载的模板并确认文件未加密");
        }
    }

    public void writeTemplate(OutputStream output) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("学生导入");
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle textStyle = workbook.createCellStyle();
            textStyle.setDataFormat(workbook.createDataFormat().getFormat("@"));

            Row header = sheet.createRow(0);
            header.setHeightInPoints(28);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            for (int column : new int[]{0, 2, 6, 8}) {
                sheet.setDefaultColumnStyle(column, textStyle);
            }
            int[] widths = {20, 14, 18, 10, 18, 24, 18, 18, 20};
            for (int i = 0; i < widths.length; i++) {
                sheet.setColumnWidth(i, widths[i] * 256);
            }
            sheet.createFreezePane(0, 1);
            addGenderValidation(sheet);
            createInstructions(workbook);
            workbook.write(output);
        }
    }

    private StudentImportRow parseRow(Row row, int rowNumber, DataFormatter formatter, FormulaEvaluator evaluator) {
        StudentImportRow item = new StudentImportRow();
        item.setRowNumber(rowNumber);
        item.setUsername(value(row, 0, formatter, evaluator));
        item.setRealName(value(row, 1, formatter, evaluator));
        item.setStudentNo(value(row, 2, formatter, evaluator));
        item.setGender(value(row, 3, formatter, evaluator));
        item.setClassName(value(row, 4, formatter, evaluator));
        item.setMajor(value(row, 5, formatter, evaluator));
        item.setPhone(value(row, 6, formatter, evaluator));
        item.setEmergencyContact(value(row, 7, formatter, evaluator));
        item.setEmergencyPhone(value(row, 8, formatter, evaluator));

        require(item.getRealName(), rowNumber, "姓名");
        require(item.getStudentNo(), rowNumber, "学号");
        if (item.getUsername().isEmpty()) {
            item.setUsername(item.getStudentNo());
        }
        if (!item.getGender().isEmpty() && !"男".equals(item.getGender()) && !"女".equals(item.getGender())) {
            throw rowError(rowNumber, "性别只能填写男或女");
        }

        limit(item.getUsername(), 50, rowNumber, "登录用户名");
        limit(item.getRealName(), 50, rowNumber, "姓名");
        limit(item.getStudentNo(), 20, rowNumber, "学号");
        limit(item.getGender(), 10, rowNumber, "性别");
        limit(item.getClassName(), 50, rowNumber, "班级");
        limit(item.getMajor(), 100, rowNumber, "专业");
        limit(item.getPhone(), 20, rowNumber, "手机号");
        limit(item.getEmergencyContact(), 50, rowNumber, "紧急联系人");
        limit(item.getEmergencyPhone(), 20, rowNumber, "紧急联系电话");
        return item;
    }

    private void validateHeader(Row header, Workbook workbook) {
        if (header == null) {
            throw new BusinessException("Excel 缺少表头，请先下载导入模板");
        }
        DataFormatter formatter = new DataFormatter(Locale.CHINA);
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        for (int i = 0; i < HEADERS.length; i++) {
            String actual = value(header, i, formatter, evaluator);
            if (!HEADERS[i].equals(actual)) {
                throw new BusinessException("第1行第" + (i + 1) + "列表头应为“" + HEADERS[i] + "”");
            }
        }
    }

    private boolean isBlank(Row row, DataFormatter formatter, FormulaEvaluator evaluator) {
        if (row == null) {
            return true;
        }
        for (int i = 0; i < HEADERS.length; i++) {
            if (!value(row, i, formatter, evaluator).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private String value(Row row, int column, DataFormatter formatter, FormulaEvaluator evaluator) {
        if (row == null) {
            return "";
        }
        Cell cell = row.getCell(column, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        return cell == null ? "" : formatter.formatCellValue(cell, evaluator).trim();
    }

    private void require(String value, int rowNumber, String field) {
        if (value == null || value.isEmpty()) {
            throw rowError(rowNumber, field + "不能为空");
        }
    }

    private void limit(String value, int max, int rowNumber, String field) {
        if (value != null && value.length() > max) {
            throw rowError(rowNumber, field + "不能超过" + max + "个字符");
        }
    }

    private BusinessException rowError(int rowNumber, String message) {
        return new BusinessException("第" + rowNumber + "行：" + message);
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        return style;
    }

    private void addGenderValidation(Sheet sheet) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        DataValidationConstraint constraint = helper.createExplicitListConstraint(new String[]{"男", "女"});
        CellRangeAddressList range = new CellRangeAddressList(1, MAX_ROWS, 3, 3);
        DataValidation validation = helper.createValidation(constraint, range);
        validation.setShowErrorBox(true);
        sheet.addValidationData(validation);
    }

    private void createInstructions(Workbook workbook) {
        Sheet sheet = workbook.createSheet("填写说明");
        String[][] rows = {
                {"字段", "是否必填", "说明"},
                {"登录用户名", "否", "留空时自动使用学号；不能与系统已有用户名重复"},
                {"姓名", "是", "学生真实姓名"},
                {"学号", "是", "不能与系统已有学号重复"},
                {"性别", "否", "填写男或女"},
                {"班级", "否", "学生所在班级"},
                {"专业", "否", "学生所学专业"},
                {"手机号", "否", "建议在 Excel 中按文本格式填写"},
                {"紧急联系人", "否", "联系人姓名"},
                {"紧急联系电话", "否", "建议在 Excel 中按文本格式填写"},
                {"导入规则", "", "新学生状态为未入住，登录默认密码为 admin123"}
        };
        CellStyle headerStyle = createHeaderStyle(workbook);
        for (int i = 0; i < rows.length; i++) {
            Row row = sheet.createRow(i);
            for (int j = 0; j < rows[i].length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(rows[i][j]);
                if (i == 0) {
                    cell.setCellStyle(headerStyle);
                }
            }
        }
        sheet.setColumnWidth(0, 20 * 256);
        sheet.setColumnWidth(1, 14 * 256);
        sheet.setColumnWidth(2, 60 * 256);
        sheet.createFreezePane(0, 1);
    }
}
