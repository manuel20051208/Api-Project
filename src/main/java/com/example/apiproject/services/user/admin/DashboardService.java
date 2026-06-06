package com.example.apiproject.services.user.admin;

import com.example.apiproject.repositories.admin.DashboardRepository;
import com.example.apiproject.repositories.projection.ClientSummaryProjection;
import com.example.apiproject.repositories.projection.DashboardProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final DashboardRepository dashboardRepository;
    private final ClientsSummaryViewService clientsSummaryViewService;

    @Transactional(readOnly = true)
    public Double sum(Long userId) {
        return dashboardRepository.getMonthlyTotal(userId);
    }

    @Transactional(readOnly = true)
    public Long showProductsAmount(Long userId) {
        return dashboardRepository.getNumberOfProducts(userId);
    }

    @Transactional(readOnly = true)
    public Long countClients(Long userId) {
        return dashboardRepository.countClients(userId);
    }

    @Transactional(readOnly = true)
    public Map<Integer, Double> returnGraphic(Long userId) {
        return dashboardRepository.findByUserId(userId).stream()
                .collect(Collectors.toMap(
                        //proxies con sus respectivos metodos
                        // (spring crea un proxie e implimenta los metodos de DashboardGraphicPointProjection)
                        DashboardProjection::getMonthNumber,
                        DashboardProjection::getMonthlyTotal,
                        (previous, current) -> previous, LinkedHashMap::new
                ));
    }

    @Transactional(readOnly = true)
    public Page<ClientSummaryProjection> showLatestSales(Long userId) {
        return clientsSummaryViewService.showAll(userId);
    }
}
