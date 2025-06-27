/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.eventi;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.StatoEventoValori;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * DTO for event summary information, used in event lists.
 * Contains only essential fields for displaying events in a catalog view.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventoSummaryDTO {
    
    private Long idEvento;
    private String nomeEvento;
    private Date dataOraInizio;
    private Date dataOraFine;
    private String luogoEvento;
    private int capienzaMassima;
    private int postiDisponibili;
    private StatoEventoValori statoEvento;
    private String nomeOrganizzatore;
    private Long idOrganizzatore;
}