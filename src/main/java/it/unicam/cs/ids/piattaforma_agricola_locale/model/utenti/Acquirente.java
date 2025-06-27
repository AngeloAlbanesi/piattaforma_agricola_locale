/*
 *   Copyright (c) 2025 
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("ACQUIRENTE")
public class Acquirente extends Utente {

    public Acquirente() {}

    public Acquirente(String nome, String cognome, String email, String passwordHash, String numeroTelefono,
            TipoRuolo tipoRuolo) {
        super(nome, cognome, email, passwordHash, numeroTelefono, tipoRuolo);
    }
}