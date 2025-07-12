package it.unicam.cs.ids.piattaforma_agricola_locale.service.pagamento.impl;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.pagamento.DatiPayPalDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.pagamento.PagamentoRequestDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.pagamento.IMetodoPagamentoStrategy;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.pagamento.PagamentoException;
import java.util.logging.Logger;

/**
 * Implementazione della strategia di pagamento tramite PayPal.
 * Questa classe rappresenta lo scheletro per l'elaborazione di pagamenti
 * tramite PayPal. Al momento logga un messaggio e restituisce successo.
 * 
 */
public class PagamentoPayPalStrategy implements IMetodoPagamentoStrategy {

    private static final Logger logger = Logger.getLogger(PagamentoPayPalStrategy.class.getName());

    /**
     * Elabora il pagamento di un ordine tramite PayPal (versione semplificata).
     * 
     * @param ordine l'ordine da elaborare per il pagamento
     * @return true se il pagamento è stato elaborato con successo
     * @throws PagamentoException       se si verifica un errore durante
     *                                  l'elaborazione del pagamento
     * @throws IllegalArgumentException se l'ordine è null
     */
    @Override
    public boolean elaboraPagamento(Ordine ordine) throws PagamentoException {
        // Validazione dell'input
        if (ordine == null) {
            throw new IllegalArgumentException("L'ordine non può essere null");
        }

        try {
            // Log dell'operazione di pagamento
            logger.info(String.format("Elaborazione pagamento PayPal per ordine ID: %s, importo: €%.2f",
                    ordine.getIdOrdine(), ordine.getImportoTotale()));

            logger.info("Pagamento PayPal elaborato con successo (modalità semplificata)");
            return true;

        } catch (Exception e) {
            logger.severe("Errore durante l'elaborazione del pagamento PayPal: " + e.getMessage());
            throw new PagamentoException("Errore durante l'elaborazione del pagamento PayPal: " + e.getMessage());
        }
    }

    /**
     * Elabora il pagamento di un ordine tramite PayPal con validazione dei dati.
     * 
     * @param ordine l'ordine da elaborare per il pagamento
     * @param datiPagamento i dati di pagamento contenenti le credenziali PayPal
     * @return true se il pagamento è stato elaborato con successo
     * @throws PagamentoException se si verifica un errore durante l'elaborazione del pagamento
     */
    @Override
    public boolean elaboraPagamento(Ordine ordine, PagamentoRequestDTO datiPagamento) throws PagamentoException {
        // Validazione dell'input
        if (ordine == null) {
            throw new IllegalArgumentException("L'ordine non può essere null");
        }
        
        if (datiPagamento == null || datiPagamento.getDatiPayPal() == null) {
            throw new PagamentoException("I dati PayPal sono obbligatori");
        }

        DatiPayPalDTO datiPayPal = datiPagamento.getDatiPayPal();

        try {
            logger.info(String.format("Elaborazione pagamento PayPal per ordine ID: %s, importo: €%.2f",
                    ordine.getIdOrdine(), ordine.getImportoTotale()));

            // Validazione credenziali PayPal
            if (!validaCredenzialiPayPal(datiPayPal)) {
                logger.warning("Credenziali PayPal non valide per ordine ID: " + ordine.getIdOrdine());
                return false;
            }

            // Simulazione autenticazione con API PayPal
            if (!simulaAutenticazionePayPal(datiPayPal)) {
                logger.warning("Autenticazione PayPal fallita per ordine ID: " + ordine.getIdOrdine());
                return false;
            }

            // Simulazione elaborazione pagamento
            if (!simulaElaborazionePagamentoPayPal(ordine.getImportoTotale())) {
                logger.warning("Elaborazione pagamento PayPal fallita per ordine ID: " + ordine.getIdOrdine());
                return false;
            }

            logger.info("Pagamento PayPal elaborato con successo per ordine ID: " + ordine.getIdOrdine());
            return true;

        } catch (Exception e) {
            logger.severe("Errore durante l'elaborazione del pagamento PayPal: " + e.getMessage());
            throw new PagamentoException("Errore durante l'elaborazione del pagamento PayPal: " + e.getMessage());
        }
    }

    /**
     * Valida le credenziali PayPal (versione permissiva per progetto universitario)
     */
    private boolean validaCredenzialiPayPal(DatiPayPalDTO datiPayPal) {
        try {
            // Validazione formato email molto semplificata
            if (!datiPayPal.getEmailPayPal().contains("@")) {
                logger.warning("Email PayPal deve contenere almeno il simbolo @");
                return false;
            }

            // Validazione password molto permissiva (lunghezza minima 1)
            if (datiPayPal.getPasswordPayPal() == null || datiPayPal.getPasswordPayPal().trim().isEmpty()) {
                logger.warning("Password PayPal non può essere vuota");
                return false;
            }

            // Simulazione: solo email specifiche falliscono per test
            if (datiPayPal.getEmailPayPal().equals("test.fail@paypal.com")) {
                logger.warning("Email PayPal specifica per test fallimento");
                return false;
            }

            // Simulazione: email che iniziano con "fail" falliscono
            if (datiPayPal.getEmailPayPal().startsWith("fail")) {
                logger.warning("Email PayPal di test per simulare fallimento (inizia con 'fail')");
                return false;
            }

            logger.info("Credenziali PayPal validate con successo");
            return true;
        } catch (Exception e) {
            logger.warning("Errore durante la validazione credenziali PayPal: " + e.getMessage());
            return false;
        }
    }

    /**
     * Simula l'autenticazione con le API PayPal (versione permissiva)
     */
    private boolean simulaAutenticazionePayPal(DatiPayPalDTO datiPayPal) {
        // Simulazione: solo account specifici sono sospesi per test
        if (datiPayPal.getEmailPayPal().equals("suspended@paypal.com")) {
            logger.warning("Simulazione: account PayPal specifico sospeso per test");
            return false;
        }

        // Simulazione: solo password specifica fallisce per test
        if (datiPayPal.getPasswordPayPal().equals("FAIL_TEST")) {
            logger.warning("Simulazione: password specifica per test fallimento");
            return false;
        }

        // Simulazione: email che contengono "blocked" sono bloccate
        if (datiPayPal.getEmailPayPal().toLowerCase().contains("blocked")) {
            logger.warning("Simulazione: account PayPal bloccato per test");
            return false;
        }

        logger.info("Autenticazione PayPal simulata con successo");
        return true;
    }

    /**
     * Simula l'elaborazione del pagamento PayPal (versione permissiva)
     */
    private boolean simulaElaborazionePagamentoPayPal(double importo) {
        // Simulazione: limite di spesa molto alto per progetto universitario
        if (importo > 10000.0) {
            logger.warning("Simulazione: limite di spesa PayPal superato (>€10000)");
            return false;
        }

        // Rimuoviamo il fallimento casuale per rendere i test più prevedibili
        // Per progetto universitario: quasi tutti i pagamenti vanno a buon fine
        
        logger.info(String.format("Elaborazione pagamento PayPal simulata con successo per importo €%.2f", importo));
        return true;
    }
}
