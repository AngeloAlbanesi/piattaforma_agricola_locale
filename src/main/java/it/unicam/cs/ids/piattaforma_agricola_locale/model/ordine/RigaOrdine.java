package it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.Acquistabile;

public class RigaOrdine {
    private Long idRiga;
    private Acquistabile acquistabile;
    private int quantitaOrdinata;
    private double prezzoUnitario;


    public RigaOrdine(Acquistabile acquistabile, int quantitaOrdinata, double prezzoUnitario) {

        this.acquistabile = acquistabile;
        this.quantitaOrdinata = quantitaOrdinata;
        this.prezzoUnitario = prezzoUnitario;
    }

    public Long getIdRiga() {
        return idRiga;
    }

    public void setIdRiga(Long idRiga) {
        this.idRiga = idRiga;
    }

    public Acquistabile getAcquistabile() {
        return acquistabile;
    }

    public void setAcquistabile(Acquistabile acquistabile) {
        this.acquistabile = acquistabile;
    }

    public int getQuantitaOrdinata() {
        return quantitaOrdinata;
    }

    public void setQuantitaOrdinata(int quantitaOrdinata) {
        this.quantitaOrdinata = quantitaOrdinata;
    }

    public double getPrezzoUnitario() {
        return prezzoUnitario;
    }

    public void setPrezzoUnitario(double prezzoUnitario) {
        this.prezzoUnitario = prezzoUnitario;
    }

    @Override
    public String toString() {
        return "RigaOrdine{" +
                "idRiga=" + idRiga +
                ", acquistabile=" + acquistabile.getNome() +
                ", quantitaOrdinata=" + quantitaOrdinata +
                ", prezzoUnitario=" + prezzoUnitario +
                '}';
    }
}
