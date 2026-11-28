package com.apiproject.repositories.projection;

import java.math.BigDecimal;

public interface CuponUsageProjection {
    Long getCuponId();
    BigDecimal getDiscount();
}
