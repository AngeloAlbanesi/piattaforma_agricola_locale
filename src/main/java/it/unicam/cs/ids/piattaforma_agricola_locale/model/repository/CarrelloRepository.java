package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.carrello.Carrello;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;

public class CarrelloRepository implements ICarrelloRepository {

    private final Map<Integer, Carrello> store = new HashMap<>();

    @Override
    public void save(Carrello carrello) {
        store.put(carrello.getIdCarrello(), carrello);
    }

    @Override
    public Optional<Carrello> findById(int idCarrello) {
        return Optional.ofNullable(store.get(idCarrello));
    }

    @Override
    public Optional<Carrello> findByAcquirente(Acquirente acquirente) {
        return store.values().stream()
                .filter(c -> c.getAcquirente().equals(acquirente))
                .findFirst();
    }

    @Override
    public List<Carrello> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteById(int idCarrello) {
        store.remove(idCarrello);
    }

    @Override
    public void deleteByAcquirente(Acquirente acquirente) {
        store.values().removeIf(c -> c.getAcquirente().equals(acquirente));
    }

}
