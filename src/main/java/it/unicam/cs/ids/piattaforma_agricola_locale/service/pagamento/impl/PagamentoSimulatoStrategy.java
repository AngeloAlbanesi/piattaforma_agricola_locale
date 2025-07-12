package it.unicam.cs.ids.piattaforma_agricola_locale.service.pagamento.impl;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.pagamento.PagamentoRequestDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.pagamento.IMetodoPagamentoStrategy;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.pagamento.PagamentoException;

/**
 * Implementazione simulata della strategia di pagamento.
 * Questa classe simula il processo di elaborazione del pagamento
 * stampando i dettagli dell'ordine e restituendo sempre successo.
 * 
 */
public class PagamentoSimulatoStrategy implements IMetodoPagamentoStrategy {
    
    private final boolean simulaSuccesso;
    
    /**
     * Costruttore di default che simula sempre un pagamento riuscito.
     */
    public PagamentoSimulatoStrategy() {
        this.simulaSuccesso = true;
    }
    
    /**
     * Costruttore che permette di specificare se simulare successo o fallimento.
     *
     * @param simulaSuccesso true per simulare successo, false per simulare fallimento
     */
    public PagamentoSimulatoStrategy(boolean simulaSuccesso) {
        this.simulaSuccesso = simulaSuccesso;
    }

    /**
     * Elabora il pagamento di un ordine in modalità simulata.
     * Il metodo stampa i dettagli dell'ordine e simula sempre
     * un pagamento riuscito.
     * 
     * @param ordine l'ordine da elaborare per il pagamento
     * @return true se il pagamento è stato elaborato con successo (sempre true in
     *         questa simulazione)
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
            // Stampa i dettagli dell'ordine per simulare l'elaborazione
            System.out.println("=== SIMULAZIONE PAGAMENTO ===");
            System.out.println("ID Ordine: " + ordine.getIdOrdine());
            System.out.println("Importo Totale: €" + ordine.getImportoTotale());
            
            if (simulaSuccesso) {
                System.out.println("Stato: Pagamento elaborato con successo (SIMULATO)");
                System.out.println("=============================");
                return true;
            } else {
                System.out.println("Stato: Pagamento fallito (SIMULATO)");
                System.out.println("=============================");
                return false;
            }

        } catch (Exception e) {
            // Gestisce eventuali errori imprevisti durante la simulazione
            throw new PagamentoException("Errore durante la simulazione del pagamento: " + e.getMessage());
        }
    }

    /**
     * Elabora il pagamento di un ordine in modalità simulata con dati di pagamento.
     * Ignora i dati specifici e usa la logica di simulazione configurata.
     * 
     * @param ordine l'ordine da elaborare per il pagamento
     * @param datiPagamento i dati di pagamento (ignorati in modalità simulata)
     * @return true se il pagamento è stato elaborato con successo
     * @throws PagamentoException se si verifica un errore durante l'elaborazione del pagamento
     */
    @Override
    public boolean elaboraPagamento(Ordine ordine, PagamentoRequestDTO datiPagamento) throws PagamentoException {
        // In modalità simulata, ignoriamo i dati specifici e usiamo la logica base
        return elaboraPagamento(ordine);
    }
}
