/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.carrello;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.utente.UserPublicDTO;
import java.util.Date;
import java.util.List;

/**
 * DTO for Carrello entity.
 * Contains cart data and list of cart items.
 */
public class CarrelloDTO {
    
    private Long idCarrello;
    private UserPublicDTO acquirente;
    private List<ElementoCarrelloDTO> elementiCarrello;
    private Date ultimaModifica;

    public CarrelloDTO() {
    }

    public CarrelloDTO(Long idCarrello, UserPublicDTO acquirente, List<ElementoCarrelloDTO> elementiCarrello, 
                      Date ultimaModifica) {
        this.idCarrello = idCarrello;
        this.acquirente = acquirente;
        this.elementiCarrello = elementiCarrello;
        this.ultimaModifica = ultimaModifica;
    }

    public Long getIdCarrello() {
        return idCarrello;
    }

    public void setIdCarrello(Long idCarrello) {
        this.idCarrello = idCarrello;
    }

    public UserPublicDTO getAcquirente() {
        return acquirente;
    }

    public void setAcquirente(UserPublicDTO acquirente) {
        this.acquirente = acquirente;
    }

    public List<ElementoCarrelloDTO> getElementiCarrello() {
        return elementiCarrello;
    }

    public void setElementiCarrello(List<ElementoCarrelloDTO> elementiCarrello) {
        this.elementiCarrello = elementiCarrello;
    }

    public Date getUltimaModifica() {
        return ultimaModifica;
    }

    public void setUltimaModifica(Date ultimaModifica) {
        this.ultimaModifica = ultimaModifica;
    }

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