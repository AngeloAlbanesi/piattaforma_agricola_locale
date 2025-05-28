package it.unicam.cs.ids.piattaforma_agricola_locale.service.observer;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

/**
 * Interfaccia che definisce il contratto per un oggetto osservabile
 * nel pattern Observer per la gestione delle notifiche degli ordini.
 * 
 * Implementa il lato Subject del pattern Observer, permettendo la gestione
 * di una lista di observer che vengono notificati quando avvengono
 * cambiamenti negli ordini che interessano i venditori.
 */
public interface OrdineObservable {
    
    /**
     * Aggiunge un observer alla lista degli observer.
     * 
     * @param observer l'observer da aggiungere alla lista delle notifiche
     * @throws IllegalArgumentException se l'observer è null
     */
    void aggiungiObserver(VenditoreObserver observer);
    
    /**
     * Rimuove un observer dalla lista degli observer.
     * 
     * @param observer l'observer da rimuovere dalla lista delle notifiche
     * @throws IllegalArgumentException se l'observer è null
     */
    void rimuoviObserver(VenditoreObserver observer);
    
    /**
     * Notifica tutti gli observer interessati ad un determinato ordine.
     * La notifica viene effettuata solo agli observer associati al venditore
     * specifico che ha prodotti coinvolti nell'ordine.
     * 
     * @param ordine l'ordine per cui notificare i cambiamenti
     * @param venditoreSpecifico il venditore specifico da notificare,
     *                          se null verranno notificati tutti i venditori
     *                          che hanno prodotti nell'ordine
     * @throws IllegalArgumentException se l'ordine è null
     */
    void notificaObservers(Ordine ordine, Venditore venditoreSpecifico);
}