/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.admin;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for moderation decision requests (approve/reject).
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModerationDecisionDTO {

    @Size(max = 500, message = "La motivazione non può superare i 500 caratteri")
    private String motivazione;

    private String note;
}