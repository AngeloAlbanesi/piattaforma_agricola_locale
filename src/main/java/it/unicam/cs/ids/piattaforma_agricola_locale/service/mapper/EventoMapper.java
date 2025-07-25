/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.service.mapper;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.eventi.AziendaPartecipanteDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.eventi.CreateEventoRequestDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.eventi.EventoDetailDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.eventi.EventoPartecipanteDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.eventi.EventoRegistrazioneDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.eventi.EventoRegistrazioneRequestDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.eventi.EventoSummaryDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.utente.UserPublicDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.Evento;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.EventoRegistrazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.AnimatoreDellaFiliera;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * MapStruct mapper for converting between Evento entities and Event DTOs.
 * Uses annotation-based mapping configuration with Spring integration.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    uses = {UtenteMapper.class}
)
@Component
public interface EventoMapper {

    /**
     * Converts Evento entity to EventoDetailDTO.
     * Includes complex relationships with organizer and participating companies.
     */
    @Mapping(target = "idEvento", source = "id")
    @Mapping(target = "nomeEvento", source = "nome")
    @Mapping(target = "descrizione", source = "descrizione")
    @Mapping(target = "dataOraInizio", source = "dataOraInizio")
    @Mapping(target = "dataOraFine", source = "dataOraFine")
    @Mapping(target = "luogoEvento", source = "luogoEvento")
    @Mapping(target = "capienzaMassima", source = "capienzaMassima")
    @Mapping(target = "postiAttualmentePrenotati", source = "postiAttualmentePrenotati")
    @Mapping(target = "postiDisponibili", expression = "java(evento.getPostiDisponibili())")
    @Mapping(target = "statoEvento", source = "statoEvento")
    @Mapping(target = "organizzatore", source = "organizzatore", qualifiedByName = "animatoreToUserPublicDTO")
    @Mapping(target = "aziendePartecipanti", source = "aziendePartecipanti", qualifiedByName = "venditoriToAziendaPartecipanteDTOList")
    EventoDetailDTO toDetailDTO(Evento evento);

    /**
     * Converts Evento entity to EventoSummaryDTO.
     * Contains only essential fields for listing views.
     */
    @Mapping(target = "idEvento", source = "id")
    @Mapping(target = "nomeEvento", source = "nome")
    @Mapping(target = "dataOraInizio", source = "dataOraInizio")
    @Mapping(target = "dataOraFine", source = "dataOraFine")
    @Mapping(target = "luogoEvento", source = "luogoEvento")
    @Mapping(target = "capienzaMassima", source = "capienzaMassima")
    @Mapping(target = "postiDisponibili", expression = "java(evento.getPostiDisponibili())")
    @Mapping(target = "statoEvento", source = "statoEvento")
    @Mapping(target = "nomeOrganizzatore", expression = "java(evento.getOrganizzatore().getNome() + \" \" + evento.getOrganizzatore().getCognome())")
    @Mapping(target = "idOrganizzatore", source = "organizzatore.idUtente")
    EventoSummaryDTO toSummaryDTO(Evento evento);

    /**
     * Converts CreateEventoRequestDTO to Evento entity.
     * Note: organizzatore and aziendePartecipanti must be set separately.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nome", source = "nomeEvento")
    @Mapping(target = "descrizione", source = "descrizione")
    @Mapping(target = "dataOraInizio", source = "dataOraInizio")
    @Mapping(target = "dataOraFine", source = "dataOraFine")
    @Mapping(target = "luogoEvento", source = "luogoEvento")
    @Mapping(target = "capienzaMassima", source = "capienzaMassima")
    @Mapping(target = "postiAttualmentePrenotati", constant = "0")
    @Mapping(target = "statoEvento", constant = "IN_PROGRAMMA")
    @Mapping(target = "organizzatore", ignore = true)
    @Mapping(target = "aziendePartecipanti", ignore = true)
    Evento fromCreateRequestDTO(CreateEventoRequestDTO createEventoRequestDTO);

    /**
     * Updates an existing Evento entity with data from CreateEventoRequestDTO.
     * Preserves ID, stato, and posti prenotati.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nome", source = "nomeEvento")
    @Mapping(target = "descrizione", source = "descrizione")
    @Mapping(target = "dataOraInizio", source = "dataOraInizio")
    @Mapping(target = "dataOraFine", source = "dataOraFine")
    @Mapping(target = "luogoEvento", source = "luogoEvento")
    @Mapping(target = "capienzaMassima", source = "capienzaMassima")
    @Mapping(target = "postiAttualmentePrenotati", ignore = true)
    @Mapping(target = "statoEvento", ignore = true)
    @Mapping(target = "organizzatore", ignore = true)
    @Mapping(target = "aziendePartecipanti", ignore = true)
    void updateFromCreateRequestDTO(CreateEventoRequestDTO createEventoRequestDTO, @MappingTarget Evento evento);

    /**
     * Converts AnimatoreDellaFiliera to UserPublicDTO.
     */
    @Named("animatoreToUserPublicDTO")
    @Mapping(target = "idUtente", source = "idUtente")
    @Mapping(target = "nome", source = "nome")
    @Mapping(target = "cognome", source = "cognome")
    @Mapping(target = "tipoRuolo", source = "tipoRuolo")
    @Mapping(target = "isAttivo", source = "attivo")
    UserPublicDTO animatoreToUserPublicDTO(AnimatoreDellaFiliera animatore);

