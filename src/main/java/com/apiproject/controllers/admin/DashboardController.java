package com.apiproject.controllers.admin;

import com.apiproject.DTOs.Admin.DashboardDTO;
import com.apiproject.enums.FileTypes;
import com.apiproject.repositories.projection.ReportDashboardProjection;
import com.apiproject.repositories.reportGenerator.ReportService;
import com.apiproject.security.AuthenticatedUser;
import com.apiproject.services.admin.DashboardService;
import com.apiproject.services.admin.ReportServiceFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Tag(name = "Admin Dashboard", description = "Dashboard analytics and metrics")
@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard-controller")
public class DashboardController {

    private final DashboardService dashboardService;
    private final ReportServiceFactory reportServiceFactory;

    @Operation(summary = "get the data from the data base")
    @GetMapping("/get-data-dashboard")
    public DashboardDTO getDashboardData(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return dashboardService.getDashboard(authenticatedUser.id());
    }

    @Operation(summary = "download the dashboard report in excel format")
    @GetMapping("/excel")
    public ResponseEntity<byte[]> downloadExcelReport(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) throws IOException {
        return downloadReport(authenticatedUser.id(), FileTypes.EXCEL);
    }

    @Operation(summary = "download the dashboard report in pdf format")
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> downloadPdfReport(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) throws IOException {
        return downloadReport(authenticatedUser.id(), FileTypes.PDF);
    }

    private ResponseEntity<byte[]> downloadReport(Long userId, FileTypes type) throws IOException {
        ReportService reportService = reportServiceFactory.getService(type);
        List<ReportDashboardProjection> data = dashboardService.getReportData(userId);
        byte[] file = reportService.export(data);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(reportService.getContentType()));
        headers.setContentDispositionFormData("attachment", reportService.getFileName());
        headers.setContentLength(file.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(file);
    }
}
