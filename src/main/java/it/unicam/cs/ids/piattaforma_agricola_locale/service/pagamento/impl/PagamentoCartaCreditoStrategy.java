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
     * Valida i dati della carta di credito (versione semplificata per progetto universitario)
     */
    private boolean validaDatiCarta(DatiCartaCreditoDTO datiCarta) {
        try {
            // Validazione data di scadenza
            if (!validaDataScadenza(datiCarta.getDataScadenza())) {
                logger.warning("Data di scadenza non valida o carta scaduta");
                return false;
            }

            // Validazione numero carta semplificata (solo lunghezza e cifre)
            if (!validaNumeroCartaSemplificato(datiCarta.getNumeroCartaCredito())) {
                logger.warning("Numero carta di credito non valido (deve essere 13-19 cifre)");
                return false;
            }

            // Simulazione: solo alcune carte specifiche falliscono per test
            if (datiCarta.getNumeroCartaCredito().equals("4000000000000002")) {
                logger.warning("Carta di test specifica per simulare fallimento");
                return false;
            }

            // Simulazione: carte che iniziano con "1111" falliscono
            if (datiCarta.getNumeroCartaCredito().startsWith("1111")) {
                logger.warning("Carta di test per simulare fallimento (inizia con 1111)");
                return false;
            }

            logger.info("Validazione carta completata con successo");
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
     * Validazione semplificata del numero carta (solo lunghezza e formato)
     * Versione permissiva per progetto universitario
     */
    private boolean validaNumeroCartaSemplificato(String numeroCartaCredito) {
        try {
            // Verifica che sia composto solo da cifre
            if (!numeroCartaCredito.matches("^[0-9]+$")) {
                return false;
            }
            
            // Verifica lunghezza (13-19 cifre come le carte reali)
            int lunghezza = numeroCartaCredito.length();
            if (lunghezza < 13 || lunghezza > 19) {
                return false;
            }
            
            // Per un progetto universitario, accettiamo qualsiasi numero che rispetti il formato
            logger.info(String.format("Numero carta validato: lunghezza %d cifre", lunghezza));
            return true;
            
        } catch (Exception e) {
            logger.warning("Errore nella validazione del numero carta: " + e.getMessage());
            return false;
        }
    }

    /**
     * Simula l'autorizzazione del pagamento con il gateway (versione permissiva)
     */
    private boolean simulaAutorizzazionePagamento(DatiCartaCreditoDTO datiCarta, double importo) {
        // Simulazione: fondi insufficienti solo per importi molto elevati (>10000 per test estremi)
        if (importo > 10000.0) {
            logger.warning("Simulazione: fondi insufficienti per importo estremamente elevato (>€10000)");
            return false;
        }

        // Simulazione: solo carte specifiche per test di fondi insufficienti
        if (datiCarta.getNumeroCartaCredito().equals("4000000000000119")) {
            logger.warning("Simulazione: carta specifica per test fondi insufficienti");
            return false;
        }

        // Simulazione: carte che iniziano con "9999" hanno fondi insufficienti
        if (datiCarta.getNumeroCartaCredito().startsWith("9999")) {
            logger.warning("Simulazione: carta di test per fondi insufficienti (inizia con 9999)");
            return false;
        }

        // Per progetto universitario: quasi tutti i pagamenti vanno a buon fine
        logger.info(String.format("Autorizzazione pagamento simulata con successo per importo €%.2f", importo));
        return true;
    }
}
