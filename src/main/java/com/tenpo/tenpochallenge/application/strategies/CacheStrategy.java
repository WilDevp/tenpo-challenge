package com.tenpo.tenpochallenge.application.strategies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Strategy that retrieves the percentage from the cache
 * Second option in the strategy chain
 */
@Component
public class CacheStrategy implements PercentageStrategy {
    
    private static final Logger logger = LoggerFactory.getLogger(CacheStrategy.class);
    private final CacheManager cacheManager;
    
    public CacheStrategy(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
    
    @Override
    public boolean canProvide() {
        Cache cache = cacheManager.getCache("percentages");
        if (cache == null) {
            logger.warn("Cache 'percentages' not found");
            return false;
        }
        
        Cache.ValueWrapper valueWrapper = cache.get("current");
        
        logger.info("Checking cache availability: {}", valueWrapper != null && valueWrapper.get() != null);
        return valueWrapper != null && valueWrapper.get() != null;
    }
    
    @Override
    public BigDecimal getPercentage() {
        logger.info("Getting percentage from cache");
        Cache cache = cacheManager.getCache("percentages");
        if (cache == null) {
            logger.error("Cache 'percentages' not found when trying to retrieve value");
            throw new IllegalStateException("Cache 'percentages' not found");
        }
        
        Cache.ValueWrapper valueWrapper = cache.get("current");
        
        if (valueWrapper != null && valueWrapper.get() != null) {
            try {
                BigDecimal percentage = (BigDecimal) valueWrapper.get();
                logger.info("Percentage retrieved from cache: {}%", 
                        percentage.multiply(BigDecimal.valueOf(100)));
                return percentage;
            } catch (ClassCastException e) {
                logger.error("Error converting cache value: {}", e.getMessage());
                Object value = valueWrapper.get();
                logger.info("Cache value type: {}, value: {}", 
                        value != null ? value.getClass().getName() : "null", value);
                throw new IllegalStateException("Cache value is not of the expected type");
            }
        }
        
        logger.warn("No percentage found in cache");
        throw new IllegalStateException("No percentage found in cache");
    }
    
    @Override
    public int getPriority() {
        return 2; // Second priority
    }
}
