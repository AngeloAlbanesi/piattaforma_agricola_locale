package it.unicam.cs.ids.piattaforma_agricola_locale.service.factory;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.*;

/**
 * Classe astratta che implementa metodi comuni per la creazione di utenti.
 * Fornisce implementazioni di base e validazioni comuni per tutte le factory concrete.
 */
public abstract class AbstractUtenteFactory implements UtenteFactory {
    
    /**
     * Verifica se il tipo di ruolo specificato richiede dati aziendali.
     * 
     * @param tipoRuolo Il tipo di ruolo da verificare
     * @return true se il ruolo richiede dati aziendali, false altrimenti
     */
    protected boolean requiresDatiAzienda(TipoRuolo tipoRuolo) {
        return tipoRuolo == TipoRuolo.PRODUTTORE || 
               tipoRuolo == TipoRuolo.TRASFORMATORE || 
               tipoRuolo == TipoRuolo.DISTRIBUTORE_DI_TIPICITA;
    }
    
    /**
     * Valida i dati base dell'utente.
     * 
     * @param nome Nome dell'utente
     * @param cognome Cognome dell'utente
     * @param email Email dell'utente
     * @param passwordHash Hash della password
     * @throws IllegalArgumentException se i dati non sono validi
     */
    protected void validateUserData(String nome, String cognome, String email, String passwordHash) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome non può essere vuoto");
        }
        
        if (cognome == null || cognome.trim().isEmpty()) {
            throw new IllegalArgumentException("Il cognome non può essere vuoto");
        }
        
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("L'email non può essere vuota");
        }
        
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Formato email non valido");
        }
        
        if (passwordHash == null || passwordHash.trim().isEmpty()) {
            throw new IllegalArgumentException("La password non può essere vuota");
        }
    }
    
    /**
     * Valida i dati dell'azienda.
     * 
     * @param datiAzienda Dati dell'azienda da validare
     * @throws IllegalArgumentException se i dati non sono validi
     */
    protected void validateDatiAzienda(DatiAzienda datiAzienda) {
        if (datiAzienda == null) {
            throw new IllegalArgumentException("I dati azienda non possono essere null");
        }
        
        if (datiAzienda.getNomeAzienda() == null || datiAzienda.getNomeAzienda().trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome dell'azienda non può essere vuoto");
        }
        
        if (datiAzienda.getPartitaIva() == null || datiAzienda.getPartitaIva().trim().isEmpty()) {
            throw new IllegalArgumentException("La partita IVA non può essere vuota");
        }
        
        if (!isValidPartitaIva(datiAzienda.getPartitaIva())) {
            throw new IllegalArgumentException("Formato partita IVA non valido");
        }
    }
    
    /**
     * Verifica se l'email ha un formato valido.
     * 
     * @param email Email da verificare
     * @return true se l'email ha un formato valido, false altrimenti
     */
    protected boolean isValidEmail(String email) {
        // Implementazione semplificata, in un caso reale si userebbe una regex più complessa
        return email != null && email.contains("@") && email.contains(".");
    }
    
    /**
     * Verifica se la partita IVA ha un formato valido.
     * 
     * @param partitaIva Partita IVA da verificare
     * @return true se la partita IVA ha un formato valido, false altrimenti
     */
    protected boolean isValidPartitaIva(String partitaIva) {
        // Implementazione semplificata per partita IVA italiana (11 cifre)
        return partitaIva != null && partitaIva.matches("\\d{11}");
    }
}