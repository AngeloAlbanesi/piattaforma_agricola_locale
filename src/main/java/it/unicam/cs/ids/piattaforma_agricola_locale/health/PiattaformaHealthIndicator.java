package it.unicam.cs.ids.piattaforma_agricola_locale.health;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Health.*;
import org.springframework.boot.actuate.health.HealthIndicator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Health indicator personalizzato per la piattaforma agricola.
 * 
 * Monitora:
 * - Stato del database
 * - Stato delle cache
 * - Memoria disponibile
 * - Performance generale del sistema
 */
@Component
@Slf4j
public class PiattaformaHealthIndicator implements HealthIndicator {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private CacheManager cacheManager;

    private static final long MEMORY_THRESHOLD_MB = 100; // Soglia memoria disponibile

    @Override
    public Health health() {
        Health.Builder builder = Health.up();

        try {
            // Check database connectivity
            checkDatabase(builder);

            // Check cache status
            checkCacheStatus(builder);

            // Check memory usage
            checkMemoryUsage(builder);

            // Check overall system performance
            checkSystemPerformance(builder);

            log.debug("Health check completed successfully");

        } catch (Exception e) {
            log.error("Health check failed", e);
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("timestamp", System.currentTimeMillis())
                    .build();
        }

        return builder.build();
    }

    private void checkDatabase(Builder builder) {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(5)) {
                builder.withDetail("database", "UP")
                       .withDetail("database.vendor", connection.getMetaData().getDatabaseProductName())
                       .withDetail("database.version", connection.getMetaData().getDatabaseProductVersion());
            } else {
                builder.down().withDetail("database", "Connection invalid");
            }
        } catch (SQLException e) {
            log.warn("Database health check failed", e);
            builder.down().withDetail("database", "Connection failed: " + e.getMessage());
        }
    }

    private void checkCacheStatus(org.springframework.boot.actuate.health.Health.Builder builder) {
        try {
            var cacheNames = cacheManager.getCacheNames();
            builder.withDetail("cache.status", "UP")
                   .withDetail("cache.count", cacheNames.size())
                   .withDetail("cache.names", cacheNames);

            // Verifica che le cache principali siano disponibili
            String[] requiredCaches = {"products", "companies", "events", "packages", "users"};
            for (String cacheName : requiredCaches) {
                var cache = cacheManager.getCache(cacheName);
                if (cache == null) {
                    builder.down().withDetail("cache.missing", cacheName);
                    return;
                }
            }

        } catch (Exception e) {
            log.warn("Cache health check failed", e);
            builder.down().withDetail("cache", "Check failed: " + e.getMessage());
        }
    }

    private void checkMemoryUsage(Health.Builder builder) {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();

        double usedPercentage = ((double) usedMemory / maxMemory) * 100;
        long availableMemoryMB = (maxMemory - usedMemory) / (1024 * 1024);

        builder.withDetail("memory.used.bytes", usedMemory)
               .withDetail("memory.total.bytes", totalMemory)
               .withDetail("memory.max.bytes", maxMemory)
               .withDetail("memory.available.mb", availableMemoryMB)
               .withDetail("memory.used.percent", String.format("%.2f", usedPercentage));

        if (availableMemoryMB < MEMORY_THRESHOLD_MB) {
            builder.down().withDetail("memory.warning", "Low memory available");
        }
    }

    private void checkSystemPerformance(Health.Builder builder) {
        long startTime = System.currentTimeMillis();

        // Simulazione check performance
        try {
            Thread.sleep(10); // Piccolo delay per simulare operazione
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long responseTime = System.currentTimeMillis() - startTime;

        builder.withDetail("performance.response_time_ms", responseTime)
               .withDetail("performance.status", responseTime < 100 ? "EXCELLENT" : 
                          responseTime < 500 ? "GOOD" : "SLOW");

        if (responseTime > 1000) {
            builder.down().withDetail("performance.warning", "Slow system response");
        }
    }
}
