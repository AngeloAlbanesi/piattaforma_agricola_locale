package it.unicam.cs.ids.piattaforma_agricola_locale.controller;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.utente.ProduttoreSummaryDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Produttore;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Utente;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IUtenteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller per la gestione degli utenti e informazioni pubbliche.
 */
@RestController
@RequestMapping("/api/utenti")
@RequiredArgsConstructor
@Slf4j
public class UtenteController {

    private final IUtenteService utenteService;

    /**
     * Ottiene la lista di tutti i produttori registrati.
     * Questo endpoint è utile per selezionare un produttore come fonte di materia
     * prima.
     *
     * @return Lista di produttori con id, nome, cognome e azienda
     */
    @GetMapping("/produttori")
    public ResponseEntity<List<ProduttoreSummaryDTO>> getAllProduttori() {
        log.info("Fetching all produttori");

        List<Utente> tuttiUtenti = utenteService.trovaTuttiGliUtenti();

        List<ProduttoreSummaryDTO> produttori = tuttiUtenti.stream()
                .filter(utente -> utente instanceof Produttore)
                .map(utente -> {
                    Produttore produttore = (Produttore) utente;
                    String nomeAzienda = (produttore.getDatiAzienda() != null &&
                            produttore.getDatiAzienda().getNomeAzienda() != null)
                                    ? produttore.getDatiAzienda().getNomeAzienda()
                                    : "N/D";

                    return new ProduttoreSummaryDTO(
                            produttore.getId(),
                            produttore.getNome(),
                            produttore.getCognome(),
                            nomeAzienda);
                })
                .collect(Collectors.toList());

        log.info("Found {} produttori", produttori.size());

        return ResponseEntity.ok(produttori);
    }
}
