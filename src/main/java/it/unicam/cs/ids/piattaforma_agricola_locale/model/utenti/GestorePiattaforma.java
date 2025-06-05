/*
 *   Copyright (c) 2025 
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti;

import java.util.HashMap;
import java.util.Map;

import it.unicam.cs.ids.piattaforma_agricola_locale.service.factory.UtenteFactory;

public class GestorePiattaforma extends Utente {

    public GestorePiattaforma(int idUtente,String nome, String cognome, String email, String passwordHash, String numeroTelefono,
             TipoRuolo tipoRuolo, boolean isAttivo, UtenteFactory utenteFactory) {
        super(idUtente,nome, cognome, email, passwordHash, numeroTelefono, tipoRuolo, isAttivo);

    }




}

