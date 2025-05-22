package it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces;

import java.util.ArrayList;
import java.util.List;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.StatoVerificaValori;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

public class CuratoreService implements ICuratoreService{

    public List<DatiAzienda> getDatiAziendaInAttesaRevisione(List<Venditore> venditori) {
        List<DatiAzienda> datiAziendaInAttesa = new ArrayList<>();
        for (Venditore venditore : venditori) {
            if (venditore.getDatiAzienda().getStatoVerifica() == StatoVerificaValori.IN_REVISIONE) {
                datiAziendaInAttesa.add(venditore.getDatiAzienda());
            }
        }
        return datiAziendaInAttesa;
    }

}
