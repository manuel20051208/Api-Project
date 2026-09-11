package com.apiproject.services.admin;

import com.apiproject.DTOs.Admin.DashboardDTO;
import com.apiproject.DTOs.General.TheThreeBestClients;
import com.apiproject.DTOs.General.TheThreeBestProducts;
import com.apiproject.config.CacheConstants;
import com.apiproject.enums.FileTypes;
import com.apiproject.exceptions.ResourceNotFoundException;
import com.apiproject.repositories.admin.DashboardRepository;
import com.apiproject.repositories.admin.ReportDashboardRepository;
import com.apiproject.repositories.admin.UserRepository;
import com.apiproject.repositories.projection.ClientSummaryProjection;
import com.apiproject.repositories.projection.DashboardProjection;
import com.apiproject.repositories.projection.ReportDashboardProjection;
import com.apiproject.repositories.reportGenerator.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final UserRepository userRepository;
    private final DashboardRepository dashboardRepository;
    private final ClientsSummaryViewService clientsSummaryViewService;
    private final ReportDashboardRepository reportDashboardRepository;
    private final RankingsService rankingsService;
    private final ReportServiceFactory reportServiceFactory;

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

    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> downloadReport(Long userId, FileTypes type) throws IOException {
        ReportService reportService = reportServiceFactory.getService(type);
        List<ReportDashboardProjection> data = getReportData(userId);
        byte[] file = reportService.export(data);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(reportService.getContentType()));
        headers.setContentDispositionFormData("attachment", reportService.getFileName());
        headers.setContentLength(file.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(file);
    }

    @Transactional(readOnly = true)
    public List<TheThreeBestClients> getBestClients(Long userId) {
        return rankingsService.getBestClients(userId);
    }

    @Transactional(readOnly = true)
    public List<TheThreeBestProducts> getBestProducts(Long userId) {
        return rankingsService.getBestProducts(userId);
    }
}
