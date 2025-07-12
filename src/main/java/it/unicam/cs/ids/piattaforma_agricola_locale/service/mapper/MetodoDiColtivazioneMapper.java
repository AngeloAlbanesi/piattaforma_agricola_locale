/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.service.mapper;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.coltivazione.MetodoDiColtivazioneDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.coltivazione.MetodoDiColtivazione;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

/**
 * MapStruct mapper for converting between MetodoDiColtivazione entities and DTOs.
 * Provides simple entity-to-DTO conversion for cultivation method information.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
@Component
public interface MetodoDiColtivazioneMapper {

    /**
     * Converts MetodoDiColtivazione entity to MetodoDiColtivazioneDTO.
     * Simple one-to-one mapping with null-safety.
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nome", source = "nome")
    @Mapping(target = "descrizione", source = "descrizione")
    @Mapping(target = "dataInizio", source = "dataInizio")
    @Mapping(target = "dataFine", source = "dataFine")
    MetodoDiColtivazioneDTO toDTO(MetodoDiColtivazione metodoDiColtivazione);

    /**
     * Converts MetodoDiColtivazioneDTO to MetodoDiColtivazione entity.
     * Used for creating new cultivation methods from DTO data.
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nome", source = "nome")
    @Mapping(target = "descrizione", source = "descrizione")
    @Mapping(target = "dataInizio", source = "dataInizio")
    @Mapping(target = "dataFine", source = "dataFine")
    MetodoDiColtivazione toEntity(MetodoDiColtivazioneDTO metodoDiColtivazioneDTO);

    /**
     * Updates an existing MetodoDiColtivazione entity with data from DTO.
     * Preserves the ID and only updates the modifiable fields.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nome", source = "nome")
    @Mapping(target = "descrizione", source = "descrizione")
    @Mapping(target = "dataInizio", source = "dataInizio")
    @Mapping(target = "dataFine", source = "dataFine")
    void updateFromDTO(MetodoDiColtivazioneDTO metodoDiColtivazioneDTO, @MappingTarget MetodoDiColtivazione metodoDiColtivazione);

    /**
     * Creates a new MetodoDiColtivazione entity from DTO, ignoring the ID.
     * Used for creation operations where ID should be auto-generated.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nome", source = "nome")
    @Mapping(target = "descrizione", source = "descrizione")
    @Mapping(target = "dataInizio", source = "dataInizio")
    @Mapping(target = "dataFine", source = "dataFine")
    MetodoDiColtivazione toEntityIgnoreId(MetodoDiColtivazioneDTO metodoDiColtivazioneDTO);
}