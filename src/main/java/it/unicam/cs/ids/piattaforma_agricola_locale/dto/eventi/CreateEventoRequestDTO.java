/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.eventi;

import it.unicam.cs.ids.piattaforma_agricola_locale.validation.ValidDateRange;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * DTO for event creation requests.
 * Contains validation annotations for input validation including custom
 * business rules.
 */
@ValidDateRange(start = "dataOraInizio", end = "dataOraFine")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateEventoRequestDTO {

    @NotBlank(message = "Il nome dell'evento è obbligatorio")
    @Size(max = 200, message = "Il nome dell'evento non può superare i 200 caratteri")
    private String nomeEvento;

    @Size(max = 1000, message = "La descrizione non può superare i 1000 caratteri")
    private String descrizione;

    @NotNull(message = "La data e ora di inizio sono obbligatorie")
    @Future(message = "La data e ora di inizio devono essere nel futuro")
    private Date dataOraInizio;

    @NotNull(message = "La data e ora di fine sono obbligatorie")
    @Future(message = "La data e ora di fine devono essere nel futuro")
    private Date dataOraFine;

    @NotBlank(message = "Il luogo dell'evento è obbligatorio")
    @Size(max = 255, message = "Il luogo dell'evento non può superare i 255 caratteri")
    private String luogoEvento;

    @NotNull(message = "La capienza massima è obbligatoria")
    @Min(value = 1, message = "La capienza massima deve essere almeno 1")
    @Max(value = 1000, message = "La capienza non può superare 1000 persone")
    private Integer capienzaMassima;

    private List<Long> idAziendePartecipanti;

    /**
     * Custom validation to ensure end date is after start date.
     */
    @AssertTrue(message = "La data di fine deve essere successiva alla data di inizio")
    public boolean isEndDateAfterStartDate() {
        if (dataOraInizio == null || dataOraFine == null) {
            return true; // Let @NotNull handle null validation
        }
        return dataOraFine.after(dataOraInizio);
    }
}