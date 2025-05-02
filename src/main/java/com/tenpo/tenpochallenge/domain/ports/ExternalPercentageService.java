package com.tenpo.tenpochallenge.domain.ports;

import java.math.BigDecimal;

/**
 * Port for the external percentage service
 * Represents the communication with an external service that provides the dynamic percentage
 */
public interface ExternalPercentageService {
    /**
     * Gets the percentage from the external service
     * @return The percentage as a decimal (e.g. 0.1 for 10%)
     */
    BigDecimal getPercentage();
}
