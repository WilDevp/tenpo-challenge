package com.tenpo.tenpochallenge.application.strategies;

import java.math.BigDecimal;

/**
 * Interface for percentage retrieval strategies
 * Following the Strategy pattern to have different interchangeable implementations
 */
public interface PercentageStrategy {
    
    /**
     * Checks if this strategy can provide a percentage
     * @return true if it can provide the percentage, false otherwise
     */
    boolean canProvide();
    
    /**
     * Gets the percentage according to the implemented strategy
     * @return The percentage as a BigDecimal
     */
    BigDecimal getPercentage();
    
    /**
     * Returns the priority of the strategy (lower number = higher priority)
     * @return the priority value
     */
    int getPriority();
}
