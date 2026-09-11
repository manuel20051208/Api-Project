package com.apiproject.controllers.admin;

import com.apiproject.DTOs.Admin.DashboardDTO;
import com.apiproject.enums.FileTypes;
import com.apiproject.security.AuthenticatedUser;
import com.apiproject.services.admin.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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

    @Operation(summary = "get the data from the data base")
    @GetMapping("/get-data-dashboard")
    public DashboardDTO getDashboardData(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return dashboardService.getDashboard(authenticatedUser.id());
    }

    @Operation(summary = "download the dashboard report in excel format")
    @GetMapping("/excel")
    public ResponseEntity<byte[]> downloadExcelReport(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) throws IOException {
        return dashboardService.downloadReport(authenticatedUser.id(), FileTypes.EXCEL);
    }

    @Operation(summary = "download the dashboard report in pdf format")
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> downloadPdfReport(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) throws IOException {
        return dashboardService.downloadReport(authenticatedUser.id(), FileTypes.PDF);
    }
}
