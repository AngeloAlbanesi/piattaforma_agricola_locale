/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.utente;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.TipoRuolo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for public user information, used when displaying other users' profiles.
 * Excludes sensitive information like email and personal details.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPublicDTO {
    
    private Long idUtente;
    private String nome;
    private String cognome;
    private TipoRuolo tipoRuolo;
    private boolean isAttivo;
}