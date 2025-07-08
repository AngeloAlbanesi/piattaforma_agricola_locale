package it.unicam.cs.ids.piattaforma_agricola_locale.dto.eventi;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for event registration requests.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventoRegistrazioneRequestDTO {
    
    @NotNull(message = "Il numero di posti è obbligatorio")
    @Min(value = 1, message = "Il numero di posti deve essere almeno 1")
    private Integer numeroPosti;
    
    @Size(max = 500, message = "Le note non possono superare i 500 caratteri")
    private String note;
}