/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.ordine;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.utente.UserPublicDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.StatoCorrente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * DTO for detailed order information, used in single order view.
 * Contains all order data including line items and buyer information.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrdineDetailDTO {
    
    private Long idOrdine;
    private Date dataOrdine;
    private double importoTotale;
    private StatoCorrente statoCorrente;
    private UserPublicDTO acquirente;
    private List<RigaOrdineDTO> righeOrdine;

    /**
     * Gets the total number of items in the order.
     * 
     * @return the total item count
     */
    public int getTotalArticoli() {
        return righeOrdine != null ? 
            righeOrdine.stream().mapToInt(RigaOrdineDTO::getQuantitaOrdinata).sum() : 0;
    }
}