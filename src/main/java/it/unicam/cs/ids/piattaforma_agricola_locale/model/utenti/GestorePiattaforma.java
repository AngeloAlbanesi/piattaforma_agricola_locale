/*
 *   Copyright (c) 2025 
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti;

import java.util.HashMap;
import java.util.Map;

import it.unicam.cs.ids.piattaforma_agricola_locale.service.factory.UtenteFactory;

public class GestorePiattaforma extends Utente {
    private Map<String, Utente> utentiRegistrati;
    private UtenteFactory utenteFactory;

    public GestorePiattaforma(String nome, String cognome, String email, String passwordHash, String numeroTelefono,
            String idUtente, TipoRuolo tipoRuolo, boolean isAttivo, UtenteFactory utenteFactory) {
        super(nome, cognome, email, passwordHash, numeroTelefono, idUtente, tipoRuolo, isAttivo);
        this.utenteFactory = utenteFactory;
        this.utentiRegistrati = new HashMap<>(); 
    }

    public void registraNuovoUtente(String nome, String cognome, String email, String passwordHash, String numeroTelefono,
            String idUtente, TipoRuolo tipoRuolo) {
        Utente nuovoUtente = utenteFactory.creaUtente(nome, cognome, email, passwordHash, numeroTelefono, idUtente,
                tipoRuolo, true, null, null);
        utentiRegistrati.put(idUtente, nuovoUtente);
    }

    public boolean approvaAccountVenditore(String idUtente) {
        return true; //TODO: Implementa la logica per approvare l'account del venditore
    }

    public boolean sospendiAccountUtente(String idUtente) {
        Utente utente = utentiRegistrati.get(idUtente);
        if (utente != null) {
            utente.disattivaAccount();
            return true;
        }
        return false;
    }


    public boolean attivaAccountUtente(String idUtente) {
        Utente utente = utentiRegistrati.get(idUtente);
        if (utente != null) {
            utente.riattivaAccount();
            return true;
        }
        return false;
    }

    public Utente getUtentiByYdId(String idUtente) {
        return utentiRegistrati.get(idUtente);
    }

    public Map<String, Utente> getTuttiUtenti() {
        return utentiRegistrati;
    }



}

