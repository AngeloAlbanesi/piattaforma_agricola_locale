/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.utente.UserPublicDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.TipoOrigineProdotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.StatoVerificaValori;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for detailed product information, used in single product view.
 * Contains all product fields plus related entities like vendor and certifications.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailDTO {
    
    private Long idProdotto;
    private String nome;
    private String descrizione;
    private double prezzo;
    private int quantitaDisponibile;
    private StatoVerificaValori statoVerifica;
    private String feedbackVerifica;
    private TipoOrigineProdotto tipoOrigine;
    private Long idProcessoTrasformazioneOriginario;
    private Long idMetodoDiColtivazione;
    private UserPublicDTO venditore;
    private List<CertificazioneDTO> certificazioni;
}