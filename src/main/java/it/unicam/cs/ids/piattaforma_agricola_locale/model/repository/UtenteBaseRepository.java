package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Utente;

import java.util.*;

public class UtenteBaseRepository implements  IUtenteBaseRepository{
    private Map<Long, Utente> utentiMap = new HashMap<>(); // Conterrà Acquirente, GestorePiattaforma
    private Long nextId = 1L;

    public Utente save(Utente utente) {
        if (utente.getId() == null) {
            utente.setId(nextId++);
        }
        utentiMap.put(utente.getId(), utente);
        return utente;
    }


    public Optional<Utente> findById(Long id) {
        return Optional.ofNullable(utentiMap.get(id));
    }

    public Optional<Utente> findByEmail(String email) {
        return utentiMap.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst();
    }
    public List<Utente> findAll() {
        return new ArrayList<>(utentiMap.values());
    }

}
