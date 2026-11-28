package com.apiproject.repositories.projection;

import java.math.BigDecimal;

import java.time.LocalDateTime;

public interface ShSaleHistoryProjection {
    Long getId();
    Long getSaleId();
    Long getProductId();
    String getProductName();
    Integer getQuantity();
    BigDecimal getUnitPrice();
    BigDecimal getSubtotal();
    BigDecimal getDiscount();
    BigDecimal getItemDiscount();
    String getSaleStatus();
    String getCuponCode();
    String getState();
    LocalDateTime getDate();
}
