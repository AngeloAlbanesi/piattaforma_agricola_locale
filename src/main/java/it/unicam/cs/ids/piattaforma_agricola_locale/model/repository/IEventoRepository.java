package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.Evento;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.AnimatoreDellaFiliera;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

import java.util.List;

public interface IEventoRepository {
    void save(Evento evento);

    Evento findById(Long id);

    List<Evento> mostraTuttiEventi();

    void deleteById(Long id);

    List<Evento> findByAnimatoreId(AnimatoreDellaFiliera organizzatore);

    List<Evento> findByNome(String nome);

    List<Evento> findByAziendaPartecipante(Venditore venditore);


}
