package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Pacchetto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.PacchettoElemento;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;

@Repository
public interface IPacchettoElementoRepository extends JpaRepository<PacchettoElemento, Long> {
    
    /**
     * Trova tutti gli elementi di un pacchetto specifico.
     */
    List<PacchettoElemento> findByPacchetto(Pacchetto pacchetto);
    
    /**
     * Trova un elemento specifico per pacchetto e prodotto.
     */
    PacchettoElemento findByPacchettoAndProdotto(Pacchetto pacchetto, Prodotto prodotto);
    
    /**
     * Elimina tutti gli elementi di un pacchetto.
     */
    void deleteByPacchetto(Pacchetto pacchetto);
    
    /**
     * Elimina un elemento specifico per pacchetto e prodotto.
     */
    void deleteByPacchettoAndProdotto(Pacchetto pacchetto, Prodotto prodotto);
}