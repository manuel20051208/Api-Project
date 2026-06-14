package com.example.apiproject.DTOs.Admin;

import com.example.apiproject.repositories.projection.ClientSummaryProjection;
import org.springframework.data.domain.Page;

import java.util.List;

public record DashboardDTO(
        Double totalSales,
        Long totalProducts,
        Long totalClients,
        List<MonthlyDataDTO> monthlyData,
        Page<ClientSummaryProjection> showLatestSales
) {
    public DashboardDTO() {
        this(0.0, 0L, 0L, List.of(new MonthlyDataDTO()), Page.empty());
    }

    public record MonthlyDataDTO(
            String monthName,
            Double monthlyTotal,
            Long numberOfProducts,
            Long countClients
    ) {
        public MonthlyDataDTO(){
            this(null, 0.0, 0L, 0L);
        }
    }
}
