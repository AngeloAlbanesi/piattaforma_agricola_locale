package it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Utente;

public interface IGestoreService {
    void RiattivaUtente(Utente utente);
    void DisattivaUtente(Utente utente);
}

