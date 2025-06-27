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
import jakarta.persistence.*;

@Entity
@Table(name = "dati_azienda")
public class DatiAzienda implements ElementoVerificabile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dati_azienda")
    private Long id;


    @Column(name = "nome_azienda", nullable = false, length = 255)
    private String nomeAzienda;
    @Column(name = "partita_iva", unique = true, length = 20)
    private String partitaIva;
    @Column(name = "indirizzo_azienda", length = 500)
    private String indirizzoAzienda;
    @Column(name = "descrizione_azienda", columnDefinition = "TEXT")
    private String descrizioneAzienda;
    @Column(name = "logo_url", length = 500)
    private String logoUrl;
    @Column(name = "sito_web_url", length = 500)
    private String sitoWebUrl;
    @Enumerated(EnumType.STRING)
    @Column(name = "stato_verifica")
    private StatoVerificaValori statoVerifica;
    @Column(name = "feedback_verifica_contenuto", columnDefinition = "TEXT")
    private String feedbackVerificaContenuto;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_azienda_associata")
    private List<Certificazione> certificazioniAzienda;

    public DatiAzienda() {
        // Default constructor for JPA
        this.certificazioniAzienda = new ArrayList<>();
    }

    public DatiAzienda(String nomeAzienda, String partitaIva, String indirizzoAzienda, String descrizioneAzienda,
            String logoUrl, String sitoWebUrl) {

        this.nomeAzienda = nomeAzienda;
        this.partitaIva = partitaIva;
        this.indirizzoAzienda = indirizzoAzienda;
        this.descrizioneAzienda = descrizioneAzienda;
        this.logoUrl = logoUrl;
        this.sitoWebUrl = sitoWebUrl;
        this.statoVerifica = StatoVerificaValori.IN_REVISIONE;
        this.certificazioniAzienda = new ArrayList<>();

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
        if (certificazione != null && certificazione.getIdAziendaAssociata() != null && certificazione.getIdAziendaAssociata().equals(this.id)) {
            this.certificazioniAzienda.add(certificazione);
        } else {
            // Gestire l'errore: la certificazione non appartiene a questa azienda
            System.err.println("Errore: tentativo di aggiungere certificazione non pertinente all'azienda.");
        }
    }

    @Override
    public Long getId() {
        return id;
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