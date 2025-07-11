/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.ordine;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.StatoCorrente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * DTO for detailed order information for vendors.
 * Contains order data with information about the buyer and purchased items.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrdineVenditoreDetailDTO {

    private Long idOrdine;
    private Date dataOrdine;
    private double importoTotale;
    private StatoCorrente statoCorrente;
    private Long idVenditore;

    // Informazioni dell'acquirente
    private String nomeAcquirente;
    private String cognomeAcquirente;
    private String emailAcquirente;
    private String telefonoAcquirente;
    private Long idAcquirente;

    // Numero totale di articoli nell'ordine
    private int numeroArticoli;

    // Dettagli dei prodotti acquistati
    private List<ArticoloAcquistatoDTO> articoliAcquistati;

    /**
     * DTO for items purchased in the order.
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ArticoloAcquistatoDTO {
        private Long idRiga;
        private Integer quantitaOrdinata;
        private Double prezzoUnitario;
        private Double prezzoTotale;

        // Informazioni dell'acquistabile
        private String tipoAcquistabile;
        private Long idAcquistabile;
        private String nomeAcquistabile;
        private String descrizioneAcquistabile;
        private String categoriaAcquistabile;
        private Integer quantitaDisponibile;

        // Informazioni aggiuntive per i prodotti
        private String unitaMisura;
        private String origine;
        private Date dataProduzione;
        private Date dataScadenza;
    }
}
