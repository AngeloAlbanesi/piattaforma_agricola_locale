/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.utente;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.TipoRuolo;

/**
 * DTO for public user information, used when displaying other users' profiles.
 * Excludes sensitive information like email and personal details.
 */
public class UserPublicDTO {
    
    private Long idUtente;
    private String nome;
    private String cognome;
    private TipoRuolo tipoRuolo;
    private boolean isAttivo;

    public UserPublicDTO() {
    }

    public UserPublicDTO(Long idUtente, String nome, String cognome, TipoRuolo tipoRuolo, boolean isAttivo) {
        this.idUtente = idUtente;
        this.nome = nome;
        this.cognome = cognome;
        this.tipoRuolo = tipoRuolo;
        this.isAttivo = isAttivo;
    }

    public Long getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(Long idUtente) {
        this.idUtente = idUtente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public TipoRuolo getTipoRuolo() {
        return tipoRuolo;
    }

    public void setTipoRuolo(TipoRuolo tipoRuolo) {
        this.tipoRuolo = tipoRuolo;
    }

    public boolean isAttivo() {
        return isAttivo;
    }

    public void setAttivo(boolean attivo) {
        isAttivo = attivo;
    }
}