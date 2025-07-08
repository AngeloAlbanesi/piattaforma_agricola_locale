package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.StatoAccreditamento;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

@Repository
public interface IVenditoreRepository extends JpaRepository<Venditore, Long> {

    List<Venditore> findByStatoAccreditamento(StatoAccreditamento stato);

    /**
     * Trova un venditore tramite l'ID dei suoi dati azienda.
     * 
     * @param datiAziendaId L'ID dei dati azienda
     * @return Il venditore associato ai dati azienda, se esiste
     */
    @Query("SELECT v FROM Venditore v WHERE v.datiAzienda.id = :datiAziendaId")
    Optional<Venditore> findByDatiAziendaId(@Param("datiAziendaId") Long datiAziendaId);
}
