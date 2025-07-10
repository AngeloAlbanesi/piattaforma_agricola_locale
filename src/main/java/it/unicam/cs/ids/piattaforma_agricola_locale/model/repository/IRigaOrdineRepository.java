package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.RigaOrdine;

@Repository
public interface IRigaOrdineRepository extends JpaRepository<RigaOrdine, Long> {

    /**
     * Trova tutte le righe ordine di un ordine specifico
     * 
     * @param ordine l'ordine
     * @return lista delle righe ordine dell'ordine
     */
    List<RigaOrdine> findByOrdine(Ordine ordine);

    /**
     * Trova tutte le righe ordine che contengono un acquistabile specifico
     * Nota: Poiché il campo acquistabile non è direttamente persistito,
     * questa query utilizza JPQL per cercare nei tre possibili campi
     * 
     * @param acquistabile l'acquistabile
     * @return lista delle righe ordine che contengono l'acquistabile
     */
    @Query("SELECT r FROM RigaOrdine r WHERE r.idAcquistabile = :id")
    List<RigaOrdine> findByIdAcquistabile(@Param("id") Long acquistabileId);

    /**
     * Elimina tutte le righe ordine di un ordine specifico
     * 
     * @param ordine l'ordine
     */
    void deleteByOrdine(Ordine ordine);

}
