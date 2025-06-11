package it.unicam.cs.ids.piattaforma_agricola_locale.service.observer;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;

/**
 * Interfaccia che definisce il contratto per un oggetto osservabile
 * nel pattern Observer per la gestione delle notifiche di creazione prodotti.
 * 
 * Implementa il lato Subject del pattern Observer, permettendo la gestione
 * di una lista di observer che vengono notificati quando vengono creati
 * nuovi prodotti che necessitano di revisione.
 */
public interface IProdottoObservable {
    
    /**
     * Aggiunge un observer alla lista degli observer.
     * 
     * @param observer l'observer da aggiungere alla lista delle notifiche
     * @throws IllegalArgumentException se l'observer è null
     */
    void aggiungiObserver(ICuratoreObserver observer);
    
    /**
     * Rimuove un observer dalla lista degli observer.
     * 
     * @param observer l'observer da rimuovere dalla lista delle notifiche
     * @throws IllegalArgumentException se l'observer è null
     */
    void rimuoviObserver(ICuratoreObserver observer);
    
    /**
     * Notifica tutti gli observer registrati della creazione di un nuovo prodotto.
     * La notifica viene effettuata quando un nuovo prodotto viene creato e
     * necessita di essere aggiunto alla coda di revisione del curatore.
     * 
     * @param prodotto il prodotto appena creato che necessita di revisione
     * @throws IllegalArgumentException se il prodotto è null
     */
    void notificaObservers(Prodotto prodotto);
}