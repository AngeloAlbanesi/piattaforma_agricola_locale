/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.service.mapper;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.utente.UserDetailDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.utente.UserPublicDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.utente.UserUpdateDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Utente;

/**
 * Mapper utility class for converting between Utente entities and User DTOs.
 * Provides static methods for mapping between different user representations.
 */
public class UtenteMapper {

    private UtenteMapper() {
        // Utility class, prevent instantiation
    }

    /**
     * Converts a Utente entity to UserDetailDTO.
     * 
     * @param utente the entity to convert
     * @return the UserDetailDTO representation
     */
    public static UserDetailDTO toDetailDTO(Utente utente) {
        if (utente == null) {
            return null;
        }

        return new UserDetailDTO(
            utente.getIdUtente(),
            utente.getNome(),
            utente.getCognome(),
            utente.getEmail(),
            utente.getNumeroTelefono(),
            utente.getTipoRuolo(),
            utente.isAttivo()
        );
    }

    /**
     * Converts a Utente entity to UserPublicDTO.
     * Excludes sensitive information like email and phone number.
     * 
     * @param utente the entity to convert
     * @return the UserPublicDTO representation
     */
    public static UserPublicDTO toPublicDTO(Utente utente) {
        if (utente == null) {
            return null;
        }

        return new UserPublicDTO(
            utente.getIdUtente(),
            utente.getNome(),
            utente.getCognome(),
            utente.getTipoRuolo(),
            utente.isAttivo()
        );
    }

    /**
     * Updates a Utente entity with data from UserUpdateDTO.
     * Does not modify ID, password, role, or status - only basic profile info.
     * 
     * @param utente the entity to update
     * @param updateDTO the DTO containing new data
     */
    public static void updateFromDTO(Utente utente, UserUpdateDTO updateDTO) {
        if (utente == null || updateDTO == null) {
            return;
        }

        utente.setNome(updateDTO.getNome());
        utente.setCognome(updateDTO.getCognome());
        utente.setEmail(updateDTO.getEmail());
        utente.setNumeroTelefono(updateDTO.getNumeroTelefono());
    }
}