package it.unicam.cs.ids.piattaforma_agricola_locale.dto.collegamento;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO per gestire il collegamento bidirezionale tra processo di trasformazione e prodotto.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CollegamentoProcessoProdottoDTO {
    
    @NotNull(message = "L'ID del processo di trasformazione è obbligatorio")
    private Long processoId;
    
    @NotNull(message = "L'ID del prodotto è obbligatorio")
    private Long prodottoId;
    
    /**
     * Indica se rimuovere il collegamento esistente invece di crearlo.
     * Default: false (crea collegamento)
     */
    private boolean rimuoviCollegamento = false;
}