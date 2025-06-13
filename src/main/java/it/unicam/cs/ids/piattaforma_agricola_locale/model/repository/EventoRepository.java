package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.Evento;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.AnimatoreDellaFiliera;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventoRepository implements IEventoRepository {

    private Map<Long, Evento> eventi = new HashMap<>();
    private Long nextId = 1L; // Semplice generazione ID

    @Override
    public void save(Evento evento) {
        if (evento.getId() == null) {
            evento.setId(nextId++); // Assumendo che Utente abbia setId
        }
        eventi.put(evento.getId(), evento);
    }

    @Override
    public Evento findById(Long id) {
        return eventi.get(id);
    }
    @Override
    public List<Evento> mostraTuttiEventi() {
        return new ArrayList<>(eventi.values());
    }
    @Override
    public void deleteById(Long id) {
        eventi.remove(id);
    }

    @Override
    public List<Evento> findByAnimatoreId(AnimatoreDellaFiliera organizzatore){
        List<Evento> eventiOrganizzati = new ArrayList<>();
        for (Evento evento : eventi.values()) {
            if (evento.getOrganizzatore().equals(organizzatore)) {
                eventiOrganizzati.add(evento);
            }
        }
        return eventiOrganizzati;
    }

    @Override
    public List<Evento> findByNome(String nome) {
        List<Evento> eventiTrovati = new ArrayList<>();
        for (Evento evento : eventi.values()) {
            if (evento.getNome().equalsIgnoreCase(nome)) {
                eventiTrovati.add(evento);
            }
        }
        return eventiTrovati;
    }

    @Override
    public List<Evento> findByAziendaPartecipante(Venditore venditore) {
        List<Evento> eventiPartecipati = new ArrayList<>();
        for (Evento evento : eventi.values()) {
            if (evento.getAziendePartecipanti().contains(venditore)) {
                eventiPartecipati.add(evento);
            }
        }
        return eventiPartecipati;
    }

}
