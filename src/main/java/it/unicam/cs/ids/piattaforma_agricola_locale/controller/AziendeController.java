package it.unicam.cs.ids.piattaforma_agricola_locale.controller;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.azienda.AziendaDetailDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Utente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IUtenteService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.mapper.AziendaMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for company management with /api/aziende path.
 * Provides endpoints for retrieving company data for the authenticated user.
 */
@RestController
@RequestMapping("/api/aziende")
@RequiredArgsConstructor
@Slf4j
public class AziendeController {

    private final IUtenteService utenteService;
    private final AziendaMapper aziendaMapper;

    /**
     * Get the company data for the authenticated user.
     * Only authenticated vendors can access this endpoint.
     *
     * @param authentication The authenticated user
     * @return The company data of the authenticated user
     */
    @GetMapping("/mia-azienda")
    @PreAuthorize("hasAnyRole('PRODUTTORE', 'TRASFORMATORE', 'DISTRIBUTORE_DI_TIPICITA')")
    public ResponseEntity<AziendaDetailDTO> getMyCompanyData(Authentication authentication) {
        // Get the authenticated user
        String username = authentication.getName();
        Utente utente = utenteService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        // Check if the user is a vendor
        if (!(utente instanceof Venditore)) {
            log.warn("User {} attempted to access company data but is not a vendor", username);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Venditore venditore = (Venditore) utente;

        // Check if the vendor has company data
        DatiAzienda datiAzienda = venditore.getDatiAzienda();
        if (datiAzienda == null) {
            log.warn("Vendor {} attempted to access company data but has no company associated", username);
            return ResponseEntity.notFound().build();
        }

        // Convert to DTO with proper structure
        AziendaDetailDTO aziendaDTO = aziendaMapper.toDetailDTO(datiAzienda, venditore);

        log.info("Retrieved company data for user: {} - Company: {}", username, aziendaDTO.getNomeAzienda());
        return ResponseEntity.ok(aziendaDTO);
    }
}