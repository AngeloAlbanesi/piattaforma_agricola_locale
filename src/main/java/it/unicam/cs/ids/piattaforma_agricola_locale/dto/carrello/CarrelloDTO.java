/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.carrello;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.utente.UserPublicDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * DTO for Carrello entity.
 * Contains cart data and list of cart items.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarrelloDTO {
    
    private Long idCarrello;
    private UserPublicDTO acquirente;
    private List<ElementoCarrelloDTO> elementiCarrello;
    private Date ultimaModifica;

    /**
     * Calculates the total price of all items in the cart.
     * 
     * @return the total cart price
     */
    public double calcolaPrezzoTotale() {
        return elementiCarrello != null ? 
            elementiCarrello.stream().mapToDouble(ElementoCarrelloDTO::calcolaPrezzoTotale).sum() : 0.0;
    }

    /**
     * Gets the total number of items in the cart.
     * 
     * @return the total item count
     */
    public int getTotalElementi() {
        return elementiCarrello != null ? 
            elementiCarrello.stream().mapToInt(ElementoCarrelloDTO::getQuantita).sum() : 0;
    }
}