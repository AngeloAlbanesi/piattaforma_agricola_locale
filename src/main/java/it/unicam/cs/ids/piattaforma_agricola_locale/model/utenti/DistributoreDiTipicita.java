/*
 *   Copyright (c) 2025 
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti;

import java.util.List;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Pacchetto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;

public class DistributoreDiTipicita extends Venditore {
    // Costruttore overload per factory (solo parametri base Venditore)
    private final List<Pacchetto> pacchettiOfferti;

    public DistributoreDiTipicita(String nome, String cognome, String email, String passwordHash,
            String numeroTelefono, DatiAzienda datiAzienda, TipoRuolo tipoRuolo) {
        super(nome, cognome, email, passwordHash, numeroTelefono, datiAzienda, tipoRuolo);
        this.pacchettiOfferti = new java.util.ArrayList<>();
    }

    public List<Pacchetto> getPacchettiOfferti() {
        return pacchettiOfferti;
    }

}