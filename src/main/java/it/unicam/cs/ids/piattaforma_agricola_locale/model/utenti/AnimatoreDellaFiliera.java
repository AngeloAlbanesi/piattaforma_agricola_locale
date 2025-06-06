/*
 *   Copyright (c) 2025 
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.Evento;

import java.util.UUID;

public class AnimatoreDellaFiliera extends Utente {

    public AnimatoreDellaFiliera(int idUtente, String nome, String cognome, String email, String passwordHash, String numeroTelefono,
                                 TipoRuolo tipoRuolo) {

        super(idUtente ,nome, cognome, email, passwordHash, numeroTelefono, tipoRuolo);
    }



    
    
}