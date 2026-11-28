package com.apiproject.repositories.projection;

import java.math.BigDecimal;

import java.time.LocalDateTime;

public interface ProductCuponAssignmentProjection {
    Long getId();
    Long getClientId();
    String getClientName();
    String getClientEmail();
    Long getCuponId();
    String getCuponCode();
    BigDecimal getDiscount();
    LocalDateTime getCuponDateLimit();
    Long getProductId();
    String getProductName();
    Integer getUsageLimit();
}