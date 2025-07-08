package it.unicam.cs.ids.piattaforma_agricola_locale.dto.processo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TraceabilityDTO {
    private Long processoId;
    private String nomeProcesso;
    private List<FaseLavorazioneDTO> fasi;
    private String tracciabilita;
}

