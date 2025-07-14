package it.unicam.cs.ids.piattaforma_agricola_locale.controller;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.ordine.OrdineDetailDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.ordine.OrdineVenditoreDetailDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.exception.OrdineException;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.RigaOrdine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.StatoCorrente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Utente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IOrdineService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IUtenteService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.mapper.OrdineMapper;
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
@RequestMapping("/api/ordini/venditori")
@RequiredArgsConstructor
@Slf4j
public class OrdineVenditoreController {

    private final IOrdineService ordineService;
    private final IUtenteService utenteService;
    private final OrdineMapper ordineMapper;

    @GetMapping
    @PreAuthorize("hasAnyRole('PRODUTTORE', 'TRASFORMATORE', 'DISTRIBUTORE_TIPICITA')")
    public ResponseEntity<Page<OrdineVenditoreDetailDTO>> getVendorOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dataOrdine") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) StatoCorrente statoFiltro,
            Authentication authentication) {

        try {
            log.info("Getting vendor orders - User: {}, Page: {}, Size: {}, Filter: {}",
                    authentication.getName(), page, size, statoFiltro);

            String email = authentication.getName();
            Venditore venditore = (Venditore) utenteService.trovaUtentePerEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utente non trovato"));

            // Get orders for this vendor
            List<Ordine> ordiniVenditore = ordineService.getOrdiniVenditore(venditore);

            // Filter by status if specified
            if (statoFiltro != null) {
                ordiniVenditore = ordiniVenditore.stream()
                        .filter(ordine -> ordine.getStatoOrdine() == statoFiltro)
                        .collect(Collectors.toList());
            }

            // Inietta l'AcquistabileService nelle righe ordine prima di convertirle in DTO
            for (Ordine ordine : ordiniVenditore) {
                ordineMapper.injectAcquistabileService(ordine,
                        ((it.unicam.cs.ids.piattaforma_agricola_locale.service.impl.OrdineService) ordineService)
                                .getCarrelloService().getAcquistabileService());
            }

            // Convert to detailed DTOs with vendor-specific information
            List<OrdineVenditoreDetailDTO> ordiniDTO = ordiniVenditore.stream()
                    .map(ordineMapper::toVenditoreDetailDTO)
                    .collect(Collectors.toList());

            // Apply sorting
            Sort sort = sortDirection.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending()
                    : Sort.by(sortBy).ascending();

            // Manual pagination
            Pageable pageable = PageRequest.of(page, size, sort);
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), ordiniDTO.size());

            List<OrdineVenditoreDetailDTO> pageContent = ordiniDTO.subList(start, end);
            Page<OrdineVenditoreDetailDTO> ordiniPage = new PageImpl<>(pageContent, pageable, ordiniDTO.size());

            return ResponseEntity.ok(ordiniPage);

        } catch (Exception e) {
            log.error("Error getting vendor orders for user: {}", authentication.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PRODUTTORE', 'TRASFORMATORE', 'DISTRIBUTORE_TIPICITA')")
    public ResponseEntity<?> getVendorOrderById(
            @PathVariable Long id,
            Authentication authentication) {

        try {
            log.info("Getting vendor order details - OrderId: {}, User: {}", id, authentication.getName());

            String email = authentication.getName();
            Venditore venditore = (Venditore) utenteService.trovaUtentePerEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utente non trovato"));

            Optional<Ordine> ordineOpt = ordineService.findOrdineById(id);
            if (ordineOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Ordine ordine = ordineOpt.get();

            // Check if vendor has access to this order using comprehensive logic
            boolean hasAccess = hasVendorAccess(ordine, venditore);

            if (!hasAccess) {
                log.warn("Unauthorized access to order - OrderId: {}, Vendor: {}", id, email);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Accesso negato",
                                "message", "Non hai accesso a questo ordine"));
            }

            // Filter order lines to show only vendor's products
            Ordine filteredOrder = createFilteredOrderForVendor(ordine, venditore);
            OrdineDetailDTO ordineDTO = ordineMapper.toDetailDTO(filteredOrder);

            return ResponseEntity.ok(ordineDTO);

        } catch (Exception e) {
            log.error("Error getting vendor order details - OrderId: {}, User: {}", id, authentication.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore interno",
                            "message", "Si è verificato un errore durante il recupero dell'ordine"));
        }
    }

    @PutMapping("/{id}/process")
    @PreAuthorize("hasAnyRole('PRODUTTORE', 'TRASFORMATORE', 'DISTRIBUTORE_TIPICITA')")
    public ResponseEntity<?> processOrder(
            @PathVariable Long id,
            Authentication authentication) {

        try {
            log.info("Processing order - OrderId: {}, Vendor: {}", id, authentication.getName());

            String email = authentication.getName();
            Venditore venditore = (Venditore) utenteService.trovaUtentePerEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utente non trovato"));

            Optional<Ordine> ordineOpt = ordineService.findOrdineById(id);
            if (ordineOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Ordine ordine = ordineOpt.get();

            // Check if vendor has access to this order
            boolean hasAccess = hasVendorAccess(ordine, venditore);

            if (!hasAccess) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Accesso negato",
                                "message", "Non hai accesso a questo ordine"));
            }

            // Check if order is in the correct state for processing
            if (ordine.getStatoOrdine() != StatoCorrente.PRONTO_PER_LAVORAZIONE) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Stato ordine non valido",
                                "message", "L'ordine deve essere pronto per la lavorazione per essere processato"));
            }

            // Use State pattern to process the order
            ordine.processa();
            ordineService.aggiornaOrdine(ordine);

            log.info("Order processed successfully - OrderId: {}, NewState: {}, Vendor: {}",
                    id, ordine.getStatoOrdine(), email);

            return ResponseEntity.ok(Map.of(
                    "message", "Ordine preso in carico per la lavorazione",
                    "statoNuovo", ordine.getStatoOrdine().toString(),
                    "ordineId", id));

        } catch (UnsupportedOperationException e) {
            log.error("Invalid state transition - OrderId: {}, Vendor: {}, Error: {}",
                    id, authentication.getName(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Transizione non valida", "message", e.getMessage()));
        } catch (OrdineException e) {
            log.error("Error processing order - OrderId: {}, Vendor: {}, Error: {}",
                    id, authentication.getName(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Errore nella lavorazione", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("Error processing order - OrderId: {}, Vendor: {}", id, authentication.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore interno",
                            "message", "Si è verificato un errore durante la lavorazione"));
        }
    }

    @PutMapping("/{id}/ship")
    @PreAuthorize("hasAnyRole('PRODUTTORE', 'TRASFORMATORE', 'DISTRIBUTORE_TIPICITA')")
    public ResponseEntity<?> shipOrder(
            @PathVariable Long id,
            @RequestBody(required = false) ShippingInfo shippingInfo,
            Authentication authentication) {

        try {
            log.info("Shipping order - OrderId: {}, Vendor: {}", id, authentication.getName());

            String email = authentication.getName();
            Venditore venditore = (Venditore) utenteService.trovaUtentePerEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utente non trovato"));

            Optional<Ordine> ordineOpt = ordineService.findOrdineById(id);
            if (ordineOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Ordine ordine = ordineOpt.get();

            // Check if vendor has access to this order
            boolean hasAccess = hasVendorAccess(ordine, venditore);

            if (!hasAccess) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Accesso negato",
                                "message", "Non hai accesso a questo ordine"));
            }

            // Check if order is in the correct state for shipping
            if (ordine.getStatoOrdine() != StatoCorrente.IN_LAVORAZIONE) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Stato ordine non valido",
                                "message", "L'ordine deve essere in lavorazione per essere spedito"));
            }

            // Use State pattern to ship the order
            ordine.spedisci();
            ordineService.aggiornaOrdine(ordine);

            log.info("Order shipped successfully - OrderId: {}, NewState: {}, Vendor: {}",
                    id, ordine.getStatoOrdine(), email);

            Map<String, Object> response = Map.of(
                    "message", "Ordine spedito con successo",
                    "statoNuovo", ordine.getStatoOrdine().toString(),
                    "ordineId", id,
                    "trackingNumber", shippingInfo != null && shippingInfo.getTrackingNumber() != null
                            ? shippingInfo.getTrackingNumber()
                            : "N/A");

            return ResponseEntity.ok(response);

        } catch (UnsupportedOperationException e) {
            log.error("Invalid state transition - OrderId: {}, Vendor: {}, Error: {}",
                    id, authentication.getName(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Transizione non valida", "message", e.getMessage()));
        } catch (OrdineException e) {
            log.error("Error shipping order - OrderId: {}, Vendor: {}, Error: {}",
                    id, authentication.getName(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Errore nella spedizione", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("Error shipping order - OrderId: {}, Vendor: {}", id, authentication.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore interno",
                            "message", "Si è verificato un errore durante la spedizione"));
        }
    }

    @GetMapping("/statistiche")
    @PreAuthorize("hasAnyRole('PRODUTTORE', 'TRASFORMATORE', 'DISTRIBUTORE_TIPICITA')")
    public ResponseEntity<?> getVendorOrderStats(Authentication authentication) {
        try {
            log.info("Getting vendor order statistics - User: {}", authentication.getName());

            String email = authentication.getName();
            Venditore venditore = (Venditore) utenteService.trovaUtentePerEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utente non trovato"));

            List<Ordine> ordiniVenditore = ordineService.getOrdiniVenditore(venditore);

            long totalOrders = ordiniVenditore.size();
            long pendingOrders = ordiniVenditore.stream()
                    .filter(o -> o.getStatoOrdine() == StatoCorrente.PRONTO_PER_LAVORAZIONE)
                    .count();
            long inProgressOrders = ordiniVenditore.stream()
                    .filter(o -> o.getStatoOrdine() == StatoCorrente.IN_LAVORAZIONE)
                    .count();
            long shippedOrders = ordiniVenditore.stream()
                    .filter(o -> o.getStatoOrdine() == StatoCorrente.SPEDITO)
                    .count();
            long deliveredOrders = ordiniVenditore.stream()
                    .filter(o -> o.getStatoOrdine() == StatoCorrente.CONSEGNATO)
                    .count();

            double totalRevenue = ordiniVenditore.stream()
                    .filter(o -> o.getStatoOrdine() != StatoCorrente.ANNULLATO)
                    .mapToDouble(Ordine::getImportoTotale)
                    .sum();

            Map<String, Object> stats = Map.of(
                    "totalOrders", totalOrders,
                    "pendingOrders", pendingOrders,
                    "inProgressOrders", inProgressOrders,
                    "shippedOrders", shippedOrders,
                    "deliveredOrders", deliveredOrders,
                    "totalRevenue", totalRevenue);

            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            log.error("Error getting vendor order statistics - User: {}", authentication.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore interno",
                            "message", "Si è verificato un errore durante il recupero delle statistiche"));
        }
    }

    private boolean isVendorProduct(RigaOrdine riga, Venditore venditore) {
        // Check if the product/package in this order line belongs to the vendor
        try {
            log.debug("Checking vendor ownership - RigaId: {}, VendorId: {}",
                    riga.getIdRiga(), venditore != null ? venditore.getIdUtente() : "null");

            if (venditore == null) {
                log.warn("Venditore is null");
                return false;
            }

            if (riga.getAcquistabile() == null) {
                log.warn("Acquistabile is null for RigaOrdine {} - trying to reload", riga.getIdRiga());

                // Force reload of the RigaOrdine with its Acquistabile relationship
                try {
                    Long ordineId = riga.getOrdine().getIdOrdine();
                    log.info("Reloading Ordine {} for RigaOrdine {}", ordineId, riga.getIdRiga());

                    // Get fresh copy from database with all relationships loaded
                    Optional<Ordine> ordineReloaded = ordineService.findOrdineById(ordineId);
                    if (ordineReloaded.isPresent()) {
                        log.info("Successfully reloaded Ordine {}, checking {} righe ordine",
                                ordineId, ordineReloaded.get().getRigheOrdine().size());

                        Long rigaId = riga.getIdRiga(); // Store the ID before lambda

                        // Log all RigaOrdine IDs in the reloaded order
                        log.info("Available RigaOrdine IDs in reloaded order: {}",
                                ordineReloaded.get().getRigheOrdine().stream()
                                        .map(r -> r.getIdRiga())
                                        .collect(Collectors.toList()));

                        // Find the corresponding RigaOrdine in the reloaded order
                        Optional<RigaOrdine> rigaReloaded = ordineReloaded.get().getRigheOrdine().stream()
                                .filter(r -> r.getIdRiga().equals(rigaId))
                                .findFirst();

                        if (rigaReloaded.isPresent()) {
                            RigaOrdine reloadedRiga = rigaReloaded.get();
                            log.info("Found reloaded RigaOrdine {} - Acquistabile is {}",
                                    rigaId, reloadedRiga.getAcquistabile() != null ? "NOT NULL" : "NULL");

                            // Check if AcquistabileService is injected
                            log.info(
                                    "RigaOrdine {} - TipoAcquistabile: {}, IdAcquistabile: {}, AcquistabileService: {}",
                                    rigaId,
                                    reloadedRiga.getTipoAcquistabile(),
                                    reloadedRiga.getIdAcquistabile(),
                                    "service is "
                                            + (reloadedRiga.toString().contains("acquistabileService") ? "available"
                                                    : "NOT available"));

                            // Force inject AcquistabileService if missing
                            try {
                                reloadedRiga.setAcquistabileService(
                                        ((it.unicam.cs.ids.piattaforma_agricola_locale.service.impl.OrdineService) ordineService)
                                                .getCarrelloService().getAcquistabileService());
                                log.info("Injected AcquistabileService into reloaded RigaOrdine {}", rigaId);

                                // Try again to get Acquistabile
                                if (reloadedRiga.getAcquistabile() != null) {
                                    log.info(
                                            "Successfully retrieved Acquistabile after service injection for RigaOrdine {}",
                                            rigaId);
                                    // Recursively call with the reloaded riga
                                    return isVendorProduct(reloadedRiga, venditore);
                                } else {
                                    log.error("Still null Acquistabile after service injection for RigaOrdine {}",
                                            rigaId);
                                    return false;
                                }
                            } catch (Exception serviceException) {
                                log.error("Failed to inject AcquistabileService: {}", serviceException.getMessage());
                                return false;
                            }
                        } else {
                            log.error("Could not find RigaOrdine {} in reloaded order", rigaId);
                            return false;
                        }
                    } else {
                        log.error("Failed to reload Ordine {} for RigaOrdine {}", ordineId, riga.getIdRiga());
                        return false;
                    }
                } catch (Exception reloadException) {
                    log.error("Exception during reload of RigaOrdine {}: {}", riga.getIdRiga(),
                            reloadException.getMessage(), reloadException);
                    return false;
                }
            }

            Venditore venditoreProdotto = riga.getAcquistabile().getVenditore();

            // Force lazy loading if needed
            if (venditoreProdotto != null) {
                venditoreProdotto.getIdUtente(); // This will trigger lazy loading
            }

            log.debug("Product details - AcquistabileId: {}, AcquistabileType: {}, VenditoreProdotto: {}",
                    riga.getAcquistabile().getId(),
                    riga.getAcquistabile().getClass().getSimpleName(),
                    venditoreProdotto != null ? venditoreProdotto.getIdUtente() : "null");

            if (venditoreProdotto == null) {
                log.warn("VenditoreProdotto is null for Acquistabile {} of type {}",
                        riga.getAcquistabile().getId(),
                        riga.getAcquistabile().getClass().getSimpleName());
                return false;
            }

            boolean isOwner = venditoreProdotto.getIdUtente().equals(venditore.getIdUtente());
            log.debug("Ownership check result: {} (ProductVendorId: {} vs RequestVendorId: {})",
                    isOwner, venditoreProdotto.getIdUtente(), venditore.getIdUtente());

            return isOwner;
        } catch (Exception e) {
            log.error("Error checking vendor ownership for order line - OrderLine: {}, Vendor: {}, Error: {}",
                    riga.getIdRiga(), venditore != null ? venditore.getIdUtente() : "null", e.getMessage(), e);
            return false;
        }
    }

    private Ordine createFilteredOrderForVendor(Ordine originalOrder, Venditore venditore) {

        Ordine filteredOrder = new Ordine();
        filteredOrder.setIdOrdine(originalOrder.getIdOrdine());
        filteredOrder.setDataOrdine(originalOrder.getDataOrdine());
        filteredOrder.setStatoCorrente(originalOrder.getStatoOrdine());
        filteredOrder.setAcquirente(originalOrder.getAcquirente());

        // Filter order lines to include only vendor's products
        List<RigaOrdine> filteredLines = originalOrder.getRigheOrdine().stream()
                .filter(riga -> isVendorProduct(riga, venditore))
                .collect(Collectors.toList());

        filteredOrder.setRigheOrdine(filteredLines);

        // Recalculate total for filtered order
        double filteredTotal = filteredLines.stream()
                .mapToDouble(riga -> riga.getQuantitaOrdinata() * riga.getPrezzoUnitario())
                .sum();
        filteredOrder.setImportoTotale(filteredTotal);

        return filteredOrder;
    }

    // Inner class for shipping information
    public static class ShippingInfo {
        private String trackingNumber;
        private String courier;
        private String estimatedDelivery;

        public String getTrackingNumber() {
            return trackingNumber;
        }

        public void setTrackingNumber(String trackingNumber) {
            this.trackingNumber = trackingNumber;
        }

        public String getCourier() {
            return courier;
        }

        public void setCourier(String courier) {
            this.courier = courier;
        }

        public String getEstimatedDelivery() {
            return estimatedDelivery;
        }

        public void setEstimatedDelivery(String estimatedDelivery) {
            this.estimatedDelivery = estimatedDelivery;
        }
    }

    // Inner class for delivery information
    public static class DeliveryInfo {
        private String deliveryDate;
        private String notes;
        private String recipientName;
        private String recipientSignature;

        public String getDeliveryDate() {
            return deliveryDate;
        }

        public void setDeliveryDate(String deliveryDate) {
            this.deliveryDate = deliveryDate;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public String getRecipientName() {
            return recipientName;
        }

        public void setRecipientName(String recipientName) {
            this.recipientName = recipientName;
        }

        public String getRecipientSignature() {
            return recipientSignature;
        }

        public void setRecipientSignature(String recipientSignature) {
            this.recipientSignature = recipientSignature;
        }
    }

    // Inner class for cancellation information
    public static class CancelInfo {
        private String reason;
        private String notes;
        private boolean refundRequested;

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public boolean isRefundRequested() {
            return refundRequested;
        }

        public void setRefundRequested(boolean refundRequested) {
            this.refundRequested = refundRequested;
        }
    }

    @PutMapping("/{id}/deliver")
    @PreAuthorize("hasAnyRole('PRODUTTORE', 'TRASFORMATORE', 'DISTRIBUTORE_TIPICITA')")
    public ResponseEntity<?> deliverOrder(
            @PathVariable Long id,
            @RequestBody(required = false) DeliveryInfo deliveryInfo,
            Authentication authentication) {

        try {
            log.info("Delivering order - OrderId: {}, Vendor: {}", id, authentication.getName());

            String email = authentication.getName();
            log.info("DELIVER AUTH - JWT Email: {}", email);

            // Debug: Check what the service actually returns
            Optional<Utente> utenteOpt = utenteService.trovaUtentePerEmail(email);
            if (utenteOpt.isEmpty()) {
                log.error("DELIVER AUTH - No user found for email: {}", email);
                throw new RuntimeException("Utente non trovato per email: " + email);
            }

            Utente utente = utenteOpt.get();
            log.info("DELIVER AUTH - Found Utente: ID={}, Email={}, Type={}",
                    utente.getIdUtente(), utente.getEmail(), utente.getClass().getSimpleName());

            if (!(utente instanceof Venditore)) {
                log.error("DELIVER AUTH - User is not a Venditore: {}", utente.getClass().getSimpleName());
                throw new RuntimeException("L'utente non è un venditore");
            }

            Venditore venditore = (Venditore) utente;
            log.info("DELIVER AUTH - Venditore cast successful: ID={}, Email={}",
                    venditore.getIdUtente(), venditore.getEmail());

            Optional<Ordine> ordineOpt = ordineService.findOrdineById(id);
            if (ordineOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Ordine ordine = ordineOpt.get();

            // Enhanced logging for debugging multi-vendor orders in DELIVER endpoint
            log.info("DELIVER - Vendor {} access check for order {} - Total lines: {}",
                    venditore.getIdUtente(), ordine.getIdOrdine(),
                    ordine.getRigheOrdine().size());

            // Check if vendor has access to this order using comprehensive logic
            boolean hasAccess = hasVendorAccess(ordine, venditore);

            if (!hasAccess) {
                log.warn("DELIVER - Access denied - Vendor {} has no access to order {}",
                        venditore.getIdUtente(), ordine.getIdOrdine());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Accesso negato",
                                "message", String.format("Il venditore %d non ha accesso all'ordine %d. " +
                                        "Verifica di essere autenticato con l'account corretto.",
                                        venditore.getIdUtente(), ordine.getIdOrdine())));
            }

            // Check if order is in the correct state for delivery
            if (ordine.getStatoOrdine() != StatoCorrente.SPEDITO) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Stato ordine non valido",
                                "message", "L'ordine deve essere spedito per la consegna"));
            }

            // Use State pattern to deliver the order
            ordine.consegna();
            ordineService.aggiornaOrdine(ordine);

            log.info("Order delivered successfully - OrderId: {}, NewState: {}, Vendor: {}",
                    id, ordine.getStatoOrdine(), email);

            Map<String, Object> response = Map.of(
                    "message", "Ordine consegnato con successo",
                    "statoNuovo", ordine.getStatoOrdine().toString(),
                    "ordineId", id,
                    "deliveryDate", deliveryInfo != null && deliveryInfo.getDeliveryDate() != null
                            ? deliveryInfo.getDeliveryDate()
                            : new java.util.Date().toString(),
                    "deliveryNotes", deliveryInfo != null && deliveryInfo.getNotes() != null
                            ? deliveryInfo.getNotes()
                            : "");

            return ResponseEntity.ok(response);

        } catch (UnsupportedOperationException e) {
            log.error("Invalid state transition - OrderId: {}, Vendor: {}, Error: {}",
                    id, authentication.getName(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Transizione non valida", "message", e.getMessage()));
        } catch (OrdineException e) {
            log.error("Error delivering order - OrderId: {}, Vendor: {}, Error: {}",
                    id, authentication.getName(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Errore nella consegna", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("Error delivering order - OrderId: {}, Vendor: {}", id, authentication.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore interno",
                            "message", "Si è verificato un errore durante la consegna"));
        }
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('PRODUTTORE', 'TRASFORMATORE', 'DISTRIBUTORE_TIPICITA')")
    public ResponseEntity<?> cancelOrder(
            @PathVariable Long id,
            @RequestBody(required = false) CancelInfo cancelInfo,
            Authentication authentication) {

        try {
            log.info("Cancelling order - OrderId: {}, Vendor: {}", id, authentication.getName());

            String email = authentication.getName();
            Venditore venditore = (Venditore) utenteService.trovaUtentePerEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utente non trovato"));

            Optional<Ordine> ordineOpt = ordineService.findOrdineById(id);
            if (ordineOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Ordine ordine = ordineOpt.get();

            // Check if vendor has access to this order
            boolean hasAccess = hasVendorAccess(ordine, venditore);

            if (!hasAccess) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Accesso negato",
                                "message", "Non hai accesso a questo ordine"));
            }

            // Check if order can be cancelled (not delivered or already cancelled)
            if (ordine.getStatoOrdine() == StatoCorrente.CONSEGNATO) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Cancellazione non consentita",
                                "message", "Non è possibile cancellare un ordine già consegnato"));
            }

            if (ordine.getStatoOrdine() == StatoCorrente.ANNULLATO) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Ordine già cancellato",
                                "message", "L'ordine è già stato cancellato"));
            }

            // Check if order is shipped (needs special handling)
            if (ordine.getStatoOrdine() == StatoCorrente.SPEDITO) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Cancellazione non consentita",
                                "message",
                                "Non è possibile cancellare un ordine già spedito. Contattare il supporto clienti."));
            }

            // Use State pattern to cancel the order
            ordine.annulla();
            ordineService.aggiornaOrdine(ordine);

            log.info("Order cancelled successfully - OrderId: {}, NewState: {}, Vendor: {}",
                    id, ordine.getStatoOrdine(), email);

            Map<String, Object> response = Map.of(
                    "message", "Ordine cancellato con successo",
                    "statoNuovo", ordine.getStatoOrdine().toString(),
                    "ordineId", id,
                    "cancelReason", cancelInfo != null && cancelInfo.getReason() != null
                            ? cancelInfo.getReason()
                            : "Non specificato",
                    "cancelDate", new java.util.Date().toString());

            return ResponseEntity.ok(response);

        } catch (UnsupportedOperationException e) {
            log.error("Invalid cancellation - OrderId: {}, Vendor: {}, Error: {}",
                    id, authentication.getName(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Cancellazione non valida", "message", e.getMessage()));
        } catch (OrdineException e) {
            log.error("Error cancelling order - OrderId: {}, Vendor: {}, Error: {}",
                    id, authentication.getName(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Errore nella cancellazione", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("Error cancelling order - OrderId: {}, Vendor: {}", id, authentication.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore interno",
                            "message", "Si è verificato un errore durante la cancellazione"));
        }
    }

    /**
     * Comprehensive access check for vendor orders.
     * Uses multiple criteria to determine if a vendor has access to an order:
     * 1. Direct order-vendor relationship (order.venditore)
     * 2. Product ownership in order lines (product.venditore)
     *
     * @param ordine    the order to check
     * @param venditore the vendor requesting access
     * @return true if vendor has access, false otherwise
     */
    private boolean hasVendorAccess(Ordine ordine, Venditore venditore) {
        if (ordine == null || venditore == null) {
            log.warn("Null parameters in hasVendorAccess - Order: {}, Vendor: {}",
                    ordine != null ? ordine.getIdOrdine() : "null",
                    venditore != null ? venditore.getIdUtente() : "null");
            return false;
        }

        // Check 1: Direct order-vendor relationship
        boolean isDirectOrderVendor = ordine.getVenditore() != null &&
                ordine.getVenditore().getIdUtente().equals(venditore.getIdUtente());

        // Check 2: Vendor has products in this order
        boolean hasProductsInOrder = ordine.getRigheOrdine().stream()
                .anyMatch(riga -> isVendorProduct(riga, venditore));

        log.info("ACCESS CHECK - Order: {}, Vendor: {}, DirectVendor: {}, HasProducts: {}, Access: {}",
                ordine.getIdOrdine(), venditore.getIdUtente(),
                isDirectOrderVendor, hasProductsInOrder,
                isDirectOrderVendor || hasProductsInOrder);

        // Grant access if EITHER condition is true
        return isDirectOrderVendor || hasProductsInOrder;
    }

    /**
     * Enhanced method to check if vendor has products in order and get
     * vendor-specific order details
     */
    private VendorOrderInfo getVendorOrderInfo(Ordine ordine, Venditore venditore) {
        List<RigaOrdine> vendorLines = ordine.getRigheOrdine().stream()
                .filter(riga -> isVendorProduct(riga, venditore))
                .collect(Collectors.toList());

        boolean hasProducts = !vendorLines.isEmpty();
        boolean allVendorProductsInSameState = true;

        if (hasProducts && vendorLines.size() > 1) {
            // If there are multiple products, they should all be in the same fulfillment
            // state
            // For simplicity, we assume they are all ready if any is ready
            log.debug("Vendor {} has {} products in order {}",
                    venditore.getIdUtente(), vendorLines.size(), ordine.getIdOrdine());
        }

        return new VendorOrderInfo(hasProducts, vendorLines, allVendorProductsInSameState);
    }

    /**
     * Inner class to hold vendor-specific order information
     */
    private static class VendorOrderInfo {
        private final boolean hasProducts;
        private final List<RigaOrdine> vendorLines;
        private final boolean allInSameState;

        public VendorOrderInfo(boolean hasProducts, List<RigaOrdine> vendorLines, boolean allInSameState) {
            this.hasProducts = hasProducts;
            this.vendorLines = vendorLines;
            this.allInSameState = allInSameState;
        }

        public boolean hasProducts() {
            return hasProducts;
        }

        public List<RigaOrdine> getVendorLines() {
            return vendorLines;
        }

        public boolean areAllInSameState() {
            return allInSameState;
        }
    }
}
