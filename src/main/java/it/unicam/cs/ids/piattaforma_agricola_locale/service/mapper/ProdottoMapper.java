/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.service.mapper;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo.CertificazioneDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo.CreateProductRequestDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo.ProductDetailDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo.ProductSummaryDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.utente.UserPublicDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper utility class for converting between Prodotto entities and Product DTOs.
 * Provides static methods for mapping between different product representations.
 */
public class ProdottoMapper {

    private ProdottoMapper() {
        // Utility class, prevent instantiation
    }

    /**
     * Converts a Prodotto entity to ProductSummaryDTO.
     * 
     * @param prodotto the entity to convert
     * @return the ProductSummaryDTO representation
     */
    public static ProductSummaryDTO toSummaryDTO(Prodotto prodotto) {
        if (prodotto == null) {
            return null;
        }

        Venditore venditore = prodotto.getVenditore();
        return new ProductSummaryDTO(
            prodotto.getId(),
            prodotto.getNome(),
            prodotto.getPrezzo(),
            prodotto.getQuantitaDisponibile(),
            prodotto.getStatoVerifica(),
            prodotto.getTipoOrigine(),
            venditore != null ? venditore.getNome() + " " + venditore.getCognome() : null,
            venditore != null ? venditore.getIdUtente() : null
        );
    }

    /**
     * Converts a Prodotto entity to ProductDetailDTO.
     * 
     * @param prodotto the entity to convert
     * @return the ProductDetailDTO representation
     */
    public static ProductDetailDTO toDetailDTO(Prodotto prodotto) {
        if (prodotto == null) {
            return null;
        }

        UserPublicDTO venditoreDTO = UtenteMapper.toPublicDTO(prodotto.getVenditore());
        List<CertificazioneDTO> certificazioniDTO = prodotto.getCertificazioni()
            .stream()
            .map(CertificazioneMapper::toDTO)
            .collect(Collectors.toList());

        return new ProductDetailDTO(
            prodotto.getId(),
            prodotto.getNome(),
            prodotto.getDescrizione(),
            prodotto.getPrezzo(),
            prodotto.getQuantitaDisponibile(),
            prodotto.getStatoVerifica(),
            prodotto.getFeedbackVerifica(),
            prodotto.getTipoOrigine(),
            prodotto.getIdProcessoTrasformazioneOriginario(),
            prodotto.getIdMetodoDiColtivazione(),
            venditoreDTO,
            certificazioniDTO
        );
    }

    /**
     * Creates a new Prodotto entity from CreateProductRequestDTO.
     * Note: The venditore must be set separately after creation.
     * 
     * @param createDTO the DTO containing product data
     * @return a new Prodotto entity
     */
    public static Prodotto fromCreateDTO(CreateProductRequestDTO createDTO) {
        if (createDTO == null) {
            return null;
        }

        // Note: Venditore must be set separately as it requires database lookup
        Prodotto prodotto = new Prodotto(
            createDTO.getNome(),
            createDTO.getDescrizione(),
            createDTO.getPrezzo(),
            createDTO.getQuantitaDisponibile(),
            null // Venditore to be set by service
        );

        prodotto.setTipoOrigine(createDTO.getTipoOrigine());
        prodotto.setIdProcessoTrasformazioneOriginario(createDTO.getIdProcessoTrasformazioneOriginario());
        prodotto.setIdMetodoDiColtivazione(createDTO.getIdMetodoDiColtivazione());

        return prodotto;
    }
}