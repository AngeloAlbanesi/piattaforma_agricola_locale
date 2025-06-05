package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IUtenteRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.UtenteRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Utente;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IGestoreService;

public class GestoreService implements IGestoreService {

    private final IUtenteRepository utenteRepository;

    public GestoreService(IUtenteRepository utenteRepository) {
        this.utenteRepository = utenteRepository;
    }

    public GestoreService() {
        this.utenteRepository = new UtenteRepository();
    }

    public void DisattivaUtente(Utente utente) {
        if (utente == null) throw new IllegalArgumentException("Utente non può essere null");
        if(utenteRepository.findById(utente.getId()) == null) throw new IllegalArgumentException("Utente non trovato");
        if(!utenteRepository.findById(utente.getId()).isAttivo()) throw new UnsupportedOperationException("Utente dià disattivato");
        utente.disattivaAccount();
        utenteRepository.save(utente);

    }

    public void RiattivaUtente(Utente utente) {
        if (utente == null) throw new IllegalArgumentException("Utente non può essere null");
        if(utenteRepository.findById(utente.getId()) == null) throw new IllegalArgumentException("Utente non trovato");
        if(utenteRepository.findById(utente.getId()).isAttivo()) throw new UnsupportedOperationException("Utente gia attivo");
        utente.riattivaAccount();
        utenteRepository.save(utente);
    }


}
