package com.apiproject.services.reportGenerator;

import com.apiproject.enums.FileTypes;
import com.apiproject.repositories.reportGenerator.ReportService;
import com.apiproject.repositories.projection.ReportDashboardProjection;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class ExcelService implements ReportService {

    private static final String[] HEADERS = {"ID Usuario", "Cliente", "Producto", "Cantidad",
            "Total Calculado", "Estado", "Fecha", "Monto Actual"};

    @Override
    public byte[] export(List<ReportDashboardProjection> data) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Reporte Usuarios");
            writeHeader(workbook, sheet);
            writeRows(workbook, sheet, data);
            autoSizeColumns(sheet);

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private void writeHeader(Workbook workbook, Sheet sheet) {
        CellStyle headerStyle = createHeaderStyle(workbook);
        Row headerRow = sheet.createRow(0);
        headerRow.setHeightInPoints(20);
        for (int i = 0; i < HEADERS.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(HEADERS[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void writeRows(Workbook workbook, Sheet sheet, List<ReportDashboardProjection> data) {
        CellStyle bodyStyle = createBodyStyle(workbook);
        CellStyle dateStyle = createDateStyle(workbook);
        CellStyle numberStyle = createNumberStyle(workbook);

        int rowIdx = 1;
        for (ReportDashboardProjection u : data) {
            Row row = sheet.createRow(rowIdx);

            Cell c = row.createCell(0);
            c.setCellValue(u.getUserId() != null ? u.getUserId() : 0);
            c.setCellStyle(bodyStyle);

            c = row.createCell(1);
            c.setCellValue(nullSafe(u.getClientName()));
            c.setCellStyle(bodyStyle);

            c = row.createCell(2);
            c.setCellValue(nullSafe(u.getProductName()));
            c.setCellStyle(bodyStyle);

            c = row.createCell(3);
            c.setCellValue(u.getQuantity() != null ? u.getQuantity() : 0);
            c.setCellStyle(bodyStyle);

            c = row.createCell(4);
            c.setCellValue(u.getTotalCalculated() != null ? u.getTotalCalculated() : 0.0);
            c.setCellStyle(numberStyle);

            c = row.createCell(5);
            c.setCellValue(nullSafe(u.getState()));
            c.setCellStyle(bodyStyle);

            c = row.createCell(6);
            if (u.getDate() != null) {
                c.setCellValue(u.getDate());
            }
            c.setCellStyle(dateStyle);

            c = row.createCell(7);
            c.setCellValue(u.getCurrentAmount() != null ? u.getCurrentAmount() : 0.0);
            c.setCellStyle(numberStyle);

            rowIdx++;
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        applyUnderlineBorder(style);

        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.BLACK.getIndex());
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        return style;
    }

    private CellStyle createBodyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        applyBorders(style, IndexedColors.GREY_25_PERCENT);

        Font font = workbook.createFont();
        font.setColor(IndexedColors.BLACK.getIndex());
        style.setFont(font);
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = createBodyStyle(workbook);
        CreationHelper helper = workbook.getCreationHelper();
        style.setDataFormat(helper.createDataFormat().getFormat("dd/mm/yyyy hh:mm"));
        return style;
    }

    private CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = createBodyStyle(workbook);
        CreationHelper helper = workbook.getCreationHelper();
        style.setDataFormat(helper.createDataFormat().getFormat("#,##0.00"));
        return style;
    }

    private void applyBorders(CellStyle style, IndexedColors color) {
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(color.getIndex());
    }

    private void applyUnderlineBorder(CellStyle style) {
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBottomBorderColor(IndexedColors.GREY_80_PERCENT.getIndex());
    }

    private void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < HEADERS.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private String nullSafe(String value) {
        return value != null ? value : "";
    }

    @Override
    public String getContentType() {
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    }

    @Override
    public FileTypes getType() {
        return FileTypes.EXCEL;
    }

    @Override
    public String getFileExtension() {
        return ".xlsx";
    }

    @Override
    public String getFileName() {
        return "reporte_usuarios" + getFileExtension();
    }
}