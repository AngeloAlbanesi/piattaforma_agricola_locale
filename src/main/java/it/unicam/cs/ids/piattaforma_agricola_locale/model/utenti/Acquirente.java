/*
 *   Copyright (c) 2025 
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti;

public class Acquirente extends Utente {

    public Acquirente(int idUtente, String nome, String cognome, String email, String passwordHash, String numeroTelefono,
            TipoRuolo tipoRuolo, boolean isAttivo) {
        super(idUtente,nome, cognome, email, passwordHash, numeroTelefono, tipoRuolo, isAttivo);
    }
}