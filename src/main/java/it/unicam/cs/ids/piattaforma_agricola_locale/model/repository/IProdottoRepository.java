package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import java.util.List;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

public interface IProdottoRepository {

    // Metodi per gestire i prodotti
    void save(Prodotto prodotto);

    Prodotto findById(int id);

    List<Prodotto> mostraTuttiIProdotti();

    void deleteById(int id);

    List<Prodotto> findByVenditoreId(Venditore venditore);

    List<Prodotto> findByNome(String nome);

}
