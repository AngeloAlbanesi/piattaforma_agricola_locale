/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.ordine;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for RigaOrdine entity.
 * Represents a single line item in an order.
 */
public class RigaOrdineDTO {
    
    private Long idRiga;
    
    @NotNull(message = "La quantità ordinata è obbligatoria")
    @Min(value = 1, message = "La quantità deve essere almeno 1")
    private Integer quantitaOrdinata;
    
    @NotNull(message = "Il prezzo unitario è obbligatorio")
    @Min(value = 0, message = "Il prezzo deve essere non negativo")
    private Double prezzoUnitario;
    
    // Information about the purchased item
    private String tipoAcquistabile; // "PRODOTTO", "PACCHETTO", "EVENTO"
    private Long idAcquistabile;
    private String nomeAcquistabile;
    private String descrizioneAcquistabile;

    public RigaOrdineDTO() {
    }

    public RigaOrdineDTO(Long idRiga, Integer quantitaOrdinata, Double prezzoUnitario, 
                        String tipoAcquistabile, Long idAcquistabile, String nomeAcquistabile, 
                        String descrizioneAcquistabile) {
        this.idRiga = idRiga;
        this.quantitaOrdinata = quantitaOrdinata;
        this.prezzoUnitario = prezzoUnitario;
        this.tipoAcquistabile = tipoAcquistabile;
        this.idAcquistabile = idAcquistabile;
        this.nomeAcquistabile = nomeAcquistabile;
        this.descrizioneAcquistabile = descrizioneAcquistabile;
    }

    public Long getIdRiga() {
        return idRiga;
    }

    public void setIdRiga(Long idRiga) {
        this.idRiga = idRiga;
    }

    public Integer getQuantitaOrdinata() {
        return quantitaOrdinata;
    }

    public void setQuantitaOrdinata(Integer quantitaOrdinata) {
        this.quantitaOrdinata = quantitaOrdinata;
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
     * Calculates the total price for this order line.
     * 
     * @return the total price (quantity * unit price)
     */
    public double calcolaPrezzoTotale() {
        return quantitaOrdinata != null && prezzoUnitario != null ? 
            quantitaOrdinata * prezzoUnitario : 0.0;
    }
}