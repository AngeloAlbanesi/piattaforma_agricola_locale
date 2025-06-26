package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.coltivazione.MetodoDiColtivazione;

@Repository
public interface IMetodoDiColtivazioneRepository extends JpaRepository<MetodoDiColtivazione, Long> {

}