    /**
     * Converts Venditore to UserPublicDTO.
     */
    @Named("venditoreToUserPublicDTO")
    @Mapping(target = "idUtente", source = "idUtente")
    @Mapping(target = "nome", source = "nome")
    @Mapping(target = "cognome", source = "cognome")
    @Mapping(target = "tipoRuolo", source = "tipoRuolo")
    @Mapping(target = "isAttivo", source = "attivo")
    UserPublicDTO venditoreToUserPublicDTO(Venditore venditore);

    /**
     * Converts List of Venditore to List of UserPublicDTO.
     */
    @Named("venditoriToUserPublicDTOList")
    List<UserPublicDTO> venditoriToUserPublicDTOList(List<Venditore> venditori);

    /**
     * Helper method to use venditoreToUserPublicDTO for list conversion.
     */
    default UserPublicDTO mapVenditoreToUserPublicDTO(Venditore venditore) {
        return venditoreToUserPublicDTO(venditore);
    }

    /**
     * Converts DatiAzienda to AziendaPartecipanteDTO.
     */
    @Named("datiAziendaToAziendaPartecipanteDTO")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nomeAzienda", source = "nomeAzienda")
    @Mapping(target = "partitaIva", source = "partitaIva")
    @Mapping(target = "indirizzoAzienda", source = "indirizzoAzienda")
    @Mapping(target = "descrizioneAzienda", source = "descrizioneAzienda")
    @Mapping(target = "sitoWebUrl", source = "sitoWebUrl")
    @Mapping(target = "certificazioniAzienda", source = "certificazioniAzienda")
    AziendaPartecipanteDTO datiAziendaToAziendaPartecipanteDTO(DatiAzienda datiAzienda);

    /**
     * Converts List of Venditore to List of AziendaPartecipanteDTO.
     */
    @Named("venditoriToAziendaPartecipanteDTOList")
    default List<AziendaPartecipanteDTO> venditoriToAziendaPartecipanteDTOList(List<Venditore> venditori) {
        if (venditori == null) {
            return null;
        }
        return venditori.stream()
                .map(venditore -> datiAziendaToAziendaPartecipanteDTO(venditore.getDatiAzienda()))
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Maps EventoRegistrazione to EventoRegistrazioneDTO.
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "eventoId", source = "evento.id")
    @Mapping(target = "nomeEvento", source = "evento.nome")
    @Mapping(target = "utente", source = "utente")
    @Mapping(target = "dataRegistrazione", source = "dataRegistrazione")
    @Mapping(target = "numeroPosti", source = "numeroPosti")
    @Mapping(target = "note", source = "note")
    EventoRegistrazioneDTO toEventoRegistrazioneDTO(EventoRegistrazione registrazione);
    
    /**
     * Maps EventoRegistrazione to EventoPartecipanteDTO.
     */
    @Mapping(target = "utenteId", source = "utente.idUtente")
    @Mapping(target = "nome", source = "utente.nome")
    @Mapping(target = "cognome", source = "utente.cognome")
    @Mapping(target = "email", source = "utente.email")
    @Mapping(target = "dataRegistrazione", source = "dataRegistrazione")
    @Mapping(target = "numeroPosti", source = "numeroPosti")
    EventoPartecipanteDTO toEventoPartecipanteDTO(EventoRegistrazione registrazione);
    
    /**
     * Maps List of EventoRegistrazione to List of EventoPartecipanteDTO.
     */
    List<EventoPartecipanteDTO> toEventoPartecipanteDTOList(List<EventoRegistrazione> registrazioni);
}