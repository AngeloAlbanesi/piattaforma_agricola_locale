/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.service.mapper;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.utente.UserDetailDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.utente.UserPublicDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.utente.UserUpdateDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Utente;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

/**
 * MapStruct mapper for converting between Utente entities and User DTOs.
 * Provides mapping between different user representations with security exclusions.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
@Component
public interface UtenteMapper {

    /**
     * Converts a Utente entity to UserDetailDTO.
     * Includes all user information except password for detailed views.
     */
    @Mapping(target = "idUtente", source = "idUtente")
    @Mapping(target = "nome", source = "nome")
    @Mapping(target = "cognome", source = "cognome")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "numeroTelefono", source = "numeroTelefono")
    @Mapping(target = "tipoRuolo", source = "tipoRuolo")
    @Mapping(target = "isAttivo", source = "attivo")
    UserDetailDTO toDetailDTO(Utente utente);

    /**
     * Converts a Utente entity to UserPublicDTO.
     * Excludes sensitive information like email and phone number for public views.
     */
    @Mapping(target = "idUtente", source = "idUtente")
    @Mapping(target = "nome", source = "nome")
    @Mapping(target = "cognome", source = "cognome")
    @Mapping(target = "tipoRuolo", source = "tipoRuolo")
    @Mapping(target = "isAttivo", source = "attivo")
    UserPublicDTO toPublicDTO(Utente utente);

    /**
     * Updates a Utente entity with data from UserUpdateDTO.
     * Does not modify ID, password, role, or status - only basic profile info.
     */
    @Mapping(target = "idUtente", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nome", source = "nome")
    @Mapping(target = "cognome", source = "cognome")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "numeroTelefono", source = "numeroTelefono")
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "tipoRuolo", ignore = true)
    @Mapping(target = "attivo", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    void updateFromDTO(UserUpdateDTO updateDTO, @MappingTarget Utente utente);

}