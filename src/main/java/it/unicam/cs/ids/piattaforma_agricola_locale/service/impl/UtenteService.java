package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IUtenteBaseRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.*;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.factory.UtenteFactory;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IUtenteService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementazione del servizio di gestione degli utenti.
 * Utilizza il pattern Factory per la creazione degli utenti e il repository per la persistenza.
 */
public class UtenteService implements IUtenteService {
    
    private final UtenteFactory utenteFactory;
    private final IUtenteBaseRepository utenteRepository;
    
    /**
     * Costruttore che accetta una factory per la creazione degli utenti e un repository per la persistenza.
     * 
     * @param utenteFactory Factory per la creazione degli utenti
     * @param utenteRepository Repository per la persistenza degli utenti
     */
    public UtenteService(UtenteFactory utenteFactory, IUtenteBaseRepository utenteRepository) {
        this.utenteFactory = utenteFactory;
        this.utenteRepository = utenteRepository;
    }
    
    @Override
    public Acquirente registraAcquirente(String nome, String cognome, String email, String password, String numeroTelefono) {
        String passwordHash = hashPassword(password);
        return utenteFactory.creaAcquirente(nome, cognome, email, passwordHash, numeroTelefono);
    }
    
    @Override
    public Produttore registraProduttore(String nome, String cognome, String email, String password, String numeroTelefono, DatiAzienda datiAzienda) {
        String passwordHash = hashPassword(password);
        return utenteFactory.creaProduttore(nome, cognome, email, passwordHash, numeroTelefono, datiAzienda);
    }
    
    @Override
    public Trasformatore registraTrasformatore(String nome, String cognome, String email, String password, String numeroTelefono, DatiAzienda datiAzienda) {
        String passwordHash = hashPassword(password);
        return utenteFactory.creaTrasformatore(nome, cognome, email, passwordHash, numeroTelefono, datiAzienda);
    }
    
    @Override
    public DistributoreDiTipicita registraDistributoreDiTipicita(String nome, String cognome, String email, String password, String numeroTelefono, DatiAzienda datiAzienda) {
        String passwordHash = hashPassword(password);
        return utenteFactory.creaDistributoreDiTipicita(nome, cognome, email, passwordHash, numeroTelefono, datiAzienda);
    }
    
    @Override
    public Curatore registraCuratore(String nome, String cognome, String email, String password, String numeroTelefono) {
        String passwordHash = hashPassword(password);
        return utenteFactory.creaCuratore(nome, cognome, email, passwordHash, numeroTelefono);
    }
    
    @Override
    public AnimatoreDellaFiliera registraAnimatoreDellaFiliera(String nome, String cognome, String email, String password, String numeroTelefono) {
        String passwordHash = hashPassword(password);
        return utenteFactory.creaAnimatoreDellaFiliera(nome, cognome, email, passwordHash, numeroTelefono);
    }
    
    @Override
    public GestorePiattaforma registraGestorePiattaforma(String nome, String cognome, String email, String password, String numeroTelefono) {
        String passwordHash = hashPassword(password);
        return utenteFactory.creaGestorePiattaforma(nome, cognome, email, passwordHash, numeroTelefono);
    }
    
    @Override
    public Optional<Utente> autenticaUtente(String email, String password) {
        Optional<Utente> utenteOpt = utenteRepository.findByEmail(email);
        
        if (utenteOpt.isPresent()) {
            Utente utente = utenteOpt.get();
            if (verificaPassword(password, utente.getPasswordHash()) && utente.isAttivo()) {
                return Optional.of(utente);
            }
        }
        
        return Optional.empty();
    }
    
    @Override
    public Optional<Utente> trovaUtentePerID(Long id) {
        return utenteRepository.findById(id);
    }
    
    @Override
    public Optional<Utente> trovaUtentePerEmail(String email) {
        return utenteRepository.findByEmail(email);
    }
    
    @Override
    public List<Utente> trovaTuttiGliUtenti() {
        return utenteRepository.findAll();
    }
    
    @Override
    public List<Utente> trovaUtentiPerTipo(TipoRuolo tipoRuolo) {
        return utenteRepository.findAll().stream()
                .filter(u -> u.getTipoRuolo() == tipoRuolo)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<Utente> aggiornaStatoAccreditamento(Long idUtente, StatoAccreditamento statoAccreditamento) {
        Optional<Utente> utenteOpt = utenteRepository.findById(idUtente);
        
        if (utenteOpt.isPresent()) {
            Utente utente = utenteOpt.get();
            
            // Aggiorna lo stato di accreditamento in base al tipo di utente
            if (utente instanceof Venditore) {
                ((Venditore) utente).setStatoAccreditamento(statoAccreditamento);
            } else if (utente instanceof Curatore) {
                ((Curatore) utente).setStatoAccreditamento(statoAccreditamento);
            } else if (utente instanceof AnimatoreDellaFiliera) {
                ((AnimatoreDellaFiliera) utente).setStatoAccreditamento(statoAccreditamento);
            } else {
                // Altri tipi di utenti non hanno stato di accreditamento
                return Optional.empty();
            }
            
            return Optional.of(utenteRepository.save(utente));
        }
        
        return Optional.empty();
    }
    
    @Override
    public boolean disattivaAccount(Long idUtente) {
        Optional<Utente> utenteOpt = utenteRepository.findById(idUtente);
        
        if (utenteOpt.isPresent()) {
            Utente utente = utenteOpt.get();
            utente.disattivaAccount();
            utenteRepository.save(utente);
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean riattivaAccount(Long idUtente) {
        Optional<Utente> utenteOpt = utenteRepository.findById(idUtente);
        
        if (utenteOpt.isPresent()) {
            Utente utente = utenteOpt.get();
            utente.riattivaAccount();
            utenteRepository.save(utente);
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean modificaPassword(Long idUtente, String vecchiaPassword, String nuovaPassword) {
        Optional<Utente> utenteOpt = utenteRepository.findById(idUtente);
        
        if (utenteOpt.isPresent()) {
            Utente utente = utenteOpt.get();
            
            // Verifica che la vecchia password sia corretta
            if (verificaPassword(vecchiaPassword, utente.getPasswordHash())) {
                // Aggiorna la password
                utente.setPasswordHash(hashPassword(nuovaPassword));
                utenteRepository.save(utente);
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Genera un hash della password.
     * In un'implementazione reale, si utilizzerebbe un algoritmo di hashing sicuro come BCrypt.
     * 
     * @param password Password in chiaro
     * @return Hash della password
     */
    private String hashPassword(String password) {
        // Implementazione semplificata per scopi dimostrativi
        // In un'applicazione reale, si utilizzerebbe BCrypt o un altro algoritmo sicuro
        return "hashed_" + password;
    }
    
    /**
     * Verifica se la password in chiaro corrisponde all'hash memorizzato.
     * 
     * @param password Password in chiaro
     * @param passwordHash Hash della password memorizzato
     * @return true se la password è corretta, false altrimenti
     */
    private boolean verificaPassword(String password, String passwordHash) {
        // Implementazione semplificata per scopi dimostrativi
        // In un'applicazione reale, si utilizzerebbe BCrypt o un altro algoritmo sicuro
        return passwordHash.equals("hashed_" + password);
    }
}