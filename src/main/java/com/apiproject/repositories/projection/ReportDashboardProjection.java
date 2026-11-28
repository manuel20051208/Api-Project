package com.apiproject.repositories.projection;

import java.math.BigDecimal;

import java.time.LocalDateTime;

public interface ReportDashboardProjection {

    Long getUserId();

    String getClientName();

    String getProductName();

    Integer getQuantity();

    BigDecimal getTotalCalculated();

    String getState();

    LocalDateTime getDate();

    BigDecimal getCurrentAmount();

}