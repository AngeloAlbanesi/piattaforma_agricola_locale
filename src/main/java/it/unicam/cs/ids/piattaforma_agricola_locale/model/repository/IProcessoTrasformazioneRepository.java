package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.ProcessoTrasformazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Trasformatore;

/**
 * Interfaccia repository per la gestione dei processi di trasformazione.
 * Definisce le operazioni CRUD e le query specifiche per i processi di
 * trasformazione.
 */
@Repository
public interface IProcessoTrasformazioneRepository extends JpaRepository<ProcessoTrasformazione, Long> {

    /**
     * Trova tutti i processi di trasformazione di un trasformatore specifico.
     *
     * @param trasformatore Il trasformatore di cui cercare i processi
     * @return Lista dei processi del trasformatore
     */
    List<ProcessoTrasformazione> findByTrasformatore(Trasformatore trasformatore);

    /**
     * Trova tutti i processi di trasformazione per nome (ricerca parziale).
     *
     * @param nome Il nome o parte del nome da cercare
     * @return Lista dei processi che corrispondono al criterio di ricerca
     */
    List<ProcessoTrasformazione> findByNomeContainingIgnoreCase(String nome);

    /**
     * Trova processi per metodo di produzione.
     *
     * @param metodoProduzione Il metodo di produzione da cercare
     * @return Lista dei processi che utilizzano il metodo specificato
     */
    List<ProcessoTrasformazione> findByMetodoProduzione(String metodoProduzione);

    /**
     * Conta il numero di processi di un trasformatore.
     *
     * @param trasformatore Il trasformatore di cui contare i processi
     * @return Il numero di processi del trasformatore
     */
    long countByTrasformatore(Trasformatore trasformatore);

    /**
     * Verifica se esiste un processo con il nome specificato per un trasformatore.
     *
     * @param nome          Il nome del processo
     * @param trasformatore Il trasformatore
     * @return true se esiste un processo con quel nome, false altrimenti
     */
    boolean existsByNomeAndTrasformatore(String nome, Trasformatore trasformatore);

    /**
     * Elimina tutti i processi di un trasformatore.
     *
     * @param trasformatore Il trasformatore di cui eliminare tutti i processi
     * @return Il numero di processi eliminati
     */
    int deleteAllByTrasformatore(Trasformatore trasformatore);
}