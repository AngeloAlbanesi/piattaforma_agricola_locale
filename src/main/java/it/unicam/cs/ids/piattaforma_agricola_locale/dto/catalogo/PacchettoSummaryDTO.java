/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for package summary information, used in package lists.
 * Contains only essential fields for displaying packages in a catalog view.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PacchettoSummaryDTO {
    
    private Long idPacchetto;
    private String nome;
    private double prezzoPacchetto;
    private int quantitaDisponibile;
    private String nomeDistributore;
    private Long idDistributore;
    private int numeroElementi;
}