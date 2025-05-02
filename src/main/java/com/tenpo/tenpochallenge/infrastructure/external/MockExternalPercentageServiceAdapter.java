package com.tenpo.tenpochallenge.infrastructure.external;

import com.tenpo.tenpochallenge.domain.ports.ExternalPercentageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

/**
 * Adapter that simulates an external service to retrieve percentages
 * Configured to fail frequently (50% probability) to demonstrate the cache mechanism
 */
@Service
public class MockExternalPercentageServiceAdapter implements ExternalPercentageService {
    
    private static final Logger logger = LoggerFactory.getLogger(MockExternalPercentageServiceAdapter.class);
    private final Random random = new Random();
    
    @Override
    public BigDecimal getPercentage() {
        // Simulate failure with 50% probability to demonstrate the cache mechanism
        if (random.nextInt(100) < 50) {
            logger.info("Simulating failure in external service");
            throw new RuntimeException("Simulated error from external service");
        }
        
        // Generate a random percentage between 5% and 20%
        BigDecimal percentage = BigDecimal.valueOf(random.nextDouble() * 0.15 + 0.05);
        percentage = percentage.setScale(2, RoundingMode.HALF_UP);
        
        logger.info("Percentage obtained from external service: {}%", percentage.multiply(BigDecimal.valueOf(100)));
        return percentage;
    }
}
