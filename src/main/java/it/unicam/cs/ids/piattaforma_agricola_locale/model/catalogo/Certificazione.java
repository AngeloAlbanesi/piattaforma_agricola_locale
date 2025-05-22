package it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.ElementoVerificabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.StatoVerificaContenuto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.StatoVerificaValori;

public class Certificazione {

    private int idCertificazione;
    private String nomeCertificazione;
    private String enteRilascio;
    private Date dataRilascio;
    private Date dataScadenza;

    public Certificazione(int idCertificazione, String nomeCertificazione, String enteRilascio, Date dataRilascio,
            Date dataScadenza) {
        this.idCertificazione = idCertificazione;
        this.nomeCertificazione = nomeCertificazione;
        this.enteRilascio = enteRilascio;
        this.dataRilascio = dataRilascio;
        this.dataScadenza = dataScadenza;
    }

    public int getIdCertificazione() {
        return idCertificazione;
    }

    public void setIdCertificazione(int idCertificazione) {
        this.idCertificazione = idCertificazione;
    }

    public String getNomeCertificazione() {
        return nomeCertificazione;
    }

    public void setNomeCertificazione(String nomeCertificazione) {
        this.nomeCertificazione = nomeCertificazione;
    }

    public String getEnteRilascio() {
        return enteRilascio;
    }

    public void setEnteRilascio(String enteRilascio) {
        this.enteRilascio = enteRilascio;
    }

    public Date getDataRilascio() {
        return dataRilascio;
    }

    public void setDataRilascio(Date dataRilascio) {
        this.dataRilascio = dataRilascio;
    }

    public Date getDataScadenza() {
        return dataScadenza;
    }

    public void setDataScadenza(Date dataScadenza) {
        this.dataScadenza = dataScadenza;
    }

    public void stampaCertificazione() {
        System.out.println(
                this.getIdCertificazione() + " " + this.getNomeCertificazione() + " Ente: " + this.getEnteRilascio() +
                        " Rilasciata il: " + this.getDataRilascio() + " Scandeza: " + this.getDataScadenza());
    }

}