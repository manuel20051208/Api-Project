package com.apiproject.repositories.projection;

import java.math.BigDecimal;

import java.time.LocalDate;

public interface SaleItemViewProjection {
    Long getId();
    Long getUserId();
    String getClientName();
    String getProductName();
    Integer getQuantity();
    BigDecimal getTotalCalculated();
    String getState();
    LocalDate getDate();
}
