package it.unicam.cs.ids.piattaforma_agricola_locale.controller;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.processo.*;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.FaseLavorazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.FonteMateriaPrima;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.FonteEsterna;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.FonteInterna;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.ProcessoTrasformazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Utente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Trasformatore;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Produttore;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IProcessoTrasformazioneRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.OwnershipValidationService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IProcessoTrasformazioneService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IUtenteService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IProdottoService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.mapper.ProcessoMapper;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.mapper.TraceabilityMapper;
import it.unicam.cs.ids.piattaforma_agricola_locale.security.RequiresAccreditation;
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

import java.util.ArrayList;
import java.util.List;
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
    private final IProcessoTrasformazioneRepository processoRepository;
    private final IUtenteService utenteService;
    private final IProdottoService prodottoService;
    private final ProcessoMapper processoMapper;
    private final TraceabilityMapper traceabilityMapper;
    private final OwnershipValidationService ownershipValidationService;

    /**
     * Create a new transformation process.
     * Only users with TRASFORMATORE role can create processes.
     */
    @PostMapping
    @RequiresAccreditation
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

        // Se e specificato un prodotto finale, collegalo al processo
        if (createProcessoRequest.getProdottoFinaleId() != null) {
            try {
                processo = processoTrasformazioneService.collegaProcessoAProdotto(
                        processo.getId(),
                        createProcessoRequest.getProdottoFinaleId());
                log.info("Connected process ID: {} to product ID: {} during creation",
                        processo.getId(), createProcessoRequest.getProdottoFinaleId());
            } catch (Exception e) {
                log.warn("Failed to connect process to product during creation: {}", e.getMessage());
                // Il processo e comunque creato, solo il collegamento fallisce
            }
        }

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
     * Get all transformation processes created by the authenticated trasformatore.
     * This endpoint must be defined BEFORE /{id} to avoid path collision.
     */
    @GetMapping("/miei")
    @PreAuthorize("hasRole('TRASFORMATORE')")
    public ResponseEntity<Page<ProcessoTrasformazioneSummaryDTO>> getMyTransformationProcesses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {

        // Get the authenticated trasformatore
        String username = authentication.getName();
        Trasformatore trasformatore = (Trasformatore) utenteService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        // Get all processes for this trasformatore
        List<ProcessoTrasformazione> processiList = processoTrasformazioneService
                .getProcessiByTrasformatore(trasformatore);

        // Convert to Page manually
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), processiList.size());
        List<ProcessoTrasformazione> pageContent = start < processiList.size()
                ? processiList.subList(start, end)
                : new ArrayList<>();
        Page<ProcessoTrasformazione> processi = new org.springframework.data.domain.PageImpl<>(
                pageContent, pageable, processiList.size());

        Page<ProcessoTrasformazioneSummaryDTO> processiDTO = processi.map(processoMapper::toSummaryDto);

        log.info("Retrieved {} transformation processes for trasformatore: {} (page {}, size {})",
                processiDTO.getTotalElements(), trasformatore.getNome(), page, size);

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
     * Get public details of a transformation process by ID.
     * This endpoint is publicly accessible to allow viewing process details
     * for transformed products in the public catalog.
     */
    @GetMapping("/pubblico/{id}")
    @PreAuthorize("permitAll()")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ResponseEntity<ProcessoTrasformazionePublicDTO> getPublicProcessoById(@PathVariable Long id) {
        return processoTrasformazioneService.getProcessoById(id)
                .map(processo -> {
                    ProcessoTrasformazionePublicDTO dto = mapToPublicDTO(processo);
                    log.info("Retrieved public transformation process details for ID: {}", id);
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> {
                    log.warn("Public transformation process with ID {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * Maps a ProcessoTrasformazione entity to a public DTO without cyclic
     * references.
     */
    private ProcessoTrasformazionePublicDTO mapToPublicDTO(ProcessoTrasformazione processo) {
        ProcessoTrasformazionePublicDTO dto = new ProcessoTrasformazionePublicDTO();
        dto.setIdProcesso(processo.getId());
        dto.setNomeProcesso(processo.getNome());
        dto.setDescrizioneProcesso(processo.getDescrizione());
        dto.setMetodoProduzione(processo.getMetodoProduzione());
        dto.setDataCreazione(null); // ProcessoTrasformazione doesn't have a creation date field

        // Map fasi lavorazione - force lazy loading
        List<FaseLavorazionePublicDTO> fasiDTO = new ArrayList<>();
        List<FaseLavorazione> fasi = processo.getFasiLavorazione();
        if (fasi != null && !fasi.isEmpty()) {
            log.debug("Mapping {} fasi for processo {}", fasi.size(), processo.getId());
            for (FaseLavorazione fase : fasi) {
                fasiDTO.add(mapFaseToPublicDTO(fase));
            }
        }
        dto.setFasiLavorazione(fasiDTO);

        return dto;
    }

    /**
     * Maps a FaseLavorazione entity to a public DTO.
     */
    private FaseLavorazionePublicDTO mapFaseToPublicDTO(FaseLavorazione fase) {
        FaseLavorazionePublicDTO dto = new FaseLavorazionePublicDTO();
        dto.setId(fase.getId());
        dto.setNome(fase.getNome());
        dto.setNumeroFase(fase.getOrdineEsecuzione());
        dto.setDescrizione(fase.getDescrizione());

        // Map fonte materia prima to list (even though there's only one)
        List<FonteMateriaPrimaPublicDTO> fontiDTO = new ArrayList<>();
        FonteMateriaPrima fonte = fase.getFonte();
        if (fonte != null) {
            log.debug("Mapping fonte for fase {}: fonte class is {}", fase.getId(), fonte.getClass().getName());
            fontiDTO.add(mapFonteToPublicDTO(fonte, fase.getMateriaPrimaUtilizzata()));
        } else {
            log.warn("Fase {} has null fonte", fase.getId());
        }
        dto.setFontiMateriePrime(fontiDTO);

        return dto;
    }

    /**
     * Maps a FonteMateriaPrima entity to a public DTO.
     */
    private FonteMateriaPrimaPublicDTO mapFonteToPublicDTO(FonteMateriaPrima fonte, String materiaPrimaUtilizzata) {
        FonteMateriaPrimaPublicDTO dto = new FonteMateriaPrimaPublicDTO();
        dto.setId(fonte.getId());

        // Get actual class name (unwrap Hibernate proxy if needed)
        String className = org.hibernate.Hibernate.getClass(fonte).getName();
        log.debug("Mapping fonte with actual class: {}", className);

        dto.setDescrizioneFonte(fonte.getDescrizione());

        // Determine type by checking actual class (not proxy)
        boolean isInterna = className.endsWith("FonteInterna");
        boolean isEsterna = className.endsWith("FonteEsterna");

        log.debug("Fonte type check - isInterna: {}, isEsterna: {}", isInterna, isEsterna);

        if (isInterna) {
            dto.setTipoFonte("INTERNA");
            try {
                // Initialize proxy before casting
                org.hibernate.Hibernate.initialize(fonte);
                FonteInterna fonteInterna = (FonteInterna) fonte;
                Produttore produttore = fonteInterna.getProduttore();
                if (produttore != null) {
                    dto.setProdottoId(produttore.getIdUtente());
                    dto.setProdottoNome(produttore.getNome() + " " + produttore.getCognome());
                    log.debug("Mapped INTERNA fonte with produttore: {} {}",
                            produttore.getNome(), produttore.getCognome());
                }
            } catch (Exception e) {
                log.error("Error accessing FonteInterna produttore: {}", e.getMessage(), e);
            }
        } else if (isEsterna) {
            dto.setTipoFonte("ESTERNA");
            try {
                // Initialize proxy before casting
                org.hibernate.Hibernate.initialize(fonte);
                FonteEsterna fonteEsterna = (FonteEsterna) fonte;
                dto.setProdottoNome(fonteEsterna.getNomeFornitore());
                log.debug("Mapped ESTERNA fonte with fornitore: {}", fonteEsterna.getNomeFornitore());
            } catch (Exception e) {
                log.error("Error accessing FonteEsterna fornitore: {}", e.getMessage(), e);
            }
        } else {
            // This should never happen with proper discriminator mapping
            log.error("Unknown fonte type for class: {} - this indicates a mapping issue", className);
            dto.setTipoFonte("ESTERNA");
        }

        // Set quantity and unit (using materia prima as description)
        dto.setQuantita(null); // Not available in current model
        dto.setUnitaMisura(materiaPrimaUtilizzata);

        return dto;
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
    @RequiresAccreditation
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
    @RequiresAccreditation
    @PreAuthorize("hasRole('TRASFORMATORE') and @ownershipValidationService.isProcessOwner(#id, authentication.name)")
    public ResponseEntity<ProcessoTrasformazioneResponseDTO> addPhaseToProcess(
            @PathVariable Long id,
            @Valid @RequestBody CreateFaseRequestDTO createFaseRequest,
            Authentication authentication) {

        // Get the authenticated user
        String username = authentication.getName();
        Trasformatore trasformatore = (Trasformatore) utenteService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        // Create FonteMateriaPrima based on tipo
        FonteMateriaPrima fonte = null;
        if (createFaseRequest.getFonte() != null) {
            if ("ESTERNA".equalsIgnoreCase(createFaseRequest.getFonte().getTipo())) {
                fonte = new FonteEsterna(createFaseRequest.getFonte().getNomeFornitore());
            } else if ("INTERNA".equalsIgnoreCase(createFaseRequest.getFonte().getTipo())) {
                // Recupera il Produttore dal database usando produttoreId
                if (createFaseRequest.getFonte().getProduttoreId() == null) {
                    throw new IllegalArgumentException("produttoreId è obbligatorio per fonte INTERNA");
                }
                Utente utente = utenteService.trovaUtentePerID(createFaseRequest.getFonte().getProduttoreId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Utente non trovato con ID: " + createFaseRequest.getFonte().getProduttoreId()));

                if (!(utente instanceof Produttore)) {
                    throw new IllegalArgumentException("L'utente con ID "
                            + createFaseRequest.getFonte().getProduttoreId() + " non è un Produttore");
                }

                fonte = new FonteInterna((Produttore) utente);
            }
        }

        // Create the phase
        FaseLavorazione fase = new FaseLavorazione(
                createFaseRequest.getNome(),
                createFaseRequest.getDescrizione(),
                createFaseRequest.getOrdineEsecuzione(),
                createFaseRequest.getMateriaPrimaUtilizzata(),
                fonte);

        // Add the phase to the process
        ProcessoTrasformazione processo = processoTrasformazioneService.aggiungiFaseAlProcesso(id, fase);

        // Map to response DTO (without fasi field)
        ProcessoTrasformazioneResponseDTO processoDTO = processoMapper.toResponseDto(processo);

        log.info("Added phase to transformation process ID: {} by trasformatore: {}", id, trasformatore.getNome());

        return ResponseEntity.status(HttpStatus.CREATED).body(processoDTO);
    }

    /**
     * Get all phases of a transformation process.
     * Only the owner can view phases of their processes.
     */
    @GetMapping("/{id}/fasi")
    @RequiresAccreditation
    @PreAuthorize("hasRole('TRASFORMATORE') and @ownershipValidationService.isProcessOwner(#id, authentication.name)")
    public ResponseEntity<List<FaseLavorazioneDTO>> getProcessPhases(
            @PathVariable Long id,
            Authentication authentication) {

        Optional<ProcessoTrasformazione> processoOpt = processoTrasformazioneService.getProcessoById(id);

        if (processoOpt.isEmpty()) {
            log.warn("Transformation process with ID {} not found", id);
            return ResponseEntity.notFound().build();
        }

        ProcessoTrasformazione processo = processoOpt.get();
        List<FaseLavorazioneDTO> fasiDTO = processo.getFasiLavorazione().stream()
                .map(processoMapper::toFaseDto)
                .toList();

        log.info("Retrieved {} phases for transformation process ID: {}", fasiDTO.size(), id);
        return ResponseEntity.ok(fasiDTO);
    }

    /**
     * Update a phase of a transformation process.
     * Only the owner can update phases of their processes.
     */
    @PutMapping("/{processoId}/fasi/{faseId}")
    @RequiresAccreditation
    @PreAuthorize("hasRole('TRASFORMATORE') and @ownershipValidationService.isProcessOwner(#processoId, authentication.name)")
    public ResponseEntity<FaseLavorazioneDTO> updatePhase(
            @PathVariable Long processoId,
            @PathVariable Long faseId,
            @Valid @RequestBody CreateFaseRequestDTO updateFaseRequest,
            Authentication authentication) {

        // Get the authenticated user
        String username = authentication.getName();
        Trasformatore trasformatore = (Trasformatore) utenteService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        // Get the process and verify the phase exists
        Optional<ProcessoTrasformazione> processoOpt = processoTrasformazioneService.getProcessoById(processoId);
        if (processoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ProcessoTrasformazione processo = processoOpt.get();
        FaseLavorazione faseToUpdate = processo.getFasiLavorazione().stream()
                .filter(f -> f.getId().equals(faseId))
                .findFirst()
                .orElse(null);

        if (faseToUpdate == null) {
            log.warn("Phase with ID {} not found in process ID {}", faseId, processoId);
            return ResponseEntity.notFound().build();
        }

        // Update the phase fields
        faseToUpdate.setNome(updateFaseRequest.getNome());
        faseToUpdate.setDescrizione(updateFaseRequest.getDescrizione());
        faseToUpdate.setOrdineEsecuzione(updateFaseRequest.getOrdineEsecuzione());
        faseToUpdate.setMateriaPrimaUtilizzata(updateFaseRequest.getMateriaPrimaUtilizzata());

        // Update FonteMateriaPrima if provided
        if (updateFaseRequest.getFonte() != null) {
            FonteMateriaPrima fonte = null;
            if ("ESTERNA".equalsIgnoreCase(updateFaseRequest.getFonte().getTipo())) {
                fonte = new FonteEsterna(updateFaseRequest.getFonte().getNomeFornitore());
            } else if ("INTERNA".equalsIgnoreCase(updateFaseRequest.getFonte().getTipo())) {
                if (updateFaseRequest.getFonte().getProduttoreId() == null) {
                    throw new IllegalArgumentException("produttoreId è obbligatorio per fonte INTERNA");
                }
                Utente utente = utenteService.trovaUtentePerID(updateFaseRequest.getFonte().getProduttoreId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Utente non trovato con ID: " + updateFaseRequest.getFonte().getProduttoreId()));

                if (!(utente instanceof Produttore)) {
                    throw new IllegalArgumentException("L'utente con ID "
                            + updateFaseRequest.getFonte().getProduttoreId() + " non è un Produttore");
                }

                fonte = new FonteInterna((Produttore) utente);
            }
            faseToUpdate.setFonte(fonte);
        }

        // Save the updated process using repository
        processoRepository.save(processo);

        // Map the updated phase to DTO
        FaseLavorazioneDTO faseDTO = processoMapper.toFaseDto(faseToUpdate);

        log.info("Updated phase ID: {} in process ID: {} by trasformatore: {}", faseId, processoId,
                trasformatore.getNome());
        return ResponseEntity.ok(faseDTO);
    }

    /**
     * Delete a phase from a transformation process.
     * Only the owner can delete phases from their processes.
     */
    @DeleteMapping("/{processoId}/fasi/{faseId}")
    @RequiresAccreditation
    @PreAuthorize("hasRole('TRASFORMATORE') and @ownershipValidationService.isProcessOwner(#processoId, authentication.name)")
    public ResponseEntity<Void> deletePhase(
            @PathVariable Long processoId,
            @PathVariable Long faseId,
            Authentication authentication) {

        // Get the authenticated user
        String username = authentication.getName();
        Trasformatore trasformatore = (Trasformatore) utenteService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        // Get the process and verify the phase exists
        Optional<ProcessoTrasformazione> processoOpt = processoTrasformazioneService.getProcessoById(processoId);
        if (processoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ProcessoTrasformazione processo = processoOpt.get();

        // Find the phase to remove
        FaseLavorazione faseToRemove = processo.getFasiLavorazione().stream()
                .filter(f -> f.getId().equals(faseId))
                .findFirst()
                .orElse(null);

        if (faseToRemove == null) {
            log.warn("Phase with ID {} not found in process ID {}", faseId, processoId);
            return ResponseEntity.notFound().build();
        }

        // Dissociate the phase from the process before removing
        faseToRemove.setProcessoTrasformazione(null);

        // Remove the phase from the list
        processo.getFasiLavorazione().remove(faseToRemove);

        // Save the updated process using repository
        processoRepository.save(processo);

        log.info("Deleted phase ID: {} from process ID: {} by trasformatore: {}", faseId, processoId,
                trasformatore.getNome());
        return ResponseEntity.noContent().build();
    }

    /**
     * Get traceability information for a transformation process.
     * This endpoint provides complete traceability data including all processing
     * phases,
     * raw materials sources, and transformation details.
     */
    @GetMapping("/{id}/tracciabilita")
    public ResponseEntity<TraceabilityDTO> getProcessTraceability(@PathVariable Long id) {
        try {
            Optional<ProcessoTrasformazione> processoOpt = processoTrasformazioneService.getProcessoTracciabilita(id);

            if (processoOpt.isEmpty()) {
                log.warn("Transformation process with ID {} not found for traceability", id);
                return ResponseEntity.notFound().build();
            }

            ProcessoTrasformazione processo = processoOpt.get();
            TraceabilityDTO traceabilityDTO = traceabilityMapper.toTraceabilityDTO(processo);

            log.info("Retrieved traceability information for transformation process ID: {}", id);
            return ResponseEntity.ok(traceabilityDTO);

        } catch (IllegalArgumentException e) {
            log.error("Invalid request for traceability of process ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error retrieving traceability for process ID {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Crea o rimuove un collegamento bidirezionale tra processo di trasformazione e
     * prodotto.
     * Solo i trasformatori possono gestire i collegamenti dei loro processi.
     * /api/processi-trasformazione/{id}/collega-prodotto?prodottoId={prodottoId}&rimuovi={true/false}
     */
    @PostMapping("/{id}/collega-prodotto")
    @RequiresAccreditation
    @PreAuthorize("hasRole('TRASFORMATORE') and @ownershipValidationService.isProcessOwner(#id, authentication.name)")
    public ResponseEntity<?> gestisciCollegamentoProcessoProdotto(
            @PathVariable Long id,
            @RequestParam Long prodottoId,
            @RequestParam(defaultValue = "false") boolean rimuovi,
            Authentication authentication) {

        try {
            String username = authentication.getName();

            if (rimuovi) {
                // Rimuovi collegamento
                ProcessoTrasformazione processo = processoTrasformazioneService
                        .scollegaProcessoDaProdotto(id, prodottoId);

                log.info("Removed connection between process ID: {} and product ID: {} by user: {}",
                        id, prodottoId, username);

                return ResponseEntity.ok().body("Collegamento rimosso con successo");

            } else {
                // Crea collegamento
                ProcessoTrasformazione processo = processoTrasformazioneService
                        .collegaProcessoAProdotto(id, prodottoId);

                log.info("Created connection between process ID: {} and product ID: {} by user: {}",
                        id, prodottoId, username);

                return ResponseEntity.ok().body("Processo e prodotto collegati con successo");
            }

        } catch (IllegalArgumentException e) {
            log.error("Invalid request for process-product connection: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error managing process-product connection: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Errore interno del server");
        }
    }

    /**
     * Ottiene informazioni sul collegamento tra un processo e un prodotto.
     * 
     */
    @GetMapping("/{id}/collegamento-prodotto/{prodottoId}")
    public ResponseEntity<?> getCollegamentoProcessoProdotto(
            @PathVariable Long id,
            @PathVariable Long prodottoId) {

        try {
            // Verifica se esiste il collegamento
            var processoOpt = processoTrasformazioneService.getProcessoById(id);

            if (processoOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            ProcessoTrasformazione processo = processoOpt.get();

            // Verifica se sono collegati
            boolean collegati = processo.getProdottoFinale() != null &&
                    processo.getProdottoFinale().getId().equals(prodottoId);

            if (collegati) {
                return ResponseEntity.ok().body("Processo e prodotto sono collegati");
            } else {
                return ResponseEntity.ok().body("Processo e prodotto non sono collegati");
            }

        } catch (Exception e) {
            log.error("Error checking process-product connection: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
