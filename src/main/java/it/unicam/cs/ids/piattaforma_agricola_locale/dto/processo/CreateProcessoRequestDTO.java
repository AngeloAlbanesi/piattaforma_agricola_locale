package it.unicam.cs.ids.piattaforma_agricola_locale.dto.processo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProcessoRequestDTO {
    private String nome;
    private String descrizione;
    private String metodoProduzione;
}
