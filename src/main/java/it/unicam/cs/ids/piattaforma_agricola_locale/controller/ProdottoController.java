package it.unicam.cs.ids.piattaforma_agricola_locale.controller;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo.*;
import it.unicam.cs.ids.piattaforma_agricola_locale.exception.ResourceOwnershipException;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.coltivazione.MetodoDiColtivazioneDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.social.ShareRequestDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.social.ShareResponseDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.processo.TraceabilityDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.TipoOrigineProdotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.coltivazione.MetodoDiColtivazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.StatoVerificaValori;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.ProcessoTrasformazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.OwnershipValidationService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.ICertificazioneService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IProdottoService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IProduttoreService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IProcessoTrasformazioneService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IUtenteService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.mapper.MetodoDiColtivazioneMapper;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.mapper.ProdottoMapper;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.mapper.TraceabilityMapper;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/prodotti")
@RequiredArgsConstructor
@Slf4j
public class ProdottoController {

    private final IProdottoService prodottoService;
    private final ProdottoMapper prodottoMapper;
    private final ICertificazioneService certificazioneService;
    private final IUtenteService utenteService;
    private final OwnershipValidationService ownershipValidationService;
    private final IProduttoreService produttoreService;
    private final MetodoDiColtivazioneMapper metodiColtivazioneMapper;
    private final IProcessoTrasformazioneService processoTrasformazioneService;
    private final TraceabilityMapper traceabilityMapper;

