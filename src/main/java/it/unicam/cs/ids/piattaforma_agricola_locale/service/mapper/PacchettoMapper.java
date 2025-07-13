/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.service.mapper;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo.*;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.utente.UserPublicDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Pacchetto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.Acquistabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.Evento;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DistributoreDiTipicita;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * MapStruct mapper for converting between Pacchetto entities and Package DTOs.
 * Handles complex polymorphic relationships for Acquistabile elements.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
@Component
public interface PacchettoMapper {

    /**
     * Converts Pacchetto entity to PacchettoDetailDTO.
     * Includes distributor information and all included elements.
     */
    @Mapping(target = "idPacchetto", source = "id")
    @Mapping(target = "nome", source = "nome")
    @Mapping(target = "descrizione", source = "descrizione")
    @Mapping(target = "prezzoPacchetto", source = "prezzo")
    @Mapping(target = "quantitaDisponibile", source = "quantitaDisponibile")
    @Mapping(target = "distributore", source = "distributore", qualifiedByName = "distributoreToUserPublicDTO")
    @Mapping(target = "elementiInclusi", source = "elementiInclusi", qualifiedByName = "acquistabiliToElementiDTO")
    PacchettoDetailDTO toDetailDTO(Pacchetto pacchetto);

    /**
     * Converts Pacchetto entity to PacchettoSummaryDTO.
     * Contains only essential fields for listing views.
     */
    @Mapping(target = "idPacchetto", source = "id")
    @Mapping(target = "nome", source = "nome")
    @Mapping(target = "prezzoPacchetto", source = "prezzo")
    @Mapping(target = "quantitaDisponibile", source = "quantitaDisponibile")
    @Mapping(target = "nomeDistributore", expression = "java(pacchetto.getDistributore().getNome() + \" \" + pacchetto.getDistributore().getCognome())")
    @Mapping(target = "idDistributore", source = "distributore.idUtente")
    @Mapping(target = "numeroElementi", expression = "java(pacchetto.getElementiInclusi() != null ? pacchetto.getElementiInclusi().size() : 0)")
    PacchettoSummaryDTO toSummaryDTO(Pacchetto pacchetto);

    /**
     * Creates a new Pacchetto entity from CreatePacchettoRequestDTO.
     * Note: All entity configuration must be handled by the service.
     */
    default Pacchetto fromCreateRequestDTO(CreatePacchettoRequestDTO createPacchettoRequestDTO) {
        // Delegate to service for proper entity creation
        throw new UnsupportedOperationException("Use service layer to create Pacchetto entities");
    }

    /**
     * Updates an existing Pacchetto entity with data from CreatePacchettoRequestDTO.
     * Note: Entity updates must be handled by the service.
     */
    default void updateFromCreateRequestDTO(CreatePacchettoRequestDTO createPacchettoRequestDTO, Pacchetto pacchetto) {
        // Delegate to service for proper entity updates
        throw new UnsupportedOperationException("Use service layer to update Pacchetto entities");
    }

    /**
     * Converts DistributoreDiTipicita to UserPublicDTO.
     */
    @Named("distributoreToUserPublicDTO")
    @Mapping(target = "idUtente", source = "idUtente")
    @Mapping(target = "nome", source = "nome")
    @Mapping(target = "cognome", source = "cognome")
    @Mapping(target = "tipoRuolo", source = "tipoRuolo")
    @Mapping(target = "isAttivo", source = "attivo")
    UserPublicDTO distributoreToUserPublicDTO(DistributoreDiTipicita distributore);

    /**
     * Converts List of Acquistabile to List of ElementoPacchettoDTO.
     * Groups duplicate elements by ID and counts quantities.
     */
    @Named("acquistabiliToElementiDTO")
    default List<ElementoPacchettoDTO> acquistabiliToElementiDTO(List<Acquistabile> acquistabili) {
        if (acquistabili == null) {
            return null;
        }
        
        return acquistabili.stream()
                .collect(Collectors.groupingBy(
                    acquistabile -> acquistabile.getId(),
                    Collectors.counting()
                ))
                .entrySet().stream()
                .map(entry -> {
                    Long elementId = entry.getKey();
                    Long count = entry.getValue();
                    
                    // Find the first occurrence to get element details
                    Acquistabile elemento = acquistabili.stream()
                            .filter(a -> a.getId().equals(elementId))
                            .findFirst()
                            .orElse(null);
                    
                    if (elemento == null) {
                        return null;
                    }
                    
                    ElementoPacchettoDTO dto = acquistabileToElementoDTO(elemento);
                    dto.setQuantita(count.intValue());
                    return dto;
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    /**
     * Converts single Acquistabile to ElementoPacchettoDTO.
     * Uses instanceof to determine the type and map accordingly.
     */
    default ElementoPacchettoDTO acquistabileToElementoDTO(Acquistabile acquistabile) {
        if (acquistabile == null) {
            return null;
        }

        ElementoPacchettoDTO dto = new ElementoPacchettoDTO();
        dto.setIdElemento(acquistabile.getId());
        dto.setNomeElemento(acquistabile.getNome());
        dto.setDescrizioneElemento(acquistabile.getDescrizione());
        dto.setPrezzoElemento(acquistabile.getPrezzo());
        dto.setQuantita(1); // Default quantity for single element

        // Determine type based on instanceof
        if (acquistabile instanceof Prodotto) {
            dto.setTipoElemento("PRODOTTO");
        } else if (acquistabile instanceof Evento) {
            dto.setTipoElemento("EVENTO");
        } else {
            dto.setTipoElemento("UNKNOWN");
        }

        return dto;
    }

    /**
     * Converts List of ElementoPacchettoRequestDTO to determine the types for service processing.
     * This is a utility method for the service layer to understand what types of elements to load.
     */
    default List<String> extractElementTypes(List<ElementoPacchettoRequestDTO> elementiRequest) {
        if (elementiRequest == null) {
            return null;
        }
        
        return elementiRequest.stream()
                .map(ElementoPacchettoRequestDTO::getTipoElemento)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Extracts element IDs by type for service processing.
     */
    default List<Long> extractElementIdsByType(List<ElementoPacchettoRequestDTO> elementiRequest, String tipo) {
        if (elementiRequest == null) {
            return null;
        }
        
        return elementiRequest.stream()
                .filter(elemento -> tipo.equals(elemento.getTipoElemento()))
                .map(ElementoPacchettoRequestDTO::getIdElemento)
                .collect(Collectors.toList());
    }
}