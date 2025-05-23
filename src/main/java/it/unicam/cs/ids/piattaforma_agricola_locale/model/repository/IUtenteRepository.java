package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import java.util.List;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.AnimatoreDellaFiliera;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Curatore;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.GestorePiattaforma;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Utente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

public interface IUtenteRepository {

    void save(Utente utente);

    Utente findById(int idUtente);

    List<Utente> mostraTuttiGliUtenti();

    void deleteById(int idUtente);

    Utente findByEmail(String email);

    List<Venditore> findAllVenditori();

    List<Acquirente> findAllAcquirenti();

    List<Curatore> findAllCuratori();

    List<AnimatoreDellaFiliera> findAllAnimatori();

    List<GestorePiattaforma> findAllGestori();

}
