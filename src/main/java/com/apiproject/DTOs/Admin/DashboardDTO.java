package com.apiproject.DTOs.Admin;

import java.math.BigDecimal;

import com.apiproject.repositories.projection.ClientSummaryProjection;
import org.springframework.data.domain.Page;

import java.util.List;

public record DashboardDTO(
        BigDecimal totalSales,
        Long totalProducts,
        Long totalClients,
        List<MonthlyDataDTO> monthlyData,
        Page<ClientSummaryProjection> showLatestSales
) {
    public DashboardDTO() {
        this(BigDecimal.ZERO, 0L, 0L, List.of(new MonthlyDataDTO()), Page.empty());
    }

    public record MonthlyDataDTO(
            String monthName,
            BigDecimal monthlyTotal,
            Long numberOfProducts,
            Long countClients
    ) {
        public MonthlyDataDTO(){
            this(null, BigDecimal.ZERO, 0L, 0L);
        }
    }
}
