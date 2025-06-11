package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.AnimatoreDellaFiliera;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.StatoAccreditamento;


import java.util.List;
import java.util.Optional;


public interface IAnimatoreRepository {
     void save(AnimatoreDellaFiliera animatore);


     Optional<AnimatoreDellaFiliera> findById(Long id);

     List<AnimatoreDellaFiliera> findAll();

     List<AnimatoreDellaFiliera> findByStatoAccreditamento(StatoAccreditamento stato);
}
