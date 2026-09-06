package com.apiproject.repositories.projection;

import java.time.LocalDateTime;

public interface ShProductCuponAssignmentProjection {
    Long getId();
    Long getClientId();
    String getClientName();
    String getClientEmail();
    Long getShCuponsId();
    String getShCuponCode();
    Double getDiscount();
    LocalDateTime getCuponDateLimit();
    Long getShProductId();
    String getProductName();
}