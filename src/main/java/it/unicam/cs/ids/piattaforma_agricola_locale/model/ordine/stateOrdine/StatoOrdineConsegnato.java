package it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.stateOrdine;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.StatoCorrente;

public class StatoOrdineConsegnato implements IStatoOrdine {

    @Override
    public void processaOrdine(Ordine ordine) {
        throw new UnsupportedOperationException("L'ordine è già stato consegnato e non può essere processato ulteriormente.");
    }

    @Override
    public void spedisciOrdine(Ordine ordine) {
        throw new UnsupportedOperationException("L'ordine è già stato consegnato e non può essere spedito nuovamente.");
    }

    @Override
    public void annullaOrdine(Ordine ordine) {
        throw new UnsupportedOperationException("L'ordine è già stato consegnato e non può essere annullato.");
    }

    @Override
    public void consegnaOrdine(Ordine ordine) {
        // L'ordine è già stato consegnato
        System.out.println("L'ordine è già stato consegnato.");
    }

    @Override
    public StatoCorrente getStatoCorrente() {
        return StatoCorrente.CONSEGNATO;
    }
}
