package it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces;

import java.util.List;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

public interface ICuratoreService {

    List<DatiAzienda> getDatiAziendaInAttesaRevisione();

    void approvaDatiAzienda(Venditore venditore, String feedbackVerifica);

    void respingiDatiAzienda(Venditore venditore, String feedbackVerifica);

    void approvaProdotto(Prodotto prodotto, String feedbackVerifica);

    void respingiProdotto(Prodotto prodotto, String feedbackVerifica);


    List<Prodotto> getProdottiInAttesaRevisione();



}
