package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;

public class DatiAziendaRepository implements IDatiAziendaRepository {

    private final Map<String, DatiAzienda> store = new HashMap<>();

    @Override
    public Optional<DatiAzienda> findById(Long id) {
        return store.values().stream()
                .filter(datiAzienda -> datiAzienda.getIdAzienda() == id)
                .findFirst();
    }

    @Override
    public Optional<DatiAzienda> findByPartitaIva(String partitaIva) {
        return store.values().stream()
                .filter(datiAzienda -> datiAzienda.getPartitaIva().equals(partitaIva))
                .findFirst();
    }

    @Override
    public void save(DatiAzienda datiAzienda) {
        store.put(datiAzienda.getPartitaIva(), datiAzienda);
    }

    @Override
    public List<DatiAzienda> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteById(String partitaIva) {
        store.remove(partitaIva);
    }


}
