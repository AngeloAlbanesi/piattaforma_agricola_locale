package it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.stateOrdine;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.StatoCorrente;

public interface IStatoOrdine {
    void processaOrdine(Ordine ordine);

    void spedisciOrdine(Ordine ordine);

    void annullaOrdine(Ordine ordine);

    void consegnaOrdine(Ordine ordine);

    /**
     * Restituisce la rappresentazione descrittiva dello stato corrente
     * 
     * @return l'enum StatoCorrente corrispondente a questo stato
     */
    StatoCorrente getStatoCorrente();

    /**
     * Metodo di utilità per effettuare la transizione di stato
     * 
     * @param ordine     l'ordine su cui effettuare la transizione
     * @param nuovoStato il nuovo stato da impostare
     */
    default void cambiaStato(Ordine ordine, IStatoOrdine nuovoStato) {
        try {
            ordine.setStato(nuovoStato);
            StatoCorrente nuovoStatoEnum = nuovoStato.getStatoCorrente();
            ordine.setStatoCorrente(nuovoStatoEnum);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
