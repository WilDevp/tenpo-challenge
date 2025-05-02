package com.tenpo.tenpochallenge.application.services;

import com.tenpo.tenpochallenge.application.strategies.PercentageStrategy;
import com.tenpo.tenpochallenge.commons.exception.NoPercentageAvailableException;
import com.tenpo.tenpochallenge.domain.ports.PercentageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

/**
 * Strategy pattern-based implementation of the percentage service
 * Uses a list of strategies ordered by priority
 */
@Service
@Primary
public class StrategyBasedPercentageServiceImpl implements PercentageService {
    
    private static final Logger logger = LoggerFactory.getLogger(StrategyBasedPercentageServiceImpl.class);
    private final List<PercentageStrategy> strategies;
    
    public StrategyBasedPercentageServiceImpl(List<PercentageStrategy> strategies) {
        // Sort strategies by priority
        this.strategies = strategies.stream()
                .sorted(Comparator.comparingInt(PercentageStrategy::getPriority))
                .toList();
        
        logger.info("Percentage strategies configured: {}", 
                this.strategies.stream()
                        .map(s -> s.getClass().getSimpleName())
                        .toList());
    }
    
    @Override
    public BigDecimal getPercentage() {
        // Try with the first available strategy
        for (PercentageStrategy strategy : strategies) {
            if (strategy.canProvide()) {
                try {
                    logger.info("Getting percentage with strategy: {}", strategy.getClass().getSimpleName());
                    return strategy.getPercentage();
                } catch (Exception e) {
                    logger.warn("Error getting percentage with strategy {}: {}", 
                            strategy.getClass().getSimpleName(), e.getMessage());
                    // Continue with the next strategy
                }
            }
        }
        
        // If we get here, we couldn't get a percentage
        logger.error("Could not get percentage with any strategy");
        throw new NoPercentageAvailableException("Could not get percentage with any available strategy");
    }
    
    @Override
    public BigDecimal getPercentageWithFallback() {
        // This method is now equivalent to getPercentage() because the fallback logic
        // is incorporated into the strategy selection
        return getPercentage();
    }
}
