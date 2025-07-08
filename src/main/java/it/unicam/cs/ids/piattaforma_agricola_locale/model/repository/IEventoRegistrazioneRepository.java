package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.Evento;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.EventoRegistrazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for event registrations.
 */
@Repository
public interface IEventoRegistrazioneRepository extends JpaRepository<EventoRegistrazione, Long> {
    
    /**
     * Find all registrations for a specific event.
     * 
     * @param evento the event
     * @return list of registrations
     */
    List<EventoRegistrazione> findByEvento(Evento evento);
    
    /**
     * Find all registrations for a specific user.
     * 
     * @param utente the user
     * @return list of registrations
     */
    List<EventoRegistrazione> findByUtente(Utente utente);
    
    /**
     * Find a registration by event and user.
     * 
     * @param evento the event
     * @param utente the user
     * @return the registration if found
     */
    Optional<EventoRegistrazione> findByEventoAndUtente(Evento evento, Utente utente);
    
    /**
     * Check if a user is registered for an event.
     * 
     * @param evento the event
     * @param utente the user
     * @return true if registered
     */
    boolean existsByEventoAndUtente(Evento evento, Utente utente);
    
    /**
     * Delete a registration by event and user.
     * 
     * @param evento the event
     * @param utente the user
     */
    void deleteByEventoAndUtente(Evento evento, Utente utente);
}