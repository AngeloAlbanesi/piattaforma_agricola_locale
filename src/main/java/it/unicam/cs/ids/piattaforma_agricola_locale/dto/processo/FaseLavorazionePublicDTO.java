package it.unicam.cs.ids.piattaforma_agricola_locale.dto.processo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO pubblico per le fasi di lavorazione.
 * Utilizzato nelle API pubbliche per evitare riferimenti ciclici.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaseLavorazionePublicDTO {
    private Long id;
    private String nome;
    private Integer numeroFase; // alias per ordineEsecuzione
    private String descrizione;
    private List<FonteMateriaPrimaPublicDTO> fontiMateriePrime;
}
