package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.StatoAccreditamento;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

import java.util.*;
import java.util.stream.Collectors;

public class VenditoreRepository implements IVenditoreRepository{
    private final  Map<Long, Venditore> venditoriMap = new HashMap<>();
    private Long nextId = 1L; // Semplice generazione ID

    @Override
    public void save(Venditore venditore) {
        if (venditore.getId() == null) {
            venditore.setId(nextId++); // Assumendo che Utente abbia setId
        }
        venditoriMap.put(venditore.getId(), venditore);

    }

    @Override
    public Optional<Venditore> findById(Long id) {
        return Optional.ofNullable(venditoriMap.get(id));
    }

    @Override
    public List<Venditore> findAll() {
        return new ArrayList<>(venditoriMap.values());
    }

    @Override
    public List<Venditore> findByStatoAccreditamento(StatoAccreditamento stato) {
        return venditoriMap.values().stream()
                .filter(v -> v.getStatoAccreditamento() == stato)
                .collect(Collectors.toList());
    }
}

