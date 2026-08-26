package com.apiproject.services.admin;

import com.apiproject.DTOs.Admin.DashboardDTO;
import com.apiproject.config.CacheConstants;
import com.apiproject.exceptions.ResourceNotFoundException;
import com.apiproject.repositories.admin.*;
import com.apiproject.repositories.projection.ClientSummaryProjection;
import com.apiproject.repositories.projection.DashboardProjection;
import com.apiproject.repositories.projection.ReportDashboardProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService{
    private final UserRepository userRepository;
    private final DashboardRepository dashboardRepository;
    private final ClientsSummaryViewService clientsSummaryViewService;
    private final ReportDashboardRepository reportDashboardRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.DASHBOARD, key = "#userId")
    public DashboardDTO getDashboard(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }

        List<DashboardProjection> rows = dashboardRepository.findByUserId(userId);
        Page<ClientSummaryProjection> showLatestSales = clientsSummaryViewService.showAllForDashBoard(userId);

        if (rows.isEmpty()) return new DashboardDTO();

        Double totalSales = rows.getFirst().getTotalSales();
        Long totalProducts = rows.getFirst().getTotalProducts();
        Long totalClients = rows.getFirst().getTotalClients();

        List<DashboardDTO.MonthlyDataDTO> list = rows.stream()
                .map(d -> new DashboardDTO.MonthlyDataDTO(
                        d.getMonthName(),
                        d.getMonthlyTotal(),
                        d.getNumberOfProducts(),
                        d.getCountClients()
                ))
                .toList();
        return new DashboardDTO(totalSales, totalProducts, totalClients, list, showLatestSales);
    }

    @Transactional(readOnly = true)
    public List<ReportDashboardProjection> getReportData(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
        return reportDashboardRepository.findAllDashboard(userId);
    }
}
