package com.tenpo.tenpochallenge.domain.ports;

import java.math.BigDecimal;

/**
 * Port for the percentage service
 */
public interface PercentageService {
    /**
     * Gets the current percentage
     * @return The percentage as a decimal (e.g. 0.1 for 10%)
     */
    BigDecimal getPercentage();
    
    /**
     * Gets the percentage with a fallback mechanism to cache
     * @return The percentage as a decimal
     */
    BigDecimal getPercentageWithFallback();
}
