package it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;

public interface IOrdineService {
    void creaNuovoOrdine(Acquirente acquirente);

    void calcolaPrezzoOrdine(Ordine ordine);
}
