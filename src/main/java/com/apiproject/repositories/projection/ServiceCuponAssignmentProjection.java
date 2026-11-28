package com.apiproject.repositories.projection;

import java.math.BigDecimal;

import java.time.LocalDateTime;

public interface ServiceCuponAssignmentProjection {
    Long getId();
    Long getClientId();
    String getClientName();
    String getClientEmail();
    Long getServiceCuponId();
    String getServiceCuponCode();
    BigDecimal getDiscount();
    LocalDateTime getCuponDateLimit();
    Long getServiceId();
    String getServiceName();
    Integer getUsageLimit();
}