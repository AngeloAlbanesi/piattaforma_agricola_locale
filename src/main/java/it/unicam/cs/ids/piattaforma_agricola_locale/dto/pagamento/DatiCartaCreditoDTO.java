package it.unicam.cs.ids.piattaforma_agricola_locale.dto.pagamento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per i dati della carta di credito
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DatiCartaCreditoDTO {
    
    @NotBlank(message = "Il numero della carta è obbligatorio")
    @Pattern(regexp = "^[0-9]{13,19}$", message = "Il numero della carta deve contenere tra 13 e 19 cifre")
    private String numeroCartaCredito;
    
    @NotBlank(message = "L'intestatario della carta è obbligatorio")
    @Size(min = 2, max = 100, message = "L'intestatario deve essere tra 2 e 100 caratteri")
    private String intestatario;
    
    @NotBlank(message = "La data di scadenza è obbligatoria")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/([0-9]{2})$", message = "La data di scadenza deve essere nel formato MM/YY")
    private String dataScadenza;
    
    @NotBlank(message = "Il CVV è obbligatorio")
    @Pattern(regexp = "^[0-9]{3,4}$", message = "Il CVV deve contenere 3 o 4 cifre")
    private String cvv;
}