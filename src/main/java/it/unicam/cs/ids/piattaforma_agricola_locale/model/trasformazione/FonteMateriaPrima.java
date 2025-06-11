package it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione;

/**
 * Interfaccia che rappresenta l'origine di una materia prima,
 * astraendo se la fonte è interna (un produttore della piattaforma)
 * o esterna.
 */
public interface FonteMateriaPrima {
    /**
     * Restituisce una descrizione testuale della fonte.
     *
     * @return una stringa che descrive la fonte.
     */
    String getDescrizione();
}