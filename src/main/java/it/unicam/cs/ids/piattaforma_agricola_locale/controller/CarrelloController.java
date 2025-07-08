package it.unicam.cs.ids.piattaforma_agricola_locale.controller;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.carrello.AddToCartRequestDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.carrello.CarrelloDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.exception.QuantitaNonDisponibileException;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.carrello.Carrello;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.carrello.ElementoCarrello;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.Acquistabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.ICarrelloService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IEventoService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IPacchettoService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IProdottoService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IUtenteService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.mapper.CarrelloMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/carrello")
@RequiredArgsConstructor
@Slf4j
public class CarrelloController {

    private final ICarrelloService carrelloService;
    private final IProdottoService prodottoService;
    private final IPacchettoService pacchettoService;
    private final IEventoService eventoService;
    private final IUtenteService utenteService;
    private final CarrelloMapper carrelloMapper;

    @PostMapping("/elementi")
    @PreAuthorize("hasRole('ACQUIRENTE')")
    public ResponseEntity<?> addToCart(
            @Valid @RequestBody AddToCartRequestDTO request,
            Authentication authentication) {

        try {
            log.info("Adding item to cart - User: {}, Type: {}, ID: {}, Quantity: {}",
                    authentication.getName(), request.getTipoAcquistabile(),
                    request.getIdAcquistabile(), request.getQuantita());

            // Get the authenticated user
            String email = authentication.getName();
            Acquirente acquirente = (Acquirente) utenteService.trovaUtentePerEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utente non trovato"));

            // Find the purchasable item based on type
            Acquistabile acquistabile = getAcquistabileByTypeAndId(
                    request.getTipoAcquistabile(), request.getIdAcquistabile());

            if (acquistabile == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Elemento non trovato",
                                "message", "L'elemento richiesto non esiste o non è disponibile"));
            }

            // Add to cart
            carrelloService.aggiungiElementoAlCarrello(acquirente, acquistabile, request.getQuantita());

