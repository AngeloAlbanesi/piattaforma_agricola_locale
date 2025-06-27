package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.StatoAccreditamento;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

@Repository
public interface IVenditoreRepository extends JpaRepository<Venditore, Long> {

    List<Venditore> findByStatoAccreditamento(StatoAccreditamento stato);
}
