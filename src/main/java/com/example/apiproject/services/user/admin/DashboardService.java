package com.example.apiproject.services.user.admin;

import com.example.apiproject.entities.admin.Dashboard;
import com.example.apiproject.entities.admin.SaleItemView;
import com.example.apiproject.repositories.admin.DashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final DashboardRepository dashboardRepository;
    private final SaleItemViewService saleItemViewService;
    private final PageableHandlerMethodArgumentResolverCustomizer pageableCustomizer;

    public Double sum() {
        return dashboardRepository.findFirstByOrderBySalesYearDescMonthNumberDesc()
                .map(Dashboard::getMonthlyTotal)
                .orElse(0.0);
    }

    public Long showProductsAmount() {
        return dashboardRepository.findFirstByOrderBySalesYearDescMonthNumberDesc()
                .map(Dashboard::getNumberOfProducts)
                .orElse(0L);
    }

    public Long countClients() {
        return dashboardRepository.findFirstByOrderBySalesYearDescMonthNumberDesc()
                .map(Dashboard::getCountClients)
                .orElse(0L);
    }

    public Map<Integer, Double> returnGraphic() {
        return dashboardRepository.findAll().stream()
                .collect(Collectors.toMap(Dashboard::getMonthNumber, Dashboard::getMonthlyTotal));
    }

    public Page<SaleItemView> showLatestSales(int pageSize){
        return saleItemViewService.showEverythingWithLimits(pageSize);
    }
}