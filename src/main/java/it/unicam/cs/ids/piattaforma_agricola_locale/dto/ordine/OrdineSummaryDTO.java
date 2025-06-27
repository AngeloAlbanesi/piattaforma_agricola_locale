/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.ordine;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.StatoCorrente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * DTO for order summary information, used in order lists and history.
 * Contains essential order data without detailed line items.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrdineSummaryDTO {
    
    private Long idOrdine;
    private Date dataOrdine;
    private double importoTotale;
    private StatoCorrente statoCorrente;
    private String nomeAcquirente;
    private Long idAcquirente;
    private int numeroArticoli;
}