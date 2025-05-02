package com.tenpo.tenpochallenge.application.factories;

import com.tenpo.tenpochallenge.domain.model.CalculationResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Factory for creating CalculationResult objects
 * Implements the Factory Method pattern
 */
@Component
public class CalculationResultFactory {
    
    /**
     * Creates a CalculationResult object from input numbers and percentage
     * @param num1 First number
     * @param num2 Second number
     * @param percentage Percentage to apply
     * @return The calculation result
     */
    public CalculationResult createResult(BigDecimal num1, BigDecimal num2, BigDecimal percentage) {
        // Calculate sum
        BigDecimal sum = num1.add(num2);
        
        // Calculate percentage amount
        BigDecimal percentageAmount = sum.multiply(percentage);
        
        // Calculate final result (sum + percentage amount)
        BigDecimal finalResult = sum.add(percentageAmount).setScale(2, RoundingMode.HALF_UP);
        
        // Create and return the immutable CalculationResult object
        return new CalculationResult(sum, percentage, finalResult);
    }
    
    /**
     * Overload that creates a CalculationResult object directly from a sum and percentage
     * @param sum Already calculated sum
     * @param percentage Percentage to apply
     * @return The calculation result
     */
    public CalculationResult createResultFromSum(BigDecimal sum, BigDecimal percentage) {
        // Calculate percentage amount
        BigDecimal percentageAmount = sum.multiply(percentage);
        
        // Calculate final result (sum + percentage amount)
        BigDecimal finalResult = sum.add(percentageAmount).setScale(2, RoundingMode.HALF_UP);
        
        // Create and return the immutable CalculationResult object
        return new CalculationResult(sum, percentage, finalResult);
    }
}
