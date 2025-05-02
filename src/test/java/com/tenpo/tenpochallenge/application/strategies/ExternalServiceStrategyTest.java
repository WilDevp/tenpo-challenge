package com.tenpo.tenpochallenge.application.strategies;

import com.tenpo.tenpochallenge.domain.ports.ExternalPercentageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExternalServiceStrategyTest {

    @Mock
    private ExternalPercentageService externalService;
    
    @InjectMocks
    private ExternalServiceStrategy strategy;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void getPercentage_ServiceReturnsPercentage_ReturnsPercentage() {
        BigDecimal expectedPercentage = new BigDecimal("0.15");
        when(externalService.getPercentage()).thenReturn(expectedPercentage);
        
        BigDecimal result = strategy.getPercentage();
        
        assertEquals(expectedPercentage, result);
        verify(externalService, times(1)).getPercentage();
    }
    
    @Test
    void getPercentage_ServiceThrowsException_PropagatesException() {
        RuntimeException expectedException = new RuntimeException("Service error");
        when(externalService.getPercentage()).thenThrow(expectedException);
        
        RuntimeException actualException = assertThrows(
                RuntimeException.class,
                () -> strategy.getPercentage()
        );
        
        assertEquals(expectedException, actualException);
        verify(externalService, times(1)).getPercentage();
    }
    
    @Test
    void canProvide_AlwaysReturnsTrue() {
        boolean result = strategy.canProvide();
        
        assertTrue(result);
    }
    
    @Test
    void getPriority_Returns1() {
        int priority = strategy.getPriority();
        
        assertEquals(1, priority);
    }
}
