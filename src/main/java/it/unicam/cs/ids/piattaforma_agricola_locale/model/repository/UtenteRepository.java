package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.AnimatoreDellaFiliera;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Curatore;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.GestorePiattaforma;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Utente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

public class UtenteRepository implements IUtenteRepository {

    private HashMap<Integer, Utente> store;

    public UtenteRepository() {
        this.store = new HashMap<>();
    }

    @Override
    public void save(Utente utente) {
        store.put(utente.getId(), utente);
    }

    @Override
    public Utente findById(int idUtente) {
        return store.get(idUtente);
    }

    @Override
    public List<Utente> mostraTuttiGliUtenti() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteById(int idUtente) {
        store.remove(idUtente);
    }

    @Override
    public Utente findByEmail(String email) {
        return store.values().stream()
                .filter(utente -> utente.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Venditore> findAllVenditori() {
        return store.values().stream()
                .filter(utente -> utente instanceof Venditore)
                .map(utente -> (Venditore) utente)
                .toList();
    }

    @Override
    public List<Acquirente> findAllAcquirenti() {
        return store.values().stream()
                .filter(utente -> utente instanceof Acquirente)
                .map(utente -> (Acquirente) utente)
                .toList();
    }

    @Override
    public List<Curatore> findAllCuratori() {
        return store.values().stream()
                .filter(utente -> utente instanceof Curatore)
                .map(utente -> (Curatore) utente)
                .toList();
    }

    @Override
    public List<AnimatoreDellaFiliera> findAllAnimatori() {
        return store.values().stream()
                .filter(utente -> utente instanceof AnimatoreDellaFiliera)
                .map(utente -> (AnimatoreDellaFiliera) utente)
                .toList();
    }

    @Override
    public List<GestorePiattaforma> findAllGestori() {
        return store.values().stream()
                .filter(utente -> utente instanceof GestorePiattaforma)
                .map(utente -> (GestorePiattaforma) utente)
                .toList();
    }

}
