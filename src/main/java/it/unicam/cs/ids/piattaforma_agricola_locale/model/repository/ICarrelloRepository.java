package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import java.util.List;
import java.util.Optional;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.carrello.Carrello;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;

public interface ICarrelloRepository {

    /**
     * Salva un carrello nel repository
     * 
     * @param carrello il carrello da salvare
     */
    void save(Carrello carrello);

    /**
     * Trova un carrello per ID
     * 
     * @param idCarrello l'ID del carrello
     * @return Optional contenente il carrello se trovato
     */
    Optional<Carrello> findById(int idCarrello);

    /**
     * Trova il carrello di un acquirente
     * 
     * @param acquirente l'acquirente proprietario del carrello
     * @return Optional contenente il carrello se trovato
     */
    Optional<Carrello> findByAcquirente(Acquirente acquirente);

    /**
     * Restituisce tutti i carrelli
     * 
     * @return lista di tutti i carrelli
     */
    List<Carrello> findAll();

    /**
     * Elimina un carrello per ID
     * 
     * @param idCarrello l'ID del carrello da eliminare
     */
    void deleteById(int idCarrello);

    /**
     * Elimina il carrello di un acquirente
     * 
     * @param acquirente l'acquirente proprietario del carrello
     */
    void deleteByAcquirente(Acquirente acquirente);

}
