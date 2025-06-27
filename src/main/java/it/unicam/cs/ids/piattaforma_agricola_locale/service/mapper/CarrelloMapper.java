/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.service.mapper;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.carrello.CarrelloDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.carrello.ElementoCarrelloDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.carrello.Carrello;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.carrello.ElementoCarrello;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.Acquistabile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper utility class for converting between Carrello entities and Cart DTOs.
 * Provides static methods for mapping between different cart representations.
 */
public class CarrelloMapper {

    private CarrelloMapper() {
        // Utility class, prevent instantiation
    }

    /**
     * Converts a Carrello entity to CarrelloDTO.
     * 
     * @param carrello the entity to convert
     * @return the CarrelloDTO representation
     */
    public static CarrelloDTO toDTO(Carrello carrello) {
        if (carrello == null) {
            return null;
        }

        List<ElementoCarrelloDTO> elementiDTO = carrello.getElementiCarrello()
            .stream()
            .map(CarrelloMapper::toElementoDTO)
            .collect(Collectors.toList());

        return new CarrelloDTO(
            carrello.getIdCarrello(),
            UtenteMapper.toPublicDTO(carrello.getAcquirente()),
            elementiDTO,
            carrello.getUltimaModifica()
        );
    }

    /**
     * Converts an ElementoCarrello entity to ElementoCarrelloDTO.
     * 
     * @param elemento the entity to convert
     * @return the ElementoCarrelloDTO representation
     */
    public static ElementoCarrelloDTO toElementoDTO(ElementoCarrello elemento) {
        if (elemento == null) {
            return null;
        }

        Acquistabile acquistabile = elemento.getAcquistabile();
        String tipoAcquistabile = determineTipoAcquistabile(acquistabile);

        return new ElementoCarrelloDTO(
            elemento.getIdElemento(),
            elemento.getQuantita(),
            elemento.getPrezzoUnitario(),
            tipoAcquistabile,
            acquistabile != null ? acquistabile.getId() : null,
            acquistabile != null ? acquistabile.getNome() : null,
            acquistabile != null ? acquistabile.getDescrizione() : null
        );
    }

    /**
     * Determines the type of Acquistabile for DTO representation.
     * 
     * @param acquistabile the Acquistabile object
     * @return string representation of the type
     */
    private static String determineTipoAcquistabile(Acquistabile acquistabile) {
        if (acquistabile == null) {
            return null;
        }

        String className = acquistabile.getClass().getSimpleName();
        switch (className) {
            case "Prodotto":
                return "PRODOTTO";
            case "Pacchetto":
                return "PACCHETTO";
            case "Evento":
                return "EVENTO";
            default:
                return "UNKNOWN";
        }
    }
}