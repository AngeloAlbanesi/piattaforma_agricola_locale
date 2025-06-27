/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.carrello;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for ElementoCarrello entity.
 * Represents a single item in the shopping cart.
 */
public class ElementoCarrelloDTO {
    
    private Long idElemento;
    
    @NotNull(message = "La quantità è obbligatoria")
    @Min(value = 1, message = "La quantità deve essere almeno 1")
    private Integer quantita;
    
    @NotNull(message = "Il prezzo unitario è obbligatorio")
    @Min(value = 0, message = "Il prezzo deve essere non negativo")
    private Double prezzoUnitario;
    
    // Information about the purchasable item
    private String tipoAcquistabile; // "PRODOTTO", "PACCHETTO", "EVENTO"
    private Long idAcquistabile;
    private String nomeAcquistabile;
    private String descrizioneAcquistabile;

    public ElementoCarrelloDTO() {
    }

    public ElementoCarrelloDTO(Long idElemento, Integer quantita, Double prezzoUnitario, 
                             String tipoAcquistabile, Long idAcquistabile, String nomeAcquistabile, 
                             String descrizioneAcquistabile) {
        this.idElemento = idElemento;
        this.quantita = quantita;
        this.prezzoUnitario = prezzoUnitario;
        this.tipoAcquistabile = tipoAcquistabile;
        this.idAcquistabile = idAcquistabile;
        this.nomeAcquistabile = nomeAcquistabile;
        this.descrizioneAcquistabile = descrizioneAcquistabile;
    }

    public Long getIdElemento() {
        return idElemento;
    }

    public void setIdElemento(Long idElemento) {
        this.idElemento = idElemento;
    }

    public Integer getQuantita() {
        return quantita;
    }

    public void setQuantita(Integer quantita) {
        this.quantita = quantita;
    }

    public Double getPrezzoUnitario() {
        return prezzoUnitario;
    }

    public void setPrezzoUnitario(Double prezzoUnitario) {
        this.prezzoUnitario = prezzoUnitario;
    }

    public String getTipoAcquistabile() {
        return tipoAcquistabile;
    }

    public void setTipoAcquistabile(String tipoAcquistabile) {
        this.tipoAcquistabile = tipoAcquistabile;
    }

    public Long getIdAcquistabile() {
        return idAcquistabile;
    }

    public void setIdAcquistabile(Long idAcquistabile) {
        this.idAcquistabile = idAcquistabile;
    }

    public String getNomeAcquistabile() {
        return nomeAcquistabile;
    }

    public void setNomeAcquistabile(String nomeAcquistabile) {
        this.nomeAcquistabile = nomeAcquistabile;
    }

    public String getDescrizioneAcquistabile() {
        return descrizioneAcquistabile;
    }

    public void setDescrizioneAcquistabile(String descrizioneAcquistabile) {
        this.descrizioneAcquistabile = descrizioneAcquistabile;
    }

    /**
     * Calculates the total price for this cart item.
     * 
     * @return the total price (quantity * unit price)
     */
    public double calcolaPrezzoTotale() {
        return quantita != null && prezzoUnitario != null ? quantita * prezzoUnitario : 0.0;
    }
}