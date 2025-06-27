/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.ordine;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for order creation requests.
 * Used when creating an order from the shopping cart.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrdineRequestDTO {
    
    @NotNull(message = "L'ID del carrello è obbligatorio")
    private Long idCarrello;
    
    @NotBlank(message = "Il metodo di pagamento è obbligatorio")
    private String metodoPagamento; // "CARTA_CREDITO", "PAYPAL", "SIMULATO"
    
    private String noteAggiuntive;
}