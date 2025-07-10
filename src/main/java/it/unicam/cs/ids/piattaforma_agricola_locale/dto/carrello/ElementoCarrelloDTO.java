/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.carrello;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for ElementoCarrello entity.
 * Represents a single item in the shopping cart.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    private String nomeVenditore;

    /**
     * Calculates the total price for this cart item.
     * 
     * @return the total price (quantity * unit price)
     */
    public double calcolaPrezzoTotale() {
        return quantita != null && prezzoUnitario != null ? quantita * prezzoUnitario : 0.0;
    }
}