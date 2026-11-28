package com.apiproject.repositories.projection;

import java.math.BigDecimal;

import java.time.LocalDateTime;

public interface ShProductCuponAssignmentProjection {
    Long getId();
    Long getClientId();
    String getClientName();
    String getClientEmail();
    Long getShCuponsId();
    String getShCuponCode();
    BigDecimal getDiscount();
    LocalDateTime getCuponDateLimit();
    Long getShProductId();
    String getProductName();
    Integer getUsageLimit();
}