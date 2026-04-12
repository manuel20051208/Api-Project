package com.example.apiproject.controllers.user.admin;

import com.example.apiproject.entities.admin.SaleItemView;
import com.example.apiproject.services.user.admin.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard-controller")
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/sum")
    public Double resultSum(){
        return dashboardService.sum();
    }

    @GetMapping("stock")
    public Long resultStocks(){
        return dashboardService.showProductsAmount();
    }

    @GetMapping("/count-clients")
    public Long resultClients(){
        return dashboardService.countClients();
    }

    @GetMapping("/data-graphic")
    public Map<Integer, Double> dataGraphic(){
        return dashboardService.returnGraphic();
    }

    @GetMapping("/lates-sales")
    public Page<SaleItemView> showLatestSalesDashboard(){
        return dashboardService.showLatestSales();
    }
}
