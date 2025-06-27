/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.eventi;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.StatoEventoValori;
import java.util.Date;

/**
 * DTO for event summary information, used in event lists.
 * Contains only essential fields for displaying events in a catalog view.
 */
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

    public EventoSummaryDTO() {
    }

    public EventoSummaryDTO(Long idEvento, String nomeEvento, Date dataOraInizio, Date dataOraFine, 
                          String luogoEvento, int capienzaMassima, int postiDisponibili, 
                          StatoEventoValori statoEvento, String nomeOrganizzatore, Long idOrganizzatore) {
        this.idEvento = idEvento;
        this.nomeEvento = nomeEvento;
        this.dataOraInizio = dataOraInizio;
        this.dataOraFine = dataOraFine;
        this.luogoEvento = luogoEvento;
        this.capienzaMassima = capienzaMassima;
        this.postiDisponibili = postiDisponibili;
        this.statoEvento = statoEvento;
        this.nomeOrganizzatore = nomeOrganizzatore;
        this.idOrganizzatore = idOrganizzatore;
    }

    public Long getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(Long idEvento) {
        this.idEvento = idEvento;
    }

    public String getNomeEvento() {
        return nomeEvento;
    }

    public void setNomeEvento(String nomeEvento) {
        this.nomeEvento = nomeEvento;
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

    public int getCapienzaMassima() {
        return capienzaMassima;
    }

    public void setCapienzaMassima(int capienzaMassima) {
        this.capienzaMassima = capienzaMassima;
    }

    public int getPostiDisponibili() {
        return postiDisponibili;
    }

    public void setPostiDisponibili(int postiDisponibili) {
        this.postiDisponibili = postiDisponibili;
    }

    public StatoEventoValori getStatoEvento() {
        return statoEvento;
    }

    public void setStatoEvento(StatoEventoValori statoEvento) {
        this.statoEvento = statoEvento;
    }

    public String getNomeOrganizzatore() {
        return nomeOrganizzatore;
    }

    public void setNomeOrganizzatore(String nomeOrganizzatore) {
        this.nomeOrganizzatore = nomeOrganizzatore;
    }

    public Long getIdOrganizzatore() {
        return idOrganizzatore;
    }

    public void setIdOrganizzatore(Long idOrganizzatore) {
        this.idOrganizzatore = idOrganizzatore;
    }
}