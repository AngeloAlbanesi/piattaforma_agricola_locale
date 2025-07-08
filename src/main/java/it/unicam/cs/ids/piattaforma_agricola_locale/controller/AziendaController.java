package it.unicam.cs.ids.piattaforma_agricola_locale.controller;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo.CertificazioneDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo.ProductSummaryDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.exception.ResourceOwnershipException;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.ICertificazioneService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IProdottoService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IUtenteService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IVenditoreService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.mapper.CertificazioneMapper;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.mapper.ProdottoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for company management.
 * Provides endpoints for retrieving and updating company data.
 */
@RestController
@RequestMapping("/api/azienda")
@RequiredArgsConstructor
@Slf4j
public class AziendaController {

    private final IVenditoreService venditoreService;
    private final IProdottoService prodottoService;
    private final ICertificazioneService certificazioneService;
    private final IUtenteService utenteService;
    private final ProdottoMapper prodottoMapper;
    private final CertificazioneMapper certificazioneMapper;

    /**
     * Get company data by ID.
     *
     * @param id The company ID
     * @return The company data
     */
    @GetMapping("/{id}")
    public ResponseEntity<DatiAzienda> getCompanyData(@PathVariable Long id) {
        Optional<Venditore> venditoreOpt = venditoreService.getVenditoreById(id);

        if (venditoreOpt.isEmpty()) {
            log.warn("Company with ID {} not found", id);
            return ResponseEntity.notFound().build();
        }

        DatiAzienda datiAzienda = venditoreOpt.get().getDatiAzienda();
        if (datiAzienda == null) {
            log.warn("Company data for vendor ID {} not found", id);
            return ResponseEntity.notFound().build();
        }

        log.info("Retrieved company data for ID: {}", id);
        return ResponseEntity.ok(datiAzienda);
    }

    /**
     * Update company data.
     * Only the company owner can update the data.
     *
     * @param id             The company ID
     * @param datiAzienda    The updated company data
     * @param authentication The authenticated user
     * @return The updated company data
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PRODUTTORE', 'TRASFORMATORE', 'DISTRIBUTORE_DI_TIPICITA')")
    public ResponseEntity<DatiAzienda> updateCompanyData(
            @PathVariable Long id,
            @Valid @RequestBody DatiAzienda datiAzienda,
            Authentication authentication) {

        // Get the authenticated user
        String username = authentication.getName();
        Venditore venditore = (Venditore) utenteService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        // Check if the authenticated user is the owner of the company
        if (!venditore.getIdUtente().equals(id)) {
            log.warn("User {} attempted to update company data for ID {}", username, id);
            throw new ResourceOwnershipException("Non sei autorizzato a modificare i dati di questa azienda");
        }

        // Update the company data
        venditore.setDatiAzienda(datiAzienda);
        venditoreService.updateVenditore(venditore);

        log.info("Updated company data for ID: {}", id);
        return ResponseEntity.ok(datiAzienda);
    }

    /**
     * Add a certification to a company.
     *
     * @param id                The company ID
     * @param certificazioneDTO The certification to add
     * @param authentication    The authenticated user
     * @return The added certification
     */
    @PostMapping("/{id}/certificazioni")
    @PreAuthorize("hasAnyRole('PRODUTTORE', 'TRASFORMATORE', 'DISTRIBUTORE_DI_TIPICITA')")
    public ResponseEntity<CertificazioneDTO> addCompanyCertification(
            @PathVariable Long id,
            @Valid @RequestBody CertificazioneDTO certificazioneDTO,
            Authentication authentication) {

        // Get the authenticated user
        String username = authentication.getName();
        Venditore venditore = (Venditore) utenteService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        // Check if the authenticated user is the owner of the company
        if (!venditore.getIdUtente().equals(id)) {
            log.warn("User {} attempted to add certification to company ID {}", username, id);
            throw new ResourceOwnershipException("Non sei autorizzato a modificare i dati di questa azienda");
        }

        // Create the certification using the service
        Certificazione savedCertificazione = certificazioneService.creaCertificazionePerAzienda(
                certificazioneDTO.getNomeCertificazione(),
                certificazioneDTO.getEnteRilascio(),
                certificazioneDTO.getDataRilascio(),
                certificazioneDTO.getDataScadenza(),
                venditore.getDatiAzienda());

        log.info("Added certification to company ID: {}", id);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(certificazioneMapper.toDTO(savedCertificazione));
    }

