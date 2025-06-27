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
 * DTO for specifying elements to include in a package.
 * Used in package creation requests to identify items to add.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ElementoPacchettoRequestDTO {
    
    @NotBlank(message = "Il tipo di elemento è obbligatorio")
    private String tipoElemento; // "PRODOTTO", "EVENTO"
    
    @NotNull(message = "L'ID dell'elemento è obbligatorio")
    private Long idElemento;
}