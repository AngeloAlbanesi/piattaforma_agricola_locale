package it.unicam.cs.ids.piattaforma_agricola_locale.controller;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.ordine.OrdineDetailDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.ordine.OrdineSummaryDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.exception.OrdineException;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.RigaOrdine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.StatoCorrente;
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
    public ResponseEntity<Page<OrdineSummaryDTO>> getVendorOrders(
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

            // Convert to DTOs
            List<OrdineSummaryDTO> ordiniDTO = ordiniVenditore.stream()
                    .map(ordineMapper::toSummaryDTO)
                    .collect(Collectors.toList());

            // Apply sorting
            Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

            // Manual pagination
            Pageable pageable = PageRequest.of(page, size, sort);
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), ordiniDTO.size());

            List<OrdineSummaryDTO> pageContent = ordiniDTO.subList(start, end);
            Page<OrdineSummaryDTO> ordiniPage = new PageImpl<>(pageContent, pageable, ordiniDTO.size());

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

            // Check if the vendor has products in this order
            boolean hasProductsInOrder = ordine.getRigheOrdine().stream()
                    .anyMatch(riga -> isVendorProduct(riga, venditore));

            if (!hasProductsInOrder) {
                log.warn("Unauthorized access to order - OrderId: {}, Vendor: {}", id, email);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Accesso negato",
                                "message", "Non hai prodotti in questo ordine"));
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

    @PutMapping("/{id}/completa")
    @PreAuthorize("hasAnyRole('PRODUTTORE', 'TRASFORMATORE', 'DISTRIBUTORE_TIPICITA')")
    public ResponseEntity<?> fulfillOrder(
            @PathVariable Long id,
            Authentication authentication) {

        try {
            log.info("Fulfilling order - OrderId: {}, Vendor: {}", id, authentication.getName());

            String email = authentication.getName();
            Venditore venditore = (Venditore) utenteService.trovaUtentePerEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utente non trovato"));

            Optional<Ordine> ordineOpt = ordineService.findOrdineById(id);
            if (ordineOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Ordine ordine = ordineOpt.get();

            // Check if the vendor has products in this order
            boolean hasProductsInOrder = ordine.getRigheOrdine().stream()
                    .anyMatch(riga -> isVendorProduct(riga, venditore));

            if (!hasProductsInOrder) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Accesso negato",
                                "message", "Non hai prodotti in questo ordine"));
            }

            // Check if order is in the correct state
            if (ordine.getStatoOrdine() != StatoCorrente.PRONTO_PER_LAVORAZIONE &&
                    ordine.getStatoOrdine() != StatoCorrente.IN_LAVORAZIONE) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Stato ordine non valido",
                                "message", "L'ordine non è nello stato corretto per essere evaso"));
            }

            // Update order state to IN_LAVORAZIONE (if not already)
            if (ordine.getStatoOrdine() == StatoCorrente.PRONTO_PER_LAVORAZIONE) {
                ordine.setStatoCorrente(StatoCorrente.IN_LAVORAZIONE);
                ordineService.aggiornaOrdine(ordine);
            }

            log.info("Order marked as in fulfillment - OrderId: {}, Vendor: {}", id, email);
            return ResponseEntity.ok(Map.of("message", "Ordine preso in carico per l'evasione"));

        } catch (OrdineException e) {
            log.error("Error fulfilling order - OrderId: {}, Vendor: {}, Error: {}",
                    id, authentication.getName(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Errore nell'evasione", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("Error fulfilling order - OrderId: {}, Vendor: {}", id, authentication.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Errore interno",
                            "message", "Si è verificato un errore durante l'evasione"));
        }
    }

    @PutMapping("/{id}/spedisci")
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

            // Check if the vendor has products in this order
            boolean hasProductsInOrder = ordine.getRigheOrdine().stream()
                    .anyMatch(riga -> isVendorProduct(riga, venditore));

            if (!hasProductsInOrder) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Accesso negato",
                                "message", "Non hai prodotti in questo ordine"));
            }

            // Check if order is in the correct state
            if (ordine.getStatoOrdine() != StatoCorrente.IN_LAVORAZIONE) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Stato ordine non valido",
                                "message", "L'ordine deve essere in lavorazione per essere spedito"));
            }

            // Update order state to SPEDITO
            ordine.setStatoCorrente(StatoCorrente.SPEDITO);
            ordineService.aggiornaOrdine(ordine);

            log.info("Order marked as shipped - OrderId: {}, Vendor: {}", id, email);

            Map<String, Object> response = Map.of(
                    "message", "Ordine marcato come spedito",
                    "trackingNumber", shippingInfo != null ? shippingInfo.getTrackingNumber() : "N/A"
            );

            return ResponseEntity.ok(response);

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
                    "totalRevenue", totalRevenue
            );

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
        // This would depend on the specific implementation of your Acquistabile hierarchy
        // For now, we'll assume products have a reference to their vendor
        try {
            return riga.getAcquistabile() != null &&
                    riga.getAcquistabile().toString().contains(venditore.getIdUtente().toString());
        } catch (Exception e) {
            log.warn("Error checking vendor ownership for order line", e);
            return false;
        }
    }

    private Ordine createFilteredOrderForVendor(Ordine originalOrder, Venditore venditore) {
        // Create a copy of the order with only the vendor's products
        // This is a simplified implementation - in a real scenario you might want to create
        // a proper deep copy or use a builder pattern
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
}
