package com.tenpo.tenpochallenge.application.strategies;

import com.tenpo.tenpochallenge.domain.ports.ExternalPercentageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Strategy that retrieves the percentage from the external service
 * First option in the strategy chain
 */
@Component
public class ExternalServiceStrategy implements PercentageStrategy {
    
    private static final Logger logger = LoggerFactory.getLogger(ExternalServiceStrategy.class);
    private final ExternalPercentageService externalService;
    private final RedisCacheManager cacheManager;
    
    public ExternalServiceStrategy(ExternalPercentageService externalService, RedisCacheManager cacheManager) {
        this.externalService = externalService;
        this.cacheManager = cacheManager;
    }
    
    @Override
    public boolean canProvide() {
        try {
            // Check if the external service is available
            // We don't call the actual service to avoid unnecessary requests
            return true;
        } catch (Exception e) {
            logger.warn("External service not available: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public BigDecimal getPercentage() {
        logger.info("Getting percentage from external service");
        BigDecimal percentage = externalService.getPercentage();
        
        // Explicitly save the percentage in Redis for cache use
        if (percentage != null) {
            try {
                logger.info("Saving percentage in cache: {}%", percentage.multiply(BigDecimal.valueOf(100)));
                Cache cache = cacheManager.getCache("percentages");
                if (cache != null) {
                    cache.put("current", percentage);
                    logger.info("Percentage successfully saved in Redis cache");
                } else {
                    logger.warn("Could not get cache 'percentages'");
                }
            } catch (Exception e) {
                logger.error("Error saving percentage in cache: {}", e.getMessage());
            }
        }
        
        return percentage;
    }
    
    @Override
    public int getPriority() {
        return 1; // Highest priority
    }
}
