package com.example.apiproject.repositories.projection;

import java.time.LocalDateTime;

public interface ClientHistoryProjection {
    Long getSaleItemId();
    Long getClientId();
    String getClientName();
    String getClientEmail();
    Long getSaleId();
    Long getUserId();
    Long getProductId();
    String getProductName();
    String getProductCategory();
    Integer getQuantity();
    Double getUnitPrice();
    Double getTotalAmount();
    String getState();
    LocalDateTime getOccurredAt();
}