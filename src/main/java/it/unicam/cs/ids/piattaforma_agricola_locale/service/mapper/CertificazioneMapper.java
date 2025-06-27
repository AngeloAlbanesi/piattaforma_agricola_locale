/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.service.mapper;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo.CertificazioneDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

/**
 * MapStruct mapper for converting between Certificazione entities and CertificazioneDTO.
 * Provides simple entity-to-DTO conversion for certification information.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
@Component
public interface CertificazioneMapper {

    /**
     * Converts a Certificazione entity to CertificazioneDTO.
     * Simple one-to-one mapping with date field handling.
     */
    @Mapping(target = "idCertificazione", source = "idCertificazione")
    @Mapping(target = "nomeCertificazione", source = "nomeCertificazione")
    @Mapping(target = "enteRilascio", source = "enteRilascio")
    @Mapping(target = "dataRilascio", source = "dataRilascio")
    @Mapping(target = "dataScadenza", source = "dataScadenza")
    @Mapping(target = "idProdottoAssociato", source = "idProdottoAssociato")
    @Mapping(target = "idAziendaAssociata", source = "idAziendaAssociata")
    CertificazioneDTO toDTO(Certificazione certificazione);

    /**
     * Creates a new Certificazione entity from CertificazioneDTO.
     * Note: associations must be handled by the service layer.
     */
    @Mapping(target = "idCertificazione", ignore = true)
    @Mapping(target = "nomeCertificazione", source = "nomeCertificazione")
    @Mapping(target = "enteRilascio", source = "enteRilascio")
    @Mapping(target = "dataRilascio", source = "dataRilascio")
    @Mapping(target = "dataScadenza", source = "dataScadenza")
    @Mapping(target = "idProdottoAssociato", ignore = true)
    @Mapping(target = "idAziendaAssociata", ignore = true)
    Certificazione fromDTO(CertificazioneDTO dto);

    /**
     * Updates an existing Certificazione entity with data from DTO.
     * Preserves the ID and associations.
     */
    @Mapping(target = "idCertificazione", ignore = true)
    @Mapping(target = "nomeCertificazione", source = "nomeCertificazione")
    @Mapping(target = "enteRilascio", source = "enteRilascio")
    @Mapping(target = "dataRilascio", source = "dataRilascio")
    @Mapping(target = "dataScadenza", source = "dataScadenza")
    @Mapping(target = "idProdottoAssociato", ignore = true)
    @Mapping(target = "idAziendaAssociata", ignore = true)
    void updateFromDTO(CertificazioneDTO dto, @MappingTarget Certificazione certificazione);

    /**
     * Creates a new Certificazione entity from DTO, ignoring the ID.
     * Used for creation operations where ID should be auto-generated.
     */
    @Mapping(target = "idCertificazione", ignore = true)
    @Mapping(target = "nomeCertificazione", source = "nomeCertificazione")
    @Mapping(target = "enteRilascio", source = "enteRilascio")
    @Mapping(target = "dataRilascio", source = "dataRilascio")
    @Mapping(target = "dataScadenza", source = "dataScadenza")
    @Mapping(target = "idProdottoAssociato", ignore = true)
    @Mapping(target = "idAziendaAssociata", ignore = true)
    Certificazione fromDTOIgnoreId(CertificazioneDTO dto);
}