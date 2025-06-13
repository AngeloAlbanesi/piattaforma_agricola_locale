package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import java.util.List;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Pacchetto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

public interface IPacchettoRepository {

    // Metodi per gestire i pacchetti
    void salva(Pacchetto pacchetto);

    Pacchetto findById(Long id);

    List<Pacchetto> mostraTuttiIPacchetti();

    void deleteById(Long id);

    List<Pacchetto> findByNome(String nome);

    List<Pacchetto> findByVenditoreId(Venditore venditore);

}
