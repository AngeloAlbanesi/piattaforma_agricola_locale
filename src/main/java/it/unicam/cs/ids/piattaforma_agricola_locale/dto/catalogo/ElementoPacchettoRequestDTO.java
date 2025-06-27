/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo;

import jakarta.validation.constraints.*;

/**
 * DTO for specifying elements to include in a package.
 * Used in package creation requests to identify items to add.
 */
public class ElementoPacchettoRequestDTO {
    
    @NotBlank(message = "Il tipo di elemento è obbligatorio")
    private String tipoElemento; // "PRODOTTO", "EVENTO"
    
    @NotNull(message = "L'ID dell'elemento è obbligatorio")
    private Long idElemento;

    public ElementoPacchettoRequestDTO() {
    }

    public ElementoPacchettoRequestDTO(String tipoElemento, Long idElemento) {
        this.tipoElemento = tipoElemento;
        this.idElemento = idElemento;
    }

    public String getTipoElemento() {
        return tipoElemento;
    }

    public void setTipoElemento(String tipoElemento) {
        this.tipoElemento = tipoElemento;
    }

    public Long getIdElemento() {
        return idElemento;
    }

    public void setIdElemento(Long idElemento) {
        this.idElemento = idElemento;
    }
}