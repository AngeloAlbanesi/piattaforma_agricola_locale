package it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces;

import java.util.Date;
import java.util.List;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

public interface IVenditoreService {

    void aggiornaDatiAzienda(Venditore venditore,  DatiAzienda datiAggiornati);


    DatiAzienda aggiungiDatiAzienda(Venditore venditore, String nomeAzienda, String partitaIva, String indirizzoAzienda,
                                    String descrizioneAzienda, String logoUrl, String sitoWebUrl);

    Certificazione aggiungiCertificazioneAdAzienda(Venditore venditore, String nomeCertificazione, String enteRilascio, Date dataRilascio, Date dataScadenza);

    void stampaCertificazioniAzienda(Venditore venditore);

    List<Certificazione> getCertificazioniAzienda(Venditore venditore);

    boolean rimuoviCertificazioneDaAzienda(Venditore venditore, int idCertificazione);
}
