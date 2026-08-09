package com.apiproject.repositories.projection;

import java.time.LocalDateTime;

public interface ReportDashboardProjection {

    Long getUserId();

    String getClientName();

    String getProductName();

    Integer getQuantity();

    Double getTotalCalculated();

    String getState();

    LocalDateTime getDate();

    Double getCurrentAmount();

}