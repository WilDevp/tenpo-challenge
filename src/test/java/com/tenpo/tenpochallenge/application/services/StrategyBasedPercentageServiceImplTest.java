package com.tenpo.tenpochallenge.application.services;

import com.tenpo.tenpochallenge.application.strategies.PercentageStrategy;
import com.tenpo.tenpochallenge.commons.exception.NoPercentageAvailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StrategyBasedPercentageServiceImplTest {

    @Mock
    private PercentageStrategy strategy1;
    
    @Mock
    private PercentageStrategy strategy2;
    
    @Mock
    private PercentageStrategy strategy3;
    
    private StrategyBasedPercentageServiceImpl percentageService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        when(strategy1.getPriority()).thenReturn(1);
        when(strategy2.getPriority()).thenReturn(2);
        when(strategy3.getPriority()).thenReturn(3);
        
        List<PercentageStrategy> strategies = Arrays.asList(strategy2, strategy3, strategy1);
        percentageService = new StrategyBasedPercentageServiceImpl(strategies);
    }
    
    @Test
    void getPercentage_FirstStrategySucceeds_ReturnsFirstStrategyResult() {
        BigDecimal expectedPercentage = new BigDecimal("0.15");
        
        when(strategy1.canProvide()).thenReturn(true);
        when(strategy1.getPercentage()).thenReturn(expectedPercentage);
        
        BigDecimal result = percentageService.getPercentage();
        
        assertEquals(expectedPercentage, result);
        
        verify(strategy1, times(1)).canProvide();
        verify(strategy1, times(1)).getPercentage();
        verify(strategy2, never()).getPercentage();
        verify(strategy3, never()).getPercentage();
    }
    
    @Test
    void getPercentage_FirstStrategyCannotProvide_TrySecondStrategy() {
        BigDecimal expectedPercentage = new BigDecimal("0.20");
        
        when(strategy1.canProvide()).thenReturn(false);
        when(strategy2.canProvide()).thenReturn(true);
        when(strategy2.getPercentage()).thenReturn(expectedPercentage);
        
        BigDecimal result = percentageService.getPercentage();
        
        assertEquals(expectedPercentage, result);
        
        verify(strategy1, times(1)).canProvide();
        verify(strategy1, never()).getPercentage();
        verify(strategy2, times(1)).canProvide();
        verify(strategy2, times(1)).getPercentage();
        verify(strategy3, never()).getPercentage();
    }
    
    @Test
    void getPercentage_FirstStrategyFailsSecondSucceeds_ReturnsSecondStrategyResult() {
        BigDecimal expectedPercentage = new BigDecimal("0.20");
        
        when(strategy1.canProvide()).thenReturn(true);
        when(strategy1.getPercentage()).thenThrow(new RuntimeException("Connection error"));
        when(strategy2.canProvide()).thenReturn(true);
        when(strategy2.getPercentage()).thenReturn(expectedPercentage);
        
        BigDecimal result = percentageService.getPercentage();
        
        assertEquals(expectedPercentage, result);
        
        verify(strategy1, times(1)).canProvide();
        verify(strategy1, times(1)).getPercentage();
        verify(strategy2, times(1)).canProvide();
        verify(strategy2, times(1)).getPercentage();
        verify(strategy3, never()).getPercentage();
    }
    
    @Test
    void getPercentage_AllStrategiesFail_ThrowsNoPercentageAvailableException() {
        when(strategy1.canProvide()).thenReturn(true);
        when(strategy1.getPercentage()).thenThrow(new RuntimeException("Strategy 1 error"));
        when(strategy2.canProvide()).thenReturn(true);
        when(strategy2.getPercentage()).thenThrow(new RuntimeException("Strategy 2 error"));
        when(strategy3.canProvide()).thenReturn(true);
        when(strategy3.getPercentage()).thenThrow(new RuntimeException("Strategy 3 error"));
        
        NoPercentageAvailableException exception = assertThrows(
                NoPercentageAvailableException.class,
                () -> percentageService.getPercentage()
        );
        
        assertTrue(exception.getMessage().contains("Could not get percentage"));
        
        verify(strategy1, times(1)).canProvide();
        verify(strategy1, times(1)).getPercentage();
        verify(strategy2, times(1)).canProvide();
        verify(strategy2, times(1)).getPercentage();
        verify(strategy3, times(1)).canProvide();
        verify(strategy3, times(1)).getPercentage();
    }
    
    @Test
    void getPercentageWithFallback_DelegatesToGetPercentage() {
        BigDecimal expectedPercentage = new BigDecimal("0.15");
        
        when(strategy1.canProvide()).thenReturn(true);
        when(strategy1.getPercentage()).thenReturn(expectedPercentage);
        
        BigDecimal result = percentageService.getPercentageWithFallback();
        
        assertEquals(expectedPercentage, result);
        
        verify(strategy1, times(1)).canProvide();
        verify(strategy1, times(1)).getPercentage();
    }
}