    @GetMapping
    public ResponseEntity<Page<ProductSummaryDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "nome") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long vendorId) {

        Sort sort = sortDirection.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Prodotto> prodotti;

        if (search != null && !search.trim().isEmpty()) {
            List<Prodotto> searchResults = prodottoService.searchProdottiByNome(search);
            // Filter only approved products from search results
            List<Prodotto> approvedSearchResults = searchResults.stream()
                    .filter(prodotto -> prodotto.getStatoVerifica() == StatoVerificaValori.APPROVATO)
                    .collect(Collectors.toList());

            List<ProductSummaryDTO> summaryDTOs = approvedSearchResults.stream()
                    .map(prodottoMapper::toSummaryDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new PageImpl<>(summaryDTOs, pageable, approvedSearchResults.size()));
        } else if (vendorId != null) {
            prodotti = prodottoService.getApprovedProdottiByVenditore(vendorId, pageable);
        } else {
            prodotti = prodottoService.getApprovedProdotti(pageable);
        }

        Page<ProductSummaryDTO> productSummaries = prodotti.map(prodottoMapper::toSummaryDTO);

        log.info("Retrieved {} products (page {}, size {})", productSummaries.getTotalElements(), page, size);
        return ResponseEntity.ok(productSummaries);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailDTO> getProductById(@PathVariable Long id) {
        return prodottoService.getProdottoById(id)
                .map(prodottoMapper::toDetailDTO)
                .map(productDetail -> {
                    log.info("Retrieved product details for ID: {}", id);
                    return ResponseEntity.ok(productDetail);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cercaProdotti")
    public ResponseEntity<List<ProductSummaryDTO>> searchProducts(
            @RequestParam String query) {

        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<Prodotto> searchResults = prodottoService.searchProdottiByNome(query);
        List<ProductSummaryDTO> summaryDTOs = searchResults.stream()
                .map(prodottoMapper::toSummaryDTO)
                .collect(Collectors.toList());

        log.info("Search for '{}' returned {} results", query, summaryDTOs.size());
        return ResponseEntity.ok(summaryDTOs);
    }

    @GetMapping("/venditori/{vendorId}")
    public ResponseEntity<List<ProductSummaryDTO>> getProductsByVendor(
            @PathVariable Long vendorId) {

        // Use pageable with default values to get approved products only
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        Page<Prodotto> approvedVendorProducts = prodottoService.getApprovedProdottiByVenditore(vendorId, pageable);

        List<ProductSummaryDTO> summaryDTOs = approvedVendorProducts.getContent().stream()
                .map(prodottoMapper::toSummaryDTO)
                .collect(Collectors.toList());

        log.info("Retrieved {} approved products for vendor ID: {}", summaryDTOs.size(), vendorId);
        return ResponseEntity.ok(summaryDTOs);
    }

    // =================== VENDOR CRUD OPERATIONS ===================

    @GetMapping("/miei-prodotti")
    @PreAuthorize("hasAnyRole('PRODUTTORE', 'TRASFORMATORE', 'DISTRIBUTORE_TIPICITA')")
    public ResponseEntity<List<ProductSummaryDTO>> getMyProducts(Authentication authentication) {
        String email = authentication.getName();
        Venditore venditore = (Venditore) utenteService.getUtenteByEmail(email);

        List<Prodotto> myProducts = prodottoService.getProdottiByVenditore(venditore.getId());
        List<ProductSummaryDTO> summaryDTOs = myProducts.stream()
                .map(prodottoMapper::toSummaryDTO)
                .collect(Collectors.toList());

        log.info("Retrieved {} products for authenticated vendor: {}", summaryDTOs.size(), email);
        return ResponseEntity.ok(summaryDTOs);
    }

    @PostMapping
    @RequiresAccreditation
    @PreAuthorize("hasAnyRole('PRODUTTORE', 'TRASFORMATORE', 'DISTRIBUTORE_TIPICITA')")
    public ResponseEntity<ProductDetailDTO> createProduct(
            @Valid @RequestBody CreateProductRequestDTO request,
            Authentication authentication) {

        String email = authentication.getName();
        Venditore venditore = (Venditore) utenteService.getUtenteByEmail(email);

        // Valida il tipo di origine prima di creare il prodotto
        if (request.getTipoOrigine() != null) {
            prodottoService.validaTipoOrigineProdotto(request.getTipoOrigine(), venditore);
        }

        Prodotto nuovoProdotto = prodottoService.creaProdotto(
                request.getNome(),
                request.getDescrizione(),
                request.getPrezzo(),
                request.getQuantitaDisponibile(),
                venditore);

        // Imposta il tipo di origine dal DTO della richiesta
        if (request.getTipoOrigine() != null) {
            nuovoProdotto.setTipoOrigine(request.getTipoOrigine());
        }

        // Imposta gli ID opzionali se forniti
        if (request.getIdProcessoTrasformazioneOriginario() != null) {
            nuovoProdotto.setIdProcessoTrasformazioneOriginario(request.getIdProcessoTrasformazioneOriginario());
        }

        if (request.getIdMetodoDiColtivazione() != null) {
            nuovoProdotto.setIdMetodoDiColtivazione(request.getIdMetodoDiColtivazione());
        }

        // Salva le modifiche al prodotto
        nuovoProdotto = prodottoService.salvaProdotto(nuovoProdotto);

        ProductDetailDTO responseDTO = prodottoMapper.toDetailDTO(nuovoProdotto);
        log.info("Created new product with ID: {} by vendor: {}", nuovoProdotto.getId(), email);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PutMapping("/{id}")
    @RequiresAccreditation
    @PreAuthorize("hasAnyRole('PRODUTTORE', 'TRASFORMATORE','DISTRIBUTORE_TIPICITA') and @ownershipValidationService.isProductOwner(#id, authentication.name)")
    @CacheEvict(value = "products", key = "#id + '_owner_' + #authentication.name")
    public ResponseEntity<ProductDetailDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequestDTO request,
            Authentication authentication) {

        String email = authentication.getName();
        log.info("Attempting to update product ID: {} by vendor: {}", id, email);

        return prodottoService.getProdottoById(id)
                .map(prodotto -> {
                    // Verify ownership again (defense in depth)
                    if (!prodotto.getVenditore().getEmail().equals(email)) {
                        log.warn("Ownership validation failed - Product ID: {}, Owner: {}, Requesting User: {}",
                                id, prodotto.getVenditore().getEmail(), email);
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).<ProductDetailDTO>build();
                    }

                    // Update non-null fields
                    if (request.getNome() != null) {
                        prodotto.setNome(request.getNome());
                    }
                    if (request.getDescrizione() != null) {
                        prodotto.setDescrizione(request.getDescrizione());
                    }
                    if (request.getPrezzo() != null) {
                        prodotto.setPrezzo(request.getPrezzo());
                    }
                    if (request.getQuantitaDisponibile() != null) {
                        prodotto.setQuantitaDisponibile(request.getQuantitaDisponibile());
                    }

                    // Save the updated product to persist changes
                    Prodotto prodottoSalvato = prodottoService.salvaProdotto(prodotto);

                    ProductDetailDTO responseDTO = prodottoMapper.toDetailDTO(prodottoSalvato);
                    log.info("Successfully updated and saved product ID: {} by vendor: {}", id, email);
                    return ResponseEntity.ok(responseDTO);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @RequiresAccreditation
    @PreAuthorize("hasAnyRole('PRODUTTORE', 'TRASFORMATORE', 'DISTRIBUTORE_TIPICITA') and @ownershipValidationService.isProductOwner(#id, authentication.name)")
    @CacheEvict(value = "products", key = "#id + '_owner_' + #authentication.name")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();
        Venditore venditore = (Venditore) utenteService.getUtenteByEmail(email);

        return prodottoService.getProdottoById(id)
                .map(prodotto -> {
                    prodottoService.rimuoviProdottoCatalogo(venditore, prodotto);
                    log.info("Deleted product ID: {} by vendor: {}", id, email);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/quantita")
    @RequiresAccreditation
    @PreAuthorize("hasAnyRole('PRODUTTORE', 'TRASFORMATORE','DISTRIBUTORE_TIPICITA') and @ownershipValidationService.isProductOwner(#id, authentication.name)")
    @CacheEvict(value = "products", key = "#id + '_owner_' + #authentication.name")
    public ResponseEntity<ProductDetailDTO> updateProductQuantity(
            @PathVariable Long id,
            @Valid @RequestBody ProductQuantityUpdateDTO request,
            Authentication authentication) {

        String email = authentication.getName();
        Venditore venditore = (Venditore) utenteService.getUtenteByEmail(email);

        return prodottoService.getProdottoById(id)
                .map(prodotto -> {
                    prodottoService.aggiornaQuantitaProdotto(venditore, prodotto, request.getQuantitaDisponibile());

                    ProductDetailDTO responseDTO = prodottoMapper.toDetailDTO(prodotto);
                    log.info("Updated quantity for product ID: {} to {} by vendor: {}", id,
                            request.getQuantitaDisponibile(), email);
                    return ResponseEntity.ok(responseDTO);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // =================== CERTIFICATION MANAGEMENT ===================

    /**
     * 
     */
    @PostMapping("/{id}/certificazioni")
    @RequiresAccreditation
    @PreAuthorize("hasAnyRole('PRODUTTORE', 'TRASFORMATORE','DISTRIBUTORE_TIPICITA')")
    public ResponseEntity<CertificazioneDTO> addCertificationToProduct(
            @PathVariable Long id,
            @Valid @RequestBody CreateCertificazioneRequestDTO request,
            Authentication authentication) {

        String email = authentication.getName();
        log.info("Vendor {} requesting to add certification to product ID: {}", email, id);

        try {
            // Precondizione 1: Venditore autenticato (già verificata da @PreAuthorize)
            // Precondizione 2: Verifica che il prodotto esista nel catalogo del venditore
            ownershipValidationService.validateProductOwnership(id, email);

            return prodottoService.getProdottoById(id)
                    .map(prodotto -> {
                        log.debug("Creating certification for product: {} by vendor: {}", prodotto.getNome(), email);

                        // Step 4: Il sistema valida i dati ricevuti e registra la nuova certificazione
                        Certificazione certificazione = certificazioneService.creaCertificazionePerProdotto(
                                request.getNomeCertificazione(),
                                request.getEnteRilascio(),
                                request.getDataRilascio(),
                                request.getDataScadenza(),
                                prodotto);

                        if (certificazione == null) {
                            log.error("Failed to create certification for product ID: {} by vendor: {}", id, email);
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).<CertificazioneDTO>build();
                        }

                        // Post-condizione 1: Certificazione creata e salvata permanentemente ✅
                        // Post-condizione 2: Certificazione correttamente associata al prodotto ✅
                        CertificazioneDTO responseDTO = mapCertificazioneToDTO(certificazione);
                        log.info(
                                "Successfully added certification '{}' (ID: {}) to product '{}' (ID: {}) by vendor: {}",
                                request.getNomeCertificazione(), certificazione.getId(), prodotto.getNome(), id, email);

                        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
                    })
                    .orElse(ResponseEntity.notFound().build());

        } catch (ResourceOwnershipException e) {
            // Precondizione 2 non soddisfatta: il prodotto non appartiene al venditore
            log.warn("Product ownership validation failed - Product ID: {}, Vendor: {}, Error: {}",
                    id, email, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            log.error("Unexpected error adding certification to product ID: {} by vendor: {} - {}",
                    id, email, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}/certificazioni/{certId}")
    @RequiresAccreditation
    @PreAuthorize("hasAnyRole('PRODUTTORE', 'TRASFORMATORE','DISTRIBUTORE_TIPICITA') and @ownershipValidationService.isProductOwner(#id, authentication.name)")
    public ResponseEntity<Void> removeCertificationFromProduct(
            @PathVariable Long id,
            @PathVariable Long certId,
            Authentication authentication) {

        String email = authentication.getName();

        return prodottoService.getProdottoById(id)
                .map(prodotto -> {
                    prodottoService.rimuoviCertificazioneDaProdotto(prodotto, certId);
                    log.info("Removed certification ID: {} from product ID: {} by vendor: {}", certId, id, email);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/certificazioni")
    public ResponseEntity<List<CertificazioneDTO>> getProductCertifications(@PathVariable Long id) {
        return prodottoService.getProdottoById(id)
                .map(prodotto -> {
                    List<Certificazione> certificazioni = prodottoService.getCertificazioniDelProdotto(prodotto);
                    List<CertificazioneDTO> dtos = certificazioni.stream()
                            .map(this::mapCertificazioneToDTO)
                            .collect(Collectors.toList());

                    log.info("Retrieved {} certifications for product ID: {}", dtos.size(), id);
                    return ResponseEntity.ok(dtos);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Helper method to map Certificazione to DTO
    private CertificazioneDTO mapCertificazioneToDTO(Certificazione certificazione) {
        return CertificazioneDTO.builder()
                .idCertificazione(certificazione.getId())
                .nomeCertificazione(certificazione.getNomeCertificazione())
                .enteRilascio(certificazione.getEnteRilascio())
                .dataRilascio(certificazione.getDataRilascio())
                .dataScadenza(certificazione.getDataScadenza())
                .idProdottoAssociato(certificazione.getIdProdottoAssociato())
                .idAziendaAssociata(certificazione.getIdAziendaAssociata())
                .build();
    }

    // =================== CULTIVATION METHODS MANAGEMENT ===================

    /**
     * Crea un nuovo metodo di coltivazione per un prodotto.
     * Solo i produttori possono creare metodi di coltivazione per i loro prodotti
     * coltivati.
     */
    @PostMapping("/{id}/metodi-coltivazione")
    @RequiresAccreditation
    @PreAuthorize("hasRole('PRODUTTORE') and @ownershipValidationService.isProductOwner(#id, authentication.name)")
    public ResponseEntity<MetodoDiColtivazioneDTO> createCultivationMethod(
            @PathVariable Long id,
            @Valid @RequestBody MetodoDiColtivazioneDTO request,
            Authentication authentication) {

        String email = authentication.getName();
        Venditore venditore = (Venditore) utenteService.getUtenteByEmail(email);

        return prodottoService.getProdottoById(id)
                .map(prodotto -> {
                    try {
                        // Verifica che il prodotto sia coltivato
                        if (!prodotto.isColtivato()) {
                            log.warn("Attempt to add cultivation method to non-cultivated product ID: {} by: {}", id,
                                    email);
                            return ResponseEntity.badRequest().<MetodoDiColtivazioneDTO>build();
                        }

                        // Converte DTO in entity
                        MetodoDiColtivazione metodoDiColtivazione = metodiColtivazioneMapper.toEntityIgnoreId(request);

                        // Crea il metodo di coltivazione usando il service
                        MetodoDiColtivazione nuovoMetodo = produttoreService.creaMetodoDiColtivazione(
                                venditore.getId(), id, metodoDiColtivazione);

                        // Converte l'entity creata in DTO per la risposta
                        MetodoDiColtivazioneDTO responseDTO = metodiColtivazioneMapper.toDTO(nuovoMetodo);

                        log.info("Created cultivation method '{}' for product ID: {} by vendor: {}",
                                request.getNome(), id, email);
                        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

                    } catch (IllegalArgumentException e) {
                        log.error("Error creating cultivation method for product ID: {} by vendor: {} - {}",
                                id, email, e.getMessage());
                        return ResponseEntity.badRequest().<MetodoDiColtivazioneDTO>build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Recupera il metodo di coltivazione associato a un prodotto.
     * Endpoint pubblico accessibile a tutti gli utenti.
     */
    @GetMapping("/{id}/metodi-coltivazione")
    public ResponseEntity<MetodoDiColtivazioneDTO> getCultivationMethod(@PathVariable Long id) {
        return prodottoService.getProdottoById(id)
                .map(prodotto -> {
                    // Verifica che il prodotto sia approvato prima di mostrare i metodi di
                    // coltivazione
                    if (prodotto.getStatoVerifica() != StatoVerificaValori.APPROVATO) {
                        log.warn("Attempt to access cultivation method for non-approved product ID: {}", id);
                        return ResponseEntity.notFound().<MetodoDiColtivazioneDTO>build();
                    }

                    MetodoDiColtivazione metodo = produttoreService.getMetodoDiColtivazioneByProdotto(id);
                    if (metodo != null) {
                        MetodoDiColtivazioneDTO dto = metodiColtivazioneMapper.toDTO(metodo);
                        log.info("Retrieved cultivation method for product ID: {}", id);
                        return ResponseEntity.ok(dto);
                    } else {
                        log.info("No cultivation method found for product ID: {}", id);
                        return ResponseEntity.notFound().<MetodoDiColtivazioneDTO>build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Aggiorna il metodo di coltivazione di un prodotto.
     * Solo il produttore proprietario può aggiornare i metodi di coltivazione.
     */
    @PutMapping("/{id}/metodi-coltivazione")
    @RequiresAccreditation
    @PreAuthorize("hasRole('PRODUTTORE') and @ownershipValidationService.isProductOwner(#id, authentication.name)")
    public ResponseEntity<MetodoDiColtivazioneDTO> updateCultivationMethod(
            @PathVariable Long id,
            @Valid @RequestBody MetodoDiColtivazioneDTO request,
            Authentication authentication) {

        String email = authentication.getName();
        Venditore venditore = (Venditore) utenteService.getUtenteByEmail(email);

        return prodottoService.getProdottoById(id)
                .map(prodotto -> {
                    try {
                        // Verifica che il prodotto sia coltivato
                        if (!prodotto.isColtivato()) {
                            log.warn("Attempt to update cultivation method for non-cultivated product ID: {} by: {}",
                                    id, email);
                            return ResponseEntity.badRequest().<MetodoDiColtivazioneDTO>build();
                        }

                        // Verifica che esista già un metodo di coltivazione
                        if (prodotto.getIdMetodoDiColtivazione() == null) {
                            log.warn("No cultivation method exists for product ID: {} to update by: {}", id, email);
                            return ResponseEntity.notFound().<MetodoDiColtivazioneDTO>build();
                        }

                        // Converte DTO in entity
                        MetodoDiColtivazione metodoDiColtivazione = metodiColtivazioneMapper.toEntityIgnoreId(request);

                        // Aggiorna il metodo di coltivazione usando il service
                        MetodoDiColtivazione metodoAggiornato = produttoreService.aggiornaMetodoDiColtivazione(
                                venditore.getId(), id, metodoDiColtivazione);

                        // Converte l'entity aggiornata in DTO per la risposta
                        MetodoDiColtivazioneDTO responseDTO = metodiColtivazioneMapper.toDTO(metodoAggiornato);

                        log.info("Updated cultivation method '{}' for product ID: {} by vendor: {}",
                                request.getNome(), id, email);
                        return ResponseEntity.ok(responseDTO);

                    } catch (IllegalArgumentException e) {
                        log.error("Error updating cultivation method for product ID: {} by vendor: {} - {}",
                                id, email, e.getMessage());
                        return ResponseEntity.badRequest().<MetodoDiColtivazioneDTO>build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Elimina il metodo di coltivazione associato a un prodotto.
     * Solo il produttore proprietario può eliminare i metodi di coltivazione.
     */
    @DeleteMapping("/{id}/metodi-coltivazione")
    @RequiresAccreditation
    @PreAuthorize("hasRole('PRODUTTORE') and @ownershipValidationService.isProductOwner(#id, authentication.name)")
    public ResponseEntity<Void> deleteCultivationMethod(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();
        Venditore venditore = (Venditore) utenteService.getUtenteByEmail(email);

        return prodottoService.getProdottoById(id)
                .map(prodotto -> {
                    try {
                        // Verifica che esista un metodo di coltivazione da eliminare
                        if (prodotto.getIdMetodoDiColtivazione() == null) {
                            log.warn("No cultivation method exists for product ID: {} to delete by: {}", id, email);
                            return ResponseEntity.notFound().<Void>build();
                        }

                        // Elimina il metodo di coltivazione usando il service
                        produttoreService.eliminaMetodoDiColtivazione(venditore.getId(), id);

                        log.info("Deleted cultivation method for product ID: {} by vendor: {}", id, email);
                        return ResponseEntity.noContent().<Void>build();

                    } catch (IllegalArgumentException e) {
                        log.error("Error deleting cultivation method for product ID: {} by vendor: {} - {}",
                                id, email, e.getMessage());
                        return ResponseEntity.badRequest().<Void>build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/share")
    @PreAuthorize("hasRole('ACQUIRENTE')") // Assicuriamoci che solo un acquirente possa condividere
    public ResponseEntity<?> condividiSuSocial(
            @PathVariable Long id,
            @RequestBody ShareRequestDTO request,
            Authentication authentication // Possiamo anche prendere il nickname dall'utente loggato
    ) {
        // Idea per un miglioramento: invece di passare il nickname, potremmo prenderlo
        // dall'utente autenticato
        // String nicknameAutenticato = authentication.getName();
        // e passarlo al service. Ma per ora, seguiamo la tua idea iniziale.

        Optional<ShareResponseDTO> responseOpt = prodottoService.condividiProdotto(id, request);

        if (responseOpt.isPresent()) {
            return ResponseEntity.ok(responseOpt.get());
        } else {
            // Se il prodotto con quell'ID non esiste
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Prodotto con ID " + id + " non trovato.");
        }
    }

    /**
     * Endpoint per ottenere la tracciabilità di un prodotto trasformato.
     * Verifica che il prodotto sia di tipo TRASFORMATO e restituisce
     * la tracciabilità completa del processo di trasformazione associato.
     *
     * @param id L'ID del prodotto di cui ottenere la tracciabilità
     * @return ResponseEntity con TraceabilityDTO o messaggio di errore
     */
    @GetMapping("/{id}/tracciabilita")
    public ResponseEntity<?> getProductTraceability(@PathVariable Long id) {
        try {
            log.info("Richiesta tracciabilità per prodotto con ID: {}", id);
            
            // Verifica che il prodotto esista
            Optional<Prodotto> prodottoOpt = prodottoService.getProdottoById(id);
            if (prodottoOpt.isEmpty()) {
                log.warn("Prodotto con ID {} non trovato", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Prodotto con ID " + id + " non trovato");
            }
            
            Prodotto prodotto = prodottoOpt.get();
            
            // Verifica che il prodotto sia di tipo TRASFORMATO
            if (prodotto.getTipoOrigine() != TipoOrigineProdotto.TRASFORMATO) {
                log.warn("Prodotto con ID {} non è di tipo TRASFORMATO. Tipo attuale: {}", 
                        id, prodotto.getTipoOrigine());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Il prodotto con ID " + id + " non è di tipo TRASFORMATO. " +
                              "La tracciabilità è disponibile solo per prodotti trasformati.");
            }
            
            // Verifica che il prodotto abbia un processo di trasformazione associato
            Long processoId = prodotto.getIdProcessoTrasformazioneOriginario();
            if (processoId == null) {
                log.warn("Prodotto trasformato con ID {} non ha un processo di trasformazione associato", id);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Errore: il prodotto trasformato non ha un processo di trasformazione associato");
            }
            
            // Recupera il processo di trasformazione con tutte le informazioni necessarie
            Optional<ProcessoTrasformazione> processoOpt = processoTrasformazioneService.getProcessoTracciabilita(processoId);
            if (processoOpt.isEmpty()) {
                log.warn("Processo di trasformazione con ID {} non trovato per prodotto {}", processoId, id);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Errore: processo di trasformazione associato non trovato");
            }
            
            ProcessoTrasformazione processo = processoOpt.get();
            
            // Converte il processo in DTO di tracciabilità
            TraceabilityDTO traceabilityDTO = traceabilityMapper.toTraceabilityDTO(processo);
            
            log.info("Tracciabilità recuperata con successo per prodotto ID: {}", id);
            return ResponseEntity.ok(traceabilityDTO);
            
        } catch (Exception e) {
            log.error("Errore durante il recupero della tracciabilità per prodotto ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore interno del server durante il recupero della tracciabilità");
        }
    }
}