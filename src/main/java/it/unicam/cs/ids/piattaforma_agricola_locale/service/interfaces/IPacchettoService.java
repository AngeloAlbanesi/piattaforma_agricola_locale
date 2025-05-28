package it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces;

import java.util.List;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Pacchetto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.Acquistabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DistributoreDiTipicita;

/**
 * Interfaccia che definisce i metodi per la gestione dei pacchetti di prodotti
 * all'interno della piattaforma agricola locale.
 *
 */
public interface IPacchettoService {

    /**
     * Crea un pacchetto di prodotti.
     *
     * @param distributore    il distributore che crea il pacchetto
     * @param nome            nome del pacchetto
     * @param descrizione     descrizione del pacchetto
     * @param prezzoPacchetto prezzo del pacchetto
     * @return il pacchetto creato
     */
    void creaPacchetto(DistributoreDiTipicita distributore, String nome, String descrizione, int quantita,
            double prezzoPacchetto);

    /**
     * Restituisce un pacchetto di prodotti dato il suo ID.
     *
     * @param id l'ID del pacchetto da restituire
     * @return il pacchetto trovato
     */

    // Pacchetto getPacchettoById(int id);

    /**
     * Restituisce tutti i pacchetti di prodotti.
     *
     * @return la lista dei pacchetti trovati
     */
    // List<Pacchetto> getAllPacchetti();

    /**
     * Aggiunge un pacchetto al catalogo.
     *
     * @param pacchetto il pacchetto da aggiungere
     * @return true se l'aggiunta ha successo, false altrimenti
     */
    // boolean aggiungiPacchettoCatalogo(Pacchetto pacchetto);

    /**
     * Rimuove un pacchetto dal catalogo.
     *
     * @param pacchetto il pacchetto da rimuovere
     * @return true se la rimozione ha successo, false altrimenti
     */
    boolean rimuoviPacchettoCatalogo(DistributoreDiTipicita distributore, Pacchetto pacchetto);

    /**
     * Aggiunge un prodotto a un pacchetto.
     *
     * @param pacchetto il pacchetto a cui aggiungere il prodotto
     * @param prodotto  il prodotto da aggiungere
     * @return true se l'aggiunta ha successo, false altrimenti
     */
    boolean aggiungiProdottoAlPacchetto(DistributoreDiTipicita distributore, Pacchetto pacchetto, Prodotto prodotto);

    /**
     * Rimuove un prodotto da un pacchetto.
     *
     * @param pacchetto il pacchetto da cui rimuovere il prodotto
     * @param prodotto  il prodotto da rimuovere
     * @return true se la rimozione ha successo, false altrimenti
     */
    boolean rimuoviProdottoDalPacchetto(DistributoreDiTipicita distributore, Pacchetto pacchetto, Prodotto prodotto);

    /**
     * Mostra tutti i pacchetti offerti da un distributore.
     *
     * @param distributore il distributore di cui mostrare i pacchetti
     */
    void mostraPacchetti(DistributoreDiTipicita distributore);

    /**
     * Decrementa la quantità disponibile di un pacchetto nel catalogo.
     * Questo metodo è utilizzato internamente dal sistema per aggiornare lo stock
     * dopo la creazione di un ordine.
     *
     * @param idPacchetto            l'ID del pacchetto di cui decrementare la
     *                               quantità
     * @param quantitaDaDecrementare la quantità da decrementare
     * @throws IllegalArgumentException                                                               se
     *                                                                                                l'ID
     *                                                                                                del
     *                                                                                                pacchetto
     *                                                                                                non
     *                                                                                                è
     *                                                                                                valido
     *                                                                                                o
     *                                                                                                la
     *                                                                                                quantità
     *                                                                                                è
     *                                                                                                negativa
     * @throws it.unicam.cs.ids.piattaforma_agricola_locale.exception.QuantitaNonDisponibileException se
     *                                                                                                la
     *                                                                                                quantità
     *                                                                                                da
     *                                                                                                decrementare
     *                                                                                                è
     *                                                                                                maggiore
     *                                                                                                di
     *                                                                                                quella
     *                                                                                                disponibile
     */
    void decrementaQuantita(int idPacchetto, int quantitaDaDecrementare);
}
