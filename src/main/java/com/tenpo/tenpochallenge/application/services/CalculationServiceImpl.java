package com.tenpo.tenpochallenge.application.services;

import com.tenpo.tenpochallenge.application.factories.CalculationResultFactory;
import com.tenpo.tenpochallenge.commons.exception.InvalidInputException;
import com.tenpo.tenpochallenge.commons.exception.NoPercentageAvailableException;
import com.tenpo.tenpochallenge.domain.model.CalculationResult;
import com.tenpo.tenpochallenge.domain.ports.CalculationService;
import com.tenpo.tenpochallenge.domain.ports.PercentageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Implementation of the calculation service
 * Responsible for performing the sum and applying the percentage
 */
@Service
public class CalculationServiceImpl implements CalculationService {
    
    private static final Logger logger = LoggerFactory.getLogger(CalculationServiceImpl.class);
    private final PercentageService percentageService;
    private final CalculationResultFactory resultFactory;
    
    public CalculationServiceImpl(PercentageService percentageService, CalculationResultFactory resultFactory) {
        this.percentageService = percentageService;
        this.resultFactory = resultFactory;
    }
    
    @Override
    public CalculationResult calculate(BigDecimal num1, BigDecimal num2) {
        if (num1 == null || num2 == null) {
            logger.error("Invalid input numbers: num1={}, num2={}", num1, num2);
            throw new InvalidInputException("Numbers cannot be null");
        }
        
        logger.info("Calculating sum of {} + {}", num1, num2);
        BigDecimal sum = num1.add(num2);
        
        try {
            BigDecimal percentage = percentageService.getPercentageWithFallback();
            logger.info("Percentage obtained: {}%", percentage.multiply(BigDecimal.valueOf(100)));
            
            return resultFactory.createResult(num1, num2, percentage);
        } catch (Exception e) {
            logger.error("Error getting percentage: {}", e.getMessage());
            throw new NoPercentageAvailableException("Could not obtain percentage for calculation", e);
        }
    }
}
