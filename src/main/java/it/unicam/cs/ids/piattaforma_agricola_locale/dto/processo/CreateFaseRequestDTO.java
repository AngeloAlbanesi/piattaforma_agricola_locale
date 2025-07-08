package it.unicam.cs.ids.piattaforma_agricola_locale.dto.processo;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.FonteMateriaPrima;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateFaseRequestDTO {
    private String nome;
    private String descrizione;
    private int ordineEsecuzione;
    private String materiaPrimaUtilizzata;
    private FonteMateriaPrima fonte;
}
