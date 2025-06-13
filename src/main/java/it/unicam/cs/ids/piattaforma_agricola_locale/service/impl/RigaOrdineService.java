package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import java.util.List;
import java.util.Optional;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.Acquistabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.RigaOrdine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.RigaOrdineRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IRigaOrdineService;

public class RigaOrdineService implements IRigaOrdineService {

    private RigaOrdineRepository rigaOrdineRepository = new RigaOrdineRepository();

    @Override
    public void creaRigaOrdine(Ordine ordine, Acquistabile acquistabile, int quantita) {

        RigaOrdine nuovaRiga = new RigaOrdine( acquistabile, quantita, acquistabile.getPrezzo());
        ordine.getRigheOrdine().add(nuovaRiga);
        rigaOrdineRepository.save(nuovaRiga);
    }

    /**
     * Trova una riga ordine per ID
     * 
     * @param idRiga l'ID della riga ordine
     * @return la riga ordine se trovata
     */
    public Optional<RigaOrdine> findRigaOrdineById(Long idRiga) {
        return rigaOrdineRepository.findById(idRiga);
    }

    /**
     * Ottiene tutte le righe ordine
     * 
     * @return lista di tutte le righe ordine
     */
    public List<RigaOrdine> getTutteLeRigheOrdine() {
        return rigaOrdineRepository.findAll();
    }

    /**
     * Ottiene le righe ordine di un ordine specifico
     * 
     * @param ordine l'ordine
     * @return lista delle righe ordine dell'ordine
     */
    public List<RigaOrdine> getRigheOrdineByOrdine(Ordine ordine) {
        return rigaOrdineRepository.findByOrdine(ordine);
    }

    /**
     * Ottiene le righe ordine che contengono un acquistabile specifico
     * 
     * @param acquistabile l'acquistabile
     * @return lista delle righe ordine che contengono l'acquistabile
     */
    public List<RigaOrdine> getRigheOrdineByAcquistabile(Acquistabile acquistabile) {
        return rigaOrdineRepository.findByAcquistabile(acquistabile);
    }

    /**
     * Aggiorna una riga ordine
     * 
     * @param rigaOrdine la riga ordine da aggiornare
     */
    public void aggiornaRigaOrdine(RigaOrdine rigaOrdine) {
        rigaOrdineRepository.update(rigaOrdine);
    }

    /**
     * Elimina una riga ordine
     * 
     * @param idRiga l'ID della riga ordine da eliminare
     */
    public void eliminaRigaOrdine(Long idRiga) {
        rigaOrdineRepository.deleteById(idRiga);
    }

    /**
     * Elimina tutte le righe ordine di un ordine
     * 
     * @param ordine l'ordine
     */
    public void eliminaRigheOrdineByOrdine(Ordine ordine) {
        rigaOrdineRepository.deleteByOrdine(ordine);
    }

    /**
     * Modifica la quantità di una riga ordine
     * 
     * @param rigaOrdine    la riga ordine da modificare
     * @param nuovaQuantita la nuova quantità
     */
    public void modificaQuantitaRigaOrdine(RigaOrdine rigaOrdine, int nuovaQuantita) {
        rigaOrdine.setQuantitaOrdinata(nuovaQuantita);
        rigaOrdineRepository.update(rigaOrdine);
    }

}
