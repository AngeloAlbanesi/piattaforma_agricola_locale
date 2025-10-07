/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo;

/**
 * Enum che rappresenta le unità di misura disponibili per i prodotti.
 * Utilizzato per standardizzare le unità di misura nel sistema.
 */
public enum UnitaMisura {
    /**
     * Chilogrammi
     */
    KG("Chilogrammi"),
    
    /**
     * Grammi
     */
    G("Grammi"),
    
    /**
     * Litri
     */
    L("Litri"),
    
    /**
     * Millilitri
     */
    ML("Millilitri");
    
    private final String descrizione;
    
    UnitaMisura(String descrizione) {
        this.descrizione = descrizione;
    }
    
    /**
     * Restituisce la descrizione dell'unità di misura.
     *
     * @return La descrizione dell'unità di misura
     */
    public String getDescrizione() {
        return descrizione;
    }
}