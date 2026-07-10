package com.example.apiproject.repositories.projection;

import java.time.LocalDate;

public interface SaleItemViewProjection {
    Long getId();
    Long getUserId();
    String getClientName();
    String getProductName();
    Integer getQuantity();
    Double getTotalCalculated();
    String getState();
    LocalDate getDate();
}
