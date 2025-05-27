package it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces;

import java.util.Date;
import java.util.List;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

public interface IVenditoreService {

    void aggiornaDatiAzienda(Venditore venditore,  DatiAzienda datiAggiornati);

    void aggiungiDatiAzienda(Venditore venditore, String nomeAzienda,String partitaIva,  String indirizzoAzienda, String descrizioneAzienda,
            String logoUrl, String sitoWebUrl);

    void aggiungiCertificazioneAzienda(Venditore venditore, Certificazione certificazione);

    // List<Ordine> getOrdiniRicevuti(String idVenditore, StatoOrdineValori
    // statoFiltro);

    // boolean aggiornaStatoOrdineRicevuto(String idVenditore, String idOrdine,
    // StatoOrdineValori nuovoStato);

}
