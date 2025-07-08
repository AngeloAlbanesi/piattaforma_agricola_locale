/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for product update requests.
 * Contains validation annotations for input validation.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductRequestDTO {
    
    @Size(max = 200, message = "Il nome del prodotto non può superare i 200 caratteri")
    private String nome;
    
    @Size(max = 1000, message = "La descrizione non può superare i 1000 caratteri")
    private String descrizione;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Il prezzo deve essere maggiore di 0")
    private Double prezzo;
    
    @Min(value = 0, message = "La quantità deve essere non negativa")
    private Integer quantitaDisponibile;
    
    private Boolean attivo;
}