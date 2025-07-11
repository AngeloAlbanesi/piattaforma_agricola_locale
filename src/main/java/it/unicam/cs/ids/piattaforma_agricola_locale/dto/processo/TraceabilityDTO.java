package it.unicam.cs.ids.piattaforma_agricola_locale.dto.processo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TraceabilityDTO {
    private Long processoId;
    private String nomeProcesso;
    private String descrizioneProcesso;
    private String metodoProduzione;
    private String nomeTrasformatore;
    private String aziendaTrasformatore;
    private List<FaseLavorazioneDTO> fasi;
    private String tracciabilita;
    private String prodottoFinale;
}

