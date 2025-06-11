package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Curatore;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.StatoAccreditamento;

import java.util.*;
import java.util.stream.Collectors;

public class CuratoreRepository implements ICuratoreRepository {
    private final Map<Long, Curatore> curatoriMap = new HashMap<>();
    private Long nextId = 1L;

    public void save(Curatore curatore) {
        if (curatore.getId() == null) {
            curatore.setId(nextId++);
        }
        curatoriMap.put(curatore.getId(), curatore);

    }
    public List<Curatore> findAll() {
        return new ArrayList<>(curatoriMap.values());
    }

    public Optional<Curatore> findById(Long id) {
        return Optional.ofNullable(curatoriMap.get(id));
    }

    public List<Curatore> findByStatoAccreditamento(StatoAccreditamento stato) {
        return curatoriMap.values().stream()
                .filter(c -> c.getStatoAccreditamento() == stato)
                .collect(Collectors.toList());
    }
}
