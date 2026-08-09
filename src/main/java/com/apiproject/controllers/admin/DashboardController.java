package com.apiproject.controllers.admin;

import com.apiproject.DTOs.Admin.DashboardDTO;
import com.apiproject.repositories.admin.ReportService;
import com.apiproject.security.AuthenticatedUser;
import com.apiproject.services.admin.DashboardService;
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

@Tag(name = "Admin Dashboard", description = "Dashboard analytics and metrics")
@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard-controller")
public class DashboardController {

    private final DashboardService dashboardService;
    private final ReportService reportService;

    @Operation(summary = "get the data from the data base")
    @GetMapping("/get-data-dashboard")
    public DashboardDTO getDashboardData(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return dashboardService.getDashboard(authenticatedUser.id());
    }

    @Operation(summary = "get the data for a report")
    @GetMapping("/excel")
    public ResponseEntity<byte[]> downloadExcelReport(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) throws IOException {
        byte[] archivo = dashboardService.generateReport(authenticatedUser.id());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(reportService.getContentType()));
        headers.setContentDispositionFormData("attachment", reportService.getFileName());
        headers.setContentLength(archivo.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(archivo);
    }
}