package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.StatoVerificaValori;

@Repository
public interface IDatiAziendaRepository extends JpaRepository<DatiAzienda, Long> {

    Optional<DatiAzienda> findByPartitaIva(String partitaIva);

    void deleteByPartitaIva(String partitaIva);
    
    /**
     * Trova aziende per stato di verifica.
     * 
     * @param statoVerifica Lo stato di verifica
     * @param pageable Parametri di paginazione
     * @return Una pagina di risultati
     */
    Page<DatiAzienda> findByStatoVerifica(StatoVerificaValori statoVerifica, Pageable pageable);
    
    /**
     * Trova aziende il cui nome contiene la stringa specificata (case-insensitive).
     * 
     * @param nomeAzienda La stringa da cercare nel nome
     * @param pageable Parametri di paginazione
     * @return Una pagina di risultati
     */
    Page<DatiAzienda> findByNomeAziendaContainingIgnoreCase(String nomeAzienda, Pageable pageable);
}
