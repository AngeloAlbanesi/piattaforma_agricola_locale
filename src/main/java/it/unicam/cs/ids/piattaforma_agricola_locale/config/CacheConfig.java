package it.unicam.cs.ids.piattaforma_agricola_locale.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

/**
 * Configurazione del caching per la piattaforma agricola.
 * 
 * Implementa una strategia di caching multi-livello con Caffeine
 * per ottimizzare le performance delle operazioni più frequenti.
 * 
 * Cache implementate:
 * - products: Cache dei prodotti (15 min TTL)
 * - companies: Cache delle aziende (30 min TTL)
 * - events: Cache degli eventi (10 min TTL)
 * - packages: Cache dei pacchetti (20 min TTL)
 * - users: Cache degli utenti (60 min TTL)
 */
@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {

    /**
     * Cache principale per i prodotti.
     * Configurata per alta frequenza di accesso.
     */
    @Bean
    public CacheManager productsCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("products");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(2000)
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .recordStats()
                .removalListener(
                        (key, value, cause) -> log.debug("Product cache entry removed: key={}, cause={}", key, cause)));
        return cacheManager;
    }

    /**
     * Cache per le aziende.
     * Meno frequente ma dati più stabili.
     */
    @Bean
    public CacheManager companiesCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("companies");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .expireAfterAccess(20, TimeUnit.MINUTES)
                .recordStats()
                .removalListener(
                        (key, value, cause) -> log.debug("Company cache entry removed: key={}, cause={}", key, cause)));
        return cacheManager;
    }

    /**
     * Cache per gli eventi.
     * Aggiornamenti frequenti, TTL breve.
     */
    @Bean
    public CacheManager eventsCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("events");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .recordStats()
                .removalListener(
                        (key, value, cause) -> log.debug("Event cache entry removed: key={}, cause={}", key, cause)));
        return cacheManager;
    }

    /**
     * Cache per i pacchetti.
     * Dati relativamente stabili.
     */
    @Bean
    public CacheManager packagesCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("packages");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(800)
                .expireAfterWrite(20, TimeUnit.MINUTES)
                .expireAfterAccess(15, TimeUnit.MINUTES)
                .recordStats()
                .removalListener(
                        (key, value, cause) -> log.debug("Package cache entry removed: key={}, cause={}", key, cause)));
        return cacheManager;
    }

    /**
     * Cache per gli utenti.
     * TTL più lungo per dati di autenticazione.
     */
    @Bean
    public CacheManager usersCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("users");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .expireAfterAccess(30, TimeUnit.MINUTES)
                .recordStats()
                .removalListener(
                        (key, value, cause) -> log.debug("User cache entry removed: key={}, cause={}", key, cause)));
        return cacheManager;
    }

    /**
     * Cache manager composito che combina tutte le cache.
     * Questo è il cache manager principale utilizzato dall'applicazione.
     */
    @Bean
    @Primary
    public CacheManager cacheManager() {
        CompositeCacheManager cacheManager = new CompositeCacheManager();
        cacheManager.setCacheManagers(java.util.Arrays.asList(
                productsCacheManager(),
                companiesCacheManager(),
                eventsCacheManager(),
                packagesCacheManager(),
                usersCacheManager()));
        cacheManager.setFallbackToNoOpCache(false);

        log.info("Cache configuration initialized with multiple cache managers");

        return cacheManager;
    }
}
