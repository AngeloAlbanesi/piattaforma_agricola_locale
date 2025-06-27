/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.utente;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.TipoRuolo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for detailed user information, typically used when a user views their own profile.
 * Includes all user data except sensitive information like password hash.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailDTO {
    
    private Long idUtente;
    
    @NotBlank(message = "Il nome è obbligatorio")
    @Size(max = 100, message = "Il nome non può superare i 100 caratteri")
    private String nome;
    
    @NotBlank(message = "Il cognome è obbligatorio")
    @Size(max = 100, message = "Il cognome non può superare i 100 caratteri")
    private String cognome;
    
    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "L'email deve essere valida")
    @Size(max = 255, message = "L'email non può superare i 255 caratteri")
    private String email;
    
    @Size(max = 20, message = "Il numero di telefono non può superare i 20 caratteri")
    private String numeroTelefono;
    
    @NotNull(message = "Il tipo di ruolo è obbligatorio")
    private TipoRuolo tipoRuolo;
    
    private boolean isAttivo;
}