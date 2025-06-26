/*
 *   Copyright (c) 2025 
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("CURATORE")
public class Curatore extends Utente {
    
    @Enumerated(EnumType.STRING)
    @Column(name = "stato_accreditamento")
    private StatoAccreditamento statoAccreditamento;

    public Curatore() {}

    public Curatore(String nome, String cognome, String email, String passwordHash, String numeroTelefono,
            TipoRuolo tipoRuolo) {
        super(nome, cognome, email, passwordHash, numeroTelefono, tipoRuolo);
        statoAccreditamento = StatoAccreditamento.PENDING;
    }

    public StatoAccreditamento getStatoAccreditamento() {
        return statoAccreditamento;
    }

    public void setStatoAccreditamento(StatoAccreditamento statoAccreditamento) {
        this.statoAccreditamento = statoAccreditamento;
    }

}