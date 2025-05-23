package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

public class ProdottoRepository implements IProdottoRepository {

    private Map<Integer, Prodotto> prodotti = new HashMap<>();

    // Implementazione dei metodi dell'interfaccia IProdottoRepository

    @Override
    public void save(Prodotto prodotto) {
        prodotti.put(prodotto.getId(), prodotto);
    }

    @Override
    public Prodotto findById(int id) {
        return prodotti.get(id);
    }

    @Override
    public List<Prodotto> mostraTuttiIProdotti() {
        return new ArrayList<>(prodotti.values());
    }

    @Override
    public void deleteById(int id) {
        prodotti.remove(id);
    }

    @Override
    public List<Prodotto> findByVenditoreId(Venditore venditore) {
        return prodotti.values().stream()
                .filter(prodotto -> prodotto.getVenditore().equals(venditore))
                .collect(Collectors.toList());
    }

    @Override
    public List<Prodotto> findByNome(String nome) {
        return prodotti.values().stream()
                .filter(prodotto -> prodotto.getNome().equalsIgnoreCase(nome))
                .collect(Collectors.toList());
    }

}
