/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.eventi;

import jakarta.validation.constraints.*;
import java.util.Date;
import java.util.List;

/**
 * DTO for event creation requests.
 * Contains validation annotations for input validation.
 */
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
    private Integer capienzaMassima;
    
    private List<Long> idAziendePartecipanti;

    public CreateEventoRequestDTO() {
    }

    public CreateEventoRequestDTO(String nomeEvento, String descrizione, Date dataOraInizio, 
                                Date dataOraFine, String luogoEvento, Integer capienzaMassima, 
                                List<Long> idAziendePartecipanti) {
        this.nomeEvento = nomeEvento;
        this.descrizione = descrizione;
        this.dataOraInizio = dataOraInizio;
        this.dataOraFine = dataOraFine;
        this.luogoEvento = luogoEvento;
        this.capienzaMassima = capienzaMassima;
        this.idAziendePartecipanti = idAziendePartecipanti;
    }

    public String getNomeEvento() {
        return nomeEvento;
    }

    public void setNomeEvento(String nomeEvento) {
        this.nomeEvento = nomeEvento;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Date getDataOraInizio() {
        return dataOraInizio;
    }

    public void setDataOraInizio(Date dataOraInizio) {
        this.dataOraInizio = dataOraInizio;
    }

    public Date getDataOraFine() {
        return dataOraFine;
    }

    public void setDataOraFine(Date dataOraFine) {
        this.dataOraFine = dataOraFine;
    }

    public String getLuogoEvento() {
        return luogoEvento;
    }

    public void setLuogoEvento(String luogoEvento) {
        this.luogoEvento = luogoEvento;
    }

    public Integer getCapienzaMassima() {
        return capienzaMassima;
    }

    public void setCapienzaMassima(Integer capienzaMassima) {
        this.capienzaMassima = capienzaMassima;
    }

    public List<Long> getIdAziendePartecipanti() {
        return idAziendePartecipanti;
    }

    public void setIdAziendePartecipanti(List<Long> idAziendePartecipanti) {
        this.idAziendePartecipanti = idAziendePartecipanti;
    }

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