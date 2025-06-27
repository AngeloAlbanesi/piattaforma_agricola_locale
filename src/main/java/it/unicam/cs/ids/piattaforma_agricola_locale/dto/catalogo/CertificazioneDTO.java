/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Date;

/**
 * DTO for Certificazione entity.
 * Used to transfer certification data between layers.
 */
public class CertificazioneDTO {
    
    private Long idCertificazione;
    
    @NotBlank(message = "Il nome della certificazione è obbligatorio")
    @Size(max = 255, message = "Il nome della certificazione non può superare i 255 caratteri")
    private String nomeCertificazione;
    
    @NotBlank(message = "L'ente di rilascio è obbligatorio")
    @Size(max = 255, message = "L'ente di rilascio non può superare i 255 caratteri")
    private String enteRilascio;
    
    @NotNull(message = "La data di rilascio è obbligatoria")
    private Date dataRilascio;
    
    private Date dataScadenza;
    
    private Long idProdottoAssociato;
    private Long idAziendaAssociata;

    public CertificazioneDTO() {
    }

    public CertificazioneDTO(Long idCertificazione, String nomeCertificazione, String enteRilascio, 
                           Date dataRilascio, Date dataScadenza, Long idProdottoAssociato, 
                           Long idAziendaAssociata) {
        this.idCertificazione = idCertificazione;
        this.nomeCertificazione = nomeCertificazione;
        this.enteRilascio = enteRilascio;
        this.dataRilascio = dataRilascio;
        this.dataScadenza = dataScadenza;
        this.idProdottoAssociato = idProdottoAssociato;
        this.idAziendaAssociata = idAziendaAssociata;
    }

    public Long getIdCertificazione() {
        return idCertificazione;
    }

    public void setIdCertificazione(Long idCertificazione) {
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

    public Long getIdProdottoAssociato() {
        return idProdottoAssociato;
    }

    public void setIdProdottoAssociato(Long idProdottoAssociato) {
        this.idProdottoAssociato = idProdottoAssociato;
    }

    public Long getIdAziendaAssociata() {
        return idAziendaAssociata;
    }

    public void setIdAziendaAssociata(Long idAziendaAssociata) {
        this.idAziendaAssociata = idAziendaAssociata;
    }
}