/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.azienda;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo.CertificazioneDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.StatoVerificaValori;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for company data details.
 * Used to transfer company information to frontend in the expected format.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AziendaDetailDTO {

    private Long id;
    private String nomeAzienda;
    private String partitaIva;
    private String codiceFiscale;
    private String descrizione;
    private IndirizzoDTO indirizzo;
    private String telefono;
    private String email;
    private String sito;
    private String logo;
    private List<CertificazioneDTO> certificazioni;
    private String dataRegistrazione;
    private StatoVerificaValori statoAccreditamento;
    private String tipologiaAzienda;

    /**
     * Inner DTO for address information
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IndirizzoDTO {
        private String via;
        private String civico;
        private String cap;
        private String citta;
        private String provincia;
        private String paese;
    }
}