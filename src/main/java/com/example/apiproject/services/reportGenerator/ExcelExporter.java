package com.example.apiproject.services.reportGenerator;

import com.example.apiproject.repositories.admin.ReportExporter;
import com.example.apiproject.repositories.projection.ReportDashboardProjection;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class ExcelExporter implements ReportExporter {


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
        String[] headers = {"ID Usuario", "Cliente", "Producto", "Cantidad",
                "Total Calculado", "Estado", "Fecha", "Monto Actual"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void writeRows(Workbook workbook, Sheet sheet, List<ReportDashboardProjection> data) {
        CellStyle dateStyle = createDateStyle(workbook);
        int rowIdx = 1;
        for (ReportDashboardProjection u : data) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(u.getUserId() != null ? u.getUserId() : 0);
            row.createCell(1).setCellValue(nullSafe(u.getClientName()));
            row.createCell(2).setCellValue(nullSafe(u.getProductName()));
            row.createCell(3).setCellValue(u.getQuantity() != null ? u.getQuantity() : 0);
            row.createCell(4).setCellValue(u.getTotalCalculated() != null ? u.getTotalCalculated() : 0.0);
            row.createCell(5).setCellValue(nullSafe(u.getState()));

            Cell dateCell = row.createCell(6);
            if (u.getDate() != null) {
                dateCell.setCellValue(u.getDate());
                dateCell.setCellStyle(dateStyle);
            }
            row.createCell(7).setCellValue(u.getCurrentAmount() != null ? u.getCurrentAmount() : 0.0);
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        CreationHelper helper = workbook.getCreationHelper();
        style.setDataFormat(helper.createDataFormat().getFormat("dd/mm/yyyy hh:mm"));
        return style;
    }

    private void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < 8; i++) {
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
    public String getFileExtension() {
        return ".xlsx";
    }
}
