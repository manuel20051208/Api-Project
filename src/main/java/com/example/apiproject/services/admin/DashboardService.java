package com.example.apiproject.services.admin;

import com.example.apiproject.DTOs.Admin.DashboardDTO;
import com.example.apiproject.config.CacheConstants;
import com.example.apiproject.exceptions.ResourceNotFoundException;
import com.example.apiproject.repositories.admin.*;
import com.example.apiproject.repositories.projection.ClientSummaryProjection;
import com.example.apiproject.repositories.projection.DashboardProjection;
import com.example.apiproject.repositories.projection.ReportDashboardProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService implements ReportService {
    private final UserRepository userRepository;
    private final DashboardRepository dashboardRepository;
    private final ClientsSummaryViewService clientsSummaryViewService;
    private final ReportExporter reportExporter;
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

    @Override
    public byte[] generateReport(Long userId) throws IOException {
        List<ReportDashboardProjection> data = reportDashboardRepository.findAllDashboard(userId);
        return reportExporter.export(data);
    }

    @Override
    public String getFileName() {
        return "reporte_usuarios" + reportExporter.getFileExtension();
    }

    @Override
    public String getContentType() {
        return reportExporter.getContentType();
    }
}
