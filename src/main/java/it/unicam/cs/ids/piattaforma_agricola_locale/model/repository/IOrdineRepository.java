package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import java.util.List;
import java.util.Optional;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.StatoCorrente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

public interface IOrdineRepository {

    /**
     * Salva un ordine nel repository
     * 
     * @param ordine l'ordine da salvare
     */
    void save(Ordine ordine);

    /**
     * Trova un ordine per ID
     * 
     * @param idOrdine l'ID dell'ordine
     * @return Optional contenente l'ordine se trovato
     */
    Optional<Ordine> findById(Long idOrdine);

    /**
     * Restituisce tutti gli ordini
     * 
     * @return lista di tutti gli ordini
     */
    List<Ordine> findAll();

    /**
     * Trova tutti gli ordini di un acquirente
     * 
     * @param acquirente l'acquirente
     * @return lista degli ordini dell'acquirente
     */
    List<Ordine> findByAcquirente(Acquirente acquirente);

    /**
     * Trova tutti gli ordini con un determinato stato
     * 
     * @param stato lo stato dell'ordine
     * @return lista degli ordini con lo stato specificato
     */
    List<Ordine> findByStato(StatoCorrente stato);

    /**
     * Trova tutti gli ordini relativi a un venditore
     * 
     * @param venditore il venditore
     * @return lista degli ordini che contengono prodotti del venditore
     */
    List<Ordine> findByVenditore(Venditore venditore);

    /**
     * Elimina un ordine per ID
     * 
     * @param idOrdine l'ID dell'ordine da eliminare
     */
    void deleteById(Long idOrdine);

    /**
     * Aggiorna un ordine esistente
     * 
     * @param ordine l'ordine da aggiornare
     */
    void update(Ordine ordine);

}
