package it.unicam.cs.ids.piattaforma_agricola_locale.service.factory;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.*;

/**
 * Interfaccia per il factory pattern che gestisce la creazione di utenti.
 * Definisce metodi type-safe per la creazione di diversi tipi di utenti.
 */
public interface UtenteFactory {
    
    /**
     * Crea un utente generico (Acquirente)
     * 
     * @param nome Nome dell'utente
     * @param cognome Cognome dell'utente
     * @param email Email dell'utente
     * @param passwordHash Hash della password
     * @param numeroTelefono Numero di telefono
     * @return Un'istanza di Acquirente
     */
    Acquirente creaAcquirente(
        String nome,
        String cognome,
        String email,
        String passwordHash,
        String numeroTelefono
    );
    
    /**
     * Crea un utente Produttore
     * 
     * @param nome Nome dell'utente
     * @param cognome Cognome dell'utente
     * @param email Email dell'utente
     * @param passwordHash Hash della password
     * @param numeroTelefono Numero di telefono
     * @param datiAzienda Dati dell'azienda associata
     * @return Un'istanza di Produttore
     */
    Produttore creaProduttore(
        String nome,
        String cognome,
        String email,
        String passwordHash,
        String numeroTelefono,
        DatiAzienda datiAzienda
    );
    
    /**
     * Crea un utente Trasformatore
     * 
     * @param nome Nome dell'utente
     * @param cognome Cognome dell'utente
     * @param email Email dell'utente
     * @param passwordHash Hash della password
     * @param numeroTelefono Numero di telefono
     * @param datiAzienda Dati dell'azienda associata
     * @return Un'istanza di Trasformatore
     */
    Trasformatore creaTrasformatore(
        String nome,
        String cognome,
        String email,
        String passwordHash,
        String numeroTelefono,
        DatiAzienda datiAzienda
    );
    
    /**
     * Crea un utente DistributoreDiTipicita
     * 
     * @param nome Nome dell'utente
     * @param cognome Cognome dell'utente
     * @param email Email dell'utente
     * @param passwordHash Hash della password
     * @param numeroTelefono Numero di telefono
     * @param datiAzienda Dati dell'azienda associata
     * @return Un'istanza di DistributoreDiTipicita
     */
    DistributoreDiTipicita creaDistributoreDiTipicita(
        String nome,
        String cognome,
        String email,
        String passwordHash,
        String numeroTelefono,
        DatiAzienda datiAzienda
    );
    
    /**
     * Crea un utente Curatore
     * 
     * @param nome Nome dell'utente
     * @param cognome Cognome dell'utente
     * @param email Email dell'utente
     * @param passwordHash Hash della password
     * @param numeroTelefono Numero di telefono
     * @return Un'istanza di Curatore
     */
    Curatore creaCuratore(
        String nome,
        String cognome,
        String email,
        String passwordHash,
        String numeroTelefono
    );
    
    /**
     * Crea un utente AnimatoreDellaFiliera
     * 
     * @param nome Nome dell'utente
     * @param cognome Cognome dell'utente
     * @param email Email dell'utente
     * @param passwordHash Hash della password
     * @param numeroTelefono Numero di telefono
     * @return Un'istanza di AnimatoreDellaFiliera
     */
    AnimatoreDellaFiliera creaAnimatoreDellaFiliera(
        String nome,
        String cognome,
        String email,
        String passwordHash,
        String numeroTelefono
    );
    
    /**
     * Crea un utente GestorePiattaforma
     * 
     * @param nome Nome dell'utente
     * @param cognome Cognome dell'utente
     * @param email Email dell'utente
     * @param passwordHash Hash della password
     * @param numeroTelefono Numero di telefono
     * @return Un'istanza di GestorePiattaforma
     */
    GestorePiattaforma creaGestorePiattaforma(
        String nome,
        String cognome,
        String email,
        String passwordHash,
        String numeroTelefono
    );
    
    /**
     * Metodo generico per creare un utente in base al tipo specificato.
     * Utile per scenari in cui il tipo di utente è determinato a runtime.
     * 
     * @param tipoRuolo Tipo di ruolo dell'utente da creare
     * @param nome Nome dell'utente
     * @param cognome Cognome dell'utente
     * @param email Email dell'utente
     * @param passwordHash Hash della password
     * @param numeroTelefono Numero di telefono
     * @param datiAzienda Dati dell'azienda (opzionale, può essere null per ruoli che non lo richiedono)
     * @return Un'istanza di Utente del tipo specificato
     * @throws IllegalArgumentException se il tipo di ruolo non è supportato
     */
    Utente creaUtente(
        TipoRuolo tipoRuolo,
        String nome,
        String cognome,
        String email,
        String passwordHash,
        String numeroTelefono,
        DatiAzienda datiAzienda
    );
}