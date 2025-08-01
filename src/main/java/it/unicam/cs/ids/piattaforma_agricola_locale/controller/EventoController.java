package it.unicam.cs.ids.piattaforma_agricola_locale.controller;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.eventi.CreateEventoRequestDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.eventi.EventoDetailDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.eventi.EventoPartecipanteDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.eventi.EventoRegistrazioneRequestDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.eventi.EventoSummaryDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.eventi.AziendaPartecipanteDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.social.PromoteRequestDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.social.ShareResponseDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.exception.BusinessRuleViolationException;
import it.unicam.cs.ids.piattaforma_agricola_locale.exception.ResourceOwnershipException;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.Evento;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.EventoRegistrazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.AnimatoreDellaFiliera;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Utente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IVenditoreRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.OwnershipValidationService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IEventoService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IUtenteService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.mapper.EventoMapper;
import it.unicam.cs.ids.piattaforma_agricola_locale.security.RequiresAccreditation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/eventi")
@RequiredArgsConstructor
@Slf4j
public class EventoController {

    private final IEventoService eventoService;
    private final IUtenteService utenteService;
    private final EventoMapper eventoMapper;
    private final OwnershipValidationService ownershipValidationService;
    private final IVenditoreRepository venditoreRepository;

    @GetMapping
    public ResponseEntity<Page<EventoSummaryDTO>> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dataOraInizio") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long organizerId) {

        Sort sort = sortDirection.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Evento> eventi;

        if (search != null && !search.trim().isEmpty()) {
            List<Evento> searchResults = eventoService.searchEventiByNome(search);
            List<EventoSummaryDTO> summaryDTOs = searchResults.stream()
                    .map(eventoMapper::toSummaryDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new PageImpl<>(summaryDTOs, pageable, searchResults.size()));
        } else if (organizerId != null) {
            List<Evento> organizerEvents = eventoService.getEventiByOrganizzatore(organizerId);
            List<EventoSummaryDTO> summaryDTOs = organizerEvents.stream()
                    .map(eventoMapper::toSummaryDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new PageImpl<>(summaryDTOs, pageable, organizerEvents.size()));
        } else {
            eventi = eventoService.getAllEventi(pageable);
        }

        Page<EventoSummaryDTO> eventSummaries = eventi.map(eventoMapper::toSummaryDTO);

