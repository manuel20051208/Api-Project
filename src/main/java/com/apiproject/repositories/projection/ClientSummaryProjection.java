package com.apiproject.repositories.projection;

import java.math.BigDecimal;

import java.time.LocalDateTime;

public interface ClientSummaryProjection {
    Long getId();
    Long getUserId();
    String getFullName();
    String getEmail();
    Long getTotalQuantity();
    BigDecimal getTotalSpent();
    BigDecimal getTotalDiscount();
    BigDecimal getTotalSpentDiscount();
    LocalDateTime getLatestSale();
}