package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Pacchetto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

public class PacchettoRepository implements IPacchettoRepository {

    private HashMap<Long, Pacchetto> store = new HashMap<>();
    private Long nextId = 1L; // Semplice generazione ID


    @Override
    public void salva(Pacchetto pacchetto) {
        // Implementazione del metodo per salvare un pacchetto
        if(pacchetto.getId() == null) {
            pacchetto.SetId(nextId++);
        }
        store.put(pacchetto.getId(), pacchetto);
    }

    @Override
    public Pacchetto findById(Long id) {
        // Implementazione del metodo per trovare un pacchetto per ID
        return store.get(id);
    }

    @Override
    public List<Pacchetto> mostraTuttiIPacchetti() {
        // Implementazione del metodo per mostrare tutti i pacchetti
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteById(Long id) {
        // Implementazione del metodo per eliminare un pacchetto per ID
        store.remove(id);
    }

    @Override
    public List<Pacchetto> findByNome(String nome) {
        // Implementazione del metodo per trovare pacchetti per nome
        return store.values().stream()
                .filter(pacchetto -> pacchetto.getNome().equalsIgnoreCase(nome))
                .collect(Collectors.toList());
    }

    @Override
    public List<Pacchetto> findByVenditoreId(Venditore venditore) {
        // Implementazione del metodo per trovare pacchetti per ID venditore
        return store.values().stream()
                .filter(pacchetto -> pacchetto.getVenditore().equals(venditore))
                .collect(Collectors.toList());
    }

}
