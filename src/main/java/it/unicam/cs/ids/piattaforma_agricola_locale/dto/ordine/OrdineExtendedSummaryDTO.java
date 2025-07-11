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
 * DTO for extended order summary information for buyers.
 * Contains order data with detailed line items and vendor information.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrdineExtendedSummaryDTO {

    private Long idOrdine;
    private Date dataOrdine;
    private double importoTotale;
    private StatoCorrente statoCorrente;
    private String nomeAcquirente;
    private Long idAcquirente;
    private int numeroArticoli;

    // Informazioni del venditore
    private Long idVenditore;
    private String nomeVenditore;
    private String emailVenditore;
    private String nomeAziendaVenditore;

    // Dettagli dei prodotti
    private List<RigaOrdineDetailDTO> articoli;

    /**
     * DTO for detailed order line information.
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RigaOrdineDetailDTO {
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

        // Informazioni del venditore specifico per questo articolo
        private String nomeVenditoreArticolo;
        private String emailVenditoreArticolo;
        private String nomeAziendaVenditoreArticolo;
    }
}
