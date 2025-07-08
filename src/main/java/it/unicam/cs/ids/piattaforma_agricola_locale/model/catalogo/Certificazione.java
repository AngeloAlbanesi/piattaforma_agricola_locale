package it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo;

import jakarta.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "certificazioni")
public class Certificazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_certificazione")
    private Long idCertificazione;

    @Column(name = "nome_certificazione", nullable = false)
    private String nomeCertificazione;

    @Column(name = "ente_rilascio", nullable = false)
    private String enteRilascio;

    @Temporal(TemporalType.DATE)
    @Column(name = "data_rilascio", nullable = false)
    private Date dataRilascio;

    @Temporal(TemporalType.DATE)
    @Column(name = "data_scadenza")
    private Date dataScadenza;

    @Column(name = "id_prodotto_associato")
    private Long idProdottoAssociato;

    @Column(name = "id_azienda_associata")
    private Long idAziendaAssociata;

    public Certificazione() {
    }

    // Costruttore per certificazione di prodotto
    public Certificazione(String nomeCertificazione, String enteRilascio, Date dataRilascio,
            Date dataScadenza, long idProdottoAssociato) {
        this.nomeCertificazione = nomeCertificazione;
        this.enteRilascio = enteRilascio;
        this.dataRilascio = dataRilascio;
        this.dataScadenza = dataScadenza;
        this.idProdottoAssociato = idProdottoAssociato;
        this.idAziendaAssociata = null; // Assicura che l'altro sia null
    }

    // Costruttore per certificazione di azienda
    public Certificazione(String nomeCertificazione, String enteRilascio, Date dataRilascio,
            Date dataScadenza, long idAziendaAssociata, boolean isAzienda) { // boolean isAzienda solo per distinguere
                                                                             // il costruttore
        if (!isAzienda)
            throw new IllegalArgumentException("Usare l'altro costruttore per prodotti");
        this.nomeCertificazione = nomeCertificazione;
        this.enteRilascio = enteRilascio;
        this.dataRilascio = dataRilascio;
        this.dataScadenza = dataScadenza;
        this.idAziendaAssociata = idAziendaAssociata;
        this.idProdottoAssociato = null; // Assicura che l'altro sia null
    }

    // Getters e Setters (inclusi per idProdottoAssociato e idAziendaAssociata)
    public Long getIdCertificazione() {
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

    public Long getIdProdottoAssociato() {
        return idProdottoAssociato;
    }

    public Long getIdAziendaAssociata() {
        return idAziendaAssociata;
    }

    /**
     * Restituisce l'ID della certificazione.
     */
    public Long getId() {
        return idCertificazione;
    }

    /**
     * Restituisce il nome della certificazione.
     */
    public String getNome() {
        return nomeCertificazione;
    }

    // Setter privati o protetti se vuoi che la modifica avvenga solo tramite
    // service
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

    public void SetIdCertificazione(Long idCertificazione) {
        this.idCertificazione = idCertificazione;
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