/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.coltivazione;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for MetodoDiColtivazione entity.
 * Used to transfer cultivation method data between layers.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MetodoDiColtivazioneDTO {
    
    private Long id;
    
    @NotBlank(message = "Il nome del metodo di coltivazione è obbligatorio")
    @Size(max = 255, message = "Il nome non può superare i 255 caratteri")
    private String nome;
    
    @Size(max = 1000, message = "La descrizione non può superare i 1000 caratteri")
    private String descrizione;
    
    private java.time.LocalDate dataInizio;
    
    private java.time.LocalDate dataFine;
}