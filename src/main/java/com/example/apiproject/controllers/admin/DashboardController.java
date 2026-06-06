package com.example.apiproject.controllers.admin;

import com.example.apiproject.repositories.projection.ClientSummaryProjection;
import com.example.apiproject.services.user.admin.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Admin Dashboard", description = "Dashboard analytics and metrics")
@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard-controller")
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "Get sum of all sales")
    @GetMapping("/sum/{userId}")
    public Double resultSum(@PathVariable Long userId) {
        return dashboardService.sum(userId);
    }

    @Operation(summary = "Get product stock amount")
    @GetMapping("/stock/{userId}")
    public Long resultStocks(@PathVariable Long userId) {
        return dashboardService.showProductsAmount(userId);
    }

    @Operation(summary = "Get current clients count")
    @GetMapping("/count-clients/{userId}")
    public Long resultClients(@PathVariable Long userId) {
        return dashboardService.countClients(userId);
    }

    @Operation(summary = "Get data for graphics")
    @GetMapping("/data-graphic/{userId}")
    public Map<Integer, Double> dataGraphic(@PathVariable Long userId) {
        return dashboardService.returnGraphic(userId);
    }

    @Operation(summary = "Get latest sales")
    @GetMapping("/latest-sales/{userId}")
    public Page<ClientSummaryProjection> showLatestSalesDashboard(
            @PathVariable Long userId) {
        return dashboardService.showLatestSales(userId);
    }
}