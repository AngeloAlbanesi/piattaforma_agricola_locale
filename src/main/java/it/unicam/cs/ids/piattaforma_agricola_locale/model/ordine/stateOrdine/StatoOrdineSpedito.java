package it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.stateOrdine;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.StatoCorrente;

public class StatoOrdineSpedito implements IStatoOrdine {

    @Override
    public void processaOrdine(Ordine ordine) {
        throw new UnsupportedOperationException("L'ordine è già stato spedito e non può essere processato nuovamente.");
    }

    @Override
    public void spedisciOrdine(Ordine ordine) {
        // L'ordine è già stato spedito
        System.out.println("L'ordine è già stato spedito.");
    }

    @Override
    public void annullaOrdine(Ordine ordine) {
        throw new UnsupportedOperationException("L'ordine non può essere annullato dopo la spedizione.");
    }

    @Override
    public void consegnaOrdine(Ordine ordine) {
        // L'ordine spedito può essere consegnato
        cambiaStato(ordine, new StatoOrdineConsegnato());
        System.out.println("Ordine consegnato con successo.");
    }

    @Override
    public StatoCorrente getStatoCorrente() {
        return StatoCorrente.SPEDITO;
    }
}
