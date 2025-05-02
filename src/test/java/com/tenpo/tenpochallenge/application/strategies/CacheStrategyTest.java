package com.tenpo.tenpochallenge.application.strategies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CacheStrategyTest {

    @Mock
    private CacheManager cacheManager;
    
    @Mock
    private Cache cache;
    
    @Mock
    private Cache.ValueWrapper valueWrapper;
    
    @InjectMocks
    private CacheStrategy strategy;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void canProvide_CacheHasValue_ReturnsTrue() {
        when(cacheManager.getCache("percentages")).thenReturn(cache);
        when(cache.get("current")).thenReturn(valueWrapper);
        when(valueWrapper.get()).thenReturn(new BigDecimal("0.15"));
        
        boolean result = strategy.canProvide();
        
        assertTrue(result);
        verify(cacheManager, atLeastOnce()).getCache("percentages");
        verify(cache, atLeastOnce()).get("current");
        verify(valueWrapper, atLeastOnce()).get();
    }
    
    @Test
    void canProvide_CacheIsNull_ReturnsFalse() {
        when(cacheManager.getCache("percentages")).thenReturn(null);
        
        boolean result = strategy.canProvide();
        
        assertFalse(result);
        verify(cacheManager, atLeastOnce()).getCache("percentages");
        verify(cache, never()).get(anyString());
    }
    
    @Test
    void canProvide_ValueWrapperIsNull_ReturnsFalse() {
        when(cacheManager.getCache("percentages")).thenReturn(cache);
        when(cache.get("current")).thenReturn(null);
        
        boolean result = strategy.canProvide();
        
        assertFalse(result);
        verify(cacheManager, atLeastOnce()).getCache("percentages");
        verify(cache, atLeastOnce()).get("current");
    }
    
    @Test
    void getPercentage_CacheHasValue_ReturnsValue() {
        BigDecimal expectedPercentage = new BigDecimal("0.15");
        
        when(cacheManager.getCache("percentages")).thenReturn(cache);
        when(cache.get("current")).thenReturn(valueWrapper);
        when(valueWrapper.get()).thenReturn(expectedPercentage);
        
        BigDecimal result = strategy.getPercentage();
        
        assertEquals(expectedPercentage, result);
        // Since getPercentage() makes its own calls to getCache() and get(),
        // even though canProvide() already made them, we allow multiple calls
        verify(cacheManager, atLeastOnce()).getCache("percentages");
        verify(cache, atLeastOnce()).get("current");
        verify(valueWrapper, atLeastOnce()).get();
    }
    
    @Test
    void getPercentage_CacheHasNoValue_ThrowsException() {
        when(cacheManager.getCache("percentages")).thenReturn(cache);
        when(cache.get("current")).thenReturn(null);
        
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> strategy.getPercentage()
        );
        
        assertTrue(exception.getMessage().contains("No percentage found in cache"));
        verify(cacheManager, atLeastOnce()).getCache("percentages");
        verify(cache, atLeastOnce()).get("current");
    }
    
    @Test
    void getPriority_Returns2() {
        int priority = strategy.getPriority();
        
        assertEquals(2, priority);
    }
}
