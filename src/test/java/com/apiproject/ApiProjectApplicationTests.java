package com.apiproject;

import com.apiproject.config.CacheConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ApiProjectApplicationTests {

    @Autowired
    private CacheManager cacheManager;

    @Test
    void contextLoads() {
    }

    /** El cache debe ser Caffeine y estar acotado (antes: mapa sin limite que llenaba la RAM). */
    @Test
    void elCacheEsCaffeineYEstaAcotado() {
        assertInstanceOf(CaffeineCacheManager.class, cacheManager);

        CaffeineCache cache = assertInstanceOf(CaffeineCache.class,
                cacheManager.getCache(CacheConstants.CUPONS));
        assertNotNull(cache);

        long maximumSize = cache.getNativeCache().policy().eviction()
                .orElseThrow(() -> new AssertionError("El cache no tiene politica de eviccion (maximumSize)"))
                .getMaximum();
        assertEquals(100, maximumSize);
    }
}
