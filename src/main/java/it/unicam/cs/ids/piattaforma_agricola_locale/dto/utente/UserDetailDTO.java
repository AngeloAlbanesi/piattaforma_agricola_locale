/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.utente;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.TipoRuolo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for detailed user information, typically used when a user views their own profile.
 * Includes all user data except sensitive information like password hash.
 */
public class UserDetailDTO {
    
    private Long idUtente;
    
    @NotBlank(message = "Il nome è obbligatorio")
    @Size(max = 100, message = "Il nome non può superare i 100 caratteri")
    private String nome;
    
    @NotBlank(message = "Il cognome è obbligatorio")
    @Size(max = 100, message = "Il cognome non può superare i 100 caratteri")
    private String cognome;
    
    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "L'email deve essere valida")
    @Size(max = 255, message = "L'email non può superare i 255 caratteri")
    private String email;
    
    @Size(max = 20, message = "Il numero di telefono non può superare i 20 caratteri")
    private String numeroTelefono;
    
    @NotNull(message = "Il tipo di ruolo è obbligatorio")
    private TipoRuolo tipoRuolo;
    
    private boolean isAttivo;

    public UserDetailDTO() {
    }

    public UserDetailDTO(Long idUtente, String nome, String cognome, String email, 
                        String numeroTelefono, TipoRuolo tipoRuolo, boolean isAttivo) {
        this.idUtente = idUtente;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.numeroTelefono = numeroTelefono;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumeroTelefono() {
        return numeroTelefono;
    }

    public void setNumeroTelefono(String numeroTelefono) {
        this.numeroTelefono = numeroTelefono;
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