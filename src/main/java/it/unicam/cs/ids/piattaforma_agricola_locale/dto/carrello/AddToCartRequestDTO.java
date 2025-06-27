/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.carrello;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for adding items to cart requests.
 * Used when a user wants to add a product, package, or event to their cart.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddToCartRequestDTO {
    
    @NotBlank(message = "Il tipo di acquistabile è obbligatorio")
    private String tipoAcquistabile; // "PRODOTTO", "PACCHETTO", "EVENTO"
    
    @NotNull(message = "L'ID dell'acquistabile è obbligatorio")
    private Long idAcquistabile;
    
    @NotNull(message = "La quantità è obbligatoria")
    @Min(value = 1, message = "La quantità deve essere almeno 1")
    private Integer quantita;
}