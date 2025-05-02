package com.tenpo.tenpochallenge.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Complete Redis configuraton for tests
 * Provides mock implementations for all required Redis components
 */
@TestConfiguration
public class TestRedisConfig {

    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory() {
        return Mockito.mock(RedisConnectionFactory.class);
    }

    @Bean
    @Primary
    public RedisCacheManager redisCacheManager() {
        return Mockito.mock(RedisCacheManager.class);
    }

    /**
     * Provides a mock CacheManager for tests
     * that always returns a mock Cache regardless of the key
     */
    @Bean
    @Primary
    public CacheManager cacheManager() {
        return new MockCacheManager();
    }

    /**
     * Mock implementation of CacheManager for tests
     */
    static class MockCacheManager implements CacheManager {
        private final Map<String, Cache> caches = new HashMap<>();

        @Override
        public Cache getCache(String name) {
            return caches.computeIfAbsent(name, k -> {
                Cache mockCache = Mockito.mock(Cache.class);
                Cache.ValueWrapper mockValueWrapper = Mockito.mock(Cache.ValueWrapper.class);
                
                Mockito.when(mockCache.get(Mockito.anyString())).thenReturn(mockValueWrapper);
                
                return mockCache;
            });
        }

        @Override
        public Collection<String> getCacheNames() {
            return caches.keySet();
        }
    }
}
