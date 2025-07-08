package it.unicam.cs.ids.piattaforma_agricola_locale.controller;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.processo.*;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.FaseLavorazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.ProcessoTrasformazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Trasformatore;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.OwnershipValidationService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IProcessoTrasformazioneService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IUtenteService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.mapper.ProcessoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Optional;

/**
 * REST controller for transformation process management.
 * Provides endpoints for CRUD operations on transformation processes.
 */
@RestController
@RequestMapping("/api/processi-trasformazione")
@RequiredArgsConstructor
@Slf4j
public class ProcessoTrasformazioneController {

    private final IProcessoTrasformazioneService processoTrasformazioneService;
    private final IUtenteService utenteService;
    private final ProcessoMapper processoMapper;
    private final OwnershipValidationService ownershipValidationService;

    /**
     * Create a new transformation process.
     * Only users with TRASFORMATORE role can create processes.
     */
    @PostMapping
    @PreAuthorize("hasRole('TRASFORMATORE')")
    public ResponseEntity<ProcessoTrasformazioneDTO> createTransformationProcess(
            @Valid @RequestBody CreateProcessoRequestDTO createProcessoRequest,
            Authentication authentication) {

        // Get the authenticated user
        String username = authentication.getName();
        Trasformatore trasformatore = (Trasformatore) utenteService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        // Create the transformation process
        ProcessoTrasformazione processo = processoTrasformazioneService.creaProcesso(
                createProcessoRequest.getNome(),
                createProcessoRequest.getDescrizione(),
                trasformatore,
                createProcessoRequest.getMetodoProduzione());

        // Map to DTO and return
        ProcessoTrasformazioneDTO processoDTO = processoMapper.toDto(processo);

        log.info("Created new transformation process: {} by trasformatore: {}",
                processoDTO.getNomeProcesso(), trasformatore.getNome());

        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(processoDTO.getIdProcesso())
                        .toUri())
                .body(processoDTO);
    }

    /**
     * Get all transformation processes with pagination.
     */
    @GetMapping
    public ResponseEntity<Page<ProcessoTrasformazioneDTO>> getAllTransformationProcesses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "nome") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(required = false) Long trasformatoreId) {

        Sort sort = sortDirection.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProcessoTrasformazione> processi;

        if (trasformatoreId != null) {
            // Filter by trasformatore - this would need additional service method
            processi = Page.empty(pageable); // Placeholder
        } else {
            processi = processoTrasformazioneService.getAllProcessi(pageable);
        }

        Page<ProcessoTrasformazioneDTO> processiDTO = processi.map(processoMapper::toDto);

        log.info("Retrieved {} transformation processes (page {}, size {})",
                processiDTO.getTotalElements(), page, size);
        return ResponseEntity.ok(processiDTO);
    }

    /**
     * Get a specific transformation process by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProcessoTrasformazioneDTO> getTransformationProcessById(@PathVariable Long id) {
        Optional<ProcessoTrasformazione> processoOpt = processoTrasformazioneService.getProcessoById(id);

        if (processoOpt.isEmpty()) {
            log.warn("Transformation process with ID {} not found", id);
            return ResponseEntity.notFound().build();
        }

        ProcessoTrasformazioneDTO processoDTO = processoMapper.toDto(processoOpt.get());

        log.info("Retrieved transformation process details for ID: {}", id);
        return ResponseEntity.ok(processoDTO);
    }

    /**
     * Update an existing transformation process.
     * Only the owner can update their processes.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TRASFORMATORE') and @ownershipValidationService.isProcessOwner(#id, authentication.name)")
    @CacheEvict(value = "users", key = "#id + '_process_owner_' + #authentication.name")
    public ResponseEntity<ProcessoTrasformazioneDTO> updateTransformationProcess(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProcessoRequestDTO updateProcessoRequest,
            Authentication authentication) {

        // Get the authenticated user
        String username = authentication.getName();
        Trasformatore trasformatore = (Trasformatore) utenteService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        // Update the transformation process
        ProcessoTrasformazione processo = processoTrasformazioneService.aggiornaProcesso(
                id,
                updateProcessoRequest.getNome(),
                updateProcessoRequest.getDescrizione(),
                updateProcessoRequest.getMetodoProduzione(),
                trasformatore);

        // Map to DTO and return
        ProcessoTrasformazioneDTO processoDTO = processoMapper.toDto(processo);

        log.info("Updated transformation process: {} by trasformatore: {}",
                processoDTO.getNomeProcesso(), trasformatore.getNome());

        return ResponseEntity.ok(processoDTO);
    }

    /**
     * Delete a transformation process.
     * Only the owner can delete their processes.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TRASFORMATORE') and @ownershipValidationService.isProcessOwner(#id, authentication.name)")
    @CacheEvict(value = "users", key = "#id + '_process_owner_' + #authentication.name")
    public ResponseEntity<Void> deleteTransformationProcess(
            @PathVariable Long id,
            Authentication authentication) {

        // Get the authenticated user
        String username = authentication.getName();
        Trasformatore trasformatore = (Trasformatore) utenteService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        // Delete the transformation process
        boolean deleted = processoTrasformazioneService.eliminaProcesso(id, trasformatore);

        if (!deleted) {
            log.warn("Failed to delete transformation process with ID: {}", id);
            return ResponseEntity.notFound().build();
        }

        log.info("Deleted transformation process with ID: {} by trasformatore: {}", id, trasformatore.getNome());
        return ResponseEntity.noContent().build();
    }

    /**
     * Add a phase to a transformation process.
     * Only the owner can add phases to their processes.
     */
    @PostMapping("/{id}/fasi")
    @PreAuthorize("hasRole('TRASFORMATORE') and @ownershipValidationService.isProcessOwner(#id, authentication.name)")
    public ResponseEntity<ProcessoTrasformazioneDTO> addPhaseToProcess(
            @PathVariable Long id,
            @Valid @RequestBody CreateFaseRequestDTO createFaseRequest,
            Authentication authentication) {

        // Get the authenticated user
        String username = authentication.getName();
        Trasformatore trasformatore = (Trasformatore) utenteService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        // Create the phase
        FaseLavorazione fase = new FaseLavorazione(
                createFaseRequest.getNome(),
                createFaseRequest.getDescrizione(),
                createFaseRequest.getOrdineEsecuzione(),
                createFaseRequest.getMateriaPrimaUtilizzata(),
                createFaseRequest.getFonte());

        // Add the phase to the process
        ProcessoTrasformazione processo = processoTrasformazioneService.aggiungiFaseAlProcesso(id, fase);

        // Map to DTO and return
        ProcessoTrasformazioneDTO processoDTO = processoMapper.toDto(processo);

        log.info("Added phase to transformation process ID: {} by trasformatore: {}", id, trasformatore.getNome());

        return ResponseEntity.status(HttpStatus.CREATED).body(processoDTO);
    }

    /**
     * Get traceability information for a transformation process.
     */
    @GetMapping("/{id}/tracciabilita")
    public ResponseEntity<TraceabilityDTO> getProcessTraceability(@PathVariable Long id) {
        // This would need to be implemented
        // For now, return not found
        log.warn("Traceability for transformation process with ID {} not found", id);
        return ResponseEntity.notFound().build();
    }
}
