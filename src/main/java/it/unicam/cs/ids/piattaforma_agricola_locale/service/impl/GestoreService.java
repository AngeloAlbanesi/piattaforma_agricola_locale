package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.*;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.*;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IGestoreService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GestoreService implements IGestoreService {


    private final UtenteBaseRepository utenteBaseRepository;
    private final VenditoreRepository venditoreRepository;
    private final CuratoreRepository curatoreRepository;
    private final AnimatoreRepository animatoreRepository;
    private final DatiAziendaRepository datiAziendaRepository; // Ancora utile per visualizzare info

    public GestoreService(UtenteBaseRepository utenteBaseRepository,
                          VenditoreRepository venditoreRepository,
                          CuratoreRepository curatoreRepository,
                          AnimatoreRepository animatoreRepository,
                          DatiAziendaRepository datiAziendaRepository) {
        this.utenteBaseRepository = utenteBaseRepository;
        this.venditoreRepository = venditoreRepository;
        this.curatoreRepository = curatoreRepository;
        this.animatoreRepository = animatoreRepository;
        this.datiAziendaRepository = datiAziendaRepository;
    }

    // --- Metodi per visualizzare richieste di accreditamento ---

    @Override
    public List<Venditore> getVenditoriInAttesaDiAccreditamento() {
        // Il chiamante dovrà poi, se necessario, recuperare i DatiAzienda separatamente
        // usando venditore.getId() e il datiAziendaRepository.
        return venditoreRepository.findByStatoAccreditamento(StatoAccreditamento.PENDING);
    }

    @Override
    public List<Curatore> getCuratoriInAttesaDiAccreditamento() {
        return curatoreRepository.findByStatoAccreditamento(StatoAccreditamento.PENDING);
    }

    @Override
    public List<AnimatoreDellaFiliera> getAnimatoriInAttesaDiAccreditamento() {
        return animatoreRepository.findByStatoAccreditamento(StatoAccreditamento.PENDING);
    }

    // --- Metodi per gestire l'accreditamento ---

    @Override
    public boolean aggiornaStatoAccreditamentoVenditore(Long venditoreId, StatoAccreditamento nuovoStato) {
        Optional<Venditore> venditoreOpt = venditoreRepository.findById(venditoreId);
        if (venditoreOpt.isPresent()) {
            Venditore venditore = venditoreOpt.get();
            venditore.setStatoAccreditamento(nuovoStato);
            venditoreRepository.save(venditore);
            System.out.println("Stato accreditamento venditore " + venditore.getEmail() + " aggiornato a " + nuovoStato);
            // Qui potresti voler inviare una notifica, loggare, ecc.
            return true;
        }
        System.err.println("Venditore con ID " + venditoreId + " non trovato.");
        return false;
    }

    @Override
    public boolean aggiornaStatoAccreditamentoCuratore(Long curatoreId, StatoAccreditamento nuovoStato) {
        Optional<Curatore> curatoreOpt = curatoreRepository.findById(curatoreId);
        if (curatoreOpt.isPresent()) {
            Curatore curatore = curatoreOpt.get();
            curatore.setStatoAccreditamento(nuovoStato);
            curatoreRepository.save(curatore);
            System.out.println("Stato accreditamento curatore " + curatore.getEmail() + " aggiornato a " + nuovoStato);
            return true;
        }
        System.err.println("Curatore con ID " + curatoreId + " non trovato.");
        return false;
    }

    @Override
    public boolean aggiornaStatoAccreditamentoAnimatore(Long animatoreId, StatoAccreditamento nuovoStato) {
        Optional<AnimatoreDellaFiliera> animatoreOpt = animatoreRepository.findById(animatoreId);
        if (animatoreOpt.isPresent()) {
            AnimatoreDellaFiliera animatore = animatoreOpt.get();
            animatore.setStatoAccreditamento(nuovoStato);
            animatoreRepository.save(animatore);
            System.out.println("Stato accreditamento animatore " + animatore.getEmail() + " aggiornato a " + nuovoStato);
            return true;
        }
        System.err.println("Animatore con ID " + animatoreId + " non trovato.");
        return false;
    }

    // --- Metodi per gestire lo stato attivo/disattivo degli utenti ---
    // Come discusso, la gestione di isAttivo può essere complessa con repository separati
    // se si vuole un metodo generico. Qui forniamo metodi specifici per tipo per chiarezza.

    @Override
    public boolean attivaDisattivaAcquirente(Long acquirenteId, boolean attivo) {
        // Assumendo che UtenteBaseRepository gestisca Acquirente
        Optional<Utente> utenteOpt = utenteBaseRepository.findById(acquirenteId);
        if (utenteOpt.isPresent() && utenteOpt.get() instanceof Acquirente) {
            Acquirente acquirente = (Acquirente) utenteOpt.get();
            acquirente.setAttivo(attivo);
            utenteBaseRepository.save(acquirente); // Salva l'acquirente modificato
            System.out.println("Acquirente " + acquirente.getEmail() + (attivo ? " attivato." : " disattivato."));
            return true;
        }
        System.err.println("Acquirente con ID " + acquirenteId + " non trovato.");
        return false;
    }

    @Override
    public boolean attivaDisattivaVenditore(Long venditoreId, boolean attivo) {
        Optional<Venditore> venditoreOpt = venditoreRepository.findById(venditoreId);
        if (venditoreOpt.isPresent()) {
            Venditore venditore = venditoreOpt.get();
            venditore.setAttivo(attivo);
            venditoreRepository.save(venditore);
            System.out.println("Venditore " + venditore.getEmail() + (attivo ? " attivato." : " disattivato."));
            return true;
        }
        System.err.println("Venditore con ID " + venditoreId + " non trovato.");
        return false;
    }

    @Override
    public boolean attivaDisattivaCuratore(Long curatoreId, boolean attivo) {
        Optional<Curatore> curatoreOpt = curatoreRepository.findById(curatoreId);
        if (curatoreOpt.isPresent()) {
            Curatore curatore = curatoreOpt.get();
            curatore.setAttivo(attivo);
            curatoreRepository.save(curatore);
            System.out.println("Curatore " + curatore.getEmail() + (attivo ? " attivato." : " disattivato."));
            return true;
        }
        System.err.println("Curatore con ID " + curatoreId + " non trovato.");
        return false;
    }

    @Override
    public boolean attivaDisattivaAnimatore(Long animatoreId, boolean attivo) {
        Optional<AnimatoreDellaFiliera> animatoreOpt = animatoreRepository.findById(animatoreId);
        if (animatoreOpt.isPresent()) {
            AnimatoreDellaFiliera animatore = animatoreOpt.get();
            animatore.setAttivo(attivo);
            animatoreRepository.save(animatore);
            System.out.println("Animatore " + animatore.getEmail() + (attivo ? " attivato." : " disattivato."));
            return true;
        }
        System.err.println("Animatore con ID " + animatoreId + " non trovato.");
        return false;
    }


    // --- Metodi di utilità/visualizzazione generale ---

    @Override
    public Optional<DatiAzienda> getDatiAziendaPerVenditore(Long venditoreId) {
        // Questo presuppone che DatiAzienda abbia un campo come idUtente o che ci sia
        // un modo per collegarlo. Se Venditore ha un riferimento diretto a DatiAzienda o al suo ID,
        // potresti recuperarlo diversamente.
        // Esempio: se DatiAzienda ha findByUtenteId(Long utenteId)
        return datiAziendaRepository.findById(venditoreId);
        // Oppure, se Venditore ha un campo datiAziendaId:
        // Optional<Venditore> vOpt = venditoreRepository.findById(venditoreId);
        // if (vOpt.isPresent() && vOpt.get().getDatiAziendaId() != null) {
        //     return datiAziendaRepository.findById(vOpt.get().getDatiAziendaId());
        // }
        // return Optional.empty();
    }

    @Override
    public List<Utente> getTuttiGliUtenti() {
        List<Utente> tuttiGliUtenti = new ArrayList<>();
        // Assumendo che UtenteBaseRepository.findAll() restituisca List<Utente>
        // e che contenga Acquirenti e Gestori
        tuttiGliUtenti.addAll(utenteBaseRepository.findAll());
        tuttiGliUtenti.addAll(venditoreRepository.findAll());
        tuttiGliUtenti.addAll(curatoreRepository.findAll());
        tuttiGliUtenti.addAll(animatoreRepository.findAll());
        // Se hai un GestorePiattaformaRepository separato e non è in UtenteBaseRepository:
        // tuttiGliUtenti.addAll(gestorePiattaformaRepository.findAll());
        return tuttiGliUtenti;
    }

    @Override
    public List<Utente> getTuttiGliUtentiAttivi() {
        return getTuttiGliUtenti().stream()
                .filter(Utente::isAttivo) // Assumendo che Utente abbia isAttivo()
                .collect(Collectors.toList());
    }
}


