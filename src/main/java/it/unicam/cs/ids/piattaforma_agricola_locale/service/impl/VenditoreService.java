package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import java.util.Date;
import java.util.UUID;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IVenditoreService;

public class VenditoreService implements IVenditoreService {

    @Override
    public void aggiornaDatiAzienda(Venditore venditore, DatiAzienda datiAggiornati) {

        venditore.getDatiAzienda().setNomeAzienda(datiAggiornati.getNomeAzienda());
        venditore.getDatiAzienda().setDescrizioneAzienda(datiAggiornati.getDescrizioneAzienda());
        venditore.getDatiAzienda().setIndirizzoAzienda(datiAggiornati.getIndirizzoAzienda());
        venditore.getDatiAzienda().setLogoUrl(datiAggiornati.getLogoUrl());
        venditore.getDatiAzienda().setSitoWebUrl(datiAggiornati.getSitoWebUrl());

    }

    @Override
    public void aggiungiDatiAzienda(Venditore venditore, String nomeAzienda, String partitaIva, String indirizzoAzienda,
            String descrizioneAzienda, String logoUrl, String sitoWebUrl) {
        DatiAzienda datiAzienda = new DatiAzienda(nomeAzienda, partitaIva, indirizzoAzienda, descrizioneAzienda,
                logoUrl, sitoWebUrl);
        venditore.setDatiAzienda(datiAzienda);
    }

    @Override
    public void aggiungiCertificazioneAzienda(Venditore venditore, String nomeCertificazione, String enteRilascio,
            Date dataRilascio,
            Date dataScadenza) {
        int idCertificazione = UUID.randomUUID().hashCode();
        Certificazione certificazione = new Certificazione(idCertificazione, nomeCertificazione, enteRilascio,
                dataRilascio, dataScadenza);
        venditore.getDatiAzienda().getCertificazioniAzienda().add(certificazione);

    }
}
