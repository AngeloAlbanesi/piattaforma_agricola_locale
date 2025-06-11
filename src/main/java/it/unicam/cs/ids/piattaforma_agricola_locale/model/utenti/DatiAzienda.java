/*
 *   Copyright (c) 2025 
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti;

import java.util.ArrayList;
import java.util.List;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.ElementoVerificabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.StatoVerificaValori;

public class DatiAzienda implements ElementoVerificabile {
    private int idVenditore; // ID del venditore
    private String nomeAzienda;
    private String partitaIva;
    private String indirizzoAzienda;
    private String descrizioneAzienda;
    private String logoUrl;
    private String sitoWebUrl;
    private StatoVerificaValori statoVerifica;
    private String feedbackVerificaContenuto;
    private List<Certificazione> certificazioniAzienda;

    public DatiAzienda(int idVenditore,String nomeAzienda, String partitaIva, String indirizzoAzienda, String descrizioneAzienda,
            String logoUrl, String sitoWebUrl) {
        this.idVenditore = idVenditore;
        this.nomeAzienda = nomeAzienda;
        this.partitaIva = partitaIva;
        this.indirizzoAzienda = indirizzoAzienda;
        this.descrizioneAzienda = descrizioneAzienda;
        this.logoUrl = logoUrl;
        this.sitoWebUrl = sitoWebUrl;
        this.statoVerifica = StatoVerificaValori.IN_REVISIONE;
        this.certificazioniAzienda = new ArrayList<>();

    }

    public DatiAzienda(){

    }

    public int getIdAzienda() {
        return idVenditore;
    }

    public void setIdVenditore(int idVenditore) {
        this.idVenditore = idVenditore;
    }

    public String getNomeAzienda() {
        return nomeAzienda;
    }

    public void setNomeAzienda(String nomeAzienda) {
        this.nomeAzienda = nomeAzienda;
    }

    public String getPartitaIva() {
        return partitaIva;
    }

    public void setPartitaIva(String partitaIva) {
        this.partitaIva = partitaIva;
    }

    public String getIndirizzoAzienda() {
        return indirizzoAzienda;
    }

    public void setIndirizzoAzienda(String indirizzoAzienda) {
        this.indirizzoAzienda = indirizzoAzienda;
    }

    public String getDescrizioneAzienda() {
        return descrizioneAzienda;
    }

    public void setDescrizioneAzienda(String descrizioneAzienda) {
        this.descrizioneAzienda = descrizioneAzienda;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getSitoWebUrl() {
        return sitoWebUrl;
    }

    public void setSitoWebUrl(String sitoWebUrl) {
        this.sitoWebUrl = sitoWebUrl;
    }

    public List<Certificazione> getCertificazioniAzienda() {
        return certificazioniAzienda;
    }

    public void aggiungiCertificazione(Certificazione certificazione) {
        if (certificazione != null && certificazione.getIdAziendaAssociata() != null && certificazione.getIdAziendaAssociata().equals(this.idVenditore)) {
            this.certificazioniAzienda.add(certificazione);
        } else {
            // Gestire l'errore: la certificazione non appartiene a questa azienda
            System.err.println("Errore: tentativo di aggiungere certificazione non pertinente all'azienda.");
        }
    }

    @Override
    public int getId() {
        return idVenditore;
    }

    @Override
    public String getFeedbackVerifica() {
        return feedbackVerificaContenuto;
    }

    @Override
    public void setFeedbackVerifica(String feedbackVerifica) {
        this.feedbackVerificaContenuto = feedbackVerifica;
    }

    @Override
    public void setStatoVerifica(StatoVerificaValori statoVerifica) {
        this.statoVerifica = statoVerifica;
    }

    @Override
    public StatoVerificaValori getStatoVerifica() {
        return statoVerifica;
    }

}