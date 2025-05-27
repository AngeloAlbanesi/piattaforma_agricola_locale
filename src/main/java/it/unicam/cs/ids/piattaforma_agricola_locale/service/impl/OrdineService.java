package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.RigaOrdine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.StatoCorrente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IOrdineService;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class OrdineService implements IOrdineService {

    private List<Ordine> ordiniList;
    
    public void creaNuovoOrdine(Acquirente acquirente) {
        int idOrdine = UUID.randomUUID().hashCode();
        Date dataOrdine = new Date();  // mette anche l'ora
        // Non è più necessario passare lo stato come parametro,
        // viene inizializzato automaticamente a "in attesa di pagamento"
        Ordine ordine = new Ordine(idOrdine, dataOrdine, acquirente);
        
        // Se avessimo una lista da gestire, potremmo aggiungere:
        // ordiniList.add(ordine);
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
