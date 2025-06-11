package it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface IGestoreService {
     List<Venditore> getVenditoriInAttesaDiAccreditamento();

     List<Curatore> getCuratoriInAttesaDiAccreditamento();

     List<AnimatoreDellaFiliera> getAnimatoriInAttesaDiAccreditamento();

     boolean aggiornaStatoAccreditamentoVenditore(Long venditoreId, StatoAccreditamento nuovoStato);

     boolean aggiornaStatoAccreditamentoCuratore(Long curatoreId, StatoAccreditamento nuovoStato);

     boolean aggiornaStatoAccreditamentoAnimatore(Long animatoreId, StatoAccreditamento nuovoStato);


     boolean attivaDisattivaAcquirente(Long acquirenteId, boolean attivo);

     boolean attivaDisattivaVenditore(Long venditoreId, boolean attivo);

     boolean attivaDisattivaCuratore(Long curatoreId, boolean attivo);

     boolean attivaDisattivaAnimatore(Long animatoreId, boolean attivo);


     Optional<DatiAzienda> getDatiAziendaPerVenditore(Long venditoreId);

     List<Utente> getTuttiGliUtenti();

     List<Utente> getTuttiGliUtentiAttivi();
    }



