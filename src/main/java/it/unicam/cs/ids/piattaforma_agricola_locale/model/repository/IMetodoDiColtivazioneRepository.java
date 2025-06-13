package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import java.util.List;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.coltivazione.MetodoDiColtivazione;

public interface IMetodoDiColtivazioneRepository {

    void save(MetodoDiColtivazione metodoDiColtivazione);

    MetodoDiColtivazione findById(Long id);

    List<MetodoDiColtivazione> findAll();

    void deleteById(Long id);

    void update(MetodoDiColtivazione metodoDiColtivazione);
}