/*
 *   Copyright (c) 2025 
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti;

import jakarta.persistence.*;
import java.util.List;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Pacchetto;

@Entity
@DiscriminatorValue("DISTRIBUTORE_TIPICITA")
public class DistributoreDiTipicita extends Venditore {
    
    @OneToMany(mappedBy = "distributore", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pacchetto> pacchettiOfferti;

    public DistributoreDiTipicita() {}

    public DistributoreDiTipicita(String nome, String cognome, String email, String passwordHash,
            String numeroTelefono, DatiAzienda datiAzienda, TipoRuolo tipoRuolo) {
        super(nome, cognome, email, passwordHash, numeroTelefono, datiAzienda, tipoRuolo);
        this.pacchettiOfferti = new java.util.ArrayList<>();
    }

    public List<Pacchetto> getPacchettiOfferti() {
        return pacchettiOfferti;
    }

}