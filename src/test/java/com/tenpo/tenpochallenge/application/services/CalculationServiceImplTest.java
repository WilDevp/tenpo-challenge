package com.tenpo.tenpochallenge.application.services;

import com.tenpo.tenpochallenge.application.factories.CalculationResultFactory;
import com.tenpo.tenpochallenge.commons.exception.InvalidInputException;
import com.tenpo.tenpochallenge.commons.exception.NoPercentageAvailableException;
import com.tenpo.tenpochallenge.domain.model.CalculationResult;
import com.tenpo.tenpochallenge.domain.ports.PercentageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CalculationServiceImplTest {

    @Mock
    private PercentageService percentageService;
    
    @Mock
    private CalculationResultFactory resultFactory;
    
    @InjectMocks
    private CalculationServiceImpl calculationService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void calculate_ValidInput_ReturnsCorrectResult() {
        BigDecimal num1 = new BigDecimal("100");
        BigDecimal num2 = new BigDecimal("200");
        BigDecimal percentage = new BigDecimal("0.10");
        
        BigDecimal expectedSum = new BigDecimal("300");
        BigDecimal expectedFinalResult = new BigDecimal("330.00");
        
        CalculationResult expectedResult = new CalculationResult(
                expectedSum, percentage, expectedFinalResult);
        
        when(percentageService.getPercentageWithFallback()).thenReturn(percentage);
        when(resultFactory.createResult(num1, num2, percentage)).thenReturn(expectedResult);
        
        CalculationResult result = calculationService.calculate(num1, num2);
        
        assertNotNull(result);
        assertEquals(expectedSum, result.getOriginalSum());
        assertEquals(percentage, result.getPercentage());
        assertEquals(expectedFinalResult, result.getFinalResult());
        
        verify(percentageService, times(1)).getPercentageWithFallback();
        verify(resultFactory, times(1)).createResult(num1, num2, percentage);
    }
    
    @Test
    void calculate_NullInput_ThrowsInvalidInputException() {
        BigDecimal num1 = null;
        BigDecimal num2 = new BigDecimal("200");
        
        InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> calculationService.calculate(num1, num2)
        );
        
        assertEquals("Numbers cannot be null", exception.getMessage());
        
        verify(percentageService, never()).getPercentageWithFallback();
        verify(resultFactory, never()).createResult(any(), any(), any());
    }
    
    @Test
    void calculate_PercentageServiceFails_ThrowsNoPercentageAvailableException() {
        BigDecimal num1 = new BigDecimal("100");
        BigDecimal num2 = new BigDecimal("200");
        
        when(percentageService.getPercentageWithFallback())
                .thenThrow(new NoPercentageAvailableException("Could not obtain the percentage"));
        
        NoPercentageAvailableException exception = assertThrows(
                NoPercentageAvailableException.class,
                () -> calculationService.calculate(num1, num2)
        );
        
        assertTrue(exception.getMessage().contains("Could not obtain"));
        
        verify(percentageService, times(1)).getPercentageWithFallback();
        verify(resultFactory, never()).createResult(any(), any(), any());
    }
}
