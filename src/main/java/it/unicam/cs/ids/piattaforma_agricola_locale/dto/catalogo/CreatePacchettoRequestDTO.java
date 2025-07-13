/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for package creation requests.
 * Contains validation annotations for input validation.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePacchettoRequestDTO {

    @NotBlank(message = "Il nome del pacchetto è obbligatorio")
    @Size(max = 200, message = "Il nome del pacchetto non può superare i 200 caratteri")
    private String nome;

    @Size(max = 1000, message = "La descrizione non può superare i 1000 caratteri")
    private String descrizione;

    @NotNull(message = "Il prezzo del pacchetto è obbligatorio")
    @DecimalMin(value = "0.01", message = "Il prezzo deve essere maggiore di zero")
    @Digits(integer = 8, fraction = 2, message = "Formato prezzo non valido")
    private Double prezzoPacchetto;

    @NotNull(message = "La quantità disponibile è obbligatoria")
    @Min(value = 0, message = "La quantità deve essere non negativa")
    private Integer quantitaDisponibile;

    @NotEmpty(message = "Il pacchetto deve contenere almeno un elemento")
    @Size(max = 20, message = "Un pacchetto non può contenere più di 20 elementi")
    private List<ElementoPacchettoRequestDTO> elementiInclusi;

    /**
     * Restituisce il prezzo totale del pacchetto (alias per prezzoPacchetto).
     */
    public Double getPrezzoTotale() {
        return prezzoPacchetto;
    }

    /**
     * Imposta il prezzo totale del pacchetto (alias per prezzoPacchetto).
     */
    public void setPrezzoTotale(Double prezzoTotale) {
        this.prezzoPacchetto = prezzoTotale;
    }
}