/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.ordine;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.StatoCorrente;
import java.util.Date;

/**
 * DTO for order summary information, used in order lists and history.
 * Contains essential order data without detailed line items.
 */
public class OrdineSummaryDTO {
    
    private Long idOrdine;
    private Date dataOrdine;
    private double importoTotale;
    private StatoCorrente statoCorrente;
    private String nomeAcquirente;
    private Long idAcquirente;
    private int numeroArticoli;

    public OrdineSummaryDTO() {
    }

    public OrdineSummaryDTO(Long idOrdine, Date dataOrdine, double importoTotale, 
                          StatoCorrente statoCorrente, String nomeAcquirente, 
                          Long idAcquirente, int numeroArticoli) {
        this.idOrdine = idOrdine;
        this.dataOrdine = dataOrdine;
        this.importoTotale = importoTotale;
        this.statoCorrente = statoCorrente;
        this.nomeAcquirente = nomeAcquirente;
        this.idAcquirente = idAcquirente;
        this.numeroArticoli = numeroArticoli;
    }

    public Long getIdOrdine() {
        return idOrdine;
    }

    public void setIdOrdine(Long idOrdine) {
        this.idOrdine = idOrdine;
    }

    public Date getDataOrdine() {
        return dataOrdine;
    }

    public void setDataOrdine(Date dataOrdine) {
        this.dataOrdine = dataOrdine;
    }

    public double getImportoTotale() {
        return importoTotale;
    }

    public void setImportoTotale(double importoTotale) {
        this.importoTotale = importoTotale;
    }

    public StatoCorrente getStatoCorrente() {
        return statoCorrente;
    }

    public void setStatoCorrente(StatoCorrente statoCorrente) {
        this.statoCorrente = statoCorrente;
    }

    public String getNomeAcquirente() {
        return nomeAcquirente;
    }

    public void setNomeAcquirente(String nomeAcquirente) {
        this.nomeAcquirente = nomeAcquirente;
    }

    public Long getIdAcquirente() {
        return idAcquirente;
    }

    public void setIdAcquirente(Long idAcquirente) {
        this.idAcquirente = idAcquirente;
    }

    public int getNumeroArticoli() {
        return numeroArticoli;
    }

    public void setNumeroArticoli(int numeroArticoli) {
        this.numeroArticoli = numeroArticoli;
    }
}