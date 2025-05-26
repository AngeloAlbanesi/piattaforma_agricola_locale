package it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.RigaOrdine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.StatoCorrente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class OrdineService {

    private List<Ordine> ordiniList;
    public void creaNuovoOrdine(Acquirente acquirente) {
        int idOrdine = UUID.randomUUID().hashCode();
        StatoCorrente statoOrdine= StatoCorrente.ATTESA_PAGAMENTO;
        Date dataOrdine = new Date();  //mette anche l'ora
        Ordine ordine = new Ordine(idOrdine,dataOrdine,statoOrdine,acquirente);
    }

    public void calcolaPrezzoOrdine(Ordine ordine) {
        double prezzoTotOrdine = 0;
        for(RigaOrdine r : ordine.getRigheOrdine()){
            double prezzoRiga = r.getPrezzoUnitario() * r.getQuantitaOrdinata();
            prezzoTotOrdine += prezzoRiga;
        }
        ordine.setImportoTotale(prezzoTotOrdine);
    }




}
