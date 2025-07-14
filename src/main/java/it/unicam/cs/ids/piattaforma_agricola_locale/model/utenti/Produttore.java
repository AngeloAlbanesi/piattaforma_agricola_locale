/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti;

import jakarta.persistence.*;

@Entity
@Table(name = "produttori")
@DiscriminatorValue("PRODUTTORE")
public class Produttore extends Venditore {

    public Produttore() {
        super();
    }

    public Produttore(String nome, String cognome, String email, String passwordHash,
            String numeroTelefono, DatiAzienda datiAzienda,
            TipoRuolo tipoRuolo) {
        super(nome, cognome, email, passwordHash, numeroTelefono, datiAzienda, tipoRuolo);
    }

}