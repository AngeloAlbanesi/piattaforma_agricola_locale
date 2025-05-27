package it.unicam.cs.ids.piattaforma_agricola_locale.model.carrello;

import java.util.Date;
import java.util.List;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;

public class carrello {
    private int idCarrello;
    private Acquirente acquirente;
    private List<ElementoCarrello> elementiCarrello;
    private Date ultimaModifica;

}
