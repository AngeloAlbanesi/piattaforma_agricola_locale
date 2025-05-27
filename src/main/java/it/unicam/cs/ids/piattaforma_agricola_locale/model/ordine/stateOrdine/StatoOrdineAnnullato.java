package it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.stateOrdine;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.StatoCorrente;

public class StatoOrdineAnnullato implements IStatoOrdine {

    @Override
    public void processaOrdine(Ordine ordine) {
        throw new UnsupportedOperationException("L'ordine è stato annullato e non può essere processato.");
    }

    @Override
    public void spedisciOrdine(Ordine ordine) {
        throw new UnsupportedOperationException("L'ordine è stato annullato e non può essere spedito.");
    }

    @Override
    public void annullaOrdine(Ordine ordine) {
        // L'ordine è già stato annullato
        System.out.println("L'ordine è già stato annullato.");
    }

    @Override
    public void consegnaOrdine(Ordine ordine) {
        throw new UnsupportedOperationException("L'ordine è stato annullato e non può essere consegnato.");
    }

    @Override
    public StatoCorrente getStatoCorrente() {
        return StatoCorrente.ANNULLATO;
    }
}
