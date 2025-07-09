
package it.unicam.cs.ids.piattaforma_agricola_locale.controller;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.admin.ModerationDecisionDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo.ProductSummaryDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.StatoAccreditamento;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Curatore;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.AnimatoreDellaFiliera;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.ICuratoreService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IProdottoService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IGestoreService;
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
public class AmministratoreController {

    private final ICuratoreService curatoreService;
    private final IProdottoService prodottoService;
    private final ProdottoMapper prodottoMapper;
    private final IUtenteService utenteService;
    private final UtenteMapper utenteMapper;
    private final IGestoreService gestoreService;

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

    // =================== PLATFORM MANAGER OPERATIONS ===================

    /**
     * Get all users (simplified version without pagination due to service
     * limitations).
     * Only curators can access this endpoint.
     */
    @GetMapping("/gestore/utenti")
    @PreAuthorize("hasRole('GESTORE_PIATTAFORMA')")
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
     * Ban a user by ID.
     */
    @PutMapping("/utenti/{id}/ban")
    @PreAuthorize("hasRole('GESTORE_PIATTAFORMA')")
    public ResponseEntity<String> banUserById(
            @PathVariable Long id,
            @Valid @RequestBody ModerationDecisionDTO banRequest,
            Authentication authentication) {

        return utenteService.trovaUtentePerID(id)
                .map(utente -> {
                    // Ban the user (disable account)
                    boolean success = utenteService.disattivaAccount(utente.getIdUtente());

                    if (success) {
                        String gestoreEmail = authentication.getName();
                        log.warn("User ID: {} (email: {}) banned by platform manager: {} - Reason: {}",
                                id, utente.getEmail(), gestoreEmail, banRequest.getMotivazione());

                        return ResponseEntity.ok("Utente bannato con successo");
                    } else {
                        return ResponseEntity.internalServerError().<String>build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all vendors pending accreditation.
     * Only platform managers can access this endpoint.
     */
    @GetMapping("/gestore/venditori/pending")
    @PreAuthorize("hasRole('GESTORE_PIATTAFORMA')")
    public ResponseEntity<List<UserPublicDTO>> getPendingVendors(Authentication authentication) {
        List<Venditore> venditori = gestoreService.getVenditoriInAttesaDiAccreditamento();
        List<UserPublicDTO> dtos = venditori.stream()
                .map(utenteMapper::toPublicDTO)
                .collect(Collectors.toList());

        String email = authentication.getName();
        log.info("Retrieved {} vendors pending accreditation by manager: {}", dtos.size(), email);
        return ResponseEntity.ok(dtos);
    }

    /**
     * Update vendor accreditation status.
     * Only platform managers can access this endpoint.
     */
    @PutMapping("/gestore/venditori/{id}/accreditamento")
    @PreAuthorize("hasRole('GESTORE_PIATTAFORMA')")
    public ResponseEntity<String> updateVendorAccreditation(
            @PathVariable Long id,
            @RequestParam StatoAccreditamento stato,
            Authentication authentication) {

        boolean success = gestoreService.aggiornaStatoAccreditamentoVenditore(id, stato);

        if (success) {
            String email = authentication.getName();
            log.info("Vendor ID: {} accreditation updated to {} by manager: {}", id, stato, email);
            return ResponseEntity.ok("Stato accreditamento venditore aggiornato: " + stato);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get all curators pending accreditation.
     * Only platform managers can access this endpoint.
     */
    @GetMapping("/gestore/curatori/pending")
    @PreAuthorize("hasRole('GESTORE_PIATTAFORMA')")
    public ResponseEntity<List<UserPublicDTO>> getPendingCurators(Authentication authentication) {
        List<Curatore> curatori = gestoreService.getCuratoriInAttesaDiAccreditamento();
        List<UserPublicDTO> dtos = curatori.stream()
                .map(utenteMapper::toPublicDTO)
                .collect(Collectors.toList());

        String email = authentication.getName();
        log.info("Retrieved {} curators pending accreditation by manager: {}", dtos.size(), email);
        return ResponseEntity.ok(dtos);
    }

    /**
     * Update curator accreditation status.
     * Only platform managers can access this endpoint.
     */
    @PutMapping("/gestore/curatori/{id}/accreditamento")
    @PreAuthorize("hasRole('GESTORE_PIATTAFORMA')")
    public ResponseEntity<String> updateCuratorAccreditation(
            @PathVariable Long id,
            @RequestParam StatoAccreditamento stato,
            Authentication authentication) {

        boolean success = gestoreService.aggiornaStatoAccreditamentoCuratore(id, stato);

        if (success) {
            String email = authentication.getName();
            log.info("Curator ID: {} accreditation updated to {} by manager: {}", id, stato, email);
            return ResponseEntity.ok("Stato accreditamento curatore aggiornato: " + stato);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get all animators pending accreditation.
     * Only platform managers can access this endpoint.
     */
    @GetMapping("/gestore/animatori/pending")
    @PreAuthorize("hasRole('GESTORE_PIATTAFORMA')")
    public ResponseEntity<List<UserPublicDTO>> getPendingAnimators(Authentication authentication) {
        List<AnimatoreDellaFiliera> animatori = gestoreService.getAnimatoriInAttesaDiAccreditamento();
        List<UserPublicDTO> dtos = animatori.stream()
                .map(utenteMapper::toPublicDTO)
                .collect(Collectors.toList());

        String email = authentication.getName();
        log.info("Retrieved {} animators pending accreditation by manager: {}", dtos.size(), email);
        return ResponseEntity.ok(dtos);
    }

    /**
     * Update animator accreditation status.
     * Only platform managers can access this endpoint.
     */
    @PutMapping("/gestore/animatori/{id}/accreditamento")
    @PreAuthorize("hasRole('GESTORE_PIATTAFORMA')")
    public ResponseEntity<String> updateAnimatorAccreditation(
            @PathVariable Long id,
            @RequestParam StatoAccreditamento stato,
            Authentication authentication) {

        boolean success = gestoreService.aggiornaStatoAccreditamentoAnimatore(id, stato);

        if (success) {
            String email = authentication.getName();
            log.info("Animator ID: {} accreditation updated to {} by manager: {}", id, stato, email);
            return ResponseEntity.ok("Stato accreditamento animatore aggiornato: " + stato);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Toggle user activation by type.
     * Only platform managers can access this endpoint.
     */
    @PutMapping("/gestore/utenti/{id}/attivazione")
    @PreAuthorize("hasRole('GESTORE_PIATTAFORMA')")
    public ResponseEntity<String> toggleUserActivation(
            @PathVariable Long id,
            @RequestParam String tipo,
            @RequestParam boolean attivo,
            Authentication authentication) {

        boolean success = false;
        String tipoUtente = tipo.toUpperCase();

        switch (tipoUtente) {
            case "ACQUIRENTE":
                success = gestoreService.attivaDisattivaAcquirente(id, attivo);
                break;
            case "VENDITORE":
                success = gestoreService.attivaDisattivaVenditore(id, attivo);
                break;
            case "CURATORE":
                success = gestoreService.attivaDisattivaCuratore(id, attivo);
                break;
            case "ANIMATORE":
                success = gestoreService.attivaDisattivaAnimatore(id, attivo);
                break;
            default:
                return ResponseEntity.badRequest().body("Tipo utente non valido: " + tipo);
        }

        if (success) {
            String statusText = attivo ? "attivato" : "disattivato";
            String email = authentication.getName();
            log.info("User ID: {} ({}) {} by manager: {}", id, tipoUtente, statusText, email);
            return ResponseEntity.ok(String.format("Utente %s %s con successo", tipoUtente, statusText));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get company data for a specific vendor.
     * Only platform managers can access this endpoint.
     */
    @GetMapping("/gestore/venditori/{id}/azienda")
    @PreAuthorize("hasRole('GESTORE_PIATTAFORMA')")
    public ResponseEntity<CompanyModerationDTO> getVendorCompanyData(
            @PathVariable Long id,
            Authentication authentication) {

        return gestoreService.getDatiAziendaPerVenditore(id)
                .map(this::mapDatiAziendaToModerationDTO)
                .map(dto -> {
                    String email = authentication.getName();
                    log.info("Retrieved company data for vendor ID: {} by manager: {}", id, email);
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all users in the platform.
     * Only platform managers can access this endpoint.
     */
    @GetMapping("/gestore/utenti")
    @PreAuthorize("hasRole('GESTORE_PIATTAFORMA')")
    public ResponseEntity<List<UserPublicDTO>> getAllUsersForManager(
            @RequestParam(required = false) boolean soloAttivi,
            Authentication authentication) {

        List<Utente> utenti = soloAttivi ? gestoreService.getTuttiGliUtentiAttivi()
                : gestoreService.getTuttiGliUtenti();

        List<UserPublicDTO> userDTOs = utenti.stream()
                .map(utenteMapper::toPublicDTO)
                .collect(Collectors.toList());

        String email = authentication.getName();
        log.info("Retrieved {} users by manager: {}", userDTOs.size(), email);
        return ResponseEntity.ok(userDTOs);
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