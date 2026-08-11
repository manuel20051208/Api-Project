package com.apiproject.services.reportGenerator;

import com.apiproject.enums.FileTypes;
import com.apiproject.repositories.reportGenerator.ReportService;
import com.apiproject.repositories.projection.ReportDashboardProjection;
import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfService implements ReportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String[] HEADERS = {"ID Usuario", "Cliente", "Producto", "Cantidad",
            "Total Calculado", "Estado", "Fecha", "Monto Actual"};
    private static final Color HEADER_BG = new Color(38, 161, 32);
    private static final Color ZEBRA_BG = new Color(220, 230, 241);
    private static final Color TEXT_COLOR = new Color(33, 37, 41);

    @Override
    public byte[] export(List<ReportDashboardProjection> data) throws IOException {
        Document document = new Document(PageSize.A4.rotate(), 20, 20, 20, 20);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter.getInstance(document, out);
            document.open();

            addTitle(document);
            PdfPTable table = createTable();
            addHeader(table);
            addRows(table, data);
            document.add(table);

            document.close();
            return out.toByteArray();
        } catch (DocumentException e) {
            throw new IOException("Error al generar el reporte PDF", e);
        }
    }

    private void addTitle(Document document) throws DocumentException {
        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD, HEADER_BG);
        Paragraph title = new Paragraph("Reporte Usuarios", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
    }

    private PdfPTable createTable() throws DocumentException {
        PdfPTable table = new PdfPTable(HEADERS.length);
        table.setWidthPercentage(100);
        table.setSpacingBefore(5);
        table.setWidths(new float[]{0.8f, 1.6f, 1.6f, 0.9f, 1.2f, 1f, 1.5f, 1.2f});
        return table;
    }

    private void addHeader(PdfPTable table) {
        Font font = new Font(Font.HELVETICA, 9, Font.BOLD, Color.WHITE);
        for (String header : HEADERS) {
            PdfPCell cell = new PdfPCell(new Phrase(header, font));
            cell.setBackgroundColor(HEADER_BG);
            cell.setPadding(6);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
        }
    }

    private void addRows(PdfPTable table, List<ReportDashboardProjection> data) {
        Font font = new Font(Font.HELVETICA, 9, Font.NORMAL, TEXT_COLOR);
        int rowIdx = 0;
        for (ReportDashboardProjection u : data) {
            Color background = rowIdx++ % 2 == 0 ? Color.WHITE : ZEBRA_BG;
            addCell(table, nullSafe(u.getUserId()), font, background);
            addCell(table, nullSafe(u.getClientName()), font, background);
            addCell(table, nullSafe(u.getProductName()), font, background);
            addCell(table, nullSafe(u.getQuantity()), font, background);
            addCell(table, formatNumber(u.getTotalCalculated()), font, background);
            addCell(table, nullSafe(u.getState()), font, background);
            addCell(table, u.getDate() != null ? u.getDate().format(DATE_FORMATTER) : "", font, background);
            addCell(table, formatNumber(u.getCurrentAmount()), font, background);
        }
    }

    private void addCell(PdfPTable table, String value, Font font, Color background) {
        PdfPCell cell = new PdfPCell(new Phrase(value, font));
        cell.setBackgroundColor(background);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private String nullSafe(Object value) {
        return value != null ? String.valueOf(value) : "";
    }

    private String formatNumber(Double value) {
        return value != null ? String.format("%.2f", value) : "0.00";
    }

    @Override
    public String getContentType() {
        return "application/pdf";
    }

    @Override
    public FileTypes getType() {
        return FileTypes.PDF;
    }

    @Override
    public String getFileExtension() {
        return ".pdf";
    }

    @Override
    public String getFileName() {
        return "reporte_usuarios" + getFileExtension();
    }
}
