package com.tenpo.tenpochallenge.application.strategies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Strategy that provides a default percentage
 * Last option in the strategy chain
 */
@Component
public class DefaultPercentageStrategy implements PercentageStrategy {
    
    private static final Logger logger = LoggerFactory.getLogger(DefaultPercentageStrategy.class);
    
    @Value("${app.calculation.default-percentage:0.10}")
    private BigDecimal defaultPercentage;
    
    @Override
    public boolean canProvide() {
        // Always able to provide a default value
        return true;
    }
    
    @Override
    public BigDecimal getPercentage() {
        logger.info("Using default percentage: {}%", defaultPercentage.multiply(BigDecimal.valueOf(100)));
        return defaultPercentage;
    }
    
    @Override
    public int getPriority() {
        return 3; // Lowest priority
    }
}
