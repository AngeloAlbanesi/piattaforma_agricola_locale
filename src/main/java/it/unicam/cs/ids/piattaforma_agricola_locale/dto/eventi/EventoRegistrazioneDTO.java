package it.unicam.cs.ids.piattaforma_agricola_locale.dto.eventi;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.utente.UserPublicDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * DTO for event registration information.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventoRegistrazioneDTO {
    
    private Long id;
    private Long eventoId;
    private String nomeEvento;
    private UserPublicDTO utente;
    private Date dataRegistrazione;
    private int numeroPosti;
    private String note;
}