        log.info("Retrieved {} events (page {}, size {})", eventSummaries.getTotalElements(), page, size);
        return ResponseEntity.ok(eventSummaries);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoDetailDTO> getEventById(@PathVariable Long id) {
        return eventoService.getEventoById(id)
                .map(eventoMapper::toDetailDTO)
                .map(eventDetail -> {
                    log.info("Retrieved event details for ID: {}", id);
                    return ResponseEntity.ok(eventDetail);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cercaEventi")
    public ResponseEntity<List<EventoSummaryDTO>> searchEvents(
            @RequestParam String query) {

        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<Evento> searchResults = eventoService.searchEventiByNome(query);
        List<EventoSummaryDTO> summaryDTOs = searchResults.stream()
                .map(eventoMapper::toSummaryDTO)
                .collect(Collectors.toList());

        log.info("Search for '{}' returned {} results", query, summaryDTOs.size());
        return ResponseEntity.ok(summaryDTOs);
    }

    @GetMapping("/organizzatori/{organizerId}")
    public ResponseEntity<List<EventoSummaryDTO>> getEventsByOrganizer(
            @PathVariable Long organizerId) {

        List<Evento> organizerEvents = eventoService.getEventiByOrganizzatore(organizerId);
        List<EventoSummaryDTO> summaryDTOs = organizerEvents.stream()
                .map(eventoMapper::toSummaryDTO)
                .collect(Collectors.toList());

        log.info("Retrieved {} events for organizer ID: {}", summaryDTOs.size(), organizerId);
        return ResponseEntity.ok(summaryDTOs);
    }

    // ===== EVENT MANAGEMENT ENDPOINTS =====

    /**
     * Create a new event.
     * Only users with ANIMATORE_DELLA_FILIERA role can create events.
     */
    @PostMapping("/creaEvento")
    @RequiresAccreditation
    @PreAuthorize("hasRole('ANIMATORE_DELLA_FILIERA')")
    public ResponseEntity<EventoDetailDTO> createEvent(
            @Valid @RequestBody CreateEventoRequestDTO createEventoRequest,
            Authentication authentication) {

        // Get the authenticated user
        String username = authentication.getName();
        AnimatoreDellaFiliera animatore = (AnimatoreDellaFiliera) utenteService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        // Create the event through service and get the saved entity with ID
        Evento eventoSalvato = eventoService.creaEvento(
                createEventoRequest.getNomeEvento(),
                createEventoRequest.getDescrizione(),
                createEventoRequest.getDataOraInizio(),
                createEventoRequest.getDataOraFine(),
                createEventoRequest.getLuogoEvento(),
                createEventoRequest.getCapienzaMassima(),
                animatore);

        // Map the saved event (with ID and organizer) to DTO
        EventoDetailDTO eventDetail = eventoMapper.toDetailDTO(eventoSalvato);

        log.info("Created new event: {} by organizer: {}", eventDetail.getNomeEvento(), animatore.getNome());

        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(eventDetail.getIdEvento())
                        .toUri())
                .body(eventDetail);
    }

    /**
     * Update an existing event.
     * Only the organizer of the event can update it.
     */
    @PutMapping("/{id}")
    @RequiresAccreditation
    @PreAuthorize("hasRole('ANIMATORE_DELLA_FILIERA') and @ownershipValidationService.isEventOwner(#id, authentication.name)")
    @CacheEvict(value = "events", key = "#id + 'owner' + #authentication.name")
    public ResponseEntity<EventoDetailDTO> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody CreateEventoRequestDTO updateEventoRequest,
            Authentication authentication) {

        // Get the authenticated user
        String username = authentication.getName();
        AnimatoreDellaFiliera animatore = (AnimatoreDellaFiliera) utenteService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        // Update the event
        eventoService.aggiornaEvento(
                id,
                updateEventoRequest.getNomeEvento(),
                updateEventoRequest.getDescrizione(),
                updateEventoRequest.getDataOraInizio(),
                updateEventoRequest.getDataOraFine(),
                updateEventoRequest.getLuogoEvento(),
                updateEventoRequest.getCapienzaMassima(),
                animatore);

        // Get the updated event and return it
        Evento updatedEvento = eventoService.getEventoById(id)
                .orElseThrow(() -> new IllegalArgumentException("Evento non trovato"));

        EventoDetailDTO eventDetail = eventoMapper.toDetailDTO(updatedEvento);

        log.info("Updated event: {} by organizer: {}", eventDetail.getNomeEvento(), animatore.getNome());

        return ResponseEntity.ok(eventDetail);
    }

    /**
     * Delete an event.
     * Only the organizer of the event can delete it.
     */
    @DeleteMapping("/{id}")
    @RequiresAccreditation
    @PreAuthorize("hasRole('ANIMATORE_DELLA_FILIERA') and @ownershipValidationService.isEventOwner(#id, authentication.name)")
    @CacheEvict(value = "events", key = "#id + 'owner' + #authentication.name")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long id,
            Authentication authentication) {

        // Get the authenticated user
        String username = authentication.getName();
        AnimatoreDellaFiliera animatore = (AnimatoreDellaFiliera) utenteService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        // Delete the event
        eventoService.eliminaEvento(id, animatore);

        log.info("Deleted event with ID: {} by organizer: {}", id, animatore.getNome());

        return ResponseEntity.noContent().build();
    }

    // ===== EVENT REGISTRATION ENDPOINTS =====

