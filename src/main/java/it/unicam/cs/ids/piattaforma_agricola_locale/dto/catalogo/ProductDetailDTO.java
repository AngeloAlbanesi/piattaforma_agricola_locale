/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.utente.UserPublicDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.TipoOrigineProdotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.StatoVerificaValori;
import java.util.List;

/**
 * DTO for detailed product information, used in single product view.
 * Contains all product fields plus related entities like vendor and certifications.
 */
public class ProductDetailDTO {
    
    private Long idProdotto;
    private String nome;
    private String descrizione;
    private double prezzo;
    private int quantitaDisponibile;
    private StatoVerificaValori statoVerifica;
    private String feedbackVerifica;
    private TipoOrigineProdotto tipoOrigine;
    private Long idProcessoTrasformazioneOriginario;
    private Long idMetodoDiColtivazione;
    private UserPublicDTO venditore;
    private List<CertificazioneDTO> certificazioni;

    public ProductDetailDTO() {
    }

    public ProductDetailDTO(Long idProdotto, String nome, String descrizione, double prezzo, 
                          int quantitaDisponibile, StatoVerificaValori statoVerifica, 
                          String feedbackVerifica, TipoOrigineProdotto tipoOrigine, 
                          Long idProcessoTrasformazioneOriginario, Long idMetodoDiColtivazione,
                          UserPublicDTO venditore, List<CertificazioneDTO> certificazioni) {
        this.idProdotto = idProdotto;
        this.nome = nome;
        this.descrizione = descrizione;
        this.prezzo = prezzo;
        this.quantitaDisponibile = quantitaDisponibile;
        this.statoVerifica = statoVerifica;
        this.feedbackVerifica = feedbackVerifica;
        this.tipoOrigine = tipoOrigine;
        this.idProcessoTrasformazioneOriginario = idProcessoTrasformazioneOriginario;
        this.idMetodoDiColtivazione = idMetodoDiColtivazione;
        this.venditore = venditore;
        this.certificazioni = certificazioni;
    }

    public Long getIdProdotto() {
        return idProdotto;
    }

    public void setIdProdotto(Long idProdotto) {
        this.idProdotto = idProdotto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public double getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }

    public int getQuantitaDisponibile() {
        return quantitaDisponibile;
    }

    public void setQuantitaDisponibile(int quantitaDisponibile) {
        this.quantitaDisponibile = quantitaDisponibile;
    }

    public StatoVerificaValori getStatoVerifica() {
        return statoVerifica;
    }

    public void setStatoVerifica(StatoVerificaValori statoVerifica) {
        this.statoVerifica = statoVerifica;
    }

    public String getFeedbackVerifica() {
        return feedbackVerifica;
    }

    public void setFeedbackVerifica(String feedbackVerifica) {
        this.feedbackVerifica = feedbackVerifica;
    }

    public TipoOrigineProdotto getTipoOrigine() {
        return tipoOrigine;
    }

    public void setTipoOrigine(TipoOrigineProdotto tipoOrigine) {
        this.tipoOrigine = tipoOrigine;
    }

    public Long getIdProcessoTrasformazioneOriginario() {
        return idProcessoTrasformazioneOriginario;
    }

    public void setIdProcessoTrasformazioneOriginario(Long idProcessoTrasformazioneOriginario) {
        this.idProcessoTrasformazioneOriginario = idProcessoTrasformazioneOriginario;
    }

    public Long getIdMetodoDiColtivazione() {
        return idMetodoDiColtivazione;
    }

    public void setIdMetodoDiColtivazione(Long idMetodoDiColtivazione) {
        this.idMetodoDiColtivazione = idMetodoDiColtivazione;
    }

    public UserPublicDTO getVenditore() {
        return venditore;
    }

    public void setVenditore(UserPublicDTO venditore) {
        this.venditore = venditore;
    }

    public List<CertificazioneDTO> getCertificazioni() {
        return certificazioni;
    }

    public void setCertificazioni(List<CertificazioneDTO> certificazioni) {
        this.certificazioni = certificazioni;
    }
}