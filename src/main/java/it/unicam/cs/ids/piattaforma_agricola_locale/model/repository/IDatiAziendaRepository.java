package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import java.util.List;
import java.util.Optional;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;

public interface IDatiAziendaRepository {

    Optional<DatiAzienda> findById(String partitaIva);

    void save(DatiAzienda datiAzienda);

    List<DatiAzienda> findAll();

    void deleteById(String partitaIva);

}
