package com.example.apiproject.controllers.admin;

import com.example.apiproject.entities.admin.SaleItemView;
import com.example.apiproject.services.user.admin.DashboardService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

@Tag(name = "Admin Dashboard", description = "Dashboard analytics and metrics")
@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard-controller")
public class DashboardController {
    private final DashboardService dashboardService;

    @Operation(summary = "Get sum of all sales")
    @GetMapping("/sum")
    public Double resultSum() {
        return dashboardService.sum();
    }

    @Operation(summary = "Get product stock amount")
    @GetMapping("stock")
    public Long resultStocks() {
        return dashboardService.showProductsAmount();
    }

    @Operation(summary = "Get current clients count")
    @GetMapping("/count-clients")
    public Long resultClients() {
        return dashboardService.countClients();
    }

    @Operation(summary = "Get data for graphics")
    @GetMapping("/data-graphic")
    public Map<Integer, Double> dataGraphic() {
        return dashboardService.returnGraphic();
    }

    @Operation(summary = "Get latest sales")
    @GetMapping("/lates-sales/{pageSize}")
    public Page<SaleItemView> showLatestSalesDashboard(@PathVariable int pageSize) {
        return dashboardService.showLatestSales(pageSize);
    }
}
