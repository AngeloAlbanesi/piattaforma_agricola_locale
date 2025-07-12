/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.coltivazione.MetodoDiColtivazioneDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.TipoOrigineProdotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.StatoVerificaValori;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for product summary information, used in product lists.
 * Contains only essential fields for displaying products in a catalog view.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSummaryDTO {
    
    private Long idProdotto;
    private String nome;
    private double prezzo;
    private int quantitaDisponibile;
    private StatoVerificaValori statoVerifica;
    private TipoOrigineProdotto tipoOrigine;
    private MetodoDiColtivazioneDTO metodoDiColtivazione;
    private String nomeVenditore;
    private Long idVenditore;
}