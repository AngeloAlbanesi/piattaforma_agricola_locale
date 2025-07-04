/*
 *   Copyright (c) 2025 
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("GESTORE_PIATTAFORMA")
public class GestorePiattaforma extends Utente {

    public GestorePiattaforma() {}

    public GestorePiattaforma(String nome, String cognome, String email, String passwordHash, String numeroTelefono,
            TipoRuolo tipoRuolo) {
        super(nome, cognome, email, passwordHash, numeroTelefono, tipoRuolo);

    }

}
