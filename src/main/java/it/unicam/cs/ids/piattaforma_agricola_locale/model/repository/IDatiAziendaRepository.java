package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;

@Repository
public interface IDatiAziendaRepository extends JpaRepository<DatiAzienda, Long> {

    Optional<DatiAzienda> findByPartitaIva(String partitaIva);

    void deleteByPartitaIva(String partitaIva);
}
