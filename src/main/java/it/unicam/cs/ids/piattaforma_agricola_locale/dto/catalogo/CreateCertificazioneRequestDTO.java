/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * DTO specifico per la richiesta di creazione di una certificazione per un prodotto.
 * Utilizzato nel caso d'uso "Venditore carica certificazioni prodotto".
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateCertificazioneRequestDTO {

    @NotBlank(message = "Il nome della certificazione è obbligatorio")
    @Size(max = 255, message = "Il nome della certificazione non può superare i 255 caratteri")
    private String nomeCertificazione;

    @NotBlank(message = "L'ente di rilascio è obbligatorio")
    @Size(max = 255, message = "L'ente di rilascio non può superare i 255 caratteri")
    private String enteRilascio;

    @NotNull(message = "La data di rilascio è obbligatoria")
    private Date dataRilascio;

    private Date dataScadenza;
}