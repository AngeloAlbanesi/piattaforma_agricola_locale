package it.unicam.cs.ids.piattaforma_agricola_locale.service.factory;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IUtenteBaseRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.*;

/**
 * Implementazione del factory pattern per la creazione di utenti, pronta per l'integrazione con Spring Boot.
 * Questa classe è progettata per essere facilmente convertita in un componente Spring.
 * 
 * Quando il progetto verrà migrato a Spring Boot, sarà sufficiente aggiungere le annotazioni
 * @Component o @Service e @Autowired per l'iniezione delle dipendenze.
 */
public class SpringReadyUtenteFactory extends AbstractUtenteFactory {
    
    private final IUtenteBaseRepository utenteRepository;
    
    /**
     * Costruttore che accetta un repository per la persistenza degli utenti.
     * In un contesto Spring, questo costruttore sarà utilizzato per l'iniezione delle dipendenze.
     * 
     * @param utenteRepository Repository per la persistenza degli utenti
     */
    public SpringReadyUtenteFactory(IUtenteBaseRepository utenteRepository) {
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
        checkEmailNotInUse(email);
        
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
        checkEmailNotInUse(email);
        
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
        checkEmailNotInUse(email);
        
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
        checkEmailNotInUse(email);
        
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
        checkEmailNotInUse(email);
        
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
        checkEmailNotInUse(email);
        
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
        checkEmailNotInUse(email);
        
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
        checkEmailNotInUse(email);
        
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
     * Verifica che l'email non sia già in uso.
     * 
     * @param email Email da verificare
     * @throws IllegalArgumentException se l'email è già in uso
     */
    private void checkEmailNotInUse(String email) {
        if (utenteRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("L'email è già in uso");
        }
    }
}