package it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.Acquistabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;

public interface IRigaOrdineService {

    void creaRigaOrdine(Ordine ordine, Acquistabile acquistabile, int quantita);

}
