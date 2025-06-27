package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.AnimatoreDellaFiliera;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.StatoAccreditamento;

@Repository
public interface IAnimatoreRepository extends JpaRepository<AnimatoreDellaFiliera, Long> {

    List<AnimatoreDellaFiliera> findByStatoAccreditamento(StatoAccreditamento stato);
}
