package it.unicam.cs.ids.piattaforma_agricola_locale.model.carrello;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.Acquistabile;

public class ElementoCarrello {
    private Acquistabile acquistabile;
    private int quantita;
    private double prezzoUnitario;

    public ElementoCarrello(Acquistabile acquistabile, int quantita) {
        this.acquistabile = acquistabile;
        this.quantita = quantita;
        this.prezzoUnitario = acquistabile.getPrezzo();
    }

    public Acquistabile getAcquistabile() {
        return acquistabile;
    }

    public void setAcquistabile(Acquistabile acquistabile) {
        this.acquistabile = acquistabile;
        this.prezzoUnitario = acquistabile.getPrezzo();
    }

    public int getQuantita() {
        return quantita;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    public double getPrezzoUnitario() {
        return prezzoUnitario;
    }

    public void setPrezzoUnitario(double prezzoUnitario) {
        this.prezzoUnitario = prezzoUnitario;
    }

    /**
     * Calcola il prezzo totale per questo elemento (quantità * prezzo unitario)
     * 
     * @return il prezzo totale dell'elemento
     */
    public double calcolaPrezzoTotale() {
        return quantita * prezzoUnitario;
    }

}
