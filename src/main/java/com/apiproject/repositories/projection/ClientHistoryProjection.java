package com.apiproject.repositories.projection;

import java.math.BigDecimal;

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
    BigDecimal getUnitPrice();
    BigDecimal getSubtotal();
    BigDecimal getTotalAmount();
    BigDecimal getDiscount();
    BigDecimal getItemDiscount();
    String getSaleStatus();
    String getCuponCode();
    String getState();
    LocalDateTime getOccurredAt();
}