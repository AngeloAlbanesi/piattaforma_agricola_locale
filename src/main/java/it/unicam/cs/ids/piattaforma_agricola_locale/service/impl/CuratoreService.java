package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import java.util.ArrayList;
import java.util.List;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Ingrediente;
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

    public CuratoreService(IVenditoreRepository venditoreRepository, IProdottoRepository prodottoRepository, IDatiAziendaRepository datiAziendaRepository) {
        this.venditoreRepository = venditoreRepository ;
        this.prodottoRepository = prodottoRepository ;
        this.datiAziendaRepository = datiAziendaRepository ;
    }
    public CuratoreService() {
        venditoreRepository = new VenditoreRepository();
        prodottoRepository = new ProdottoRepository();
        datiAziendaRepository = new DatiAziendaRepository();
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
        }
    }

    @Override
    public void respingiProdotto(Prodotto prodotto, String feedbackVerifica) {
        if (prodotto.getStatoVerifica() == StatoVerificaValori.IN_REVISIONE) {
            prodotto.setStatoVerifica(StatoVerificaValori.RESPINTO);
            prodotto.setFeedbackVerifica(feedbackVerifica);
            prodottoRepository.save(prodotto);
        }
    }



    @Override
    public List<Prodotto> getProdottiInAttesaRevisione() {
        List<Prodotto> prodottiInAttesa = new ArrayList<>();
        for (Prodotto p : prodottoRepository.mostraTuttiIProdotti()){
            if(p.getStatoVerifica() == StatoVerificaValori.IN_REVISIONE){
                prodottiInAttesa.add(p);
            }
        }
        return prodottiInAttesa;

    }

}
