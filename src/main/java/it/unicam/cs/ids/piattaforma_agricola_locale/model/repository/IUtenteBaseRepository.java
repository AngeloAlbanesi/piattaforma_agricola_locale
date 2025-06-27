package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUtenteBaseRepository extends JpaRepository<Utente, Long> {

    // Metodi ereditati da JpaRepository: save, findById, findAll

    // Metodo personalizzato per trovare utente per email
    Optional<Utente> findByEmail(String email);
}
