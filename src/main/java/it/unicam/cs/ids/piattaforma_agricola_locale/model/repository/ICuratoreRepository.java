package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Curatore;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.StatoAccreditamento;

import java.util.List;
import java.util.Optional;


public interface ICuratoreRepository {


    void save(Curatore curatore);

    Optional<Curatore> findById(Long id);

    List<Curatore> findByStatoAccreditamento(StatoAccreditamento stato);
}
