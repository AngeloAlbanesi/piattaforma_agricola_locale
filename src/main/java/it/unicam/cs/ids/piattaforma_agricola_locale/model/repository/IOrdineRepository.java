package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.StatoCorrente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

@Repository
public interface IOrdineRepository extends JpaRepository<Ordine, Long> {

    /**
     * Trova tutti gli ordini di un acquirente
     * 
     * @param acquirente l'acquirente
     * @return lista degli ordini dell'acquirente
     */
    List<Ordine> findByAcquirente(Acquirente acquirente);

    /**
     * Trova tutti gli ordini con un determinato stato
     * 
     * @param stato lo stato dell'ordine
     * @return lista degli ordini con lo stato specificato
     */
    @Query("SELECT o FROM Ordine o WHERE o.statoCorrente = :stato")
    List<Ordine> findByStato(@Param("stato") StatoCorrente stato);

    /**
     * Trova tutti gli ordini relativi a un venditore
     * 
     * @param venditore il venditore
     * @return lista degli ordini che contengono prodotti del venditore
     */
    @Query("SELECT o FROM Ordine o JOIN o.righeOrdine r WHERE r.idAcquistabile IN :ids AND r.tipoAcquistabile = 'PRODOTTO'")
    List<Ordine> findByProdottoIds(@Param("ids") List<Long> ids);

    @Query("SELECT o FROM Ordine o JOIN o.righeOrdine r WHERE r.idAcquistabile IN :ids AND r.tipoAcquistabile = 'PACCHETTO'")
    List<Ordine> findByPacchettoIds(@Param("ids") List<Long> ids);

}
