package it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces;

import java.util.List;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

/**
 * Interfaccia che definisce i metodi per la gestione dei prodotti offerti dai venditori
 * all'interno della piattaforma agricola locale.
 *
 */
public interface IProdottoService {


    void creaProdotto( String nome, String descrizione, double prezzo, int quantitaDisponibile,
            Venditore venditore);
    
    /**
     * Restituisce la lista dei prodotti offerti da un venditore.
     *
     * @param venditore il venditore di cui ottenere i prodotti
     * @return lista dei prodotti offerti
     */
    List<Prodotto> getProdottiOfferti(Venditore venditore);

    /**
     * Aggiunge un prodotto al catalogo.
     *
     * @param prodotto il prodotto da aggiungere
     * @return true se l'aggiunta ha successo, false altrimenti
     */
    boolean aggiungiProdottoCatalogo(Prodotto prodotto);

    /**
     * Rimuove un prodotto dal catalogo.
     *
     * @param prodotto il prodotto da rimuovere
     * @return true se la rimozione ha successo, false altrimenti
     */
    boolean rimuoviProdottoCatalogo(Prodotto prodotto);
    
    /**
     * Aggiorna la quantità disponibile di un prodotto nel catalogo.
     *
     * @param prodotto il prodotto da aggiornare
     * @param nuovaQuantita la nuova quantità disponibile
     * @return true se l'aggiornamento ha successo, false altrimenti
     */
    boolean aggiornaProdottoCatalogo(Prodotto prodotto, int nuovaQuantita);
}
