/*
 *   Copyright (c) 2025 
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.exception;

/**
 * Eccezione lanciata quando un utente tenta di accedere a una risorsa
 * che richiede accreditamento ma il suo stato di accreditamento non è
 * ACCREDITATO.
 */
public class UtenteNonAccreditatoException extends RuntimeException {

    private final String tipoUtente;
    private final String statoAccreditamento;

    public UtenteNonAccreditatoException(String tipoUtente, String statoAccreditamento) {
        super(String.format("Utente di tipo %s con stato accreditamento %s non può eseguire questa operazione. " +
                "Contatta il gestore della piattaforma per ottenere l'accreditamento.",
                tipoUtente, statoAccreditamento));
        this.tipoUtente = tipoUtente;
        this.statoAccreditamento = statoAccreditamento;
    }

    public UtenteNonAccreditatoException(String tipoUtente, String statoAccreditamento, String message) {
        super(message);
        this.tipoUtente = tipoUtente;
        this.statoAccreditamento = statoAccreditamento;
    }

    public String getTipoUtente() {
        return tipoUtente;
    }

    public String getStatoAccreditamento() {
        return statoAccreditamento;
    }
}
