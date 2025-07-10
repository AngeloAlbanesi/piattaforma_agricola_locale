/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.ordine;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for RigaOrdine entity.
 * Represents a single line item in an order.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    private String nomeVenditore;

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