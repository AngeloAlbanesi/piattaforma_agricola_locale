package it.unicam.cs.ids.piattaforma_agricola_locale.service.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IUtenteBaseRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.*;

/**
 * Implementazione concreta del factory pattern per la creazione di utenti.
 * Questa classe fornisce metodi type-safe per creare diversi tipi di utenti
 * e si integra con il repository per la persistenza.
 */
@Component
public class SimpleUtenteFactory extends AbstractUtenteFactory {
    
    private final IUtenteBaseRepository utenteRepository;
    
    /**
     * Costruttore che accetta un repository per la persistenza degli utenti.
     * 
     * @param utenteRepository Repository per la persistenza degli utenti
     */
    @Autowired
    public SimpleUtenteFactory(IUtenteBaseRepository utenteRepository) {
        this.utenteRepository = utenteRepository;
    }
    
    @Override
    public Acquirente creaAcquirente(
            String nome,
            String cognome,
            String email,
            String passwordHash,
            String numeroTelefono) {
        
        validateUserData(nome, cognome, email, passwordHash);
        
        Acquirente acquirente = new Acquirente(
                nome, 
                cognome, 
                email, 
                passwordHash, 
                numeroTelefono, 
                TipoRuolo.ACQUIRENTE);
        
        return (Acquirente) utenteRepository.save(acquirente);
    }

    @Override
    public Produttore creaProduttore(
            String nome,
            String cognome,
            String email,
            String passwordHash,
            String numeroTelefono,
            DatiAzienda datiAzienda) {
        
        validateUserData(nome, cognome, email, passwordHash);
        validateDatiAzienda(datiAzienda);
        
        Produttore produttore = new Produttore(
                nome, 
                cognome, 
                email, 
                passwordHash, 
                numeroTelefono, 
                datiAzienda, 
                TipoRuolo.PRODUTTORE);
        
        return (Produttore) utenteRepository.save(produttore);
    }

    @Override
    public Trasformatore creaTrasformatore(
            String nome,
            String cognome,
            String email,
            String passwordHash,
            String numeroTelefono,
            DatiAzienda datiAzienda) {
        
        validateUserData(nome, cognome, email, passwordHash);
        validateDatiAzienda(datiAzienda);
        
        Trasformatore trasformatore = new Trasformatore(
                nome, 
                cognome, 
                email, 
                passwordHash, 
                numeroTelefono, 
                datiAzienda, 
                TipoRuolo.TRASFORMATORE);
        
        return (Trasformatore) utenteRepository.save(trasformatore);
    }

    @Override
    public DistributoreDiTipicita creaDistributoreDiTipicita(
            String nome,
            String cognome,
            String email,
            String passwordHash,
            String numeroTelefono,
            DatiAzienda datiAzienda) {
        
        validateUserData(nome, cognome, email, passwordHash);
        validateDatiAzienda(datiAzienda);
        
        DistributoreDiTipicita distributore = new DistributoreDiTipicita(
                nome, 
                cognome, 
                email, 
                passwordHash, 
                numeroTelefono, 
                datiAzienda, 
                TipoRuolo.DISTRIBUTORE_DI_TIPICITA);
        
        return (DistributoreDiTipicita) utenteRepository.save(distributore);
    }

    @Override
    public Curatore creaCuratore(
            String nome,
            String cognome,
            String email,
            String passwordHash,
            String numeroTelefono) {
        
        validateUserData(nome, cognome, email, passwordHash);
        
        Curatore curatore = new Curatore(
                nome, 
                cognome, 
                email, 
                passwordHash, 
                numeroTelefono, 
                TipoRuolo.CURATORE);
        
        return (Curatore) utenteRepository.save(curatore);
    }

    @Override
    public AnimatoreDellaFiliera creaAnimatoreDellaFiliera(
            String nome,
            String cognome,
            String email,
            String passwordHash,
            String numeroTelefono) {
        
        validateUserData(nome, cognome, email, passwordHash);
        
        AnimatoreDellaFiliera animatore = new AnimatoreDellaFiliera(
                nome, 
                cognome, 
                email, 
                passwordHash, 
                numeroTelefono, 
                TipoRuolo.ANIMATORE_DELLA_FILIERA);
        
        return (AnimatoreDellaFiliera) utenteRepository.save(animatore);
    }

    @Override
    public GestorePiattaforma creaGestorePiattaforma(
            String nome,
            String cognome,
            String email,
            String passwordHash,
            String numeroTelefono) {
        
        validateUserData(nome, cognome, email, passwordHash);
        
        GestorePiattaforma gestore = new GestorePiattaforma(
                nome, 
                cognome, 
                email, 
                passwordHash, 
                numeroTelefono, 
                TipoRuolo.GESTORE_PIATTAFORMA);
        
        return (GestorePiattaforma) utenteRepository.save(gestore);
    }

    @Override
    public Utente creaUtente(
            TipoRuolo tipoRuolo,
            String nome,
            String cognome,
            String email,
            String passwordHash,
            String numeroTelefono,
            DatiAzienda datiAzienda) {
        
        validateUserData(nome, cognome, email, passwordHash);
        
        // Verifica che i dati azienda siano forniti per i ruoli che li richiedono
        if (requiresDatiAzienda(tipoRuolo) && datiAzienda == null) {
            throw new IllegalArgumentException("I dati azienda sono richiesti per il ruolo " + tipoRuolo);
        }
        
        Utente utente;
        
        switch (tipoRuolo) {
            case ACQUIRENTE:
                utente = new Acquirente(nome, cognome, email, passwordHash, numeroTelefono, tipoRuolo);
                break;
            case PRODUTTORE:
                validateDatiAzienda(datiAzienda);
                utente = new Produttore(nome, cognome, email, passwordHash, numeroTelefono, datiAzienda, tipoRuolo);
                break;
            case TRASFORMATORE:
                validateDatiAzienda(datiAzienda);
                utente = new Trasformatore(nome, cognome, email, passwordHash, numeroTelefono, datiAzienda, tipoRuolo);
                break;
            case DISTRIBUTORE_DI_TIPICITA:
                validateDatiAzienda(datiAzienda);
                utente = new DistributoreDiTipicita(nome, cognome, email, passwordHash, numeroTelefono, datiAzienda, tipoRuolo);
                break;
            case CURATORE:
                utente = new Curatore(nome, cognome, email, passwordHash, numeroTelefono, tipoRuolo);
                break;
            case ANIMATORE_DELLA_FILIERA:
                utente = new AnimatoreDellaFiliera(nome, cognome, email, passwordHash, numeroTelefono, tipoRuolo);
                break;
            case GESTORE_PIATTAFORMA:
                utente = new GestorePiattaforma(nome, cognome, email, passwordHash, numeroTelefono, tipoRuolo);
                break;
            default:
                throw new IllegalArgumentException("Tipo di ruolo non supportato: " + tipoRuolo);
        }
        
        return utenteRepository.save(utente);
    }
    
    /**
     * Estende la validazione dell'utente per verificare anche se l'email è già in uso.
     * 
     * @param nome Nome dell'utente
     * @param cognome Cognome dell'utente
     * @param email Email dell'utente
     * @param passwordHash Hash della password
     * @throws IllegalArgumentException se i dati non sono validi o l'email è già in uso
     */
    @Override
    protected void validateUserData(String nome, String cognome, String email, String passwordHash) {
        // Chiama il metodo della classe padre per la validazione di base
        super.validateUserData(nome, cognome, email, passwordHash);
        
        // Verifica aggiuntiva specifica per questa implementazione: email già in uso
        if (utenteRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("L'email è già in uso");
        }
    }
}