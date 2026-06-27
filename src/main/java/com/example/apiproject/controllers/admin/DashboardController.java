package com.example.apiproject.controllers.admin;

import com.example.apiproject.DTOs.Admin.DashboardDTO;
import com.example.apiproject.security.AuthenticatedUser;
import com.example.apiproject.services.user.admin.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
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
}