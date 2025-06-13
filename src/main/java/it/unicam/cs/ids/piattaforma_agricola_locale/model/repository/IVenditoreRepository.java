package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.StatoAccreditamento;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface IVenditoreRepository {

    void save(Venditore venditore);


    Optional<Venditore> findById(long id);

    List<Venditore> findAll();

    List<Venditore> findByStatoAccreditamento(StatoAccreditamento stato);
}
