package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.Acquistabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.RigaOrdine;

public class RigaOrdineRepository implements IRigaOrdineRepository {

    private final Map<Long, RigaOrdine> store = new HashMap<>();
    private Long nextId = 1L;

    @Override
    public void save(RigaOrdine rigaOrdine) {
        if(rigaOrdine.getIdRiga()==null){
            rigaOrdine.setIdRiga(nextId++);
        }
        store.put(rigaOrdine.getIdRiga(), rigaOrdine);
    }

    @Override
    public Optional<RigaOrdine> findById(Long idRiga) {
        return Optional.ofNullable(store.get(idRiga));
    }

    @Override
    public List<RigaOrdine> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<RigaOrdine> findByOrdine(Ordine ordine) {
        // Utilizziamo le righe ordine direttamente dall'ordine
        return new ArrayList<>(ordine.getRigheOrdine());
    }

    @Override
    public List<RigaOrdine> findByAcquistabile(Acquistabile acquistabile) {
        return store.values().stream()
                .filter(riga -> riga.getAcquistabile().equals(acquistabile))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long idRiga) {
        store.remove(idRiga);
    }

    @Override
    public void deleteByOrdine(Ordine ordine) {
        List<Long> righeDaRimuovere = ordine.getRigheOrdine().stream()
                .map(RigaOrdine::getIdRiga)
                .collect(Collectors.toList());

        righeDaRimuovere.forEach(store::remove);
    }

    @Override
    public void update(RigaOrdine rigaOrdine) {
        if (store.containsKey(rigaOrdine.getIdRiga())) {
            store.put(rigaOrdine.getIdRiga(), rigaOrdine);
        }
    }

}
