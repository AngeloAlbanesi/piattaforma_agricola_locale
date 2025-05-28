package it.unicam.cs.ids.piattaforma_agricola_locale.service.pagamento;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;

/**
 * Interfaccia Strategy per gestire diversi metodi di pagamento.
 * Permette di implementare diverse strategie di pagamento in modo flessibile
 * utilizzando il pattern Strategy.
 */
public interface IMetodoPagamentoStrategy {
    
    /**
     * Esegue il tentativo di pagamento per l'ordine specificato.
     * 
     * @param ordine L'ordine da pagare.
     * @return true se il pagamento è andato a buon fine, false altrimenti.
     * @throws PagamentoException se si verifica un errore durante il processo di pagamento.
     */
    boolean elaboraPagamento(Ordine ordine) throws PagamentoException;
}
