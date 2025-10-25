package it.unicam.cs.ids.piattaforma_agricola_locale.dto.processo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateFaseRequestDTO {
    private String nome;
    private String descrizione;
    private int ordineEsecuzione;
    private String materiaPrimaUtilizzata;
    private FonteRequestDTO fonte;

    @Getter
    @Setter
    public static class FonteRequestDTO {
        private String tipo; // "ESTERNA" o "INTERNA"
        private String nomeFornitore; // Solo per tipo ESTERNA
        private Long produttoreId; // Solo per tipo INTERNA
    }
}
