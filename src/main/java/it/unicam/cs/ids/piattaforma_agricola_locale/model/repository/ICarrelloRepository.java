package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.carrello.Carrello;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;

@Repository
public interface ICarrelloRepository extends JpaRepository<Carrello, Long> {

    /**
     * Trova il carrello di un acquirente
     * 
     * @param acquirente l'acquirente proprietario del carrello
     * @return Optional contenente il carrello se trovato
     */
    Optional<Carrello> findByAcquirente(Acquirente acquirente);

    /**
     * Elimina il carrello di un acquirente
     * 
     * @param acquirente l'acquirente proprietario del carrello
     */
    void deleteByAcquirente(Acquirente acquirente);

}
