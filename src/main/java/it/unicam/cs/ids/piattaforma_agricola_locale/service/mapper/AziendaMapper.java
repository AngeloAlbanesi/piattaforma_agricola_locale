/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.service.mapper;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.azienda.AziendaDetailDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo.CertificazioneDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper per convertire entità azienda in DTO per il frontend.
 */
@Component
public class AziendaMapper {

    /**
     * Converte DatiAzienda in AziendaDetailDTO
     */
    public AziendaDetailDTO toDetailDTO(DatiAzienda datiAzienda, Venditore venditore) {
        if (datiAzienda == null) {
            return null;
        }

        return AziendaDetailDTO.builder()
                .id(datiAzienda.getId())
                .nomeAzienda(datiAzienda.getNomeAzienda())
                .partitaIva(datiAzienda.getPartitaIva())
                .descrizione(datiAzienda.getDescrizioneAzienda())
                .indirizzo(parseIndirizzo(datiAzienda.getIndirizzoAzienda()))
                .telefono(venditore != null ? venditore.getNumeroTelefono() : "Non specificato")
                .email(venditore != null ? venditore.getEmail() : "Non specificato")
                .sito(datiAzienda.getSitoWebUrl())
                .logo(datiAzienda.getLogoUrl())
                .certificazioni(mapCertificazioni(datiAzienda))
                .statoAccreditamento(datiAzienda.getStatoVerifica())
                .tipologiaAzienda(determinaTipologiaAzienda(venditore))
                .build();
    }

    /**
     * Parsifica l'indirizzo completo in un oggetto strutturato
     */
    private AziendaDetailDTO.IndirizzoDTO parseIndirizzo(String indirizzoCompleto) {
        if (indirizzoCompleto == null || indirizzoCompleto.trim().isEmpty()) {
            return AziendaDetailDTO.IndirizzoDTO.builder()
                    .via("Non specificato")
                    .build();
        }

        // Per ora manteniamo l'indirizzo come via completa
        // In futuro si potrebbe implementare un parser più sofisticato
        return AziendaDetailDTO.IndirizzoDTO.builder()
                .via(indirizzoCompleto)
                .build();
    }

    /**
     * Mappa le certificazioni aziendali
     */
    private List<CertificazioneDTO> mapCertificazioni(DatiAzienda datiAzienda) {
        if (datiAzienda.getCertificazioniAzienda() == null) {
            return List.of();
        }

        return datiAzienda.getCertificazioniAzienda().stream()
                .map(cert -> CertificazioneDTO.builder()
                        .idCertificazione(cert.getId())
                        .nomeCertificazione(cert.getNome())
                        .enteRilascio(cert.getEnteRilascio())
                        .dataRilascio(cert.getDataRilascio())
                        .dataScadenza(cert.getDataScadenza())
                        .idAziendaAssociata(cert.getIdAziendaAssociata())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Determina la tipologia dell'azienda basandosi sul tipo di venditore
     */
    private String determinaTipologiaAzienda(Venditore venditore) {
        if (venditore == null) {
            return "ALTRO";
        }

        String tipoVenditore = venditore.getClass().getSimpleName();
        return switch (tipoVenditore) {
            case "Produttore" -> "PRODUZIONE";
            case "Trasformatore" -> "TRASFORMAZIONE";
            case "DistributoreDiTipicita" -> "DISTRIBUZIONE";
            default -> "ALTRO";
        };
    }
}