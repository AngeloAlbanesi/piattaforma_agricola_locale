package it.unicam.cs.ids.piattaforma_agricola_locale.dto.processo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pubblico per le fonti di materie prime.
 * Utilizzato nelle API pubbliche per evitare riferimenti ciclici.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FonteMateriaPrimaPublicDTO {
    private Long id;
    private String tipoFonte; // "INTERNA" o "ESTERNA"
    private String descrizioneFonte;
    private Long prodottoId;
    private String prodottoNome;
    private Double quantita;
    private String unitaMisura;
}
