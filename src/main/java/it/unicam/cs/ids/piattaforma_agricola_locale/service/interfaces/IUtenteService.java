package it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.*;
import java.util.List;
import java.util.Optional;

/**
 * Interfaccia per il servizio di gestione degli utenti.
 * Definisce le operazioni di base per la gestione degli utenti.
 */
public interface IUtenteService {
    
    /**
     * Registra un nuovo utente acquirente.
     * 
     * @param nome Nome dell'utente
     * @param cognome Cognome dell'utente
     * @param email Email dell'utente
     * @param password Password in chiaro (verrà hashata)
     * @param numeroTelefono Numero di telefono
     * @return L'utente creato
     */
    Acquirente registraAcquirente(String nome, String cognome, String email, String password, String numeroTelefono);
    
    /**
     * Registra un nuovo utente produttore.
     * 
     * @param nome Nome dell'utente
     * @param cognome Cognome dell'utente
     * @param email Email dell'utente
     * @param password Password in chiaro (verrà hashata)
     * @param numeroTelefono Numero di telefono
     * @param datiAzienda Dati dell'azienda
     * @return L'utente creato
     */
    Produttore registraProduttore(String nome, String cognome, String email, String password, String numeroTelefono, DatiAzienda datiAzienda);
    
    /**
     * Registra un nuovo utente trasformatore.
     * 
     * @param nome Nome dell'utente
     * @param cognome Cognome dell'utente
     * @param email Email dell'utente
     * @param password Password in chiaro (verrà hashata)
     * @param numeroTelefono Numero di telefono
     * @param datiAzienda Dati dell'azienda
     * @return L'utente creato
     */
    Trasformatore registraTrasformatore(String nome, String cognome, String email, String password, String numeroTelefono, DatiAzienda datiAzienda);
    
    /**
     * Registra un nuovo utente distributore di tipicità.
     * 
     * @param nome Nome dell'utente
     * @param cognome Cognome dell'utente
     * @param email Email dell'utente
     * @param password Password in chiaro (verrà hashata)
     * @param numeroTelefono Numero di telefono
     * @param datiAzienda Dati dell'azienda
     * @return L'utente creato
     */
    DistributoreDiTipicita registraDistributoreDiTipicita(String nome, String cognome, String email, String password, String numeroTelefono, DatiAzienda datiAzienda);
    
    /**
     * Registra un nuovo utente curatore.
     * 
     * @param nome Nome dell'utente
     * @param cognome Cognome dell'utente
     * @param email Email dell'utente
     * @param password Password in chiaro (verrà hashata)
     * @param numeroTelefono Numero di telefono
     * @return L'utente creato
     */
    Curatore registraCuratore(String nome, String cognome, String email, String password, String numeroTelefono);
    
    /**
     * Registra un nuovo utente animatore della filiera.
     * 
     * @param nome Nome dell'utente
     * @param cognome Cognome dell'utente
     * @param email Email dell'utente
     * @param password Password in chiaro (verrà hashata)
     * @param numeroTelefono Numero di telefono
     * @return L'utente creato
     */
    AnimatoreDellaFiliera registraAnimatoreDellaFiliera(String nome, String cognome, String email, String password, String numeroTelefono);
    
    /**
     * Registra un nuovo utente gestore piattaforma.
     * 
     * @param nome Nome dell'utente
     * @param cognome Cognome dell'utente
     * @param email Email dell'utente
     * @param password Password in chiaro (verrà hashata)
     * @param numeroTelefono Numero di telefono
     * @return L'utente creato
     */
    GestorePiattaforma registraGestorePiattaforma(String nome, String cognome, String email, String password, String numeroTelefono);
    
    /**
     * Autentica un utente con email e password.
     * 
     * @param email Email dell'utente
     * @param password Password in chiaro
     * @return L'utente autenticato, se le credenziali sono corrette
     */
    Optional<Utente> autenticaUtente(String email, String password);
    
    /**
     * Trova un utente per ID.
     * 
     * @param id ID dell'utente
     * @return L'utente trovato, se esiste
     */
    Optional<Utente> trovaUtentePerID(Long id);
    
    /**
     * Trova un utente per email.
     * 
     * @param email Email dell'utente
     * @return L'utente trovato, se esiste
     */
    Optional<Utente> trovaUtentePerEmail(String email);
    
    /**
     * Trova tutti gli utenti.
     * 
     * @return Lista di tutti gli utenti
     */
    List<Utente> trovaTuttiGliUtenti();
    
    /**
     * Trova tutti gli utenti di un determinato tipo.
     * 
     * @param tipoRuolo Tipo di ruolo degli utenti da trovare
     * @return Lista degli utenti del tipo specificato
     */
    List<Utente> trovaUtentiPerTipo(TipoRuolo tipoRuolo);
    
    /**
     * Aggiorna lo stato di accreditamento di un utente.
     * 
     * @param idUtente ID dell'utente
     * @param statoAccreditamento Nuovo stato di accreditamento
     * @return L'utente aggiornato, se esiste
     */
    Optional<Utente> aggiornaStatoAccreditamento(Long idUtente, StatoAccreditamento statoAccreditamento);
    
    /**
     * Disattiva un account utente.
     * 
     * @param idUtente ID dell'utente
     * @return true se l'operazione è riuscita, false altrimenti
     */
    boolean disattivaAccount(Long idUtente);
    
    /**
     * Riattiva un account utente.
     * 
     * @param idUtente ID dell'utente
     * @return true se l'operazione è riuscita, false altrimenti
     */
    boolean riattivaAccount(Long idUtente);
    
    /**
     * Modifica la password di un utente.
     * 
     * @param idUtente ID dell'utente
     * @param vecchiaPassword Vecchia password in chiaro
     * @param nuovaPassword Nuova password in chiaro
     * @return true se l'operazione è riuscita, false altrimenti
     */
    boolean modificaPassword(Long idUtente, String vecchiaPassword, String nuovaPassword);
}