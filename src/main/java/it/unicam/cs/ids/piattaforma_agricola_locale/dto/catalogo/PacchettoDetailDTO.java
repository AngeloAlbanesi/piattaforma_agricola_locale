/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.utente.UserPublicDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for detailed package information, used in single package view.
 * Contains all package fields plus related entities like distributor and included items.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PacchettoDetailDTO {
    
    private Long idPacchetto;
    private String nome;
    private String descrizione;
    private double prezzoPacchetto;
    private int quantitaDisponibile;
    private UserPublicDTO distributore;
    private List<ElementoPacchettoDTO> elementiInclusi;

    /**
     * Gets the total number of items included in the package.
     * 
     * @return the total item count
     */
    public int getTotalElementi() {
        return elementiInclusi != null ? elementiInclusi.size() : 0;
    }
}