            log.info("Item added to cart successfully - User: {}", email);
            return ResponseEntity.ok(Map.of("message", "Elemento aggiunto al carrello con successo"));

        } catch (QuantitaNonDisponibileException e) {
            log.warn("Insufficient quantity - User: {}, Error: {}", authentication.getName(), e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Quantità non disponibile", "message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request - User: {}, Error: {}", authentication.getName(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Richiesta non valida", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("Error adding item to cart - User: {}", authentication.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore interno", "message",
                            "Si è verificato un errore durante l'aggiunta al carrello"));
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ACQUIRENTE')")
    public ResponseEntity<CarrelloDTO> getCart(Authentication authentication) {
        try {
            log.info("Getting cart for user: {}", authentication.getName());

            String email = authentication.getName();
            Acquirente acquirente = (Acquirente) utenteService.trovaUtentePerEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utente non trovato"));

            Optional<Carrello> carrelloOpt = carrelloService.getCarrelloAcquirente(acquirente);

            if (carrelloOpt.isPresent()) {
                CarrelloDTO carrelloDTO = carrelloMapper.toDTO(carrelloOpt.get());
                return ResponseEntity.ok(carrelloDTO);
            } else {
                // Return empty cart
                CarrelloDTO emptyCart = CarrelloDTO.builder()
                        .acquirente(carrelloMapper.acquirenteToUserPublicDTO(acquirente))
                        .elementiCarrello(java.util.List.of())
                        .build();
                return ResponseEntity.ok(emptyCart);
            }

        } catch (Exception e) {
            log.error("Error getting cart for user: {}", authentication.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/elementi/{itemId}")
    @PreAuthorize("hasRole('ACQUIRENTE')")
    public ResponseEntity<?> updateCartItem(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateQuantityRequest request,
            Authentication authentication) {

        try {
            log.info("Updating cart item - User: {}, ItemId: {}, NewQuantity: {}",
                    authentication.getName(), itemId, request.getQuantita());

            String email = authentication.getName();
            Acquirente acquirente = (Acquirente) utenteService.trovaUtentePerEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utente non trovato"));

            Optional<Carrello> carrelloOpt = carrelloService.getCarrelloAcquirente(acquirente);
            if (carrelloOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Carrello carrello = carrelloOpt.get();
            Optional<ElementoCarrello> elementoOpt = carrello.getElementiCarrello().stream()
                    .filter(elemento -> elemento.getIdElemento().equals(itemId))
                    .findFirst();

            if (elementoOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            ElementoCarrello elemento = elementoOpt.get();

            // Remove the old item and add with new quantity
            carrelloService.rimuoviElementoDalCarrello(acquirente, elemento);
            carrelloService.aggiungiElementoAlCarrello(acquirente, elemento.getAcquistabile(), request.getQuantita());

            log.info("Cart item updated successfully - User: {}, ItemId: {}", email, itemId);
            return ResponseEntity.ok(Map.of("message", "Quantità aggiornata con successo"));

        } catch (QuantitaNonDisponibileException e) {
            log.warn("Insufficient quantity for update - User: {}, Error: {}", authentication.getName(),
                    e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Quantità non disponibile", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating cart item - User: {}, ItemId: {}", authentication.getName(), itemId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore interno", "message",
                            "Si è verificato un errore durante l'aggiornamento"));
        }
    }

    @DeleteMapping("/elementi/{itemId}")
    @PreAuthorize("hasRole('ACQUIRENTE')")
    public ResponseEntity<?> removeCartItem(
            @PathVariable Long itemId,
            Authentication authentication) {

        try {
            log.info("Removing cart item - User: {}, ItemId: {}", authentication.getName(), itemId);

            String email = authentication.getName();
            Acquirente acquirente = (Acquirente) utenteService.trovaUtentePerEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utente non trovato"));

            Optional<Carrello> carrelloOpt = carrelloService.getCarrelloAcquirente(acquirente);
            if (carrelloOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Carrello carrello = carrelloOpt.get();
            Optional<ElementoCarrello> elementoOpt = carrello.getElementiCarrello().stream()
                    .filter(elemento -> elemento.getIdElemento().equals(itemId))
                    .findFirst();

            if (elementoOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            carrelloService.rimuoviElementoDalCarrello(acquirente, elementoOpt.get());

            log.info("Cart item removed successfully - User: {}, ItemId: {}", email, itemId);
            return ResponseEntity.ok(Map.of("message", "Elemento rimosso dal carrello"));

        } catch (Exception e) {
            log.error("Error removing cart item - User: {}, ItemId: {}", authentication.getName(), itemId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore interno", "message",
                            "Si è verificato un errore durante la rimozione"));
        }
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ACQUIRENTE')")
    public ResponseEntity<?> clearCart(Authentication authentication) {
        try {
            log.info("Clearing cart for user: {}", authentication.getName());

            String email = authentication.getName();
            Acquirente acquirente = (Acquirente) utenteService.trovaUtentePerEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utente non trovato"));

            carrelloService.svuotaCarrello(acquirente);

            log.info("Cart cleared successfully for user: {}", email);
            return ResponseEntity.ok(Map.of("message", "Carrello svuotato con successo"));

        } catch (Exception e) {
            log.error("Error clearing cart for user: {}", authentication.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore interno", "message",
                            "Si è verificato un errore durante lo svuotamento del carrello"));
        }
    }

    @GetMapping("/sommario")
    @PreAuthorize("hasRole('ACQUIRENTE')")
    public ResponseEntity<?> getCartSummary(Authentication authentication) {
        try {
            log.info("Getting cart summary for user: {}", authentication.getName());

            String email = authentication.getName();
            Acquirente acquirente = (Acquirente) utenteService.trovaUtentePerEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utente non trovato"));

            Optional<Carrello> carrelloOpt = carrelloService.getCarrelloAcquirente(acquirente);

            Map<String, Object> summary = new HashMap<>();

            if (carrelloOpt.isPresent()) {
                Carrello carrello = carrelloOpt.get();
                double totale = carrelloService.calcolaPrezzoTotaleCarrello(acquirente);
                int totalElementi = carrello.getElementiCarrello().stream()
                        .mapToInt(ElementoCarrello::getQuantita)
                        .sum();

                summary.put("totalElementi", totalElementi);
                summary.put("totale", totale);
                summary.put("ultimaModifica", carrello.getUltimaModifica());
            } else {
                summary.put("totalElementi", 0);
                summary.put("totale", 0.0);
                summary.put("ultimaModifica", null);
            }

            return ResponseEntity.ok(summary);

        } catch (Exception e) {
            log.error("Error getting cart summary for user: {}", authentication.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore interno", "message",
                            "Si è verificato un errore durante il recupero del riepilogo"));
        }
    }

    private Acquistabile getAcquistabileByTypeAndId(String tipo, Long id) {
        switch (tipo.toUpperCase()) {
            case "PRODOTTO":
                return prodottoService.getProdottoById(id).orElse(null);
            case "PACCHETTO":
                return pacchettoService.getPacchettoById(id).orElse(null);
            case "EVENTO":
                return eventoService.getEventoById(id).orElse(null);
            default:
                return null;
        }
    }

    // Inner class for update quantity request
    public static class UpdateQuantityRequest {
        @NotNull(message = "La quantità è obbligatoria")
        @Min(value = 1, message = "La quantità deve essere almeno 1")
        private Integer quantita;

        public Integer getQuantita() {
            return quantita;
        }

        public void setQuantita(Integer quantita) {
            this.quantita = quantita;
        }
    }
}