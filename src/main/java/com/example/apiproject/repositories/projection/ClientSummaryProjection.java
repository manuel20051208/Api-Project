package com.example.apiproject.repositories.projection;

import java.time.LocalDateTime;

public interface ClientSummaryProjection {
    Long getId();
    Long getUserId();
    String getFullName();
    String getEmail();
    Long getTotalQuantity();
    Double getTotalSpent();
    LocalDateTime getLatestSale();
}