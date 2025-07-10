/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.service.mapper;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.carrello.CarrelloDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.carrello.ElementoCarrelloDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.utente.UserPublicDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.carrello.Carrello;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.carrello.ElementoCarrello;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Pacchetto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.Acquistabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.Evento;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * MapStruct mapper for converting between Carrello entities and Cart DTOs.
 * Handles complex polymorphic relationships for cart items.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    uses = {UtenteMapper.class}
)
@Component
public interface CarrelloMapper {

    /**
     * Converts a Carrello entity to CarrelloDTO.
     * Includes buyer information and all cart items.
     */
    @Mapping(target = "idCarrello", source = "idCarrello")
    @Mapping(target = "acquirente", source = "acquirente", qualifiedByName = "acquirenteToUserPublicDTO")
    @Mapping(target = "elementiCarrello", source = "elementiCarrello", qualifiedByName = "elementiCarrelloToDTO")
    @Mapping(target = "ultimaModifica", source = "ultimaModifica")
    CarrelloDTO toDTO(Carrello carrello);

    /**
     * Converts Acquirente to UserPublicDTO.
     */
    @Named("acquirenteToUserPublicDTO")
    @Mapping(target = "idUtente", source = "idUtente")
    @Mapping(target = "nome", source = "nome")
    @Mapping(target = "cognome", source = "cognome")
    @Mapping(target = "tipoRuolo", source = "tipoRuolo")
    @Mapping(target = "isAttivo", source = "attivo")
    UserPublicDTO acquirenteToUserPublicDTO(Acquirente acquirente);

    /**
     * Converts List of ElementoCarrello to List of ElementoCarrelloDTO.
     * Handles polymorphic conversion for different Acquistabile implementations.
     */
    @Named("elementiCarrelloToDTO")
    default List<ElementoCarrelloDTO> elementiCarrelloToDTO(List<ElementoCarrello> elementiCarrello) {
        if (elementiCarrello == null) {
            return null;
        }
        
        return elementiCarrello.stream()
                .map(this::elementoCarrelloToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Converts single ElementoCarrello to ElementoCarrelloDTO.
     * Uses instanceof to determine the type and map accordingly.
     */
    default ElementoCarrelloDTO elementoCarrelloToDTO(ElementoCarrello elemento) {
        if (elemento == null) {
            return null;
        }

        ElementoCarrelloDTO dto = new ElementoCarrelloDTO();
        dto.setIdElemento(elemento.getIdElemento());
        dto.setQuantita(elemento.getQuantita());
        dto.setPrezzoUnitario(elemento.getPrezzoUnitario());

        // Get the Acquistabile and determine its type
        Acquistabile acquistabile = elemento.getAcquistabile();
        if (acquistabile != null) {
            dto.setIdAcquistabile(acquistabile.getId());
            dto.setNomeAcquistabile(acquistabile.getNome());
            dto.setDescrizioneAcquistabile(acquistabile.getDescrizione());
            
            // Set seller name
            if (acquistabile.getVenditore() != null && 
                acquistabile.getVenditore().getDatiAzienda() != null) {
                dto.setNomeVenditore(acquistabile.getVenditore().getDatiAzienda().getNomeAzienda());
            }

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
     * Creates a new ElementoCarrello entity from ElementoCarrelloDTO.
     * Note: the Acquistabile object must be set separately by the service.
     */
    @Mapping(target = "idElemento", source = "idElemento")
    @Mapping(target = "quantita", source = "quantita")
    @Mapping(target = "prezzoUnitario", source = "prezzoUnitario")
    @Mapping(target = "carrello", ignore = true)
    @Mapping(target = "acquistabile", ignore = true)
    ElementoCarrello elementoCarrelloDTOToEntity(ElementoCarrelloDTO elementoCarrelloDTO);

    /**
     * Updates an existing ElementoCarrello entity with data from DTO.
     */
    @Mapping(target = "idElemento", ignore = true)
    @Mapping(target = "quantita", source = "quantita")
    @Mapping(target = "prezzoUnitario", source = "prezzoUnitario")
    @Mapping(target = "carrello", ignore = true)
    @Mapping(target = "acquistabile", ignore = true)
    void updateElementoCarrelloFromDTO(ElementoCarrelloDTO elementoCarrelloDTO, @MappingTarget ElementoCarrello elementoCarrello);

    /**
     * Converts CarrelloDTO back to Carrello entity.
     * Note: acquirente and elementiCarrello must be handled separately by the service.
     */
    @Mapping(target = "idCarrello", source = "idCarrello")
    @Mapping(target = "ultimaModifica", source = "ultimaModifica")
    @Mapping(target = "acquirente", ignore = true)
    @Mapping(target = "elementiCarrello", ignore = true)
    Carrello fromDTO(CarrelloDTO carrelloDTO);
}