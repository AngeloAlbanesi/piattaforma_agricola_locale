package it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.stateOrdine;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.StatoCorrente;

public class StatoOrdineInLavorazione implements IStatoOrdine {

    @Override
    public void processaOrdine(Ordine ordine) {
        // L'ordine è già in lavorazione, non è necessaria nessuna azione
        System.out.println("L'ordine è già in fase di lavorazione.");
    }

    @Override
    public void spedisciOrdine(Ordine ordine) {
        // La lavorazione è completata, l'ordine può essere spedito
        cambiaStato(ordine, new StatoOrdineSpedito());
        System.out.println("Ordine spedito con successo.");
    }

    @Override
    public void annullaOrdine(Ordine ordine) {
        // È possibile annullare l'ordine anche durante la lavorazione
        cambiaStato(ordine, new StatoOrdineAnnullato());
        System.out.println("Ordine annullato con successo.");
    }

    @Override
    public void consegnaOrdine(Ordine ordine) {
        // Non è possibile consegnare un ordine che non è ancora stato spedito
        throw new UnsupportedOperationException("L'ordine deve essere prima spedito prima di poter essere consegnato.");
    }

    @Override
    public StatoCorrente getStatoCorrente() {
        return StatoCorrente.IN_LAVORAZIONE;
    }
}
