/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti;

import java.util.List;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;

public class Produttore extends Venditore {

    public Produttore(String nome, String cognome, String email, String passwordHash,
                      String numeroTelefono, DatiAzienda datiAzienda,
                      TipoRuolo tipoRuolo) {
        super(nome, cognome, email, passwordHash, numeroTelefono, datiAzienda, tipoRuolo);
    }

}