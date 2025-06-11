package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Utente;


import java.util.List;
import java.util.Optional;

public interface IUtenteBaseRepository {

     Utente save(Utente utente);

     Optional<Utente> findById(Long id);

     Optional<Utente> findByEmail(String email);
     List<Utente> findAll();
}
