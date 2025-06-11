/*
 *   Copyright (c) 2025 
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.Evento;



public class AnimatoreDellaFiliera extends Utente {
    private StatoAccreditamento statoAccreditamento;
    public AnimatoreDellaFiliera(String nome, String cognome, String email, String passwordHash, String numeroTelefono,
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