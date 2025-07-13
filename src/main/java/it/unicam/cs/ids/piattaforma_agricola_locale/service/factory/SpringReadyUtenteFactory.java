package it.unicam.cs.ids.piattaforma_agricola_locale.service.factory;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IUtenteBaseRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.*;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder; 
import org.springframework.stereotype.Component;

/**
 * Implementazione del factory pattern per la creazione di utenti, integrata con
 * Spring Boot.
 * Questa classe è un componente Spring che utilizza l'iniezione delle
 * dipendenze.
 */
@Component
@Primary
public class SpringReadyUtenteFactory extends AbstractUtenteFactory {

    private final IUtenteBaseRepository utenteRepository;
    private final PasswordEncoder passwordEncoder; 

    /**
     * Costruttore che accetta un repository e un password encoder.
     * Utilizza l'iniezione delle dipendenze di Spring.
     *
     * @param utenteRepository Repository per la persistenza degli utenti
     * @param passwordEncoder  Encoder per le password
     */
    public SpringReadyUtenteFactory(IUtenteBaseRepository utenteRepository, PasswordEncoder passwordEncoder) { 
        this.utenteRepository = utenteRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public Acquirente creaAcquirente(
            String nome,
            String cognome,
            String email,
            String password, // <-- Ora riceviamo la password in chiaro
            String numeroTelefono) {

        return (Acquirente) creaUtente(TipoRuolo.ACQUIRENTE, nome, cognome, email, password, numeroTelefono, null);
    }

    @Override
    public Produttore creaProduttore(
            String nome,
            String cognome,
            String email,
            String password, // <-- Ora riceviamo la password in chiaro
            String numeroTelefono,
            DatiAzienda datiAzienda) {

        return (Produttore) creaUtente(TipoRuolo.PRODUTTORE, nome, cognome, email, password, numeroTelefono, datiAzienda);
    }

    @Override
    public Trasformatore creaTrasformatore(
            String nome,
            String cognome,
            String email,
            String password, // <-- Ora riceviamo la password in chiaro
            String numeroTelefono,
            DatiAzienda datiAzienda) {

        return (Trasformatore) creaUtente(TipoRuolo.TRASFORMATORE, nome, cognome, email, password, numeroTelefono, datiAzienda);
    }

    @Override
    public DistributoreDiTipicita creaDistributoreDiTipicita(
            String nome,
            String cognome,
            String email,
            String password, // <-- Ora riceviamo la password in chiaro
            String numeroTelefono,
            DatiAzienda datiAzienda) {

        return (DistributoreDiTipicita) creaUtente(TipoRuolo.DISTRIBUTORE_DI_TIPICITA, nome, cognome, email, password, numeroTelefono, datiAzienda);
    }

    @Override
    public Curatore creaCuratore(
            String nome,
            String cognome,
            String email,
            String password, 
            String numeroTelefono) {

        return (Curatore) creaUtente(TipoRuolo.CURATORE, nome, cognome, email, password, numeroTelefono, null);
    }

    @Override
    public AnimatoreDellaFiliera creaAnimatoreDellaFiliera(
            String nome,
            String cognome,
            String email,
            String password, 
            String numeroTelefono) {

        return (AnimatoreDellaFiliera) creaUtente(TipoRuolo.ANIMATORE_DELLA_FILIERA, nome, cognome, email, password, numeroTelefono, null);
    }

    @Override
    public GestorePiattaforma creaGestorePiattaforma(
            String nome,
            String cognome,
            String email,
            String password, 
            String numeroTelefono) {

        return (GestorePiattaforma) creaUtente(TipoRuolo.GESTORE_PIATTAFORMA, nome, cognome, email, password, numeroTelefono, null);
    }

    @Override
    public Utente creaUtente(
            TipoRuolo tipoRuolo,
            String nome,
            String cognome,
            String email,
            String password,
            String numeroTelefono,
            DatiAzienda datiAzienda) {

        validateUserData(nome, cognome, email, password); 
        checkEmailNotInUse(email);

        if (requiresDatiAzienda(tipoRuolo) && datiAzienda == null) {
            throw new IllegalArgumentException("I dati azienda sono richiesti per il ruolo " + tipoRuolo);
        }


        String passwordHash = passwordEncoder.encode(password);

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

    private void checkEmailNotInUse(String email) {
        if (utenteRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("L'email è già in uso");
        }
    }
}