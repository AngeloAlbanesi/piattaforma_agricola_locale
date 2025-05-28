package it.unicam.cs.ids.piattaforma_agricola_locale.service.observer;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.RigaOrdine;

import java.util.List;

/**
 * Interfaccia che definisce il contratto per un observer
 * nel pattern Observer per la gestione delle notifiche degli ordini.
 * 
 * Implementa il lato Observer del pattern, definendo il metodo
 * di callback che viene invocato quando un ordine subisce modifiche
 * che interessano i prodotti del venditore.
 */
public interface IVenditoreObserver {
    
    /**
     * Metodo di callback invocato quando un ordine viene modificato
     * e include prodotti di competenza del venditore osservatore.
     * 
     * Questo metodo viene chiamato automaticamente dal Subject
     * (OrdineObservable) quando si verificano cambiamenti negli ordini
     * che coinvolgono i prodotti del venditore.
     * 
     * @param ordine l'ordine che ha subito modifiche
     * @param righeDiCompetenza la lista delle righe d'ordine che contengono
     *                         prodotti di competenza del venditore.
     *                         Queste sono le righe che il venditore deve
     *                         gestire dal punto di vista dell'inventario
     * @throws IllegalArgumentException se l'ordine o la lista delle righe è null
     */
    void update(Ordine ordine, List<RigaOrdine> righeDiCompetenza);
}