/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.controller;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.admin.ModerationDecisionDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo.ProductSummaryDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.StatoAccreditamento;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.ICuratoreService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IProdottoService;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.admin.UserStatusUpdateDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.utente.UserPublicDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Utente;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IUtenteService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.mapper.ProdottoMapper;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.mapper.UtenteMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for content moderation APIs.
 * Handles product and company approval/rejection by curators.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class GestorePiattaformaController {

    private final ICuratoreService curatoreService;
    private final IProdottoService prodottoService;
    private final ProdottoMapper prodottoMapper;
    private final IUtenteService utenteService;
    private final UtenteMapper utenteMapper;

    // =================== PRODUCT MODERATION ===================

    @GetMapping("/prodotti/pending")
    @PreAuthorize("hasRole('CURATORE')")
    public ResponseEntity<List<ProductSummaryDTO>> getPendingProducts(Authentication authentication) {
        List<Prodotto> prodottiInAttesa = curatoreService.getProdottiInAttesaRevisione();
        List<ProductSummaryDTO> dtos = prodottiInAttesa.stream()
                .map(prodottoMapper::toSummaryDTO)
                .collect(Collectors.toList());

        String email = authentication.getName();
        log.info("Retrieved {} products pending approval by curator: {}", dtos.size(), email);
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/prodotti/{id}/approva")
    @PreAuthorize("hasRole('CURATORE')")
    public ResponseEntity<String> approveProduct(
            @PathVariable Long id,
            @Valid @RequestBody ModerationDecisionDTO decision,
            Authentication authentication) {

        return prodottoService.getProdottoById(id)
                .map(prodotto -> {
                    String feedbackVerifica = decision.getMotivazione() != null ? decision.getMotivazione()
                            : "Prodotto approvato";

                    curatoreService.approvaProdotto(prodotto, feedbackVerifica);

                    String email = authentication.getName();
                    log.info("Product ID: {} approved by curator: {} with feedback: {}",
                            id, email, feedbackVerifica);
                    return ResponseEntity.ok("Prodotto approvato con successo");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/prodotti/{id}/rifiuta")
    @PreAuthorize("hasRole('CURATORE')")
    public ResponseEntity<String> rejectProduct(
            @PathVariable Long id,
            @Valid @RequestBody ModerationDecisionDTO decision,
            Authentication authentication) {

        return prodottoService.getProdottoById(id)
                .map(prodotto -> {
                    String feedbackVerifica = decision.getMotivazione() != null ? decision.getMotivazione()
                            : "Prodotto respinto";

                    curatoreService.respingiProdotto(prodotto, feedbackVerifica);

                    String email = authentication.getName();
                    log.warn("Product ID: {} rejected by curator: {} with feedback: {}",
                            id, email, feedbackVerifica);
                    return ResponseEntity.ok("Prodotto respinto");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // =================== COMPANY MODERATION ===================

    @GetMapping("/aziende/pending")
    @PreAuthorize("hasRole('CURATORE')")
    public ResponseEntity<List<CompanyModerationDTO>> getPendingCompanies(Authentication authentication) {
        List<DatiAzienda> aziende = curatoreService.getDatiAziendaInAttesaRevisione();
        List<CompanyModerationDTO> dtos = aziende.stream()
                .map(this::mapDatiAziendaToModerationDTO)
                .collect(Collectors.toList());

        String email = authentication.getName();
        log.info("Retrieved {} companies pending approval by curator: {}", dtos.size(), email);
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/aziende/{id}/approva")
    @PreAuthorize("hasRole('CURATORE')")
    public ResponseEntity<String> approveCompany(
            @PathVariable Long id,
            @Valid @RequestBody ModerationDecisionDTO decision,
            Authentication authentication) {

        // Find the vendor associated with this company data
        Venditore venditore = findVenditorByAziendaId(id);
        if (venditore == null) {
            return ResponseEntity.notFound().build();
        }

        String feedbackVerifica = decision.getMotivazione() != null ? decision.getMotivazione() : "Azienda approvata";

        curatoreService.approvaDatiAzienda(venditore, feedbackVerifica);

        String email = authentication.getName();
        log.info("Company ID: {} approved by curator: {} with feedback: {}",
                id, email, feedbackVerifica);
        return ResponseEntity.ok("Azienda approvata con successo");
    }

    @PutMapping("/aziende/{id}/rifiuta")
    @PreAuthorize("hasRole('CURATORE')")
    public ResponseEntity<String> rejectCompany(
            @PathVariable Long id,
            @Valid @RequestBody ModerationDecisionDTO decision,
            Authentication authentication) {

        // Find the vendor associated with this company data
        Venditore venditore = findVenditorByAziendaId(id);
        if (venditore == null) {
            return ResponseEntity.notFound().build();
        }

        String feedbackVerifica = decision.getMotivazione() != null ? decision.getMotivazione() : "Azienda respinta";

        curatoreService.respingiDatiAzienda(venditore, feedbackVerifica);

        String email = authentication.getName();
        log.warn("Company ID: {} rejected by curator: {} with feedback: {}",
                id, email, feedbackVerifica);
        return ResponseEntity.ok("Azienda respinta");
    }

    // =================== USER MANAGEMENT ===================

    /**
     * Get all users (simplified version without pagination due to service
     * limitations).
     * Only curators can access this endpoint.
     */
    @GetMapping("/utenti")
    @PreAuthorize("hasRole('CURATORE')")
    public ResponseEntity<List<UserPublicDTO>> getAllUsers(
            @RequestParam(required = false) String search,
            Authentication authentication) {

        List<Utente> utenti = utenteService.trovaTuttiGliUtenti();

        // Apply search filter if provided
        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.toLowerCase();
            utenti = utenti.stream()
                    .filter(utente -> utente.getNome().toLowerCase().contains(searchLower) ||
                            utente.getCognome().toLowerCase().contains(searchLower) ||
                            utente.getEmail().toLowerCase().contains(searchLower))
                    .collect(Collectors.toList());
        }

        List<UserPublicDTO> userDTOs = utenti.stream()
                .map(utenteMapper::toPublicDTO)
                .collect(Collectors.toList());

        String email = authentication.getName();
        log.info("Retrieved {} users by curator: {}", userDTOs.size(), email);
        return ResponseEntity.ok(userDTOs);
    }

    /**
     * Update user status (enable/disable account).
     * Only curators can update user status.
     */
    @PutMapping("/utenti/{id}/stato")
    @PreAuthorize("hasRole('CURATORE')")
    public ResponseEntity<String> updateUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody UserStatusUpdateDTO statusUpdate,
            Authentication authentication) {

        boolean success;
        if (statusUpdate.getAttivo()) {
            success = utenteService.riattivaAccount(id);
        } else {
            success = utenteService.disattivaAccount(id);
        }

        if (success) {
            String statusText = statusUpdate.getAttivo() ? "abilitato" : "disabilitato";
            String email = authentication.getName();
            log.info("User ID: {} {} by curator: {}", id, statusText, email);

            return ResponseEntity.ok("Stato utente aggiornato: " + statusText);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Ban a user by email.
     * Only curators can ban users.
     */
    @PutMapping("/utenti/{email}/ban")
    @PreAuthorize("hasRole('CURATORE')")
    public ResponseEntity<String> banUserByEmail(
            @PathVariable String email,
            @Valid @RequestBody ModerationDecisionDTO banRequest,
            Authentication authentication) {

        return utenteService.trovaUtentePerEmail(email)
                .map(utente -> {
                    // Ban the user (disable account)
                    boolean success = utenteService.disattivaAccount(utente.getIdUtente());

                    if (success) {
                        String curatorEmail = authentication.getName();
                        log.warn("User with email: {} banned by curator: {} - Reason: {}",
                                email, curatorEmail, banRequest.getMotivazione());

                        return ResponseEntity.ok("Utente bannato con successo");
                    } else {
                        return ResponseEntity.internalServerError().<String>build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Approve a vendor application.
     * Only curators can approve vendors.
     */
    @PutMapping("/venditori/{email}/approva")
    @PreAuthorize("hasRole('CURATORE')")
    public ResponseEntity<String> approveVendor(
            @PathVariable String email,
            @Valid @RequestBody ModerationDecisionDTO decision,
            Authentication authentication) {

        return utenteService.trovaUtentePerEmail(email)
                .filter(utente -> utente instanceof Venditore)
                .map(utente -> {
                    Venditore venditore = (Venditore) utente;

                    // Approve the vendor
                    String feedbackVerifica = decision.getMotivazione() != null ? decision.getMotivazione()
                            : "Venditore approvato";

                    // Update accreditation status to approved
                    utenteService.aggiornaStatoAccreditamento(venditore.getIdUtente(), StatoAccreditamento.ACCREDITATO);

                    String curatorEmail = authentication.getName();
                    log.info("Vendor email: {} approved by curator: {} with feedback: {}",
                            email, curatorEmail, feedbackVerifica);

                    return ResponseEntity.ok("Venditore approvato con successo");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // =================== HELPER METHODS ===================

    private CompanyModerationDTO mapDatiAziendaToModerationDTO(DatiAzienda datiAzienda) {
        return CompanyModerationDTO.builder()
                .id(datiAzienda.getId())
                .nomeAzienda(datiAzienda.getNome())
                .partitaIva(datiAzienda.getPartitaIva())
                .indirizzo(datiAzienda.getIndirizzo())
                .telefono(datiAzienda.getTelefono())
                .email(datiAzienda.getEmail())
                .sitoWeb(datiAzienda.getSitoWeb())
                .statoVerifica(datiAzienda.getStatoVerifica().toString())
                .feedbackVerifica(datiAzienda.getFeedbackVerifica())
                .build();
    }

    private Venditore findVenditorByAziendaId(Long aziendaId) {
        return utenteService.findVenditoreByDatiAziendaId(aziendaId).orElse(null);
    }

    /**
     * DTO for company moderation information
     */
    @lombok.Data
    @lombok.Builder
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class CompanyModerationDTO {
        private Long id;
        private String nomeAzienda;
        private String partitaIva;
        private String indirizzo;
        private String telefono;
        private String email;
        private String sitoWeb;
        private String statoVerifica;
        private String feedbackVerifica;
    }
}