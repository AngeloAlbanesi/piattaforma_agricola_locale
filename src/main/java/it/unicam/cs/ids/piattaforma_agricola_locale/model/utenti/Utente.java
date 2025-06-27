/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "utenti")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_utente")
public abstract class Utente implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utente")
    private Long idUtente;
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;
    @Column(name = "cognome", nullable = false, length = 100)
    private String cognome;
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;
    @Column(name = "numero_telefono", length = 20)
    private String numeroTelefono;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_ruolo", nullable = false)
    private TipoRuolo tipoRuolo;
    @Column(name = "is_attivo", nullable = false)
    private boolean isAttivo;

    protected Utente() {
        // Default constructor for JPA
    }

    public Utente(String nome, String cognome, String email, String passwordHash, String numeroTelefono,
            TipoRuolo tipoRuolo) {

        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.passwordHash = passwordHash;
        this.numeroTelefono = numeroTelefono;
        this.tipoRuolo = tipoRuolo;
        this.isAttivo = true;
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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

    public boolean modificaPassword(String nuovaPassword) {

        // TODO: implementare la logica per modificare la password
        // Ad esempio, potresti aggiornare il campo passwordHash con un nuovo hash della
        // password
        // Restituisci true se la modifica è avvenuta con successo, altrimenti false
        return false;
    }

    public void disattivaAccount() {
        this.isAttivo = false;
    }

    public void riattivaAccount() {
        this.isAttivo = true;
    }

    public Long getId() {
        return idUtente;
    }

    public Long getIdUtente() {
        return idUtente;
    }

    public boolean isAttivo() {
        return isAttivo;
    }

    public void setAttivo(boolean attivo) {
        isAttivo = attivo;
    }

    public void setIdUtente(Long idUtente) {
        this.idUtente = idUtente;
    }

    public void setId(Long id) {
        this.idUtente = id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Restituisce il ruolo dell'utente come un'autorità di Spring Security
        return List.of(new SimpleGrantedAuthority(tipoRuolo.name()));
    }

    @Override
    public String getPassword() {
        // Spring Security chiamerà questo metodo per ottenere la password codificata
        return passwordHash;
    }

    @Override
    public String getUsername() {
        // Usiamo l'email come username univoco per l'autenticazione
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        // Per semplicità, l'account non scade mai
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Per semplicità, l'account non viene mai bloccato
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Per semplicità, le credenziali non scadono mai
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Lo stato "abilitato" dell'utente corrisponde al nostro campo "isAttivo"
        return this.isAttivo;
    }

}