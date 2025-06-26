package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.Evento;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.AnimatoreDellaFiliera;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

@Repository
public interface IEventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByOrganizzatore(AnimatoreDellaFiliera organizzatore);

    List<Evento> findByNomeEventoContainingIgnoreCase(String nome);

    List<Evento> findByAziendePartecipanti(Venditore venditore);

}
