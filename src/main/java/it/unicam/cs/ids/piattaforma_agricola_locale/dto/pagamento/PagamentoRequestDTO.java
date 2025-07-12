package it.unicam.cs.ids.piattaforma_agricola_locale.dto.pagamento;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per la richiesta di pagamento con dati specifici per metodo
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagamentoRequestDTO {
    
    @NotBlank(message = "Il metodo di pagamento è obbligatorio")
    private String metodoPagamento; // "CARTA_CREDITO", "PAYPAL", "SIMULATO"
    
    @Valid
    private DatiCartaCreditoDTO datiCartaCredito;
    
    @Valid
    private DatiPayPalDTO datiPayPal;
    
    // Per il pagamento simulato non servono dati aggiuntivi
}