package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.StatoVerificaValori;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.*;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.ICuratoreService;

public class CuratoreService implements ICuratoreService {
   

    private final IVenditoreRepository venditoreRepository ;
    private final IProdottoRepository prodottoRepository ;
    private final IDatiAziendaRepository datiAziendaRepository ;
    
    // Coda interna per gestire i prodotti in attesa di revisione
    private final Queue<Prodotto> codaRevisioneProdotti;

    public CuratoreService(IVenditoreRepository venditoreRepository, IProdottoRepository prodottoRepository, IDatiAziendaRepository datiAziendaRepository) {
        this.venditoreRepository = venditoreRepository ;
        this.prodottoRepository = prodottoRepository ;
        this.datiAziendaRepository = datiAziendaRepository ;
        this.codaRevisioneProdotti = new LinkedList<>();
    }

    public CuratoreService() {
        venditoreRepository = new VenditoreRepository();
        prodottoRepository = new ProdottoRepository();
        datiAziendaRepository = new DatiAziendaRepository();
        this.codaRevisioneProdotti = new LinkedList<>();
    }

    @Override
    public List<DatiAzienda> getDatiAziendaInAttesaRevisione() {
        List<DatiAzienda> datiAziendaInAttesa = new ArrayList<>();
        for (Venditore venditore : venditoreRepository.findAll()) {
            if (venditore.getDatiAzienda().getStatoVerifica() == StatoVerificaValori.IN_REVISIONE) {
                datiAziendaInAttesa.add(venditore.getDatiAzienda());
            }
        }
        return datiAziendaInAttesa;
    }

    @Override
    public void approvaDatiAzienda(Venditore venditore, String feedbackVerifica) {

        DatiAzienda datiAzienda = venditore.getDatiAzienda();
        if (datiAzienda != null && datiAzienda.getStatoVerifica() == StatoVerificaValori.IN_REVISIONE) {
            datiAzienda.setStatoVerifica(StatoVerificaValori.APPROVATO);
            datiAzienda.setFeedbackVerifica(feedbackVerifica);

            datiAziendaRepository.save(datiAzienda);
        }
    }


    @Override
    public void respingiDatiAzienda(Venditore venditore, String feedbackVerifica) {

        DatiAzienda datiAzienda = venditore.getDatiAzienda();
        if (datiAzienda != null && datiAzienda.getStatoVerifica() == StatoVerificaValori.IN_REVISIONE) {
            datiAzienda.setStatoVerifica(StatoVerificaValori.RESPINTO);
            datiAzienda.setFeedbackVerifica(feedbackVerifica);

            datiAziendaRepository.save(datiAzienda);
        }
    }

    @Override
    public void approvaProdotto(Prodotto prodotto, String feedbackVerifica) {
        if (prodotto.getStatoVerifica() == StatoVerificaValori.IN_REVISIONE) {
            prodotto.setStatoVerifica(StatoVerificaValori.APPROVATO);
            prodotto.setFeedbackVerifica(feedbackVerifica);
            prodottoRepository.save(prodotto);
            
            // Rimuove il prodotto dalla coda interna poiché non è più in revisione
            codaRevisioneProdotti.remove(prodotto);
        }
    }

    @Override
    public void respingiProdotto(Prodotto prodotto, String feedbackVerifica) {
        if (prodotto.getStatoVerifica() == StatoVerificaValori.IN_REVISIONE) {
            prodotto.setStatoVerifica(StatoVerificaValori.RESPINTO);
            prodotto.setFeedbackVerifica(feedbackVerifica);
            prodottoRepository.save(prodotto);
            
            // Rimuove il prodotto dalla coda interna poiché non è più in revisione
            codaRevisioneProdotti.remove(prodotto);
        }
    }

    @Override
    public List<Prodotto> getProdottiInAttesaRevisione() {
        
        List<Prodotto> prodottiInAttesa = new ArrayList<>();
        
        // Sincronizza la coda interna con lo stato attuale dei prodotti
        sincronizzaCodeDiRevisione();
        
        // Restituisce una copia della coda per evitare modifiche esterne
        prodottiInAttesa.addAll(codaRevisioneProdotti);
        
        return prodottiInAttesa;
    }
    
    /**
     * Aggiunge un prodotto alla coda di revisione interna.
     * Questo metodo viene chiamato automaticamente dall'observer quando viene creato un nuovo prodotto.
     * 
     * @param prodotto il prodotto da aggiungere alla coda di revisione
     * @throws IllegalArgumentException se il prodotto è null o non è in stato IN_REVISIONE
     */
    public void aggiungiProdottoALlaCodeDiRevisione(Prodotto prodotto) {
        if (prodotto == null) {
            throw new IllegalArgumentException("Il prodotto non può essere null");
        }
        
        if (prodotto.getStatoVerifica() != StatoVerificaValori.IN_REVISIONE) {
            throw new IllegalArgumentException("Solo i prodotti in stato IN_REVISIONE possono essere aggiunti alla coda");
        }
        
        // Aggiunge solo se non è già presente nella coda
        if (!codaRevisioneProdotti.contains(prodotto)) {
            codaRevisioneProdotti.offer(prodotto);
        }
    }
    
    /**
     * Sincronizza la coda interna con lo stato attuale dei prodotti nel repository.
     * Rimuove i prodotti che non sono più in stato IN_REVISIONE e aggiunge quelli nuovi.
     * Questo metodo mantiene la compatibilità con il sistema esistente.
     */
    private void sincronizzaCodeDiRevisione() {
        // Rimuove dalla coda i prodotti che non sono più in stato IN_REVISIONE
        codaRevisioneProdotti.removeIf(prodotto -> 
            prodotto.getStatoVerifica() != StatoVerificaValori.IN_REVISIONE);
        
        // Controlla se ci sono prodotti IN_REVISIONE nel repository che non sono nella coda
        // (per compatibilità con prodotti creati prima dell'implementazione del pattern Observer)
        for (Prodotto p : prodottoRepository.mostraTuttiIProdotti()) {
            if (p.getStatoVerifica() == StatoVerificaValori.IN_REVISIONE 
                && !codaRevisioneProdotti.contains(p)) {
                codaRevisioneProdotti.offer(p);
            }
        }
    }

}
