package it.unicam.cs.ids.piattaforma_agricola_locale.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.CacheManager;
import jakarta.annotation.PostConstruct;

/**
 * Configurazione delle metriche custom per il monitoraggio della piattaforma
 * agricola.
 *
 * Implementa metriche business-specific per:
 * - Performance delle operazioni principali
 * - Utilizzo delle cache
 * - Metriche di business (ordini, prodotti, utenti)
 * - Performance del database
 */
@Configuration
@Slf4j
public class MetricsConfig {

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private CacheManager cacheManager;

    // Contatori per operazioni business
    private Counter productViewCounter;
    private Counter orderCreatedCounter;
    private Counter userRegistrationCounter;
    private Counter authenticationAttemptCounter;
    private Counter authenticationSuccessCounter;
    private Counter cacheHitCounter;
    private Counter cacheMissCounter;

    // Timer per performance monitoring
    private Timer productSearchTimer;
    private Timer orderProcessingTimer;
    private Timer databaseQueryTimer;
    private Timer cacheOperationTimer;

    @PostConstruct
    public void initializeMetrics() {
        log.info("Initializing custom metrics for Piattaforma Agricola Locale");

        // Business Metrics - Contatori
        productViewCounter = Counter.builder("piattaforma.products.views")
                .description("Numero totale di visualizzazioni prodotti")
                .tag("component", "product-service")
                .register(meterRegistry);

        orderCreatedCounter = Counter.builder("piattaforma.orders.created")
                .description("Numero totale di ordini creati")
                .tag("component", "order-service")
                .register(meterRegistry);

        userRegistrationCounter = Counter.builder("piattaforma.users.registrations")
                .description("Numero totale di registrazioni utenti")
                .tag("component", "auth-service")
                .register(meterRegistry);

        authenticationAttemptCounter = Counter.builder("piattaforma.auth.attempts")
                .description("Tentativi di autenticazione totali")
                .tag("component", "auth-service")
                .register(meterRegistry);

        authenticationSuccessCounter = Counter.builder("piattaforma.auth.success")
                .description("Autenticazioni riuscite")
                .tag("component", "auth-service")
                .register(meterRegistry);

        // Cache Metrics
        cacheHitCounter = Counter.builder("piattaforma.cache.hits")
                .description("Cache hits totali")
                .tag("component", "cache-service")
                .register(meterRegistry);

        cacheMissCounter = Counter.builder("piattaforma.cache.misses")
                .description("Cache misses totali")
                .tag("component", "cache-service")
                .register(meterRegistry);

        // Performance Metrics - Timer
        productSearchTimer = Timer.builder("piattaforma.products.search.duration")
                .description("Tempo di ricerca prodotti")
                .tag("component", "product-service")
                .register(meterRegistry);

        orderProcessingTimer = Timer.builder("piattaforma.orders.processing.duration")
                .description("Tempo di elaborazione ordini")
                .tag("component", "order-service")
                .register(meterRegistry);

        databaseQueryTimer = Timer.builder("piattaforma.database.query.duration")
                .description("Tempo di esecuzione query database")
                .tag("component", "database")
                .register(meterRegistry);

        cacheOperationTimer = Timer.builder("piattaforma.cache.operation.duration")
                .description("Tempo di operazioni cache")
                .tag("component", "cache-service")
                .register(meterRegistry);

        // Gauge per metriche di stato
        setupGaugeMetrics();

        log.info("Custom metrics initialization completed");
    }

    private void setupGaugeMetrics() {
        // Gauge per cache size
        Gauge.builder("piattaforma.cache.size", () -> getCacheSize())
                .description("Dimensione totale delle cache")
                .tag("component", "cache-service")
                .register(meterRegistry);

        // Gauge per memoria heap utilizzata (%)
        Gauge.builder("piattaforma.memory.heap.usage.percent", () -> getHeapUsagePercent())
                .description("Percentuale utilizzo memoria heap")
                .tag("component", "jvm")
                .register(meterRegistry);
    }

    private double getCacheSize() {
        try {
            return cacheManager.getCacheNames().size();
        } catch (Exception e) {
            return 0.0;
        }
    }

    private double getHeapUsagePercent() {
        try {
            Runtime runtime = Runtime.getRuntime();
            long total = runtime.totalMemory();
            long free = runtime.freeMemory();
            return ((double) (total - free) / total) * 100;
        } catch (Exception e) {
            return 0.0;
        }
    }

    // Bean accessors per uso nei servizi
    @Bean
    public Counter productViewCounter() {
        return productViewCounter;
    }

    @Bean
    public Counter orderCreatedCounter() {
        return orderCreatedCounter;
    }

    @Bean
    public Counter userRegistrationCounter() {
        return userRegistrationCounter;
    }

    @Bean
    public Counter authenticationAttemptCounter() {
        return authenticationAttemptCounter;
    }

    @Bean
    public Counter authenticationSuccessCounter() {
        return authenticationSuccessCounter;
    }

    @Bean
    public Counter cacheHitCounter() {
        return cacheHitCounter;
    }

    @Bean
    public Counter cacheMissCounter() {
        return cacheMissCounter;
    }

    @Bean
    public Timer productSearchTimer() {
        return productSearchTimer;
    }

    @Bean
    public Timer orderProcessingTimer() {
        return orderProcessingTimer;
    }

    @Bean
    public Timer databaseQueryTimer() {
        return databaseQueryTimer;
    }

    @Bean
    public Timer cacheOperationTimer() {
        return cacheOperationTimer;
    }
}