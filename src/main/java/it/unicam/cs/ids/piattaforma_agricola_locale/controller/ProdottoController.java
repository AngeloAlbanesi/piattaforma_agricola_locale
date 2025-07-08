package it.unicam.cs.ids.piattaforma_agricola_locale.controller;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo.*;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.coltivazione.MetodoDiColtivazioneDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.coltivazione.MetodoDiColtivazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.OwnershipValidationService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.ICertificazioneService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IProdottoService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IProduttoreService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IUtenteService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.mapper.MetodoDiColtivazioneMapper;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.mapper.ProdottoMapper;
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
            prodotti = Page.empty();
            List<ProductSummaryDTO> summaryDTOs = searchResults.stream()
                    .map(prodottoMapper::toSummaryDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new PageImpl<>(summaryDTOs, pageable, searchResults.size()));
        } else if (vendorId != null) {
            List<Prodotto> vendorProducts = prodottoService.getProdottiByVenditore(vendorId);
            List<ProductSummaryDTO> summaryDTOs = vendorProducts.stream()
                    .map(prodottoMapper::toSummaryDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new PageImpl<>(summaryDTOs, pageable, vendorProducts.size()));
        } else {
            prodotti = prodottoService.getAllProdotti(pageable);
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

        List<Prodotto> vendorProducts = prodottoService.getProdottiByVenditore(vendorId);
        List<ProductSummaryDTO> summaryDTOs = vendorProducts.stream()
                .map(prodottoMapper::toSummaryDTO)
                .collect(Collectors.toList());

        log.info("Retrieved {} products for vendor ID: {}", summaryDTOs.size(), vendorId);
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
    @PreAuthorize("hasAnyRole('PRODUTTORE', 'TRASFORMATORE', 'DISTRIBUTORE_TIPICITA')")
    public ResponseEntity<ProductDetailDTO> createProduct(
            @Valid @RequestBody CreateProductRequestDTO request,
            Authentication authentication) {

        String email = authentication.getName();
        Venditore venditore = (Venditore) utenteService.getUtenteByEmail(email);

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
    @PreAuthorize("hasAnyRole('PRODUTTORE', 'TRASFORMATORE','DISTRIBUTORE_TIPICITA') and @ownershipValidationService.isProductOwner(#id, authentication.name)")
    @CacheEvict(value = "products", key = "#id + '_owner_' + #authentication.name")
    public ResponseEntity<ProductDetailDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequestDTO request,
            Authentication authentication) {

        String email = authentication.getName();

        return prodottoService.getProdottoById(id)
                .map(prodotto -> {
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
                    // Note: 'attivo' field is not available in Prodotto model

                    // Note: In a real implementation, you'd save the product here
                    // For now, assuming the service handles persistence

                    ProductDetailDTO responseDTO = prodottoMapper.toDetailDTO(prodotto);
                    log.info("Updated product ID: {} by vendor: {}", id, email);
                    return ResponseEntity.ok(responseDTO);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
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

    @PostMapping("/{id}/certificazioni")
    @PreAuthorize("hasAnyRole('PRODUTTORE', 'TRASFORMATORE','DISTRIBUTORE_TIPICITA') and @ownershipValidationService.isProductOwner(#id, authentication.name)")
    public ResponseEntity<CertificazioneDTO> addCertificationToProduct(
            @PathVariable Long id,
            @Valid @RequestBody CertificazioneDTO request,
            Authentication authentication) {

        String email = authentication.getName();

        return prodottoService.getProdottoById(id)
                .map(prodotto -> {
                    Certificazione certificazione = certificazioneService.creaCertificazionePerProdotto(
                            request.getNome(),
                            request.getEnteRilascio(),
                            request.getDataRilascio(),
                            request.getDataScadenza(),
                            prodotto);

                    CertificazioneDTO responseDTO = mapCertificazioneToDTO(certificazione);
                    log.info("Added certification {} to product ID: {} by vendor: {}", request.getNome(), id, email);
                    return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}/certificazioni/{certId}")
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
                .build();
    }

    // =================== CULTIVATION METHODS MANAGEMENT ===================

    /**
     * Crea un nuovo metodo di coltivazione per un prodotto.
     * Solo i produttori possono creare metodi di coltivazione per i loro prodotti
     * coltivati.
     */
    @PostMapping("/{id}/metodi-coltivazione")
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
     */
    @GetMapping("/{id}/metodi-coltivazione")
    public ResponseEntity<MetodoDiColtivazioneDTO> getCultivationMethod(@PathVariable Long id) {
        return prodottoService.getProdottoById(id)
                .map(prodotto -> {
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
}