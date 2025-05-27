package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.RigaOrdine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.StatoCorrente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.OrdineRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.RigaOrdineRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IOrdineService;

public class OrdineService implements IOrdineService {

    private OrdineRepository ordineRepository = new OrdineRepository();
    private RigaOrdineRepository rigaOrdineRepository = new RigaOrdineRepository();

    @Override
    public void creaNuovoOrdine(Acquirente acquirente) {
        int idOrdine = UUID.randomUUID().hashCode();
        Date dataOrdine = new Date(); // mette anche l'ora
        // Non è più necessario passare lo stato come parametro,
        // viene inizializzato automaticamente a "in attesa di pagamento"
        Ordine ordine = new Ordine(idOrdine, dataOrdine, acquirente);
        ordineRepository.save(ordine);
    }

    @Override
    public void calcolaPrezzoOrdine(Ordine ordine) {
        double prezzoTotOrdine = 0;
        for (RigaOrdine r : ordine.getRigheOrdine()) {
            double prezzoRiga = r.getPrezzoUnitario() * r.getQuantitaOrdinata();
            prezzoTotOrdine += prezzoRiga;
        }
        ordine.setImportoTotale(prezzoTotOrdine);
        ordineRepository.update(ordine);
    }

    /**
     * Trova un ordine per ID
     * 
     * @param idOrdine l'ID dell'ordine
     * @return l'ordine se trovato
     */
    public Optional<Ordine> findOrdineById(int idOrdine) {
        return ordineRepository.findById(idOrdine);
    }

    /**
     * Ottiene tutti gli ordini
     * 
     * @return lista di tutti gli ordini
     */
    public List<Ordine> getTuttiGliOrdini() {
        return ordineRepository.findAll();
    }

    /**
     * Ottiene gli ordini di un acquirente
     * 
     * @param acquirente l'acquirente
     * @return lista degli ordini dell'acquirente
     */
    public List<Ordine> getOrdiniAcquirente(Acquirente acquirente) {
        return ordineRepository.findByAcquirente(acquirente);
    }

    /**
     * Ottiene gli ordini per stato
     * 
     * @param stato lo stato degli ordini
     * @return lista degli ordini con lo stato specificato
     */
    public List<Ordine> getOrdiniPerStato(StatoCorrente stato) {
        return ordineRepository.findByStato(stato);
    }

    /**
     * Ottiene gli ordini relativi a un venditore
     * 
     * @param venditore il venditore
     * @return lista degli ordini che contengono prodotti del venditore
     */
    public List<Ordine> getOrdiniVenditore(Venditore venditore) {
        return ordineRepository.findByVenditore(venditore);
    }

    /**
     * Salva un ordine
     * 
     * @param ordine l'ordine da salvare
     */
    public void salvaOrdine(Ordine ordine) {
        ordineRepository.save(ordine);
        // Salva anche tutte le righe ordine
        for (RigaOrdine riga : ordine.getRigheOrdine()) {
            rigaOrdineRepository.save(riga);
        }
    }

    /**
     * Aggiorna un ordine
     * 
     * @param ordine l'ordine da aggiornare
     */
    public void aggiornaOrdine(Ordine ordine) {
        ordineRepository.update(ordine);
    }

    /**
     * Elimina un ordine
     * 
     * @param idOrdine l'ID dell'ordine da eliminare
     */
    public void eliminaOrdine(int idOrdine) {
        Optional<Ordine> ordineOpt = ordineRepository.findById(idOrdine);
        if (ordineOpt.isPresent()) {
            rigaOrdineRepository.deleteByOrdine(ordineOpt.get());
            ordineRepository.deleteById(idOrdine);
        }
    }

}
