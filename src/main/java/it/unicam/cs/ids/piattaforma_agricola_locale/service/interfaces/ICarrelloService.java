package it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces;

import java.util.List;
import java.util.Optional;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.carrello.ElementoCarrello;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.carrello.Carrello;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.Acquistabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;

public interface ICarrelloService {

    /**
     * Crea un nuovo carrello per un acquirente
     * 
     * @param acquirente l'acquirente
     * @return il carrello creato
     */
    Carrello creaNuovoCarrello(Acquirente acquirente);

    /**
     * Ottiene il carrello di un acquirente
     * 
     * @param acquirente l'acquirente
     * @return il carrello se esiste
     */
    Optional<Carrello> getCarrelloAcquirente(Acquirente acquirente);

    /**
     * Aggiunge un elemento al carrello
     * 
     * @param acquirente   l'acquirente
     * @param acquistabile l'acquistabile da aggiungere
     * @param quantita     la quantità
     */
    void aggiungiElementoAlCarrello(Acquirente acquirente, Acquistabile acquistabile, int quantita);

    /**
     * Rimuove un elemento dal carrello
     * 
     * @param acquirente l'acquirente
     * @param elemento   l'elemento da rimuovere
     */
    void rimuoviElementoDalCarrello(Acquirente acquirente, ElementoCarrello elemento);

    /**
     * Svuota il carrello di un acquirente
     * 
     * @param acquirente l'acquirente
     */
    void svuotaCarrello(Acquirente acquirente);

    /**
     * Calcola il prezzo totale del carrello
     * 
     * @param acquirente l'acquirente
     * @return il prezzo totale
     */
    double calcolaPrezzoTotaleCarrello(Acquirente acquirente);

    /**
     * Ottiene tutti i carrelli
     * 
     * @return lista di tutti i carrelli
     */
    List<Carrello> getTuttiICarrelli();

}
