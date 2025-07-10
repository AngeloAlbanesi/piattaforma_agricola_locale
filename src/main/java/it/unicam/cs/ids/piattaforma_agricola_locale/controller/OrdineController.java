package it.unicam.cs.ids.piattaforma_agricola_locale.controller;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.ordine.CreateOrdineRequestDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.ordine.OrdineDetailDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.ordine.OrdineSummaryDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.exception.CarrelloVuotoException;
import it.unicam.cs.ids.piattaforma_agricola_locale.exception.OrdineException;
import it.unicam.cs.ids.piattaforma_agricola_locale.exception.QuantitaNonDisponibileAlCheckoutException;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.StatoCorrente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IOrdineService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IUtenteService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.mapper.OrdineMapper;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.pagamento.IMetodoPagamentoStrategy;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.pagamento.PagamentoException;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.pagamento.impl.PagamentoCartaCreditoStrategy;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.pagamento.impl.PagamentoPayPalStrategy;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.pagamento.impl.PagamentoSimulatoStrategy;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ordini")
@RequiredArgsConstructor
@Slf4j
public class OrdineController {

    private final IOrdineService ordineService;
    private final IUtenteService utenteService;
    private final OrdineMapper ordineMapper;

    @PostMapping
    @PreAuthorize("hasRole('ACQUIRENTE')")
    public ResponseEntity<?> createOrder(
            @Valid @RequestBody CreateOrdineRequestDTO request,
            Authentication authentication) {

        try {
            log.info("Creating order from cart - User: {}, PaymentMethod: {}",
                    authentication.getName(), request.getMetodoPagamento());

            String email = authentication.getName();
            Acquirente acquirente = (Acquirente) utenteService.trovaUtentePerEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utente non trovato"));

            // Create order from cart
            Ordine ordine = ordineService.creaOrdineDaCarrello(acquirente);

            // Process payment if specified
            if (request.getMetodoPagamento() != null && !request.getMetodoPagamento().trim().isEmpty()) {
                try {
                    IMetodoPagamentoStrategy strategiaPagamento = getPaymentStrategy(request.getMetodoPagamento());
                    ordineService.confermaPagamento(ordine, strategiaPagamento);
                    log.info("Payment processed successfully for order: {}", ordine.getIdOrdine());
                } catch (IllegalArgumentException iae) {
                    log.warn("Invalid payment method for order: {}, Method: {}", ordine.getIdOrdine(), request.getMetodoPagamento());
                    // L'ordine rimane in stato ATTESA_PAGAMENTO
                } catch (PagamentoException pe) {
                    log.warn("Payment failed for order: {}, Error: {}", ordine.getIdOrdine(), pe.getMessage());
                    // L'ordine rimane in stato ATTESA_PAGAMENTO
                } catch (OrdineException oe) {
                    log.error("Order error during payment processing for order: {}, Error: {}", ordine.getIdOrdine(), oe.getMessage());
                    // Rilancia l'eccezione per essere gestita dal blocco catch esterno
                    throw oe;
                }
            }

            log.info("Order created successfully - OrderId: {}, User: {}", ordine.getIdOrdine(), email);

            OrdineDetailDTO ordineDTO = ordineMapper.toDetailDTO(ordine);
            return ResponseEntity.status(HttpStatus.CREATED).body(ordineDTO);

        } catch (CarrelloVuotoException e) {
            log.warn("Cannot create order with empty cart - User: {}", authentication.getName());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Carrello vuoto", "message", "Non è possibile creare un ordine con il carrello vuoto"));
        } catch (QuantitaNonDisponibileAlCheckoutException e) {
            log.warn("Insufficient quantity at checkout - User: {}, Error: {}", authentication.getName(), e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Quantità non disponibile", "message", e.getMessage()));
        } catch (OrdineException e) {
            log.error("Error creating order - User: {}, Error: {}", authentication.getName(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Errore nella creazione dell'ordine", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error creating order - User: {}", authentication.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore interno", "message", "Si è verificato un errore durante la creazione dell'ordine"));
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ACQUIRENTE')")
    public ResponseEntity<Page<OrdineSummaryDTO>> getUserOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dataOrdine") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            Authentication authentication) {

        try {
            log.info("Getting orders for user: {} - Page: {}, Size: {}",
                    authentication.getName(), page, size);

            String email = authentication.getName();
            Acquirente acquirente = (Acquirente) utenteService.trovaUtentePerEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utente non trovato"));

            // Get user orders
            List<Ordine> ordini = ordineService.getOrdiniAcquirente(acquirente);

            // Convert to DTOs
            List<OrdineSummaryDTO> ordiniDTO = ordini.stream()
                    .map(ordineMapper::toSummaryDTO)
                    .collect(Collectors.toList());

            // Apply sorting
            Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

            // Manual pagination since we already have the data
            Pageable pageable = PageRequest.of(page, size, sort);
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), ordiniDTO.size());

            List<OrdineSummaryDTO> pageContent = ordiniDTO.subList(start, end);
            Page<OrdineSummaryDTO> ordiniPage = new PageImpl<>(pageContent, pageable, ordiniDTO.size());

            return ResponseEntity.ok(ordiniPage);

        } catch (Exception e) {
            log.error("Error getting orders for user: {}", authentication.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ACQUIRENTE')")
    public ResponseEntity<?> getOrderById(
            @PathVariable Long id,
            Authentication authentication) {

        try {
            log.info("Getting order details - OrderId: {}, User: {}", id, authentication.getName());

            String email = authentication.getName();
            Acquirente acquirente = (Acquirente) utenteService.trovaUtentePerEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utente non trovato"));

            Optional<Ordine> ordineOpt = ordineService.findOrdineById(id);
            if (ordineOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Ordine ordine = ordineOpt.get();

            // Check if the order belongs to the authenticated user
            if (!ordine.getAcquirente().getIdUtente().equals(acquirente.getIdUtente())) {
                log.warn("Unauthorized access to order - OrderId: {}, User: {}, Owner: {}",
                        id, email, ordine.getAcquirente().getEmail());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Accesso negato", "message", "Non hai i permessi per visualizzare questo ordine"));
            }

            OrdineDetailDTO ordineDTO = ordineMapper.toDetailDTO(ordine);
            return ResponseEntity.ok(ordineDTO);

        } catch (Exception e) {
            log.error("Error getting order details - OrderId: {}, User: {}", id, authentication.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore interno", "message", "Si è verificato un errore durante il recupero dell'ordine"));
        }
    }

    @PutMapping("/{id}/pagamento")
    @PreAuthorize("hasRole('ACQUIRENTE')")
    public ResponseEntity<?> confirmPayment(
            @PathVariable Long id,
            @Valid @RequestBody PaymentConfirmationRequest request,
            Authentication authentication) {

        try {
            log.info("Confirming payment - OrderId: {}, User: {}, PaymentMethod: {}",
                    id, authentication.getName(), request.getMetodoPagamento());

            String email = authentication.getName();
            Acquirente acquirente = (Acquirente) utenteService.trovaUtentePerEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utente non trovato"));

            Optional<Ordine> ordineOpt = ordineService.findOrdineById(id);
            if (ordineOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Ordine ordine = ordineOpt.get();

            // Check ownership
            if (!ordine.getAcquirente().getIdUtente().equals(acquirente.getIdUtente())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Accesso negato", "message", "Non hai i permessi per questo ordine"));
            }

            // Check if order is in the correct state for payment
            if (ordine.getStatoOrdine() != StatoCorrente.ATTESA_PAGAMENTO) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Stato ordine non valido",
                                "message", "L'ordine non è nello stato corretto per confermare il pagamento"));
            }

            // Get payment strategy and confirm payment
            IMetodoPagamentoStrategy strategiaPagamento = getPaymentStrategy(request.getMetodoPagamento());
            ordineService.confermaPagamento(ordine, strategiaPagamento);

            log.info("Payment confirmed successfully - OrderId: {}, User: {}", id, email);
            return ResponseEntity.ok(Map.of("message", "Pagamento confermato con successo"));

        } catch (IllegalArgumentException e) {
            log.warn("Invalid payment method - OrderId: {}, User: {}, Method: {}",
                    id, authentication.getName(), request.getMetodoPagamento());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Metodo di pagamento non valido", "message", e.getMessage()));
        } catch (PagamentoException e) {
            log.error("Payment processing error - OrderId: {}, User: {}, Error: {}",
                    id, authentication.getName(), e.getMessage());
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
                    .body(Map.of("error", "Errore nel pagamento", "message", e.getMessage()));
        } catch (OrdineException e) {
            log.error("Order error during payment confirmation - OrderId: {}, User: {}, Error: {}",
                    id, authentication.getName(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Errore nell'ordine", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("Error confirming payment - OrderId: {}, User: {}", id, authentication.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore interno", "message", "Si è verificato un errore durante la conferma del pagamento"));
        }
    }

    @GetMapping("/{id}/stato")
    @PreAuthorize("hasRole('ACQUIRENTE')")
    public ResponseEntity<?> getOrderStatus(
            @PathVariable Long id,
            Authentication authentication) {

        try {
            log.info("Getting order status - OrderId: {}, User: {}", id, authentication.getName());

            String email = authentication.getName();
            Acquirente acquirente = (Acquirente) utenteService.trovaUtentePerEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utente non trovato"));

            Optional<Ordine> ordineOpt = ordineService.findOrdineById(id);
            if (ordineOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Ordine ordine = ordineOpt.get();

            // Check ownership
            if (!ordine.getAcquirente().getIdUtente().equals(acquirente.getIdUtente())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Accesso negato"));
            }

            Map<String, Object> status = Map.of(
                    "ordineId", ordine.getIdOrdine(),
                    "stato", ordine.getStatoOrdine(),
                    "dataOrdine", ordine.getDataOrdine(),
                    "importoTotale", ordine.getImportoTotale()
            );

            return ResponseEntity.ok(status);

        } catch (Exception e) {
            log.error("Error getting order status - OrderId: {}, User: {}", id, authentication.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore interno"));
        }
    }

    @PutMapping("/{id}/cancella")
    @PreAuthorize("hasRole('ACQUIRENTE')")
    public ResponseEntity<?> cancelOrder(
            @PathVariable Long id,
            Authentication authentication) {

        try {
            log.info("Cancelling order - OrderId: {}, User: {}", id, authentication.getName());

            String email = authentication.getName();
            Acquirente acquirente = (Acquirente) utenteService.trovaUtentePerEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utente non trovato"));

            Optional<Ordine> ordineOpt = ordineService.findOrdineById(id);
            if (ordineOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Ordine ordine = ordineOpt.get();

            // Check ownership
            if (!ordine.getAcquirente().getIdUtente().equals(acquirente.getIdUtente())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Accesso negato"));
            }

            // Check if order can be cancelled (only certain states allow cancellation)
            if (ordine.getStatoOrdine() != StatoCorrente.ATTESA_PAGAMENTO &&
                    ordine.getStatoOrdine() != StatoCorrente.PRONTO_PER_LAVORAZIONE) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Cancellazione non consentita",
                                "message", "L'ordine non può essere cancellato nel suo stato attuale"));
            }

            // Cancel the order
            ordine.setStatoCorrente(StatoCorrente.ANNULLATO);
            ordineService.aggiornaOrdine(ordine);

            log.info("Order cancelled successfully - OrderId: {}, User: {}", id, email);
            return ResponseEntity.ok(Map.of("message", "Ordine cancellato con successo"));

        } catch (OrdineException e) {
            log.error("Error cancelling order - OrderId: {}, User: {}, Error: {}",
                    id, authentication.getName(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Errore nella cancellazione", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("Error cancelling order - OrderId: {}, User: {}", id, authentication.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore interno", "message", "Si è verificato un errore durante la cancellazione"));
        }
    }

    private IMetodoPagamentoStrategy getPaymentStrategy(String metodoPagamento) {
        switch (metodoPagamento.toUpperCase()) {
            case "CARTA_CREDITO":
                return new PagamentoCartaCreditoStrategy();
            case "PAYPAL":
                return new PagamentoPayPalStrategy();
            case "SIMULATO":
                return new PagamentoSimulatoStrategy();
            default:
                throw new IllegalArgumentException("Metodo di pagamento non supportato: " + metodoPagamento);
        }
    }

    // Inner class for payment confirmation request
    public static class PaymentConfirmationRequest {
        @NotBlank(message = "Il metodo di pagamento è obbligatorio")
        private String metodoPagamento;

        public String getMetodoPagamento() {
            return metodoPagamento;
        }

        public void setMetodoPagamento(String metodoPagamento) {
            this.metodoPagamento = metodoPagamento;
        }
    }
}
