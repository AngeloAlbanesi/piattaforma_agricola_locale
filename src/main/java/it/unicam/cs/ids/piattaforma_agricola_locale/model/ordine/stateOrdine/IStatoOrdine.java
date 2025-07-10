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
     * @return l'enum StatoCorrente corrispondente a questo stato
     */
    StatoCorrente getStatoCorrente();
    
    /**
     * Metodo di utilità per effettuare la transizione di stato
     * @param ordine l'ordine su cui effettuare la transizione
     * @param nuovoStato il nuovo stato da impostare
     */
    default void cambiaStato(Ordine ordine, IStatoOrdine nuovoStato) {
        try {
            System.out.println("DEBUG - cambiaStato - Da: " + 
                (ordine.getStato() != null ? ordine.getStato().getClass().getSimpleName() : "null") + 
                " a: " + nuovoStato.getClass().getSimpleName());
            
            // Imposta il nuovo stato
            ordine.setStato(nuovoStato);
            
            // Aggiorna anche il campo persistente statoCorrente
            StatoCorrente nuovoStatoEnum = nuovoStato.getStatoCorrente();
            System.out.println("DEBUG - cambiaStato - Nuovo stato enum: " + nuovoStatoEnum);
            
            ordine.setStatoCorrente(nuovoStatoEnum);
            System.out.println("DEBUG - cambiaStato - Stato aggiornato con successo");
        } catch (Exception e) {
            System.err.println("DEBUG - Errore in cambiaStato: " + e.getMessage());
            e.printStackTrace();
            throw e; // Rilancia l'eccezione per essere gestita dal chiamante
        }
    }
}
