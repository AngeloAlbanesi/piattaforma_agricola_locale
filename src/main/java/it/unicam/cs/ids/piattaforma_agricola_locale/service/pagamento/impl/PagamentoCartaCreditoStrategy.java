package it.unicam.cs.ids.piattaforma_agricola_locale.service.pagamento.impl;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.pagamento.DatiCartaCreditoDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.pagamento.PagamentoRequestDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.pagamento.IMetodoPagamentoStrategy;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.pagamento.PagamentoException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.logging.Logger;

/**
 * Implementazione della strategia di pagamento tramite carta di credito.
 * Questa classe rappresenta lo scheletro per l'elaborazione di pagamenti
 * tramite carta di credito. Al momento logga un messaggio e restituisce
 * successo.
 * 
 */
public class PagamentoCartaCreditoStrategy implements IMetodoPagamentoStrategy {

    private static final Logger logger = Logger.getLogger(PagamentoCartaCreditoStrategy.class.getName());

    /**
     * Elabora il pagamento di un ordine tramite carta di credito (versione semplificata).
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
            logger.info(String.format("Elaborazione pagamento carta di credito per ordine ID: %s, importo: €%.2f",
                    ordine.getIdOrdine(), ordine.getImportoTotale()));

            logger.info("Pagamento carta di credito elaborato con successo (modalità semplificata)");
            return true;

        } catch (Exception e) {
            logger.severe("Errore durante l'elaborazione del pagamento carta di credito: " + e.getMessage());
            throw new PagamentoException(
                    "Errore durante l'elaborazione del pagamento carta di credito: " + e.getMessage());
        }
    }

    /**
     * Elabora il pagamento di un ordine tramite carta di credito con validazione dei dati.
     * 
     * @param ordine l'ordine da elaborare per il pagamento
     * @param datiPagamento i dati di pagamento contenenti le informazioni della carta
     * @return true se il pagamento è stato elaborato con successo
     * @throws PagamentoException se si verifica un errore durante l'elaborazione del pagamento
     */
    @Override
    public boolean elaboraPagamento(Ordine ordine, PagamentoRequestDTO datiPagamento) throws PagamentoException {
        // Validazione dell'input
        if (ordine == null) {
            throw new IllegalArgumentException("L'ordine non può essere null");
        }
        
        if (datiPagamento == null || datiPagamento.getDatiCartaCredito() == null) {
            throw new PagamentoException("I dati della carta di credito sono obbligatori");
        }

        DatiCartaCreditoDTO datiCarta = datiPagamento.getDatiCartaCredito();

        try {
            logger.info(String.format("Elaborazione pagamento carta di credito per ordine ID: %s, importo: €%.2f",
                    ordine.getIdOrdine(), ordine.getImportoTotale()));

            // Validazione dei dati della carta
            if (!validaDatiCarta(datiCarta)) {
                logger.warning("Validazione dati carta fallita per ordine ID: " + ordine.getIdOrdine());
                return false;
            }

            // Simulazione comunicazione con gateway di pagamento
            if (!simulaAutorizzazionePagamento(datiCarta, ordine.getImportoTotale())) {
                logger.warning("Autorizzazione pagamento negata per ordine ID: " + ordine.getIdOrdine());
                return false;
            }

            logger.info("Pagamento carta di credito elaborato con successo per ordine ID: " + ordine.getIdOrdine());
            return true;

        } catch (Exception e) {
            logger.severe("Errore durante l'elaborazione del pagamento carta di credito: " + e.getMessage());
            throw new PagamentoException(
                    "Errore durante l'elaborazione del pagamento carta di credito: " + e.getMessage());
        }
    }

    /**
     * Valida i dati della carta di credito
     */
    private boolean validaDatiCarta(DatiCartaCreditoDTO datiCarta) {
        try {
            // Validazione data di scadenza
            if (!validaDataScadenza(datiCarta.getDataScadenza())) {
                logger.warning("Data di scadenza non valida o carta scaduta");
                return false;
            }

            // Validazione numero carta (algoritmo di Luhn semplificato)
            if (!validaNumeroCartaLuhn(datiCarta.getNumeroCartaCredito())) {
                logger.warning("Numero carta di credito non valido");
                return false;
            }

            // Simulazione: alcune carte di test falliscono sempre
            if (datiCarta.getNumeroCartaCredito().startsWith("4000000000000002")) {
                logger.warning("Carta di test per simulare fallimento");
                return false;
            }

            return true;
        } catch (Exception e) {
            logger.warning("Errore durante la validazione dei dati carta: " + e.getMessage());
            return false;
        }
    }

    /**
     * Valida la data di scadenza della carta
     */
    private boolean validaDataScadenza(String dataScadenza) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
            String[] parti = dataScadenza.split("/");
            if (parti.length != 2) return false;
            
            int mese = Integer.parseInt(parti[0]);
            int anno = 2000 + Integer.parseInt(parti[1]);
            
            LocalDate scadenza = LocalDate.of(anno, mese, 1).plusMonths(1).minusDays(1);
            return scadenza.isAfter(LocalDate.now());
        } catch (DateTimeParseException | NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validazione semplificata del numero carta con algoritmo di Luhn
     */
    private boolean validaNumeroCartaLuhn(String numeroCartaCredito) {
        try {
            int sum = 0;
            boolean alternate = false;
            for (int i = numeroCartaCredito.length() - 1; i >= 0; i--) {
                int n = Integer.parseInt(numeroCartaCredito.substring(i, i + 1));
                if (alternate) {
                    n *= 2;
                    if (n > 9) {
                        n = (n % 10) + 1;
                    }
                }
                sum += n;
                alternate = !alternate;
            }
            return (sum % 10 == 0);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Simula l'autorizzazione del pagamento con il gateway
     */
    private boolean simulaAutorizzazionePagamento(DatiCartaCreditoDTO datiCarta, double importo) {
        // Simulazione: fondi insufficienti per importi > 1000
        if (importo > 1000.0) {
            logger.warning("Simulazione: fondi insufficienti per importo elevato");
            return false;
        }

        // Simulazione: alcune carte di test per fondi insufficienti
        if (datiCarta.getNumeroCartaCredito().startsWith("4000000000000119")) {
            logger.warning("Simulazione: carta con fondi insufficienti");
            return false;
        }

        // Simulazione successo
        logger.info("Autorizzazione pagamento simulata con successo");
        return true;
    }
}
