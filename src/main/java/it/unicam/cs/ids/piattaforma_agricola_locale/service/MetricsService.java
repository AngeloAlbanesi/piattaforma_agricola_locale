package it.unicam.cs.ids.piattaforma_agricola_locale.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service per gestire le metriche custom della piattaforma.
 * 
 * Questo service fornisce metodi di utilità per registrare
 * eventi business nelle metriche Micrometer in modo centralizzato.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MetricsService {

    private final Counter productViewCounter;
    private final Counter orderCreatedCounter;
    private final Counter userRegistrationCounter;
    private final Counter authenticationAttemptCounter;
    private final Counter authenticationSuccessCounter;
    private final Counter cacheHitCounter;
    private final Counter cacheMissCounter;

    private final Timer productSearchTimer;
    private final Timer orderProcessingTimer;
    private final Timer databaseQueryTimer;
    private final Timer cacheOperationTimer;

    /**
     * Registra la visualizzazione di un prodotto.
     * 
     * @param productId ID del prodotto visualizzato
     * @param category  Categoria del prodotto
     */
    public void recordProductView(Long productId, String category) {
        productViewCounter.increment();
        log.debug("Product view recorded - ID: {}, Category: {}", productId, category);
    }

    /**
     * Registra la creazione di un nuovo ordine.
     * 
     * @param orderId     ID dell'ordine creato
     * @param totalAmount Importo totale dell'ordine
     */
    public void recordOrderCreated(Long orderId, Double totalAmount) {
        orderCreatedCounter.increment();
        log.debug("Order created recorded - ID: {}, Amount: {}", orderId, totalAmount);
    }

    /**
     * Registra una nuova registrazione utente.
     * 
     * @param userEmail Email dell'utente registrato
     * @param userRole  Ruolo dell'utente
     */
    public void recordUserRegistration(String userEmail, String userRole) {
        userRegistrationCounter.increment();
        log.debug("User registration recorded - Email: {}, Role: {}", userEmail, userRole);
    }

    /**
     * Registra un tentativo di autenticazione.
     * 
     * @param userEmail Email dell'utente
     * @param success   Se l'autenticazione è riuscita
     */
    public void recordAuthenticationAttempt(String userEmail, boolean success) {
        authenticationAttemptCounter.increment();
        if (success) {
            authenticationSuccessCounter.increment();
        }
        log.debug("Authentication attempt recorded - Email: {}, Success: {}", userEmail, success);
    }

    /**
     * Registra un hit della cache.
     * 
     * @param cacheName Nome della cache
     * @param key       Chiave cercata
     */
    public void recordCacheHit(String cacheName, Object key) {
        cacheHitCounter.increment();
        log.debug("Cache hit recorded - Cache: {}, Key: {}", cacheName, key);
    }

    /**
     * Registra un miss della cache.
     * 
     * @param cacheName Nome della cache
     * @param key       Chiave cercata
     */
    public void recordCacheMiss(String cacheName, Object key) {
        cacheMissCounter.increment();
        log.debug("Cache miss recorded - Cache: {}, Key: {}", cacheName, key);
    }

    /**
     * Misura il tempo di una ricerca prodotti.
     * 
     * @param searchAction Azione di ricerca da misurare
     * @return Il risultato dell'azione
     */
    public <T> T timeProductSearch(Timer.Sample sample, java.util.function.Supplier<T> searchAction) {
        try {
            T result = searchAction.get();
            sample.stop(productSearchTimer);
            log.debug("Product search timed");
            return result;
        } catch (Exception e) {
            sample.stop(productSearchTimer);
            log.error("Product search failed", e);
            throw e;
        }
    }

    /**
     * Misura il tempo di elaborazione di un ordine.
     * 
     * @param orderAction Azione di elaborazione da misurare
     * @return Il risultato dell'azione
     */
    public <T> T timeOrderProcessing(Timer.Sample sample, java.util.function.Supplier<T> orderAction) {
        try {
            T result = orderAction.get();
            sample.stop(orderProcessingTimer);
            log.debug("Order processing timed");
            return result;
        } catch (Exception e) {
            sample.stop(orderProcessingTimer);
            log.error("Order processing failed", e);
            throw e;
        }
    }

    /**
     * Misura il tempo di una query al database.
     * 
     * @param queryAction Azione di query da misurare
     * @return Il risultato dell'azione
     */
    public <T> T timeDatabaseQuery(Timer.Sample sample, java.util.function.Supplier<T> queryAction) {
        try {
            T result = queryAction.get();
            sample.stop(databaseQueryTimer);
            log.debug("Database query timed");
            return result;
        } catch (Exception e) {
            sample.stop(databaseQueryTimer);
            log.error("Database query failed", e);
            throw e;
        }
    }

    /**
     * Misura il tempo di un'operazione cache.
     * 
     * @param cacheAction Azione cache da misurare
     * @return Il risultato dell'azione
     */
    public <T> T timeCacheOperation(Timer.Sample sample, java.util.function.Supplier<T> cacheAction) {
        try {
            T result = cacheAction.get();
            sample.stop(cacheOperationTimer);
            log.debug("Cache operation timed");
            return result;
        } catch (Exception e) {
            sample.stop(cacheOperationTimer);
            log.error("Cache operation failed", e);
            throw e;
        }
    }

    /**
     * Crea un Timer.Sample per misurare operazioni.
     * 
     * @return Un nuovo Timer.Sample
     */
    public Timer.Sample startTimer() {
        return Timer.start();
    }
}
