package it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces;

import java.util.List;
import java.util.Optional;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.Acquistabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.RigaOrdine;

public interface IRigaOrdineService {

    void creaRigaOrdine(Ordine ordine, Acquistabile acquistabile, int quantita);

    /**
     * Trova una riga ordine per ID
     * 
     * @param idRiga l'ID della riga ordine
     * @return la riga ordine se trovata
     */
    Optional<RigaOrdine> findRigaOrdineById(Long idRiga);

    /**
     * Ottiene tutte le righe ordine
     * 
     * @return lista di tutte le righe ordine
     */
    List<RigaOrdine> getTutteLeRigheOrdine();

    /**
     * Ottiene le righe ordine di un ordine specifico
     * 
     * @param ordine l'ordine
     * @return lista delle righe ordine dell'ordine
     */
    List<RigaOrdine> getRigheOrdineByOrdine(Ordine ordine);

    /**
     * Ottiene le righe ordine che contengono un acquistabile specifico
     * 
     * @param acquistabile l'acquistabile
     * @return lista delle righe ordine che contengono l'acquistabile
     */
    List<RigaOrdine> getRigheOrdineByAcquistabile(Acquistabile acquistabile);

    /**
     * Aggiorna una riga ordine
     * 
     * @param rigaOrdine la riga ordine da aggiornare
     */
    void aggiornaRigaOrdine(RigaOrdine rigaOrdine);

    /**
     * Elimina una riga ordine
     * 
     * @param idRiga l'ID della riga ordine da eliminare
     */
    void eliminaRigaOrdine(Long idRiga);

    /**
     * Elimina tutte le righe ordine di un ordine
     * 
     * @param ordine l'ordine
     */
    void eliminaRigheOrdineByOrdine(Ordine ordine);

    /**
     * Modifica la quantità di una riga ordine
     * 
     * @param rigaOrdine    la riga ordine da modificare
     * @param nuovaQuantita la nuova quantità
     */
    void modificaQuantitaRigaOrdine(RigaOrdine rigaOrdine, int nuovaQuantita);

}
