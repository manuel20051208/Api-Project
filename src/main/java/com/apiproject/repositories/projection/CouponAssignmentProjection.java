package com.apiproject.repositories.projection;

public interface CouponAssignmentProjection {
    Long getId();
    String getClientName();
    String getClientEmail();
    Integer getUsageLimit();
    Long getUsedCount();
    String getProductName();
}