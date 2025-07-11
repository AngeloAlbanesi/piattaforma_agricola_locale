/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.service.mapper;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo.CertificazioneDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo.CreateProductRequestDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo.ProductDetailDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo.ProductSummaryDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.coltivazione.MetodoDiColtivazioneDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.utente.UserPublicDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.coltivazione.MetodoDiColtivazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IMetodoDiColtivazioneRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * MapStruct mapper for converting between Prodotto entities and Product DTOs.
 * Handles complex relationships with vendors and certifications.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    uses = {UtenteMapper.class, CertificazioneMapper.class, MetodoDiColtivazioneMapper.class}
)
@Component
public abstract class ProdottoMapper {

    @Autowired
    protected IMetodoDiColtivazioneRepository metodoDiColtivazioneRepository;
    
    @Autowired
    protected MetodoDiColtivazioneMapper metodoDiColtivazioneMapper;

    /**
     * Converts a Prodotto entity to ProductSummaryDTO.
     * Contains only essential fields for listing views.
     */
    @Mapping(target = "idProdotto", source = "id")
    @Mapping(target = "nome", source = "nome")
    @Mapping(target = "prezzo", source = "prezzo")
    @Mapping(target = "quantitaDisponibile", source = "quantitaDisponibile")
    @Mapping(target = "statoVerifica", source = "statoVerifica")
    @Mapping(target = "tipoOrigine", source = "tipoOrigine")
    @Mapping(target = "metodoDiColtivazione", expression = "java(mapMetodoDiColtivazione(prodotto.getIdMetodoDiColtivazione()))")
    @Mapping(target = "nomeVenditore", expression = "java(prodotto.getVenditore() != null ? prodotto.getVenditore().getNome() + \" \" + prodotto.getVenditore().getCognome() : null)")
    @Mapping(target = "idVenditore", source = "venditore.idUtente")
    public abstract ProductSummaryDTO toSummaryDTO(Prodotto prodotto);

    /**
     * Converts a Prodotto entity to ProductDetailDTO.
     * Includes complete product information with vendor and certifications.
     */
    @Mapping(target = "idProdotto", source = "id")
    @Mapping(target = "nome", source = "nome")
    @Mapping(target = "descrizione", source = "descrizione")
    @Mapping(target = "prezzo", source = "prezzo")
    @Mapping(target = "quantitaDisponibile", source = "quantitaDisponibile")
    @Mapping(target = "statoVerifica", source = "statoVerifica")
    @Mapping(target = "feedbackVerifica", source = "feedbackVerifica")
    @Mapping(target = "tipoOrigine", source = "tipoOrigine")
    @Mapping(target = "idProcessoTrasformazioneOriginario", source = "idProcessoTrasformazioneOriginario")
    @Mapping(target = "idMetodoDiColtivazione", source = "idMetodoDiColtivazione")
    @Mapping(target = "metodoDiColtivazione", expression = "java(mapMetodoDiColtivazione(prodotto.getIdMetodoDiColtivazione()))")
    @Mapping(target = "venditore", source = "venditore", qualifiedByName = "venditoreToUserPublicDTO")
    @Mapping(target = "certificazioni", source = "certificazioni")
    public abstract ProductDetailDTO toDetailDTO(Prodotto prodotto);

    /**
     * Creates a new Prodotto entity from CreateProductRequestDTO.
     * Note: All entity configuration must be handled by the service.
     */
    public Prodotto fromCreateDTO(CreateProductRequestDTO createDTO) {
        // Delegate to service for proper entity creation
        throw new UnsupportedOperationException("Use service layer to create Prodotto entities");
    }

    /**
     * Updates an existing Prodotto entity with data from CreateProductRequestDTO.
     * Note: Entity updates must be handled by the service.
     */
    public void updateFromCreateDTO(CreateProductRequestDTO createDTO, Prodotto prodotto) {
        // Delegate to service for proper entity updates
        throw new UnsupportedOperationException("Use service layer to update Prodotto entities");
    }

    /**
     * Converts Venditore to UserPublicDTO for product information.
     */
    @Named("venditoreToUserPublicDTO")
    public UserPublicDTO venditoreToUserPublicDTO(Venditore venditore) {
        if (venditore == null) {
            return null;
        }
        
        return UserPublicDTO.builder()
            .idUtente(venditore.getIdUtente())
            .nome(venditore.getNome())
            .cognome(venditore.getCognome())
            .tipoRuolo(venditore.getTipoRuolo())
            .isAttivo(venditore.isAttivo())
            .build();
    }

    /**
     * Converts List of Certificazione to List of CertificazioneDTO.
     */
    public abstract List<CertificazioneDTO> certificazioniToDTO(List<Certificazione> certificazioni);

    /**
     * Maps cultivation method ID to MetodoDiColtivazioneDTO.
     * Fetches the cultivation method from repository and converts to DTO.
     */
    protected MetodoDiColtivazioneDTO mapMetodoDiColtivazione(Long idMetodoDiColtivazione) {
        if (idMetodoDiColtivazione == null) {
            return null;
        }
        
        Optional<MetodoDiColtivazione> metodoDiColtivazione = metodoDiColtivazioneRepository.findById(idMetodoDiColtivazione);
        return metodoDiColtivazione.map(metodoDiColtivazioneMapper::toDTO).orElse(null);
    }
}