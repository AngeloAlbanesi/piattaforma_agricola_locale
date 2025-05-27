package it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.stateOrdine;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.StatoCorrente;

public class StatoOrdinePagatoProntoPerLavorazione implements IStatoOrdine {

    @Override
    public void processaOrdine(Ordine ordine) {
        // Inizia la lavorazione dell'ordine (preparare gli ingredienti, confezionare, ecc.)
        cambiaStato(ordine, new StatoOrdineInLavorazione());
    }

    @Override
    public void spedisciOrdine(Ordine ordine) {
        throw new UnsupportedOperationException("L'ordine non può essere spedito finché non è stato lavorato.");
    }

    @Override
    public void annullaOrdine(Ordine ordine) {
        // È possibile annullare l'ordine anche se è pagato ma non ancora in lavorazione
        cambiaStato(ordine, new StatoOrdineAnnullato());
    }

    @Override
    public void consegnaOrdine(Ordine ordine) {
        throw new UnsupportedOperationException("L'ordine non può essere consegnato finché non è stato lavorato e spedito.");
    }

    @Override
    public StatoCorrente getStatoCorrente() {
        return StatoCorrente.PRONTO_PER_LAVORAZIONE;
    }
}