    /**
     * Register for an event.
     * Any authenticated user can register for an event.
     */
    @PostMapping("/{id}/registra")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> registerForEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventoRegistrazioneRequestDTO registrazioneRequest,
            Authentication authentication) {

        // Get the authenticated user
        String username = authentication.getName();
        Utente utente = utenteService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        // Register for the event
        eventoService.registraUtenteEvento(
                id,
                utente,
                registrazioneRequest.getNumeroPosti(),
                registrazioneRequest.getNote());

        log.info("User {} registered for event ID: {} with {} spots", username, id,
                registrazioneRequest.getNumeroPosti());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Cancel registration for an event.
     * Users can only cancel their own registrations.
     */
    @DeleteMapping("/{id}/registra")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> cancelEventRegistration(
            @PathVariable Long id,
            Authentication authentication) {

        // Get the authenticated user
        String username = authentication.getName();
        Utente utente = utenteService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        // Cancel the registration
        eventoService.cancellaRegistrazioneUtente(id, utente);

        log.info("User {} cancelled registration for event ID: {}", username, id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Get the list of participants for an event.
     * Only the organizer of the event can see the participants.
     */
    @GetMapping("/{id}/partecipanti")
    @RequiresAccreditation
    @PreAuthorize("hasRole('ANIMATORE_DELLA_FILIERA') and @ownershipValidationService.isEventOwner(#id, authentication.name)")
    public ResponseEntity<List<EventoPartecipanteDTO>> getEventParticipants(
            @PathVariable Long id,
            Authentication authentication) {

        // Get the registrations and map to DTOs
        List<EventoRegistrazione> registrazioni = eventoService.getRegistrazioniEvento(id);
        List<EventoPartecipanteDTO> partecipanti = eventoMapper.toEventoPartecipanteDTOList(registrazioni);

        log.info("Retrieved {} participants for event ID: {}", partecipanti.size(), id);

        return ResponseEntity.ok(partecipanti);
    }

    @PostMapping("/{id}/promote")
    @RequiresAccreditation
    @PreAuthorize("hasRole('ANIMATORE_DELLA_FILIERA')") // Autorizzazione specifica
    public ResponseEntity<?> promuoviEvento(
            @PathVariable Long id,
            @RequestBody PromoteRequestDTO request) {
        Optional<ShareResponseDTO> responseOpt = eventoService.promuoviEvento(id, request);

        if (responseOpt.isPresent()) {
            return ResponseEntity.ok(responseOpt.get());
        } else {
            // Se l'evento con quell'ID non esiste
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Evento con ID " + id + " non trovato.");
        }
    }

    // ===== EVENT STATE MANAGEMENT ENDPOINTS =====

    /**
     * Start an event.
     * Only the organizer of the event can start it.
     * Changes status from IN_PROGRAMMA to IN_CORSO.
     */
    @PatchMapping("/{id}/inizia")
    @RequiresAccreditation
    @PreAuthorize("hasRole('ANIMATORE_DELLA_FILIERA') and @ownershipValidationService.isEventOwner(#id, authentication.name)")
    public ResponseEntity<EventoDetailDTO> iniziaEvento(
            @PathVariable Long id,
            Authentication authentication) {

        // Get the authenticated user
        String username = authentication.getName();
        AnimatoreDellaFiliera animatore = (AnimatoreDellaFiliera) utenteService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        try {
            // Start the event
            eventoService.iniziaEvento(id, animatore);

            // Get the updated event and return it
            Evento updatedEvento = eventoService.getEventoById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Evento non trovato"));

            EventoDetailDTO eventDetail = eventoMapper.toDetailDTO(updatedEvento);

            log.info("Started event ID: {} by organizer: {}", id, animatore.getNome());

            return ResponseEntity.ok(eventDetail);

        } catch (IllegalArgumentException e) {
            log.warn("Failed to start event ID: {} - {}", id, e.getMessage());
            throw new BusinessRuleViolationException(e.getMessage());
        }
    }

    /**
     * End an event.
     * Only the organizer of the event can end it.
     * Changes status from IN_CORSO to CONCLUSO.
     */
    @PatchMapping("/{id}/termina")
    @RequiresAccreditation
    @PreAuthorize("hasRole('ANIMATORE_DELLA_FILIERA') and @ownershipValidationService.isEventOwner(#id, authentication.name)")
    public ResponseEntity<EventoDetailDTO> terminaEvento(
            @PathVariable Long id,
            Authentication authentication) {

        // Get the authenticated user
        String username = authentication.getName();
        AnimatoreDellaFiliera animatore = (AnimatoreDellaFiliera) utenteService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        try {
            // End the event
            eventoService.terminaEvento(id, animatore);

            // Get the updated event and return it
            Evento updatedEvento = eventoService.getEventoById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Evento non trovato"));

            EventoDetailDTO eventDetail = eventoMapper.toDetailDTO(updatedEvento);

            log.info("Ended event ID: {} by organizer: {}", id, animatore.getNome());

            return ResponseEntity.ok(eventDetail);

        } catch (IllegalArgumentException e) {
            log.warn("Failed to end event ID: {} - {}", id, e.getMessage());
            throw new BusinessRuleViolationException(e.getMessage());
        }
    }

    /**
     * Cancel an event.
     * Only the organizer of the event can cancel it.
     * Changes status to ANNULLATO (cannot cancel if already CONCLUSO).
     */
    @PatchMapping("/{id}/annulla")
    @RequiresAccreditation
    @PreAuthorize("hasRole('ANIMATORE_DELLA_FILIERA') and @ownershipValidationService.isEventOwner(#id, authentication.name)")
    public ResponseEntity<EventoDetailDTO> annullaEvento(
            @PathVariable Long id,
            Authentication authentication) {

        // Get the authenticated user
        String username = authentication.getName();
        AnimatoreDellaFiliera animatore = (AnimatoreDellaFiliera) utenteService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        try {
            // Cancel the event
            eventoService.annullaEvento(id, animatore);

            // Get the updated event and return it
            Evento updatedEvento = eventoService.getEventoById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Evento non trovato"));

            EventoDetailDTO eventDetail = eventoMapper.toDetailDTO(updatedEvento);

            log.info("Cancelled event ID: {} by organizer: {}", id, animatore.getNome());

            return ResponseEntity.ok(eventDetail);

        } catch (IllegalArgumentException e) {
            log.warn("Failed to cancel event ID: {} - {}", id, e.getMessage());
            throw new BusinessRuleViolationException(e.getMessage());
        }
    }

    // ===== COMPANY PARTICIPANT MANAGEMENT ENDPOINTS =====

    /**
     * Add a company as participant to an event.
     * Only the organizer of the event can add participants.
     */
    @PostMapping("/{id}/partecipanti-azienda/{venditorId}")
    @RequiresAccreditation
    @PreAuthorize("hasRole('ANIMATORE_DELLA_FILIERA') and @ownershipValidationService.isEventOwner(#id, authentication.name)")
    public ResponseEntity<Void> aggiungiAziendaPartecipante(
            @PathVariable Long id,
            @PathVariable Long venditorId,
            Authentication authentication) {

        try {
            // Get the vendor user
            Venditore venditore = venditoreRepository.findById(venditorId)
                    .orElseThrow(() -> new IllegalArgumentException("Venditore con ID " + venditorId + " non trovato"));

            // Add the company participant
            eventoService.aggiungiAziendaPartecipante(id, venditore);

            log.info("Added company participant (vendor ID: {}) to event ID: {}", venditorId, id);

            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (IllegalArgumentException e) {
            log.warn("Failed to add company participant to event ID: {} - {}", id, e.getMessage());
            throw new BusinessRuleViolationException(e.getMessage());
        }
    }

    /**
     * Remove a company participant from an event.
     * Only the organizer of the event can remove participants.
     */
    @DeleteMapping("/{id}/partecipanti-azienda/{venditorId}")
    @RequiresAccreditation
    @PreAuthorize("hasRole('ANIMATORE_DELLA_FILIERA') and @ownershipValidationService.isEventOwner(#id, authentication.name)")
    public ResponseEntity<Void> rimuoviAziendaPartecipante(
            @PathVariable Long id,
            @PathVariable Long venditorId,
            Authentication authentication) {

        try {
            // Get the vendor user
            Venditore venditore = venditoreRepository.findById(venditorId)
                    .orElseThrow(() -> new IllegalArgumentException("Venditore con ID " + venditorId + " non trovato"));

            // Remove the company participant
            eventoService.rimuoviAziendaPartecipante(id, venditore);

            log.info("Removed company participant (vendor ID: {}) from event ID: {}", venditorId, id);

            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            log.warn("Failed to remove company participant from event ID: {} - {}", id, e.getMessage());
            throw new BusinessRuleViolationException(e.getMessage());
        }
    }

    /**
     * Get the list of company participants for an event.
     * Only the organizer of the event can see the company participants.
     */
    @GetMapping("/{id}/partecipanti-azienda")
    @RequiresAccreditation
    @PreAuthorize("hasRole('ANIMATORE_DELLA_FILIERA') and @ownershipValidationService.isEventOwner(#id, authentication.name)")
    public ResponseEntity<List<AziendaPartecipanteDTO>> getAziendePartecipanti(
            @PathVariable Long id,
            Authentication authentication) {

        // Get the event
        Evento evento = eventoService.getEventoById(id)
                .orElseThrow(() -> new IllegalArgumentException("Evento con ID " + id + " non trovato"));

        // Get company participants and map to DTOs manually
        List<AziendaPartecipanteDTO> aziendePartecipanti = evento.getAziendePartecipanti()
                .stream()
                .map(venditore -> {
                    var datiAzienda = venditore.getDatiAzienda();
                    return AziendaPartecipanteDTO.builder()
                            .id(datiAzienda.getId())
                            .nomeAzienda(datiAzienda.getNomeAzienda())
                            .partitaIva(datiAzienda.getPartitaIva())
                            .indirizzoAzienda(datiAzienda.getIndirizzoAzienda())
                            .descrizioneAzienda(datiAzienda.getDescrizioneAzienda())
                            .sitoWebUrl(datiAzienda.getSitoWebUrl())
                            .certificazioniAzienda(List.of()) // Simplified for now
                            .build();
                })
                .collect(Collectors.toList());

        log.info("Retrieved {} company participants for event ID: {}", aziendePartecipanti.size(), id);

        return ResponseEntity.ok(aziendePartecipanti);
    }
}