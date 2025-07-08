/*
 *   Copyright (c) 2025 
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.service;

import it.unicam.cs.ids.piattaforma_agricola_locale.exception.ResourceOwnershipException;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.Evento;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.ProcessoTrasformazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.AnimatoreDellaFiliera;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Trasformatore;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Utente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IEventoService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IProcessoTrasformazioneService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IProdottoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for validating resource ownership.
 * Provides centralized ownership validation logic for different resource types.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OwnershipValidationService {

    private final IProdottoService prodottoService;
    private final IProcessoTrasformazioneService processoTrasformazioneService;
    private final IEventoService eventoService;

    /**
     * Validates if a user owns a specific product.
     * Cached per migliorare le performance delle validazioni frequenti.
     * 
     * @param productId The product ID
     * @param userEmail The user's email
     * @return true if the user owns the product
     * @throws ResourceOwnershipException if the user doesn't own the product
     */
    @Cacheable(value = "products", key = "#productId + '_owner_' + #userEmail")
    public boolean validateProductOwnership(Long productId, String userEmail) {
        log.debug("Validating product ownership - Product ID: {}, User: {}", productId, userEmail);

        Optional<Prodotto> prodottoOpt = prodottoService.getProdottoById(productId);
        if (prodottoOpt.isEmpty()) {
            log.warn("Product not found - ID: {}", productId);
            throw new ResourceOwnershipException("Prodotto non trovato");
        }

        Prodotto prodotto = prodottoOpt.get();
        Venditore owner = prodotto.getVenditore();

        if (!owner.getEmail().equals(userEmail)) {
            log.warn("Product ownership validation failed - Product ID: {}, Owner: {}, Requesting User: {}",
                    productId, owner.getEmail(), userEmail);
            throw new ResourceOwnershipException("Non sei autorizzato a modificare questo prodotto");
        }

        log.debug("Product ownership validated successfully - Product ID: {}, User: {}", productId, userEmail);
        return true;
    }

    /**
     * Validates if a user owns a specific transformation process.
     * Cached per migliorare le performance delle validazioni frequenti.
     * 
     * @param processId The process ID
     * @param userEmail The user's email
     * @return true if the user owns the process
     * @throws ResourceOwnershipException if the user doesn't own the process
     */
    @Cacheable(value = "users", key = "#processId + '_process_owner_' + #userEmail")
    public boolean validateProcessOwnership(Long processId, String userEmail) {
        log.debug("Validating process ownership - Process ID: {}, User: {}", processId, userEmail);

        Optional<ProcessoTrasformazione> processoOpt = processoTrasformazioneService.getProcessoById(processId);
        if (processoOpt.isEmpty()) {
            log.warn("Process not found - ID: {}", processId);
            throw new ResourceOwnershipException("Processo di trasformazione non trovato");
        }

        ProcessoTrasformazione processo = processoOpt.get();
        Trasformatore owner = processo.getTrasformatore();

        if (!owner.getEmail().equals(userEmail)) {
            log.warn("Process ownership validation failed - Process ID: {}, Owner: {}, Requesting User: {}",
                    processId, owner.getEmail(), userEmail);
            throw new ResourceOwnershipException("Non sei autorizzato a modificare questo processo di trasformazione");
        }

        log.debug("Process ownership validated successfully - Process ID: {}, User: {}", processId, userEmail);
        return true;
    }

    /**
     * Validates if a user owns a specific event.
     * Cached per migliorare le performance delle validazioni frequenti.
     * 
     * @param eventId   The event ID
     * @param userEmail The user's email
     * @return true if the user owns the event
     * @throws ResourceOwnershipException if the user doesn't own the event
     */
    @Cacheable(value = "events", key = "#eventId + '_owner_' + #userEmail")
    public boolean validateEventOwnership(Long eventId, String userEmail) {
        log.debug("Validating event ownership - Event ID: {}, User: {}", eventId, userEmail);

        Optional<Evento> eventoOpt = eventoService.getEventoById(eventId);
        if (eventoOpt.isEmpty()) {
            log.warn("Event not found - ID: {}", eventId);
            throw new ResourceOwnershipException("Evento non trovato");
        }

        Evento evento = eventoOpt.get();
        AnimatoreDellaFiliera organizer = evento.getOrganizzatore();

        if (!organizer.getEmail().equals(userEmail)) {
            log.warn("Event ownership validation failed - Event ID: {}, Organizer: {}, Requesting User: {}",
                    eventId, organizer.getEmail(), userEmail);
            throw new ResourceOwnershipException("Non sei autorizzato a modificare questo evento");
        }

        log.debug("Event ownership validated successfully - Event ID: {}, User: {}", eventId, userEmail);
        return true;
    }

    /**
     * Generic ownership validation method that can be used with SpEL expressions.
     * 
     * @param resourceId   The resource ID
     * @param userEmail    The user's email
     * @param resourceType The type of resource (product, process, event)
     * @return true if the user owns the resource
     */
    public boolean isOwner(Long resourceId, String userEmail, String resourceType) {
        switch (resourceType.toLowerCase()) {
            case "product":
                return validateProductOwnership(resourceId, userEmail);
            case "process":
                return validateProcessOwnership(resourceId, userEmail);
            case "event":
                return validateEventOwnership(resourceId, userEmail);
            default:
                log.error("Unknown resource type: {}", resourceType);
                throw new IllegalArgumentException("Tipo di risorsa non supportato: " + resourceType);
        }
    }

    /**
     * Validates if a user is the owner of a product (for SpEL usage).
     * 
     * @param productId The product ID
     * @param userEmail The user's email
     * @return true if the user owns the product
     */
    public boolean isProductOwner(Long productId, String userEmail) {
        try {
            return validateProductOwnership(productId, userEmail);
        } catch (ResourceOwnershipException e) {
            return false;
        }
    }

    /**
     * Validates if a user is the owner of a process (for SpEL usage).
     * 
     * @param processId The process ID
     * @param userEmail The user's email
     * @return true if the user owns the process
     */
    public boolean isProcessOwner(Long processId, String userEmail) {
        try {
            return validateProcessOwnership(processId, userEmail);
        } catch (ResourceOwnershipException e) {
            return false;
        }
    }

    /**
     * Validates if a user is the owner of an event (for SpEL usage).
     * 
     * @param eventId   The event ID
     * @param userEmail The user's email
     * @return true if the user owns the event
     */
    public boolean isEventOwner(Long eventId, String userEmail) {
        try {
            return validateEventOwnership(eventId, userEmail);
        } catch (ResourceOwnershipException e) {
            return false;
        }
    }
}
