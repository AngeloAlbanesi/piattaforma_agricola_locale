/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for product quantity update requests.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductQuantityUpdateDTO {
    
    @NotNull(message = "La quantità è obbligatoria")
    @Min(value = 0, message = "La quantità deve essere non negativa")
    private Integer quantitaDisponibile;
}