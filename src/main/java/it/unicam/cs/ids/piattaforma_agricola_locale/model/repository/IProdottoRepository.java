package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import java.util.List;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

public interface IProdottoRepository {

    // Metodi per gestire i prodotti
    void save(Prodotto prodotto);

    Prodotto findById(Long id);

    List<Prodotto> mostraTuttiIProdotti();

    void deleteById(Long id);

    List<Prodotto> findByVenditore(Venditore venditore);

    List<Prodotto> findByNome(String nome);

}
