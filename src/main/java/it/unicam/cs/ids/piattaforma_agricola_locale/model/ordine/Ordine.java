package it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine;


import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Ordine {
    int idOrdine;
    Date dataOrdine;
    double importoTotale;
    StatoCorrente StatoOrdine;
    Acquirente acquirente;
    List<RigaOrdine> righeOrdine;

    public Ordine(int idOrdine,Date dataOrdine,StatoCorrente StatoOrdine,Acquirente acquirente) {
        this.idOrdine = idOrdine;
        this.dataOrdine = dataOrdine;
        this.importoTotale = 0.0;
        this.StatoOrdine = StatoOrdine;
        this.acquirente = acquirente;
        this.righeOrdine = new ArrayList<>();
    }

    public int getIdOrdine() {
        return idOrdine;
    }

    public void setIdOrdine(int idOrdine) {
        this.idOrdine = idOrdine;
    }

    public double getImportoTotale() {
        return importoTotale;
    }

    public void setImportoTotale(double importoTotale) {
        this.importoTotale = importoTotale;
    }

    public Date getDataOrdine() {
        return dataOrdine;
    }

    public void setDataOrdine(Date dataOrdine) {
        this.dataOrdine = dataOrdine;
    }

    public Acquirente getAcquirente() {
        return acquirente;
    }

    public void setAcquirente(Acquirente acquirente) {
        this.acquirente = acquirente;
    }

    public StatoCorrente getStatoOrdine() {
        return StatoOrdine;
    }

    public void setStatoOrdine(StatoCorrente statoOrdine) {
        StatoOrdine = statoOrdine;
    }

    public List<RigaOrdine> getRigheOrdine() {
        return righeOrdine;
    }

    public void setRigheOrdine(List<RigaOrdine> righeOrdine) {
        this.righeOrdine = righeOrdine;
    }
}
