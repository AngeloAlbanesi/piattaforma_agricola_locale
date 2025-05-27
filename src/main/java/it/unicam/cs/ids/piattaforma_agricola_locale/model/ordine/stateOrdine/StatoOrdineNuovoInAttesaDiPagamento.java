package it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.stateOrdine;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.StatoCorrente;

public class StatoOrdineNuovoInAttesaDiPagamento implements IStatoOrdine {

    @Override
    public void processaOrdine(Ordine ordine) {
        // Il pagamento è stato effettuato, passiamo allo stato "Pagato Pronto Per Lavorazione"
        cambiaStato(ordine, new StatoOrdinePagatoProntoPerLavorazione());
    }

    @Override
    public void spedisciOrdine(Ordine ordine) {
        // Non è possibile spedire un ordine in attesa di pagamento
        throw new UnsupportedOperationException("L'ordine è in attesa di pagamento e non può essere spedito.");
    }

    @Override
    public void annullaOrdine(Ordine ordine) {
        // Annulla l'ordine in attesa di pagamento
        cambiaStato(ordine, new StatoOrdineAnnullato());
    }

    @Override
    public void consegnaOrdine(Ordine ordine) {
        // Non è possibile consegnare un ordine in attesa di pagamento
        throw new UnsupportedOperationException("L'ordine è in attesa di pagamento e non può essere consegnato.");
    }

    @Override
    public StatoCorrente getStatoCorrente() {
        return StatoCorrente.ATTESA_PAGAMENTO;
    }
}
