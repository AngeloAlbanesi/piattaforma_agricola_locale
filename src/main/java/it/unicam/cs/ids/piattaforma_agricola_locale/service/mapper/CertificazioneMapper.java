/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.service.mapper;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo.CertificazioneDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;

/**
 * Mapper utility class for converting between Certificazione entities and CertificazioneDTO.
 * Provides static methods for mapping between entity and DTO representations.
 */
public class CertificazioneMapper {

    private CertificazioneMapper() {
        // Utility class, prevent instantiation
    }

    /**
     * Converts a Certificazione entity to CertificazioneDTO.
     * 
     * @param certificazione the entity to convert
     * @return the CertificazioneDTO representation
     */
    public static CertificazioneDTO toDTO(Certificazione certificazione) {
        if (certificazione == null) {
            return null;
        }

        return new CertificazioneDTO(
            certificazione.getIdCertificazione(),
            certificazione.getNomeCertificazione(),
            certificazione.getEnteRilascio(),
            certificazione.getDataRilascio(),
            certificazione.getDataScadenza(),
            certificazione.getIdProdottoAssociato(),
            certificazione.getIdAziendaAssociata()
        );
    }

    /**
     * Creates a new Certificazione entity from CertificazioneDTO.
     * 
     * @param dto the DTO containing certification data
     * @return a new Certificazione entity
     */
    public static Certificazione fromDTO(CertificazioneDTO dto) {
        if (dto == null) {
            return null;
        }

        Certificazione certificazione = new Certificazione();
        certificazione.SetIdCertificazione(dto.getIdCertificazione());
        certificazione.setNomeCertificazione(dto.getNomeCertificazione());
        certificazione.setEnteRilascio(dto.getEnteRilascio());
        certificazione.setDataRilascio(dto.getDataRilascio());
        certificazione.setDataScadenza(dto.getDataScadenza());

        return certificazione;
    }
}