package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import java.util.List;
import java.util.Optional;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.Acquistabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.RigaOrdine;

public interface IRigaOrdineRepository {

    /**
     * Salva una riga ordine nel repository
     * 
     * @param rigaOrdine la riga ordine da salvare
     */
    void save(RigaOrdine rigaOrdine);

    /**
     * Trova una riga ordine per ID
     * 
     * @param idRiga l'ID della riga ordine
     * @return Optional contenente la riga ordine se trovata
     */
    Optional<RigaOrdine> findById(Long idRiga);

    /**
     * Restituisce tutte le righe ordine
     * 
     * @return lista di tutte le righe ordine
     */
    List<RigaOrdine> findAll();

    /**
     * Trova tutte le righe ordine di un ordine specifico
     * 
     * @param ordine l'ordine
     * @return lista delle righe ordine dell'ordine
     */
    List<RigaOrdine> findByOrdine(Ordine ordine);

    /**
     * Trova tutte le righe ordine che contengono un acquistabile specifico
     * 
     * @param acquistabile l'acquistabile
     * @return lista delle righe ordine che contengono l'acquistabile
     */
    List<RigaOrdine> findByAcquistabile(Acquistabile acquistabile);

    /**
     * Elimina una riga ordine per ID
     * 
     * @param idRiga l'ID della riga ordine da eliminare
     */
    void deleteById(Long idRiga);

    /**
     * Elimina tutte le righe ordine di un ordine specifico
     * 
     * @param ordine l'ordine
     */
    void deleteByOrdine(Ordine ordine);

    /**
     * Aggiorna una riga ordine esistente
     * 
     * @param rigaOrdine la riga ordine da aggiornare
     */
    void update(RigaOrdine rigaOrdine);

}
