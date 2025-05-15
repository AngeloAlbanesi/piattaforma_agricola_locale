/*
 *   Copyright (c) 2025 
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti;

public class Acquirente extends Utente {

    public Acquirente(String nome, String cognome, String email, String passwordHash, String numeroTelefono,
            String idUtente, TipoRuolo tipoRuolo, boolean isAttivo) {
        super(nome, cognome, email, passwordHash, numeroTelefono, idUtente, tipoRuolo, isAttivo);
    }
}