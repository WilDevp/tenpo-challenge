package com.tenpo.tenpochallenge.application.strategies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultPercentageStrategyTest {

    @InjectMocks
    private DefaultPercentageStrategy strategy;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Inject value for defaultPercentage
        ReflectionTestUtils.setField(strategy, "defaultPercentage", new BigDecimal("0.10"));
    }
    
    @Test
    void canProvide_AlwaysReturnsTrue() {
        boolean result = strategy.canProvide();
        
        assertTrue(result);
    }
    
    @Test
    void getPercentage_ReturnsDefaultPercentage() {
        BigDecimal expectedPercentage = new BigDecimal("0.10");
        
        BigDecimal result = strategy.getPercentage();
        
        assertEquals(expectedPercentage, result);
    }
    
    @Test
    void getPriority_Returns3() {
        int priority = strategy.getPriority();
        
        assertEquals(3, priority);
    }
}
