package com.tenpo.tenpochallenge.domain.ports;

import com.tenpo.tenpochallenge.domain.model.CalculationResult;

import java.math.BigDecimal;

/**
 * Port for the calculation service
 */
public interface CalculationService {
    /**
     * Performs the calculation by adding two numbers and applying a percentage to the sum
     * @param num1 First number
     * @param num2 Second number
     * @return Result of the calculation
     */
    CalculationResult calculate(BigDecimal num1, BigDecimal num2);
}
