package com.tenpo.tenpochallenge.application.services;

import com.tenpo.tenpochallenge.commons.exception.NoPercentageAvailableException;
import com.tenpo.tenpochallenge.commons.exception.ServiceUnavailableException;
import com.tenpo.tenpochallenge.domain.ports.ExternalPercentageService;
import com.tenpo.tenpochallenge.domain.ports.PercentageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Implementation of the percentage service
 * Responsible for retrieving the percentage and maintaining a cache for fallback
 */
@Service
public class PercentageServiceImpl implements PercentageService {
    
    private static final Logger logger = LoggerFactory.getLogger(PercentageServiceImpl.class);
    private static final BigDecimal DEFAULT_PERCENTAGE = new BigDecimal("0.10"); // 10% default
    
    private final ExternalPercentageService externalPercentageService;
    private final AtomicReference<BigDecimal> lastKnownPercentage = new AtomicReference<>(DEFAULT_PERCENTAGE);
    
    public PercentageServiceImpl(ExternalPercentageService externalPercentageService) {
        this.externalPercentageService = externalPercentageService;
    }
    
    @Override
    @Cacheable(value = "percentages", key = "'current'", unless = "#result == null")
    public BigDecimal getPercentage() {
        try {
            logger.info("Obtaining percentage from external service");
            BigDecimal percentage = externalPercentageService.getPercentage();
            // Update the last known percentage for fallback
            lastKnownPercentage.set(percentage);
            logger.info("Percentage obtained successfully: {}%", percentage.multiply(BigDecimal.valueOf(100)));
            return percentage;
        } catch (Exception e) {
            logger.error("Error obtaining percentage from external service: {}", e.getMessage());
            throw new ServiceUnavailableException("Error obtaining percentage from external service", e);
        }
    }
    
    @Override
    public BigDecimal getPercentageWithFallback() {
        try {
            // First attempt: get from external service (with cache)
            return getPercentage();
        } catch (ServiceUnavailableException e) {
            // Second attempt: use the last known percentage
            BigDecimal cachedPercentage = lastKnownPercentage.get();
            if (cachedPercentage != null) {
                logger.info("Using last known percentage: {}%", 
                        cachedPercentage.multiply(BigDecimal.valueOf(100)));
                return cachedPercentage;
            }
            
            // If everything fails, throw exception
            logger.error("No percentage available (neither external service nor cache)");
            throw new NoPercentageAvailableException("No percentage available to perform the calculation");
        }
    }
}
