/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.service.mapper;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.ordine.CreateOrdineRequestDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.ordine.OrdineDetailDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.ordine.OrdineSummaryDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.ordine.RigaOrdineDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.utente.UserPublicDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Pacchetto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.Acquistabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.Evento;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.RigaOrdine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * MapStruct mapper for converting between Ordine entities and Order DTOs.
 * Handles complex relationships and polymorphic order line items.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
@Component
public interface OrdineMapper {

    /**
     * Converts Ordine entity to OrdineDetailDTO.
     * Includes complete order information with line items and buyer details.
     */
    @Mapping(target = "idOrdine", source = "idOrdine")
    @Mapping(target = "dataOrdine", source = "dataOrdine")
    @Mapping(target = "importoTotale", source = "importoTotale")
    @Mapping(target = "statoCorrente", source = "statoOrdine")
    @Mapping(target = "acquirente", source = "acquirente", qualifiedByName = "acquirenteToUserPublicDTO")
    @Mapping(target = "righeOrdine", source = "righeOrdine", qualifiedByName = "righeOrdineToDTO")
    OrdineDetailDTO toDetailDTO(Ordine ordine);

    /**
     * Converts Ordine entity to OrdineSummaryDTO.
     * Contains only essential fields for listing views.
     */
    @Mapping(target = "idOrdine", source = "idOrdine")
    @Mapping(target = "dataOrdine", source = "dataOrdine")
    @Mapping(target = "importoTotale", source = "importoTotale")
    @Mapping(target = "statoCorrente", source = "statoOrdine")
    @Mapping(target = "nomeAcquirente", expression = "java(ordine.getAcquirente().getNome() + \" \" + ordine.getAcquirente().getCognome())")
    @Mapping(target = "idAcquirente", source = "acquirente.idUtente")
    @Mapping(target = "numeroArticoli", expression = "java(ordine.getRigheOrdine() != null ? ordine.getRigheOrdine().size() : 0)")
    OrdineSummaryDTO toSummaryDTO(Ordine ordine);

    /**
     * Creates a new Ordine entity from CreateOrdineRequestDTO.
     * Note: All entity configuration must be handled by the service.
     */
    default Ordine fromCreateRequestDTO(CreateOrdineRequestDTO createOrdineRequestDTO) {
        // Delegate to service for proper entity creation
        throw new UnsupportedOperationException("Use service layer to create Ordine entities");
    }

    /**
     * Updates an existing Ordine entity with data from CreateOrdineRequestDTO.
     * Note: Entity updates must be handled by the service.
     */
    default void updateFromCreateRequestDTO(CreateOrdineRequestDTO createOrdineRequestDTO, Ordine ordine) {
        // Delegate to service for proper entity updates
        throw new UnsupportedOperationException("Use service layer to update Ordine entities");
    }

    /**
     * Converts Acquirente to UserPublicDTO.
     */
    @Named("acquirenteToUserPublicDTO")
    @Mapping(target = "idUtente", source = "idUtente")
    @Mapping(target = "nome", source = "nome")
    @Mapping(target = "cognome", source = "cognome")
    @Mapping(target = "tipoRuolo", source = "tipoRuolo")
    @Mapping(target = "attivo", source = "attivo")
    UserPublicDTO acquirenteToUserPublicDTO(Acquirente acquirente);

    /**
     * Converts List of RigaOrdine to List of RigaOrdineDTO.
     */
    @Named("righeOrdineToDTO")
    default List<RigaOrdineDTO> righeOrdineToDTO(List<RigaOrdine> righeOrdine) {
        if (righeOrdine == null) {
            return null;
        }
        
        return righeOrdine.stream()
                .map(this::rigaOrdineToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Converts single RigaOrdine to RigaOrdineDTO.
     * Handles polymorphic Acquistabile items.
     */
    default RigaOrdineDTO rigaOrdineToDTO(RigaOrdine rigaOrdine) {
        if (rigaOrdine == null) {
            return null;
        }

        RigaOrdineDTO dto = new RigaOrdineDTO();
        dto.setIdRiga(rigaOrdine.getIdRiga());
        dto.setQuantitaOrdinata(rigaOrdine.getQuantitaOrdinata());
        dto.setPrezzoUnitario(rigaOrdine.getPrezzoUnitario());

        // Get the Acquistabile and determine its type
        Acquistabile acquistabile = rigaOrdine.getAcquistabile();
        if (acquistabile != null) {
            dto.setIdAcquistabile(acquistabile.getId());
            dto.setNomeAcquistabile(acquistabile.getNome());
            dto.setDescrizioneAcquistabile(acquistabile.getDescrizione());

            // Determine type based on instanceof
            if (acquistabile instanceof Prodotto) {
                dto.setTipoAcquistabile("PRODOTTO");
            } else if (acquistabile instanceof Pacchetto) {
                dto.setTipoAcquistabile("PACCHETTO");
            } else if (acquistabile instanceof Evento) {
                dto.setTipoAcquistabile("EVENTO");
            } else {
                dto.setTipoAcquistabile("UNKNOWN");
            }
        }

        return dto;
    }

    /**
     * Converts RigaOrdineDTO back to RigaOrdine.
     * Note: Entity creation must be handled by the service.
     */
    default RigaOrdine rigaOrdineDTOToEntity(RigaOrdineDTO rigaOrdineDTO) {
        // Delegate to service for proper entity creation
        throw new UnsupportedOperationException("Use service layer to create RigaOrdine entities");
    }

    /**
     * Updates an existing RigaOrdine entity with data from RigaOrdineDTO.
     * Note: Entity updates must be handled by the service.
     */
    default void updateRigaOrdineFromDTO(RigaOrdineDTO rigaOrdineDTO, RigaOrdine rigaOrdine) {
        // Delegate to service for proper entity updates
        throw new UnsupportedOperationException("Use service layer to update RigaOrdine entities");
    }
}