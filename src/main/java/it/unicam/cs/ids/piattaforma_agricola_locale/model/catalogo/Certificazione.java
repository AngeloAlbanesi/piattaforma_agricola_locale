package it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Certificazione {

    private int idCertificazione; // Deve essere univoco globalmente
    private String nomeCertificazione;
    private String enteRilascio;
    private Date dataRilascio;
    private Date dataScadenza;

    // Chiavi esterne: solo una sarà valorizzata
    private Integer idProdottoAssociato; // ID del prodotto a cui è associata
    private Integer idAziendaAssociata;  // ID dell'azienda a cui è associata

    // Costruttore per certificazione di prodotto
    public Certificazione(int idCertificazione, String nomeCertificazione, String enteRilascio, Date dataRilascio,
                          Date dataScadenza, int idProdottoAssociato) {
        this.idCertificazione = idCertificazione;
        this.nomeCertificazione = nomeCertificazione;
        this.enteRilascio = enteRilascio;
        this.dataRilascio = dataRilascio;
        this.dataScadenza = dataScadenza;
        this.idProdottoAssociato = idProdottoAssociato;
        this.idAziendaAssociata = null; // Assicura che l'altro sia null
    }

    // Costruttore per certificazione di azienda
    public Certificazione(int idCertificazione, String nomeCertificazione, String enteRilascio, Date dataRilascio,
                          Date dataScadenza, Integer idAziendaAssociata, boolean isAzienda) { // boolean isAzienda solo per distinguere il costruttore
        if (!isAzienda) throw new IllegalArgumentException("Usare l'altro costruttore per prodotti");
        this.idCertificazione = idCertificazione;
        this.nomeCertificazione = nomeCertificazione;
        this.enteRilascio = enteRilascio;
        this.dataRilascio = dataRilascio;
        this.dataScadenza = dataScadenza;
        this.idAziendaAssociata = idAziendaAssociata;
        this.idProdottoAssociato = null; // Assicura che l'altro sia null
    }


    // Getters e Setters (inclusi per idProdottoAssociato e idAziendaAssociata)
    public int getIdCertificazione() {
        return idCertificazione;
    }

    public String getNomeCertificazione() {
        return nomeCertificazione;
    }

    public String getEnteRilascio() {
        return enteRilascio;
    }

    public Date getDataRilascio() {
        return dataRilascio;
    }

    public Date getDataScadenza() {
        return dataScadenza;
    }

    public Integer getIdProdottoAssociato() {
        return idProdottoAssociato;
    }

    public Integer getIdAziendaAssociata() {
        return idAziendaAssociata;
    }

    // Setter privati o protetti se vuoi che la modifica avvenga solo tramite service
    public void setNomeCertificazione(String nomeCertificazione) {
        this.nomeCertificazione = nomeCertificazione;
    }

    public void setEnteRilascio(String enteRilascio) {
        this.enteRilascio = enteRilascio;
    }

    public void setDataRilascio(Date dataRilascio) {
        this.dataRilascio = dataRilascio;
    }

    public void setDataScadenza(Date dataScadenza) {
        this.dataScadenza = dataScadenza;
    }

    // Non dovresti cambiare l'associazione dopo la creazione,
    // altrimenti la logica di "solo uno" si complica.
    // Se necessario, si cancella e si ricrea.

    public void stampaCertificazione() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println("  Certificazione: " + nomeCertificazione +
                " (ID: " + idCertificazione +
                ", Ente: " + enteRilascio +
                ", Rilascio: " + sdf.format(dataRilascio) +
                ", Scadenza: " + sdf.format(dataScadenza) +
                (idProdottoAssociato != null ? ", Prodotto ID: " + idProdottoAssociato : "") +
                (idAziendaAssociata != null ? ", Azienda ID: " + idAziendaAssociata : "") +
                ")");
    }
}