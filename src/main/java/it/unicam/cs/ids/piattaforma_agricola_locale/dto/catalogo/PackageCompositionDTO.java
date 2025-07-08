/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for detailed package composition information.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PackageCompositionDTO {
    
    private Long id;
    private String nome;
    private String descrizione;
    private Double prezzoTotale;
    private Integer quantitaDisponibile;
    private List<ElementoPacchettoDTO> elementi;
    private Double prezzoCalcolato;
    private Boolean disponibilitaCompleta;
}