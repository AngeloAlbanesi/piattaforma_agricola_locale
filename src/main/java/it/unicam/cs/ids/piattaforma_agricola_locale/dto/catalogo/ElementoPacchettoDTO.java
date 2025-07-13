/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing an item included in a package.
 * This represents polymorphic Acquistabile items (Prodotto, Evento) in packages.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ElementoPacchettoDTO {
    
    private String tipoElemento; // "PRODOTTO", "EVENTO"
    private Long idElemento;
    private String nomeElemento;
    private String descrizioneElemento;
    private double prezzoElemento;
    private int quantita; // Quantita di questo elemento nel pacchetto
}