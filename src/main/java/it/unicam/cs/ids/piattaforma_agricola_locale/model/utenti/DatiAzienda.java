/*
 *   Copyright (c) 2025 
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti;

import java.util.List;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.ElementoVerificabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.StatoVerificaContenuto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.StatoVerificaValori;

public class DatiAzienda implements ElementoVerificabile {
    private String nomeAzienda;
    private String partitaIva;
    private String indirizzoAzienda;
    private String descrizioneAzienda;
    private String logoUrl;
    private String sitoWebUrl;
    private String statoVerificaContenuto;
    private String feedbackVerificaContenuto;
    private List<Certificazione> certificazioniAzienda;

    public DatiAzienda(String nomeAzienda, String partitaIva, String indirizzoAzienda, String descrizioneAzienda,
            String logoUrl, String sitoWebUrl, String statoVerificaContenuto, String feedbackVerificaContenuto,
            List<Certificazione> certificazioniAzienda) {
        this.nomeAzienda = nomeAzienda;
        this.partitaIva = partitaIva;
        this.indirizzoAzienda = indirizzoAzienda;
        this.descrizioneAzienda = descrizioneAzienda;
        this.logoUrl = logoUrl;
        this.sitoWebUrl = sitoWebUrl;
        this.statoVerificaContenuto = statoVerificaContenuto;
        this.feedbackVerificaContenuto = feedbackVerificaContenuto;
        this.certificazioniAzienda = certificazioniAzienda;
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

    public String getStatoVerificaContenuto() {
        return statoVerificaContenuto;
    }

    public void setStatoVerificaContenuto(String statoVerificaContenuto) {
        this.statoVerificaContenuto = statoVerificaContenuto;
    }

    public String getFeedbackVerificaContenuto() {
        return feedbackVerificaContenuto;
    }

    public void setFeedbackVerificaContenuto(String feedbackVerificaContenuto) {
        this.feedbackVerificaContenuto = feedbackVerificaContenuto;
    }

    public List<Certificazione> getCertificazioniAzienda() {
        return certificazioniAzienda;
    }

    public void setCertificazioniAzienda(List<Certificazione> certificazioniAzienda) {
        this.certificazioniAzienda = certificazioniAzienda;
    }
   
    @Override
    public String getIdElemento() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getIdElemento'");
    }
    @Override
    public String getFeedbackVerifica() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getFeedbackVerifica'");
    }
    @Override
    public void setFeedbackVerifica(String feedbackVerifica) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setFeedbackVerifica'");
    }
    @Override
    public void setStatoVerifica(StatoVerificaValori statoVerifica) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setStatoVerifica'");
    }
    @Override
    public StatoVerificaValori getStatoVerifica() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getStatoVerifica'");
    }

}