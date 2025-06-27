/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.eventi;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.utente.UserPublicDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.StatoEventoValori;
import java.util.Date;
import java.util.List;

/**
 * DTO for detailed event information, used in single event view.
 * Contains all event fields plus related entities like organizer and participating companies.
 */
public class EventoDetailDTO {
    
    private Long idEvento;
    private String nomeEvento;
    private String descrizione;
    private Date dataOraInizio;
    private Date dataOraFine;
    private String luogoEvento;
    private int capienzaMassima;
    private int postiAttualmentePrenotati;
    private int postiDisponibili;
    private StatoEventoValori statoEvento;
    private UserPublicDTO organizzatore;
    private List<UserPublicDTO> aziendePartecipanti;

    public EventoDetailDTO() {
    }

    public EventoDetailDTO(Long idEvento, String nomeEvento, String descrizione, Date dataOraInizio, 
                         Date dataOraFine, String luogoEvento, int capienzaMassima, 
                         int postiAttualmentePrenotati, int postiDisponibili, StatoEventoValori statoEvento, 
                         UserPublicDTO organizzatore, List<UserPublicDTO> aziendePartecipanti) {
        this.idEvento = idEvento;
        this.nomeEvento = nomeEvento;
        this.descrizione = descrizione;
        this.dataOraInizio = dataOraInizio;
        this.dataOraFine = dataOraFine;
        this.luogoEvento = luogoEvento;
        this.capienzaMassima = capienzaMassima;
        this.postiAttualmentePrenotati = postiAttualmentePrenotati;
        this.postiDisponibili = postiDisponibili;
        this.statoEvento = statoEvento;
        this.organizzatore = organizzatore;
        this.aziendePartecipanti = aziendePartecipanti;
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

    public int getCapienzaMassima() {
        return capienzaMassima;
    }

    public void setCapienzaMassima(int capienzaMassima) {
        this.capienzaMassima = capienzaMassima;
    }

    public int getPostiAttualmentePrenotati() {
        return postiAttualmentePrenotati;
    }

    public void setPostiAttualmentePrenotati(int postiAttualmentePrenotati) {
        this.postiAttualmentePrenotati = postiAttualmentePrenotati;
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

    public UserPublicDTO getOrganizzatore() {
        return organizzatore;
    }

    public void setOrganizzatore(UserPublicDTO organizzatore) {
        this.organizzatore = organizzatore;
    }

    public List<UserPublicDTO> getAziendePartecipanti() {
        return aziendePartecipanti;
    }

    public void setAziendePartecipanti(List<UserPublicDTO> aziendePartecipanti) {
        this.aziendePartecipanti = aziendePartecipanti;
    }

    /**
     * Gets the number of participating companies.
     * 
     * @return the number of participating companies
     */
    public int getNumeroAziendePartecipanti() {
        return aziendePartecipanti != null ? aziendePartecipanti.size() : 0;
    }
}