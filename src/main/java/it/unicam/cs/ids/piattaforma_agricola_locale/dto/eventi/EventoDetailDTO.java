/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.eventi;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import it.unicam.cs.ids.piattaforma_agricola_locale.config.DateDeserializer;
import it.unicam.cs.ids.piattaforma_agricola_locale.config.DateSerializer;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.utente.UserPublicDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.StatoEventoValori;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * DTO for detailed event information, used in single event view.
 * Contains all event fields plus related entities like organizer and participating companies.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventoDetailDTO {
    
    private Long idEvento;
    private String nomeEvento;
    private String descrizione;
    @JsonDeserialize(using = DateDeserializer.class)
    @JsonSerialize(using = DateSerializer.class)
    private Date dataOraInizio;
    @JsonDeserialize(using = DateDeserializer.class)
    @JsonSerialize(using = DateSerializer.class)
    private Date dataOraFine;
    private String luogoEvento;
    private int capienzaMassima;
    private int postiAttualmentePrenotati;
    private int postiDisponibili;
    private StatoEventoValori statoEvento;
    private UserPublicDTO organizzatore;
    private List<AziendaPartecipanteDTO> aziendePartecipanti;

    /**
     * Gets the number of participating companies.
     * 
     * @return the number of participating companies
     */
    public int getNumeroAziendePartecipanti() {
        return aziendePartecipanti != null ? aziendePartecipanti.size() : 0;
    }
}