package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.StatoCorrente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

public class OrdineRepository implements IOrdineRepository {

    private final Map<Integer, Ordine> store = new HashMap<>();

    @Override
    public void save(Ordine ordine) {
        store.put(ordine.getIdOrdine(), ordine);
    }

    @Override
    public Optional<Ordine> findById(int idOrdine) {
        return Optional.ofNullable(store.get(idOrdine));
    }

    @Override
    public List<Ordine> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Ordine> findByAcquirente(Acquirente acquirente) {
        return store.values().stream()
                .filter(ordine -> ordine.getAcquirente().equals(acquirente))
                .collect(Collectors.toList());
    }

    @Override
    public List<Ordine> findByStato(StatoCorrente stato) {
        return store.values().stream()
                .filter(ordine -> ordine.getStatoOrdine().equals(stato))
                .collect(Collectors.toList());
    }

    @Override
    public List<Ordine> findByVenditore(Venditore venditore) {
        return store.values().stream()
                .filter(ordine -> ordine.getRigheOrdine().stream()
                        .anyMatch(riga -> riga.getAcquistabile().getVenditore().equals(venditore)))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(int idOrdine) {
        store.remove(idOrdine);
    }

    @Override
    public void update(Ordine ordine) {
        if (store.containsKey(ordine.getIdOrdine())) {
            store.put(ordine.getIdOrdine(), ordine);
        }
    }

}
