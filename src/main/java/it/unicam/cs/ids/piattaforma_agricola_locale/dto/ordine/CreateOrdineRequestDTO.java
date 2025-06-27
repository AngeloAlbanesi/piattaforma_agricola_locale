/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.ordine;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for order creation requests.
 * Used when creating an order from the shopping cart.
 */
public class CreateOrdineRequestDTO {
    
    @NotNull(message = "L'ID del carrello è obbligatorio")
    private Long idCarrello;
    
    @NotBlank(message = "Il metodo di pagamento è obbligatorio")
    private String metodoPagamento; // "CARTA_CREDITO", "PAYPAL", "SIMULATO"
    
    private String noteAggiuntive;

    public CreateOrdineRequestDTO() {
    }

    public CreateOrdineRequestDTO(Long idCarrello, String metodoPagamento, String noteAggiuntive) {
        this.idCarrello = idCarrello;
        this.metodoPagamento = metodoPagamento;
        this.noteAggiuntive = noteAggiuntive;
    }

    public Long getIdCarrello() {
        return idCarrello;
    }

    public void setIdCarrello(Long idCarrello) {
        this.idCarrello = idCarrello;
    }

    public String getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(String metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public String getNoteAggiuntive() {
        return noteAggiuntive;
    }

    public void setNoteAggiuntive(String noteAggiuntive) {
        this.noteAggiuntive = noteAggiuntive;
    }
}