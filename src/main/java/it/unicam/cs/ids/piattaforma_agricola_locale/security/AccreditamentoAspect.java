/*
 *   Copyright (c) 2025 
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.security;

import it.unicam.cs.ids.piattaforma_agricola_locale.exception.UtenteNonAccreditatoException;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.AnimatoreDellaFiliera;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Curatore;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.StatoAccreditamento;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Utente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IUtenteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Aspect che gestisce il controllo dell'accreditamento per i metodi annotati
 * con @RequiresAccreditation.
 * 
 * Verifica che l'utente autenticato sia accreditato (stato ACCREDITATO)
 * per eseguire l'operazione richiesta.
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AccreditamentoAspect {

    private final IUtenteService utenteService;

    /**
     * Intercetta tutti i metodi annotati con @RequiresAccreditation
     * e verifica lo stato di accreditamento dell'utente.
     */
    @Around("@annotation(requiresAccreditation)")
    public Object checkAccreditation(ProceedingJoinPoint joinPoint, RequiresAccreditation requiresAccreditation)
            throws Throwable {
        log.debug("Checking accreditation for method: {}", joinPoint.getSignature().getName());

        // Ottieni l'autenticazione corrente
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("No authenticated user found for accreditation check");
            throw new UtenteNonAccreditatoException("UNKNOWN", "NOT_AUTHENTICATED",
                    "Utente non autenticato");
        }

        String userEmail = authentication.getName();
        log.debug("Checking accreditation for user: {}", userEmail);

        // Recupera l'utente dal database
        Utente utente = utenteService.trovaUtentePerEmail(userEmail)
                .orElseThrow(() -> {
                    log.error("User not found in database: {}", userEmail);
                    return new UtenteNonAccreditatoException("UNKNOWN", "NOT_FOUND",
                            "Utente non trovato nel sistema");
                });

        // Verifica se l'utente ha bisogno di accreditamento
        StatoAccreditamento statoAccreditamento = getStatoAccreditamento(utente);

        if (statoAccreditamento == null) {
            // L'utente non ha bisogno di accreditamento (es. ACQUIRENTE)
            log.debug("User {} does not require accreditation", userEmail);
            return joinPoint.proceed();
        }

        // Verifica che l'utente sia accreditato
        if (statoAccreditamento != StatoAccreditamento.ACCREDITATO) {
            String tipoUtente = utente.getClass().getSimpleName();
            log.warn("User {} of type {} is not accredited. Status: {}",
                    userEmail, tipoUtente, statoAccreditamento);

            String message = requiresAccreditation.message().isEmpty()
                    ? null
                    : requiresAccreditation.message();

            throw new UtenteNonAccreditatoException(tipoUtente, statoAccreditamento.toString(), message);
        }

        log.debug("User {} is properly accredited. Proceeding with method execution", userEmail);

        // L'utente è accreditato, procedi con l'esecuzione del metodo
        return joinPoint.proceed();
    }

    /**
     * Estrae lo stato di accreditamento dall'utente.
     * Restituisce null se l'utente non ha bisogno di accreditamento (es.
     * Acquirente).
     */
    private StatoAccreditamento getStatoAccreditamento(Utente utente) {
        if (utente instanceof Venditore) {
            return ((Venditore) utente).getStatoAccreditamento();
        } else if (utente instanceof Curatore) {
            return ((Curatore) utente).getStatoAccreditamento();
        } else if (utente instanceof AnimatoreDellaFiliera) {
            return ((AnimatoreDellaFiliera) utente).getStatoAccreditamento();
        }

        // Altri tipi di utenti (es. Acquirente) non hanno bisogno di accreditamento
        return null;
    }
}
