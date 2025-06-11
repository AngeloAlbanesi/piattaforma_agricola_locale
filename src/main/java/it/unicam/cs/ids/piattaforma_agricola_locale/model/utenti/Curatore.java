/*
 *   Copyright (c) 2025 
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.StatoVerificaValori;

import java.util.Scanner;

public class Curatore extends Utente {
    private StatoAccreditamento statoAccreditamento;
    public Curatore(int idUtente, String nome, String cognome, String email, String passwordHash, String numeroTelefono,
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