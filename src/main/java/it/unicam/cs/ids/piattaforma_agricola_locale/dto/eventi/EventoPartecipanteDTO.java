package it.unicam.cs.ids.piattaforma_agricola_locale.dto.eventi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * DTO for event participant information.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventoPartecipanteDTO {
    
    private Long utenteId;
    private String nome;
    private String cognome;
    private String email;
    private Date dataRegistrazione;
    private int numeroPosti;
}