    /**
     * Remove a certification from a company.
     *
     * @param id             The company ID
     * @param certId         The certification ID
     * @param authentication The authenticated user
     * @return No content
     */
    @DeleteMapping("/{id}/certificazioni/{certId}")
    @PreAuthorize("hasAnyRole('PRODUTTORE', 'TRASFORMATORE', 'DISTRIBUTORE_DI_TIPICITA')")
    public ResponseEntity<Void> removeCompanyCertification(
            @PathVariable Long id,
            @PathVariable Long certId,
            Authentication authentication) {

        // Get the authenticated user
        String username = authentication.getName();
        Venditore venditore = (Venditore) utenteService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        // Check if the authenticated user is the owner of the company
        if (!venditore.getIdUtente().equals(id)) {
            log.warn("User {} attempted to remove certification from company ID {}", username, id);
            throw new ResourceOwnershipException("Non sei autorizzato a modificare i dati di questa azienda");
        }

        // Remove the certification
        certificazioneService.rimuoviCertificazione(certId, venditore.getDatiAzienda());

        log.info("Removed certification {} from company ID: {}", certId, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all certifications for a company.
     *
     * @param id The company ID
     * @return The list of certifications
     */
    @GetMapping("/{id}/certificazioni")
    public ResponseEntity<List<CertificazioneDTO>> getCompanyCertifications(@PathVariable Long id) {
        Optional<Venditore> venditoreOpt = venditoreService.getVenditoreById(id);

        if (venditoreOpt.isEmpty()) {
            log.warn("Company with ID {} not found", id);
            return ResponseEntity.notFound().build();
        }

        DatiAzienda datiAzienda = venditoreOpt.get().getDatiAzienda();
        if (datiAzienda == null) {
            log.warn("Company data for vendor ID {} not found", id);
            return ResponseEntity.notFound().build();
        }

        List<Certificazione> certificazioni = datiAzienda.getCertificazioniAzienda();
        List<CertificazioneDTO> certificazioniDTO = certificazioni.stream()
                .map(certificazioneMapper::toDTO)
                .collect(Collectors.toList());

        log.info("Retrieved {} certifications for company ID: {}", certificazioniDTO.size(), id);
        return ResponseEntity.ok(certificazioniDTO);
    }

    /**
     * Get all products for a company.
     *
     * @param id            The company ID
     * @param page          The page number
     * @param size          The page size
     * @param sortBy        The field to sort by
     * @param sortDirection The sort direction
     * @return The page of products
     */
    @GetMapping("/{id}/prodotti")
    public ResponseEntity<Page<ProductSummaryDTO>> getCompanyProducts(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "nome") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Optional<Venditore> venditoreOpt = venditoreService.getVenditoreById(id);

        if (venditoreOpt.isEmpty()) {
            log.warn("Company with ID {} not found", id);
            return ResponseEntity.notFound().build();
        }

        Sort sort = sortDirection.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Prodotto> prodotti = prodottoService.getProdottiByVenditore(venditoreOpt.get(), pageable);
        Page<ProductSummaryDTO> prodottiDTO = prodotti.map(prodottoMapper::toSummaryDTO);

        log.info("Retrieved {} products for company ID: {}", prodottiDTO.getTotalElements(), id);
        return ResponseEntity.ok(prodottiDTO);
    }

    /**
     * Search for companies.
     *
     * @param query The search query
     * @param page  The page number
     * @param size  The page size
     * @return The page of companies
     */
    @GetMapping("/cercaAzienda")
    public ResponseEntity<Page<DatiAzienda>> searchCompanies(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<DatiAzienda> aziende = venditoreService.searchAziende(query, pageable);

        log.info("Search for '{}' returned {} companies", query, aziende.getTotalElements());
        return ResponseEntity.ok(aziende);
    }
}