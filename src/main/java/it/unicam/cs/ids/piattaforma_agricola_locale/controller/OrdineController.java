package it.unicam.cs.ids.piattaforma_agricola_locale.controller;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.ordine.CreateOrdineRequestDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.ordine.OrdineDetailDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.ordine.OrdineExtendedSummaryDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.pagamento.PagamentoRequestDTO;
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

import java.util.ArrayList;
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

            // Create multiple orders from cart (one per vendor)
            // Gli ordini vengono creati in stato ATTESA_PAGAMENTO
            List<Ordine> ordini = ordineService.creaOrdiniDaCarrello(acquirente);
            
            // NOTA: Il pagamento NON viene più elaborato automaticamente qui.
            // L'acquirente deve usare l'endpoint PUT /api/ordini/{id}/pagamento 
            // per confermare il pagamento con i dati specifici del metodo scelto.
            log.info("Orders created in ATTESA_PAGAMENTO state. Payment method preference: {}", 
                    request.getMetodoPagamento());

            log.info("Orders created successfully - OrderCount: {}, User: {}", ordini.size(), email);

            // Convert all orders to DTOs
            List<OrdineDetailDTO> ordiniDTO = new ArrayList<>();
            for (Ordine ordine : ordini) {
                // Inietta l'AcquistabileService nelle righe ordine prima di convertirle in DTO
                ordineMapper.injectAcquistabileService(ordine,
                        ((it.unicam.cs.ids.piattaforma_agricola_locale.service.impl.OrdineService) ordineService)
                                .getCarrelloService().getAcquistabileService());
                ordiniDTO.add(ordineMapper.toDetailDTO(ordine));
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "ordini", ordiniDTO,
                    "count", ordini.size(),
                    "message", "Ordini creati con successo per " + ordini.size() + " venditori"));

        } catch (CarrelloVuotoException e) {
            log.warn("Cannot create order with empty cart - User: {}", authentication.getName());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Carrello vuoto", "message",
                            "Non è possibile creare un ordine con il carrello vuoto"));
        } catch (QuantitaNonDisponibileAlCheckoutException e) {
            log.warn("Insufficient quantity at checkout - User: {}, Error: {}", authentication.getName(),
                    e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Quantità non disponibile", "message", e.getMessage()));
        } catch (OrdineException e) {
            log.error("Error creating order - User: {}, Error: {}", authentication.getName(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Errore nella creazione dell'ordine", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error creating order - User: {}", authentication.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore interno", "message",
                            "Si è verificato un errore durante la creazione dell'ordine"));
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ACQUIRENTE')")
    public ResponseEntity<Page<OrdineExtendedSummaryDTO>> getUserOrders(
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

            // Inietta l'AcquistabileService nelle righe ordine prima di convertirle in DTO
            for (Ordine ordine : ordini) {
                ordineMapper.injectAcquistabileService(ordine,
                        ((it.unicam.cs.ids.piattaforma_agricola_locale.service.impl.OrdineService) ordineService)
                                .getCarrelloService().getAcquistabileService());
            }

            // Convert to extended DTOs with detailed information
            List<OrdineExtendedSummaryDTO> ordiniDTO = ordini.stream()
                    .map(ordineMapper::toExtendedSummaryDTO)
                    .collect(Collectors.toList());

            // Apply sorting
            Sort sort = sortDirection.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();

            // Manual pagination since we already have the data
            Pageable pageable = PageRequest.of(page, size, sort);
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), ordiniDTO.size());

            List<OrdineExtendedSummaryDTO> pageContent = ordiniDTO.subList(start, end);
            Page<OrdineExtendedSummaryDTO> ordiniPage = new PageImpl<>(pageContent, pageable, ordiniDTO.size());

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
                        .body(Map.of("error", "Accesso negato", "message",
                                "Non hai i permessi per visualizzare questo ordine"));
            }

            // Inietta l'AcquistabileService nelle righe ordine prima di convertirle in DTO
            ordineMapper.injectAcquistabileService(ordine,
                    ((it.unicam.cs.ids.piattaforma_agricola_locale.service.impl.OrdineService) ordineService)
                            .getCarrelloService().getAcquistabileService());

            OrdineDetailDTO ordineDTO = ordineMapper.toDetailDTO(ordine);
            return ResponseEntity.ok(ordineDTO);

        } catch (Exception e) {
            log.error("Error getting order details - OrderId: {}, User: {}", id, authentication.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore interno", "message",
                            "Si è verificato un errore durante il recupero dell'ordine"));
        }
    }

    @PutMapping("/{id}/pagamento")
    @PreAuthorize("hasRole('ACQUIRENTE')")
    public ResponseEntity<?> confirmPayment(
            @PathVariable Long id,
            @Valid @RequestBody PagamentoRequestDTO request,
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

            // Get payment strategy and confirm payment with payment data
            IMetodoPagamentoStrategy strategiaPagamento = getPaymentStrategy(request.getMetodoPagamento());
            
            // Use the new method with payment data if available, otherwise use the simple one
            boolean pagamentoRiuscito;
            if (hasPaymentData(request)) {
                pagamentoRiuscito = strategiaPagamento.elaboraPagamento(ordine, request);
            } else {
                pagamentoRiuscito = strategiaPagamento.elaboraPagamento(ordine);
            }
            
            if (pagamentoRiuscito) {
                // Update order status manually since we're calling the strategy directly
                ordine.setStatoCorrente(StatoCorrente.PRONTO_PER_LAVORAZIONE);
                ordineService.aggiornaOrdine(ordine);
                
                log.info("Payment confirmed successfully - OrderId: {}, User: {}", id, email);
                return ResponseEntity.ok(Map.of("message", "Pagamento confermato con successo",
                        "ordineId", ordine.getIdOrdine(), "stato", ordine.getStatoOrdine()));
            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Pagamento fallito", 
                                "message", "Il pagamento non è stato autorizzato. Verificare i dati inseriti."));
            }

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
                    .body(Map.of("error", "Errore interno", "message",
                            "Si è verificato un errore durante la conferma del pagamento"));
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
                    "importoTotale", ordine.getImportoTotale());

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
                    .body(Map.of("error", "Errore interno", "message",
                            "Si è verificato un errore durante la cancellazione"));
        }
    }

    private IMetodoPagamentoStrategy getPaymentStrategy(String metodoPagamento) {
        switch (metodoPagamento.toUpperCase()) {
            case "CARTA_CREDITO":
                return new PagamentoCartaCreditoStrategy();
            case "PAYPAL":
                return new PagamentoPayPalStrategy();
            default:
                throw new IllegalArgumentException("Metodo di pagamento non supportato: " + metodoPagamento);
        }
    }

    /**
     * Verifica se la richiesta di pagamento contiene dati specifici per il metodo
     */
    private boolean hasPaymentData(PagamentoRequestDTO request) {
        return (request.getDatiCartaCredito() != null) || (request.getDatiPayPal() != null);
    }
}
