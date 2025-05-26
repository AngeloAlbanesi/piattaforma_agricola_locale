/*
 *   Copyright (c) 2025 
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.Evento;

import java.util.UUID;

public class AnimatoreDellaFiliera extends Utente {

    public AnimatoreDellaFiliera(int idUtente, String nome, String cognome, String email, String passwordHash, String numeroTelefono,
                                 TipoRuolo tipoRuolo, boolean isAttivo) {

        super(idUtente ,nome, cognome, email, passwordHash, numeroTelefono, tipoRuolo, isAttivo);
    }

    public Evento creaEvento(){
        return null;//TODO : creare evento, anche i parametri del metodo 
    }

    public boolean modificaEvento(Evento evento){
        return false;//TODO : modificare evento
    }

    public boolean annullaEvento(Evento evento){
        return false;//TODO : annullare evento
    }

    public boolean invitaAziendaAdEvento(Evento evento, Venditore venditore){
        return false;//TODO : invita azienda ad evento
    }

    
    
}