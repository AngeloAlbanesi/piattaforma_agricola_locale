package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.coltivazione.MetodoDiColtivazione;

public class MetodoDiColtivazioneRepository implements IMetodoDiColtivazioneRepository {

    private Map<Long, MetodoDiColtivazione> metodiDiColtivazione = new HashMap<>();

    @Override
    public void save(MetodoDiColtivazione metodoDiColtivazione) {
        metodiDiColtivazione.put(metodoDiColtivazione.getId(), metodoDiColtivazione);
    }

    @Override
    public MetodoDiColtivazione findById(Long id) {
        return metodiDiColtivazione.get(id);
    }

    @Override
    public List<MetodoDiColtivazione> findAll() {
        return new ArrayList<>(metodiDiColtivazione.values());
    }

    @Override
    public void deleteById(Long id) {
        metodiDiColtivazione.remove(id);
    }

    @Override
    public void update(MetodoDiColtivazione metodoDiColtivazione) {
        if (metodoDiColtivazione.getId() != 0 && metodiDiColtivazione.containsKey(metodoDiColtivazione.getId())) {
            metodiDiColtivazione.put(metodoDiColtivazione.getId(), metodoDiColtivazione);
        }
    }
}