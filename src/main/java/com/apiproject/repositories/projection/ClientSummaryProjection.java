package com.apiproject.repositories.projection;

import java.time.LocalDateTime;

public interface ClientSummaryProjection {
    Long getId();
    Long getUserId();
    String getFullName();
    String getEmail();
    Long getTotalQuantity();
    Double getTotalSpent();
    Double getTotalDiscount();
    Double getTotalSpentDiscount();
    LocalDateTime getLatestSale();
}