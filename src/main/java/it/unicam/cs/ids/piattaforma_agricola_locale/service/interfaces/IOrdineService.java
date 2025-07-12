package it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces;

import java.util.List;
import java.util.Optional;

import it.unicam.cs.ids.piattaforma_agricola_locale.exception.OrdineException;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.StatoCorrente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.pagamento.IMetodoPagamentoStrategy;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.pagamento.PagamentoException;

public interface IOrdineService {

    void creaNuovoOrdine(Acquirente acquirente) throws OrdineException;

    void calcolaPrezzoOrdine(Ordine ordine) throws OrdineException;

    /**
     * Trova un ordine per ID
     * 
     * @param idOrdine l'ID dell'ordine
     * @return l'ordine se trovato
     */
    Optional<Ordine> findOrdineById(Long idOrdine);

    /**
     * Ottiene tutti gli ordini
     * 
     * @return lista di tutti gli ordini
     */
    List<Ordine> getTuttiGliOrdini();

    /**
     * Ottiene gli ordini di un acquirente
     * 
     * @param acquirente l'acquirente
     * @return lista degli ordini dell'acquirente
     */
    List<Ordine> getOrdiniAcquirente(Acquirente acquirente);

    /**
     * Ottiene gli ordini per stato
     * 
     * @param stato lo stato degli ordini
     * @return lista degli ordini con lo stato specificato
     */
    List<Ordine> getOrdiniPerStato(StatoCorrente stato);

    /**
     * Ottiene gli ordini relativi a un venditore
     * 
     * @param venditore il venditore
     * @return lista degli ordini che contengono prodotti del venditore
     */
    List<Ordine> getOrdiniVenditore(Venditore venditore);

    /**
     * Salva un ordine
     * 
     * @param ordine l'ordine da salvare
     * @throws OrdineException se si verifica un errore durante il salvataggio
     */
    void salvaOrdine(Ordine ordine) throws OrdineException;

    /**
     * Aggiorna un ordine
     * 
     * @param ordine l'ordine da aggiornare
     * @throws OrdineException se si verifica un errore durante l'aggiornamento
     */
    void aggiornaOrdine(Ordine ordine) throws OrdineException;

    /**
     * Elimina un ordine
     * 
     * @param idOrdine l'ID dell'ordine da eliminare
     * @throws OrdineException se si verifica un errore durante l'eliminazione
     */
    void eliminaOrdine(Long idOrdine) throws OrdineException;

    /**
     * Crea un ordine dal carrello dell'acquirente
     * 
     * @param acquirente l'acquirente di cui si vuole convertire il carrello in
     *                   ordine
     * @return l'ordine creato
     * @throws it.unicam.cs.ids.piattaforma_agricola_locale.exception.CarrelloVuotoException                    se
     *                                                                                                          il
     *                                                                                                          carrello
     *                                                                                                          è
     *                                                                                                          vuoto
     * @throws it.unicam.cs.ids.piattaforma_agricola_locale.exception.QuantitaNonDisponibileAlCheckoutException se
     *                                                                                                          la
     *                                                                                                          quantità
     *                                                                                                          richiesta
     *                                                                                                          non
     *                                                                                                          è
     *                                                                                                          disponibile
     * @throws OrdineException                                                                                  se
     *                                                                                                          si
     *                                                                                                          verifica
     *                                                                                                          un
     *                                                                                                          errore
     *                                                                                                          generico
     *                                                                                                          durante
     *                                                                                                          la
     *                                                                                                          creazione
     *                                                                                                          dell'ordine
     *                                                                                                          dell'ordine
     */
    @Deprecated
    Ordine creaOrdineDaCarrello(Acquirente acquirente) throws OrdineException;

    /**
     * NUOVO: Crea ordini separati per ogni venditore dal carrello dell'acquirente
     * 
     * @param acquirente l'acquirente di cui si vuole convertire il carrello in
     *                   ordini
     * @return lista degli ordini creati, uno per ogni venditore
     * @throws it.unicam.cs.ids.piattaforma_agricola_locale.exception.CarrelloVuotoException                    se
     *                                                                                                          il
     *                                                                                                          carrello
     *                                                                                                          è
     *                                                                                                          vuoto
     * @throws it.unicam.cs.ids.piattaforma_agricola_locale.exception.QuantitaNonDisponibileAlCheckoutException se
     *                                                                                                          la
     *                                                                                                          quantità
     *                                                                                                          richiesta
     *                                                                                                          non
     *                                                                                                          è
     *                                                                                                          disponibile
     * @throws OrdineException                                                                                  se
     *                                                                                                          si
     *                                                                                                          verifica
     *                                                                                                          un
     *                                                                                                          errore
     *                                                                                                          durante
     *                                                                                                          la
     *                                                                                                          creazione
     *                                                                                                          degli
     *                                                                                                          ordini
     */
    List<Ordine> creaOrdiniDaCarrello(Acquirente acquirente) throws OrdineException;

    /**
     * Conferma il pagamento di un ordine utilizzando la strategia di pagamento
     * specificata
     * e gestisce la transizione di stato. Questo metodo attiva il pattern Observer
     * quando
     * l'ordine transisce allo stato PRONTO_PER_LAVORAZIONE.
     *
     * @param ordine             l'ordine di cui confermare il pagamento
     * @param strategiaPagamento la strategia di pagamento da utilizzare
     * @throws OrdineException    se si verifica un errore durante la conferma del
     *                            pagamento
     * @throws PagamentoException se si verifica un errore durante l'elaborazione
     *                            del pagamento
     */
    void confermaPagamento(Ordine ordine, IMetodoPagamentoStrategy strategiaPagamento)
            throws OrdineException, PagamentoException;

    /**
     * Avanza lo stato di un ordine al prossimo stato logico
     * 
     * @param ordine    l'ordine di cui avanzare lo stato
     * @param venditore il venditore che richiede l'avanzamento (per controllo
     *                  autorizzazioni)
     * @throws OrdineException       se si verifica un errore durante l'avanzamento
     * @throws IllegalStateException se l'ordine non può avanzare dal suo stato
     *                               attuale
     */
    Ordine avanzaStatoOrdine(Ordine ordine, Venditore venditore)
            throws OrdineException, IllegalStateException;
}
