package com.tenpo.tenpochallenge.domain.model;

import java.math.BigDecimal;

/**
 * Immutable value object representing the result of a calculation
 */
public class CalculationResult {
    private final BigDecimal originalSum;
    private final BigDecimal percentage;
    private final BigDecimal finalResult;

    public CalculationResult(BigDecimal originalSum, BigDecimal percentage, BigDecimal finalResult) {
        this.originalSum = originalSum;
        this.percentage = percentage;
        this.finalResult = finalResult;
    }

    public BigDecimal getOriginalSum() {
        return originalSum;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public BigDecimal getFinalResult() {
        return finalResult;
    }
}
