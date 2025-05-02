package com.tenpo.tenpochallenge.application.decorators;

import com.tenpo.tenpochallenge.domain.model.CalculationResult;
import com.tenpo.tenpochallenge.domain.ports.CalculationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Decorator for the calculation service that adds logging and time measurement functionalities
 * Example of applying the Decorator pattern
 */
@Service
@Primary
public class LoggingCalculationServiceDecorator implements CalculationService {

    private static final Logger logger = LoggerFactory.getLogger(LoggingCalculationServiceDecorator.class);
    private final CalculationService delegate;

    public LoggingCalculationServiceDecorator(@Qualifier("calculationServiceImpl") CalculationService delegate) {
        this.delegate = delegate;
    }

    @Override
    public CalculationResult calculate(BigDecimal num1, BigDecimal num2) {
        logger.info("Starting calculation with parameters: num1={}, num2={}", num1, num2);
        long startTime = System.currentTimeMillis();

        try {
            CalculationResult result = delegate.calculate(num1, num2);
            long executionTime = System.currentTimeMillis() - startTime;
            
            logger.info("Calculation completed in {}ms. Sum: {}, Percentage: {}%, Final result: {}", 
                    executionTime, 
                    result.getOriginalSum(), 
                    result.getPercentage().multiply(BigDecimal.valueOf(100)),
                    result.getFinalResult());
            
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("Error in calculation after {}ms: {}", executionTime, e.getMessage());
            throw e;
        }
    }
}
