package it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.Acquistabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.RigaOrdine;

import java.util.List;
import java.util.UUID;

public class RigaOrdineService {

    private List<RigaOrdine> righeOrdiniList;
    public void CreaRigaOrdine(Ordine ordine, Acquistabile acquistabile, int quantita) {
        int idRiga = UUID.randomUUID().hashCode();
        RigaOrdine nuovaRiga = new RigaOrdine(idRiga,acquistabile,quantita, acquistabile.getPrezzo());
        ordine.getRigheOrdine().add(nuovaRiga);
        righeOrdiniList.add(nuovaRiga);

    }


}
