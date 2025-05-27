package it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces;

import java.util.List;
import java.util.Optional;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.StatoCorrente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

public interface IOrdineService {

    void creaNuovoOrdine(Acquirente acquirente);

    void calcolaPrezzoOrdine(Ordine ordine);

    /**
     * Trova un ordine per ID
     * 
     * @param idOrdine l'ID dell'ordine
     * @return l'ordine se trovato
     */
    Optional<Ordine> findOrdineById(int idOrdine);

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
     */
    void salvaOrdine(Ordine ordine);

    /**
     * Aggiorna un ordine
     * 
     * @param ordine l'ordine da aggiornare
     */
    void aggiornaOrdine(Ordine ordine);

    /**
     * Elimina un ordine
     * 
     * @param idOrdine l'ID dell'ordine da eliminare
     */
    void eliminaOrdine(int idOrdine);
}
