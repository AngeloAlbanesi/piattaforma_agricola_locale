package it.unicam.cs.ids.piattaforma_agricola_locale.dto.processo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO semplificato per la lista dei processi di trasformazione.
 * Contiene solo i campi necessari per la visualizzazione in tabella.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessoTrasformazioneSummaryDTO {
    private Long id;
    private String nome;
    private String descrizione;
    private String stato;
    private LocalDateTime dataCreazione;
    private LocalDateTime dataUltimaModifica;
    private Integer numeroFasi;
}
