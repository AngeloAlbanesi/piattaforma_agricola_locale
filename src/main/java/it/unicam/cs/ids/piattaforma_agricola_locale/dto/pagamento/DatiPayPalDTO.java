package it.unicam.cs.ids.piattaforma_agricola_locale.dto.pagamento;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per i dati di pagamento PayPal
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DatiPayPalDTO {
    
    @NotBlank(message = "L'email PayPal è obbligatoria")
    @Email(message = "L'email PayPal deve essere valida")
    private String emailPayPal;
    
    @NotBlank(message = "La password PayPal è obbligatoria")
    private String passwordPayPal;
}