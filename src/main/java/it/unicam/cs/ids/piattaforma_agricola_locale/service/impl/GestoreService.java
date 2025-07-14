package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.*;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.*;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IGestoreService;

@Service
public class GestoreService implements IGestoreService {

    private final IUtenteBaseRepository utenteBaseRepository;
    private final IVenditoreRepository venditoreRepository;
    private final ICuratoreRepository curatoreRepository;
    private final IAnimatoreRepository animatoreRepository;
    private final IDatiAziendaRepository datiAziendaRepository;

    @Autowired
    public GestoreService(IUtenteBaseRepository utenteBaseRepository,
            IVenditoreRepository venditoreRepository,
            ICuratoreRepository curatoreRepository,
            IAnimatoreRepository animatoreRepository,
            IDatiAziendaRepository datiAziendaRepository) {
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
            return true;
        }
        return false;
    }

    @Override
    public boolean aggiornaStatoAccreditamentoCuratore(Long curatoreId, StatoAccreditamento nuovoStato) {
        Optional<Curatore> curatoreOpt = curatoreRepository.findById(curatoreId);
        if (curatoreOpt.isPresent()) {
            Curatore curatore = curatoreOpt.get();
            curatore.setStatoAccreditamento(nuovoStato);
            curatoreRepository.save(curatore);
            return true;
        }
        return false;
    }

    @Override
    public boolean aggiornaStatoAccreditamentoAnimatore(Long animatoreId, StatoAccreditamento nuovoStato) {
        Optional<AnimatoreDellaFiliera> animatoreOpt = animatoreRepository.findById(animatoreId);
        if (animatoreOpt.isPresent()) {
            AnimatoreDellaFiliera animatore = animatoreOpt.get();
            animatore.setStatoAccreditamento(nuovoStato);
            animatoreRepository.save(animatore);
            return true;
        }
        return false;
    }

    // --- Metodi per gestire lo stato attivo/disattivo degli utenti ---

    @Override
    public boolean attivaDisattivaAcquirente(Long acquirenteId, boolean attivo) {
        // Assumendo che UtenteBaseRepository gestisca Acquirente
        Optional<Utente> utenteOpt = utenteBaseRepository.findById(acquirenteId);
        if (utenteOpt.isPresent() && utenteOpt.get() instanceof Acquirente) {
            Acquirente acquirente = (Acquirente) utenteOpt.get();
            acquirente.setAttivo(attivo);
            utenteBaseRepository.save(acquirente);
            return true;
        }
        return false;
    }

    @Override
    public boolean attivaDisattivaVenditore(Long venditoreId, boolean attivo) {
        Optional<Venditore> venditoreOpt = venditoreRepository.findById(venditoreId);
        if (venditoreOpt.isPresent()) {
            Venditore venditore = venditoreOpt.get();
            venditore.setAttivo(attivo);
            venditoreRepository.save(venditore);
            return true;
        }
        return false;
    }

    @Override
    public boolean attivaDisattivaCuratore(Long curatoreId, boolean attivo) {
        Optional<Curatore> curatoreOpt = curatoreRepository.findById(curatoreId);
        if (curatoreOpt.isPresent()) {
            Curatore curatore = curatoreOpt.get();
            curatore.setAttivo(attivo);
            curatoreRepository.save(curatore);
            return true;
        }
        return false;
    }

    @Override
    public boolean attivaDisattivaAnimatore(Long animatoreId, boolean attivo) {
        Optional<AnimatoreDellaFiliera> animatoreOpt = animatoreRepository.findById(animatoreId);
        if (animatoreOpt.isPresent()) {
            AnimatoreDellaFiliera animatore = animatoreOpt.get();
            animatore.setAttivo(attivo);
            animatoreRepository.save(animatore);
            return true;
        }
        return false;
    }

    // --- Metodi di utilità/visualizzazione generale ---

    @Override
    public Optional<DatiAzienda> getDatiAziendaPerVenditore(Long venditoreId) {

        return datiAziendaRepository.findById(venditoreId);

    }

    @Override
    public List<Utente> getTuttiGliUtenti() {
        List<Utente> tuttiGliUtenti = new ArrayList<>();

        tuttiGliUtenti.addAll(utenteBaseRepository.findAll());
        tuttiGliUtenti.addAll(venditoreRepository.findAll());
        tuttiGliUtenti.addAll(curatoreRepository.findAll());
        tuttiGliUtenti.addAll(animatoreRepository.findAll());

        return tuttiGliUtenti;
    }

    @Override
    public List<Utente> getTuttiGliUtentiAttivi() {
        return getTuttiGliUtenti().stream()
                .filter(Utente::isAttivo) // Assumendo che Utente abbia isAttivo()
                .collect(Collectors.toList());
    }
}
