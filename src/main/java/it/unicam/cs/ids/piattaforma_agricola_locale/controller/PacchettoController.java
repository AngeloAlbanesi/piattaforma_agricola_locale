package it.unicam.cs.ids.piattaforma_agricola_locale.controller;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo.*;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Pacchetto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.Acquistabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DistributoreDiTipicita;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IPacchettoService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IProdottoService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IUtenteService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.mapper.PacchettoMapper;
import jakarta.validation.Valid;
import org.hibernate.Hibernate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pacchetti")
@RequiredArgsConstructor
@Slf4j
public class PacchettoController {

    private final IPacchettoService pacchettoService;
    private final PacchettoMapper pacchettoMapper;
    private final IProdottoService prodottoService;
    private final IUtenteService utenteService;

    @GetMapping
    public ResponseEntity<Page<PacchettoSummaryDTO>> getAllPackages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "nome") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long distributorId) {

        Sort sort = sortDirection.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Pacchetto> pacchetti;

        if (search != null && !search.trim().isEmpty()) {
            List<Pacchetto> searchResults = pacchettoService.searchPacchettiByNome(search);
            List<PacchettoSummaryDTO> summaryDTOs = searchResults.stream()
                    .map(pacchettoMapper::toSummaryDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new PageImpl<>(summaryDTOs, pageable, searchResults.size()));
        } else if (distributorId != null) {
            List<Pacchetto> distributorPackages = pacchettoService.getPacchettiByDistributore(distributorId);
            List<PacchettoSummaryDTO> summaryDTOs = distributorPackages.stream()
                    .map(pacchettoMapper::toSummaryDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new PageImpl<>(summaryDTOs, pageable, distributorPackages.size()));
        } else {
            pacchetti = pacchettoService.getAllPacchetti(pageable);
        }

        Page<PacchettoSummaryDTO> packageSummaries = pacchetti.map(pacchettoMapper::toSummaryDTO);

        log.info("Retrieved {} packages (page {}, size {})", packageSummaries.getTotalElements(), page, size);
        return ResponseEntity.ok(packageSummaries);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacchettoDetailDTO> getPackageById(@PathVariable Long id) {
        return pacchettoService.getPacchettoById(id)
                .map(pacchettoMapper::toDetailDTO)
                .map(packageDetail -> {
                    log.info("Retrieved package details for ID: {}", id);
                    return ResponseEntity.ok(packageDetail);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cercaPacchetti")
    public ResponseEntity<List<PacchettoSummaryDTO>> searchPackages(
            @RequestParam String query) {

        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<Pacchetto> searchResults = pacchettoService.searchPacchettiByNome(query);
        List<PacchettoSummaryDTO> summaryDTOs = searchResults.stream()
                .map(pacchettoMapper::toSummaryDTO)
                .collect(Collectors.toList());

        log.info("Search for '{}' returned {} results", query, summaryDTOs.size());
        return ResponseEntity.ok(summaryDTOs);
    }

    @GetMapping("/distributori/{distributorId}")
    public ResponseEntity<List<PacchettoSummaryDTO>> getPackagesByDistributor(
            @PathVariable Long distributorId) {

        List<Pacchetto> distributorPackages = pacchettoService.getPacchettiByDistributore(distributorId);
        List<PacchettoSummaryDTO> summaryDTOs = distributorPackages.stream()
                .map(pacchettoMapper::toSummaryDTO)
                .collect(Collectors.toList());

        log.info("Retrieved {} packages for distributor ID: {}", summaryDTOs.size(), distributorId);
        return ResponseEntity.ok(summaryDTOs);
    }

    // =================== DISTRIBUTOR CRUD OPERATIONS ===================

    @PostMapping
    @PreAuthorize("hasRole('DISTRIBUTORE_DI_TIPICITA')")
    public ResponseEntity<PacchettoDetailDTO> createPackage(
            @Valid @RequestBody CreatePacchettoRequestDTO request,
            Authentication authentication) {

        String email = authentication.getName();
        DistributoreDiTipicita distributore = (DistributoreDiTipicita) utenteService.getUtenteByEmail(email);

        pacchettoService.creaPacchetto(
                distributore,
                request.getNome(),
                request.getDescrizione(),
                request.getQuantitaDisponibile(),
                request.getPrezzoTotale());

        // Note: Service doesn't return the created package, so we'll fetch by name
        // This is a limitation of the current service interface
        List<Pacchetto> distributorPackages = pacchettoService.getPacchettiByDistributore(distributore.getId());
        Pacchetto nuovoPacchetto = distributorPackages.stream()
                .filter(p -> p.getNome().equals(request.getNome()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Package creation failed"));

        PacchettoDetailDTO responseDTO = pacchettoMapper.toDetailDTO(nuovoPacchetto);
        log.info("Created new package with ID: {} by distributor: {}", nuovoPacchetto.getId(), email);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DISTRIBUTORE_DI_TIPICITA')")
    public ResponseEntity<PacchettoDetailDTO> updatePackage(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePacchettoRequestDTO request,
            Authentication authentication) {

        String email = authentication.getName();
        DistributoreDiTipicita distributore = (DistributoreDiTipicita) utenteService.getUtenteByEmail(email);

        return pacchettoService.getPacchettoById(id)
                .map(pacchetto -> {
                    // Verify ownership
                    if (!pacchetto.getDistributore().getId().equals(distributore.getId())) {
                        log.warn("Unauthorized package update attempt - Package ID: {}, User: {}", id, email);
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).<PacchettoDetailDTO>build();
                    }

                    // Update non-null fields
                    if (request.getNome() != null) {
                        pacchetto.setNome(request.getNome());
                    }
                    if (request.getDescrizione() != null) {
                        pacchetto.setDescrizione(request.getDescrizione());
                    }
                    if (request.getPrezzoTotale() != null) {
                        pacchetto.setPrezzoTotale(request.getPrezzoTotale());
                    }
                    if (request.getQuantitaDisponibile() != null) {
                        pacchetto.setQuantitaDisponibile(request.getQuantitaDisponibile());
                    }
                    // Note: 'attivo' field is not available in Pacchetto model

                    PacchettoDetailDTO responseDTO = pacchettoMapper.toDetailDTO(pacchetto);
                    log.info("Updated package ID: {} by distributor: {}", id, email);
                    return ResponseEntity.ok(responseDTO);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DISTRIBUTORE_DI_TIPICITA')")
    public ResponseEntity<Void> deletePackage(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();
        DistributoreDiTipicita distributore = (DistributoreDiTipicita) utenteService.getUtenteByEmail(email);

        return pacchettoService.getPacchettoById(id)
                .map(pacchetto -> {
                    // Verify ownership
                    if (!pacchetto.getDistributore().getId().equals(distributore.getId())) {
                        log.warn("Unauthorized package deletion attempt - Package ID: {}, User: {}", id, email);
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).<Void>build();
                    }

                    pacchettoService.rimuoviPacchettoCatalogo(distributore, pacchetto);
                    log.info("Deleted package ID: {} by distributor: {}", id, email);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // =================== PACKAGE PRODUCT MANAGEMENT ===================

    @PostMapping("/{id}/prodotti")
    @PreAuthorize("hasRole('DISTRIBUTORE_DI_TIPICITA')")
    public ResponseEntity<PacchettoDetailDTO> addProductToPackage(
            @PathVariable Long id,
            @Valid @RequestBody ElementoPacchettoRequestDTO request,
            Authentication authentication) {

        String email = authentication.getName();
        DistributoreDiTipicita distributore = (DistributoreDiTipicita) utenteService.getUtenteByEmail(email);

        return pacchettoService.getPacchettoById(id)
                .map(pacchetto -> {
                    // Verify ownership
                    if (!pacchetto.getDistributore().getId().equals(distributore.getId())) {
                        log.warn("Unauthorized product addition attempt - Package ID: {}, User: {}", id, email);
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).<PacchettoDetailDTO>build();
                    }

                    return prodottoService.getProdottoById(request.getIdProdotto())
                            .map(prodotto -> {
                                pacchettoService.aggiungiProdottoAlPacchetto(distributore, pacchetto, prodotto);

                                PacchettoDetailDTO responseDTO = pacchettoMapper.toDetailDTO(pacchetto);
                                log.info("Added product ID: {} to package ID: {} by distributor: {}",
                                        request.getIdProdotto(), id, email);
                                return ResponseEntity.ok(responseDTO);
                            })
                            .orElse(ResponseEntity.badRequest().<PacchettoDetailDTO>build());
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}/prodotti/{productId}")
    @PreAuthorize("hasRole('DISTRIBUTORE_DI_TIPICITA')")
    public ResponseEntity<PacchettoDetailDTO> removeProductFromPackage(
            @PathVariable Long id,
            @PathVariable Long productId,
            Authentication authentication) {

        String email = authentication.getName();
        DistributoreDiTipicita distributore = (DistributoreDiTipicita) utenteService.getUtenteByEmail(email);

        return pacchettoService.getPacchettoById(id)
                .map(pacchetto -> {
                    // Verify ownership
                    if (!pacchetto.getDistributore().getId().equals(distributore.getId())) {
                        log.warn("Unauthorized product removal attempt - Package ID: {}, User: {}", id, email);
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).<PacchettoDetailDTO>build();
                    }

                    return prodottoService.getProdottoById(productId)
                            .map(prodotto -> {
                                pacchettoService.rimuoviProdottoDalPacchetto(distributore, pacchetto, prodotto);

                                PacchettoDetailDTO responseDTO = pacchettoMapper.toDetailDTO(pacchetto);
                                log.info("Removed product ID: {} from package ID: {} by distributor: {}",
                                        productId, id, email);
                                return ResponseEntity.ok(responseDTO);
                            })
                            .orElse(ResponseEntity.badRequest().<PacchettoDetailDTO>build());
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/prezzi")
    @PreAuthorize("hasRole('DISTRIBUTORE_DI_TIPICITA')")
    public ResponseEntity<PacchettoDetailDTO> updatePackagePricing(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePacchettoRequestDTO request,
            Authentication authentication) {

        String email = authentication.getName();
        DistributoreDiTipicita distributore = (DistributoreDiTipicita) utenteService.getUtenteByEmail(email);

        return pacchettoService.getPacchettoById(id)
                .map(pacchetto -> {
                    // Verify ownership
                    if (!pacchetto.getDistributore().getId().equals(distributore.getId())) {
                        log.warn("Unauthorized pricing update attempt - Package ID: {}, User: {}", id, email);
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).<PacchettoDetailDTO>build();
                    }

                    if (request.getPrezzoTotale() != null) {
                        pacchetto.setPrezzoTotale(request.getPrezzoTotale());
                    }

                    PacchettoDetailDTO responseDTO = pacchettoMapper.toDetailDTO(pacchetto);
                    log.info("Updated pricing for package ID: {} to {} by distributor: {}",
                            id, request.getPrezzoTotale(), email);
                    return ResponseEntity.ok(responseDTO);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/composizione")
    public ResponseEntity<PackageCompositionDTO> getPackageComposition(@PathVariable Long id) {
        return pacchettoService.getPacchettoById(id)
                .map(pacchetto -> {
                    PackageCompositionDTO composition = PackageCompositionDTO.builder()
                            .id(pacchetto.getId())
                            .nome(pacchetto.getNome())
                            .descrizione(pacchetto.getDescrizione())
                            .prezzoTotale(pacchetto.getPrezzoTotale())
                            .quantitaDisponibile(pacchetto.getQuantitaDisponibile())
                            .elementi(pacchetto.getElementi().stream()
                                    .map(elemento -> ElementoPacchettoDTO.builder()
                                            .tipoElemento(getActualClassName(elemento))
                                            .idElemento(elemento.getId())
                                            .nomeElemento(elemento.getNome())
                                            .descrizioneElemento(elemento.getDescrizione())
                                            .prezzoElemento(elemento.getPrezzo())
                                            .build())
                                    .collect(Collectors.toList()))
                            .prezzoCalcolato(calculatePackagePrice(pacchetto))
                            .disponibilitaCompleta(checkPackageAvailability(pacchetto))
                            .build();

                    log.info("Retrieved composition for package ID: {}", id);
                    return ResponseEntity.ok(composition);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Helper methods
    private Double calculatePackagePrice(Pacchetto pacchetto) {
        // sum the individual prices of all elements
        return pacchetto.getElementi().stream()
                .mapToDouble(elemento -> elemento.getPrezzo())
                .sum();
    }

    /**
     * Ottiene il nome della classe reale, gestendo i proxy di Hibernate
     */
    private String getActualClassName(Acquistabile elemento) {
        return Hibernate.getClass(elemento).getSimpleName().toUpperCase();
    }

    /**
     * Verifica la disponibilita completa di un pacchetto.
     * Un pacchetto e considerato completamente disponibile se:
     * 1. Il pacchetto stesso ha quantita disponibile > 0
     * 2. Tutti gli elementi inclusi sono disponibili e verificati
     * 3. Per i prodotti: quantita > 0 e stato APPROVATO
     * 4. Per gli eventi: posti disponibili e stato valido
     * 5. Per i pacchetti annidati: verifica ricorsiva
     * 
     * @param pacchetto Il pacchetto da verificare
     * @return true se il pacchetto e completamente disponibile, false altrimenti
     */
    private Boolean checkPackageAvailability(Pacchetto pacchetto) {
        if (pacchetto == null) {
            log.warn("Tentativo di verificare disponibilita di un pacchetto null");
            return false;
        }

        // Verifica 1: Il pacchetto stesso deve avere quantita disponibile
        if (pacchetto.getQuantitaDisponibile() <= 0) {
            log.debug("Pacchetto ID {} non disponibile: quantita = {}",
                    pacchetto.getId(), pacchetto.getQuantitaDisponibile());
            return false;
        }

        // Verifica 2: Controllo elementi inclusi
        List<Acquistabile> elementi = pacchetto.getElementi();
        if (elementi == null || elementi.isEmpty()) {
            log.debug("Pacchetto ID {} non ha elementi inclusi", pacchetto.getId());
            return true; // Un pacchetto vuoto e tecnicamente disponibile se ha quantita > 0
        }

        // Verifica 3: Controllo disponibilita di ogni elemento
        for (Acquistabile elemento : elementi) {
            if (!isElementoAvailable(elemento)) {
                log.debug("Pacchetto ID {} non disponibile: elemento {} non disponibile",
                        pacchetto.getId(), elemento.getId());
                return false;
            }
        }

        log.debug("Pacchetto ID {} completamente disponibile", pacchetto.getId());
        return true;
    }

    /**
     * Verifica la disponibilita di un singolo elemento acquistabile.
     * 
     * @param elemento L'elemento da verificare
     * @return true se l'elemento e disponibile, false altrimenti
     */
    private boolean isElementoAvailable(Acquistabile elemento) {
        if (elemento == null) {
            return false;
        }

        // Gestione per tipo di elemento
        if (elemento instanceof Prodotto) {
            return isProdottoAvailable((Prodotto) elemento);
        } else if (elemento instanceof Pacchetto) {
            // Verifica ricorsiva per pacchetti annidati
            return checkPackageAvailability((Pacchetto) elemento);
        } else if (elemento.getClass().getSimpleName().equals("Evento")) {
            // Gestione eventi con cast sicuro
            try {
                return isEventoAvailable(elemento);
            } catch (Exception e) {
                log.warn("Errore nella verifica disponibilita evento ID {}: {}",
                        elemento.getId(), e.getMessage());
                return false;
            }
        } else {
            // Tipo sconosciuto - per sicurezza consideriamo non disponibile
            log.warn("Tipo di elemento sconosciuto: {}", elemento.getClass().getSimpleName());
            return false;
        }
    }

    /**
     * Verifica la disponibilita di un prodotto.
     * 
     * @param prodotto Il prodotto da verificare
     * @return true se il prodotto e disponibile, false altrimenti
     */
    private boolean isProdottoAvailable(Prodotto prodotto) {
        if (prodotto == null) {
            return false;
        }

        // Verifica quantita disponibile
        if (prodotto.getQuantitaDisponibile() <= 0) {
            log.debug("Prodotto ID {} non disponibile: quantita = {}",
                    prodotto.getId(), prodotto.getQuantitaDisponibile());
            return false;
        }

        // Verifica stato di verifica - solo prodotti approvati sono vendibili
        if (prodotto.getStatoVerifica() == null ||
                !prodotto.getStatoVerifica().equals(
                        it.unicam.cs.ids.piattaforma_agricola_locale.model.common.StatoVerificaValori.APPROVATO)) {
            log.debug("Prodotto ID {} non disponibile: stato verifica = {}",
                    prodotto.getId(), prodotto.getStatoVerifica());
            return false;
        }

        return true;
    }

    /**
     * Verifica la disponibilita di un evento.
     * Controlla posti disponibili e stato dell'evento.
     * 
     * @param evento L'evento da verificare (come Acquistabile)
     * @return true se l'evento e disponibile, false altrimenti
     */
    private boolean isEventoAvailable(Acquistabile evento) {
        if (evento == null) {
            return false;
        }

        try {
            // Utilizziamo reflection per accedere ai metodi specifici dell'evento
            // senza fare cast diretto che potrebbe fallire
            Class<?> eventoClass = evento.getClass();

            // Verifica se ha posti disponibili
            if (eventoClass.getSimpleName().equals("Evento")) {
                try {
                    java.lang.reflect.Method getPostiDisponibili = eventoClass.getMethod("getPostiDisponibili");
                    Integer postiDisponibili = (Integer) getPostiDisponibili.invoke(evento);

                    if (postiDisponibili == null || postiDisponibili <= 0) {
                        log.debug("Evento ID {} non disponibile: posti disponibili = {}",
                                evento.getId(), postiDisponibili);
                        return false;
                    }

                    // Verifica stato evento
                    java.lang.reflect.Method getStatoEvento = eventoClass.getMethod("getStatoEvento");
                    Object statoEvento = getStatoEvento.invoke(evento);

                    if (statoEvento == null) {
                        log.debug("Evento ID {} non disponibile: stato evento null", evento.getId());
                        return false;
                    }

                    // Verifica che lo stato sia IN_PROGRAMMA o IN_CORSO
                    String statoString = statoEvento.toString();
                    if (!statoString.equals("IN_PROGRAMMA") && !statoString.equals("IN_CORSO")) {
                        log.debug("Evento ID {} non disponibile: stato = {}",
                                evento.getId(), statoString);
                        return false;
                    }

                    // Verifica data evento (opzionale - per eventi futuri)
                    try {
                        java.lang.reflect.Method getDataOraInizio = eventoClass.getMethod("getDataOraInizio");
                        java.util.Date dataInizio = (java.util.Date) getDataOraInizio.invoke(evento);

                        if (dataInizio != null && dataInizio.before(new java.util.Date())) {
                            // Evento gia iniziato - verifichiamo se e ancora in corso
                            java.lang.reflect.Method getDataOraFine = eventoClass.getMethod("getDataOraFine");
                            java.util.Date dataFine = (java.util.Date) getDataOraFine.invoke(evento);

                            if (dataFine != null && dataFine.before(new java.util.Date())) {
                                log.debug("Evento ID {} non disponibile: evento concluso", evento.getId());
                                return false;
                            }
                        }
                    } catch (Exception e) {
                        // Se non riusciamo a verificare le date, continuiamo
                        log.debug("Impossibile verificare date per evento ID {}: {}",
                                evento.getId(), e.getMessage());
                    }

                } catch (Exception e) {
                    log.warn("Errore nella verifica dettagliata evento ID {}: {}",
                            evento.getId(), e.getMessage());
                    return false;
                }
            }

            log.debug("Evento ID {} disponibile", evento.getId());
            return true;

        } catch (Exception e) {
            log.error("Errore critico nella verifica evento ID {}: {}",
                    evento.getId(), e.getMessage());
            return false;
        }
    }
}