/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.ordine;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.utente.UserPublicDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.StatoCorrente;
import java.util.Date;
import java.util.List;

/**
 * DTO for detailed order information, used in single order view.
 * Contains all order data including line items and buyer information.
 */
public class OrdineDetailDTO {
    
    private Long idOrdine;
    private Date dataOrdine;
    private double importoTotale;
    private StatoCorrente statoCorrente;
    private UserPublicDTO acquirente;
    private List<RigaOrdineDTO> righeOrdine;

    public OrdineDetailDTO() {
    }

    public OrdineDetailDTO(Long idOrdine, Date dataOrdine, double importoTotale, 
                         StatoCorrente statoCorrente, UserPublicDTO acquirente, 
                         List<RigaOrdineDTO> righeOrdine) {
        this.idOrdine = idOrdine;
        this.dataOrdine = dataOrdine;
        this.importoTotale = importoTotale;
        this.statoCorrente = statoCorrente;
        this.acquirente = acquirente;
        this.righeOrdine = righeOrdine;
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

    public UserPublicDTO getAcquirente() {
        return acquirente;
    }

    public void setAcquirente(UserPublicDTO acquirente) {
        this.acquirente = acquirente;
    }

    public List<RigaOrdineDTO> getRigheOrdine() {
        return righeOrdine;
    }

    public void setRigheOrdine(List<RigaOrdineDTO> righeOrdine) {
        this.righeOrdine = righeOrdine;
    }

    /**
     * Gets the total number of items in the order.
     * 
     * @return the total item count
     */
    public int getTotalArticoli() {
        return righeOrdine != null ? 
            righeOrdine.stream().mapToInt(RigaOrdineDTO::getQuantitaOrdinata).sum() : 0;
    }
}