package com.apiproject.repositories.projection;

import java.time.LocalDateTime;

public interface ShSaleHistoryProjection {
    Long getId();
    Long getSaleId();
    Long getProductId();
    String getProductName();
    Integer getQuantity();
    Double getUnitPrice();
    Double getSubtotal();
    Double getDiscount();
    String getCuponCode();
    String getState();
    LocalDateTime getDate();
}
