package it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo;

import java.util.Date;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.ElementoVerificabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.StatoVerificaContenuto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.StatoVerificaValori;

public class Certificazione implements ElementoVerificabile {
    private String idCertificazione;
    private String nomeCertificazione;
    private String enteRilascio;
    private Date dataRilascio;
    private Date dataScadenza;
    private StatoVerificaValori statoVerifica;
    private String feedbackVerifica;

    public Certificazione(String idCertificazione, String nomeCertificazione, String enteRilascio, Date dataRilascio,
            Date dataScadenza) {
        this.idCertificazione = idCertificazione;
        this.nomeCertificazione = nomeCertificazione;
        this.enteRilascio = enteRilascio;
        this.dataRilascio = dataRilascio;
        this.dataScadenza = dataScadenza;
        this.statoVerifica = StatoVerificaValori.IN_REVISIONE;
    }

    public StatoVerificaValori getStatoVerifica() {
        return statoVerifica;
    }

    public String getIdCertificazione() {
        return idCertificazione;
    }

    public String getFeedbackVerifica() {
        return feedbackVerifica;
    }

    public void setFeedbackVerifica(String feedbackVerifica) {
        this.feedbackVerifica = feedbackVerifica;
    }

    @Override
    public String getIdElemento() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getIdElemento'");
    }

    @Override
    public void setStatoVerifica(StatoVerificaValori statoVerifica) {
        this.statoVerifica = statoVerifica;
    }

}