package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.StatoAccreditamento;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

import java.util.List;
import java.util.Optional;

public interface IVenditoreRepository {

    void save(Venditore venditore);

    Optional<Venditore> findById(Long id);

    List<Venditore> findAll();

    List<Venditore> findByStatoAccreditamento(StatoAccreditamento stato);
}
