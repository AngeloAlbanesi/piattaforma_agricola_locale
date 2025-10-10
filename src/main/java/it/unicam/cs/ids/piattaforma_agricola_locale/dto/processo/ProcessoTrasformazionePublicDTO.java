package it.unicam.cs.ids.piattaforma_agricola_locale.dto.processo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO pubblico per i processi di trasformazione.
 * Utilizzato nelle API pubbliche per evitare riferimenti ciclici.
 * Corrisponde alla struttura attesa dal frontend.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessoTrasformazionePublicDTO {
    private Long idProcesso;
    private String nomeProcesso;
    private String descrizioneProcesso;
    private String metodoProduzione;
    private String dataCreazione;
    private List<FaseLavorazionePublicDTO> fasiLavorazione;
}
