package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.Acquistabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Pacchetto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.RigaOrdine;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.observer.IVenditoreObserver;

import java.util.List;

/**
 * Implementazione concreta di {@link IVenditoreObserver} che gestisce la logica
 * di aggiornamento dell'inventario quando vengono creati nuovi ordini.
 * 
 * Questa classe implementa il pattern Observer per permettere ai venditori
 * di reagire automaticamente alle notifiche d'ordine, decrementando
 * le quantità disponibili dei prodotti e pacchetti ordinati.
 */
public class VenditoreObserverService implements IVenditoreObserver {

    private final ProdottoService prodottoService;
    private final PacchettoService pacchettoService;


    /**
     * Costruttore per il testing che permette di iniettare mock dependencies.
     * 
     * @param prodottoService  il service per gestire i prodotti
     * @param pacchettoService il service per gestire i pacchetti
     */
    public VenditoreObserverService(ProdottoService prodottoService, PacchettoService pacchettoService) {
        this.prodottoService = prodottoService;
        this.pacchettoService = pacchettoService;
    }

    /**
     * Metodo di callback invocato quando un ordine viene creato o modificato.
     * Per ogni riga d'ordine di competenza del venditore, decrementa la quantità
     * disponibile del prodotto o pacchetto corrispondente.
     * 
     * @param ordine            l'ordine che ha subito modifiche
     * @param righeDiCompetenza la lista delle righe d'ordine che contengono
     *                          prodotti di competenza del venditore
     * @throws IllegalArgumentException se l'ordine o la lista delle righe è null
     */
    @Override
    public void update(Ordine ordine, List<RigaOrdine> righeDiCompetenza) {
        // Validazione parametri di input
        if (ordine == null) {
            throw new IllegalArgumentException("L'ordine non può essere null");
        }
        if (righeDiCompetenza == null) {
            throw new IllegalArgumentException("La lista delle righe di competenza non può essere null");
        }

        // Processa ogni riga d'ordine di competenza del venditore
        for (RigaOrdine riga : righeDiCompetenza) {
            if (riga == null || riga.getAcquistabile() == null) {
                continue; // Salta righe null o con acquistabile null
            }

            Acquistabile acquistabile = riga.getAcquistabile();
            int quantitaOrdinata = riga.getQuantitaOrdinata();

            try {
                // Gestisce il decremento in base al tipo di acquistabile
                if (acquistabile instanceof Prodotto) {
                    Prodotto prodotto = (Prodotto) acquistabile;
                    prodottoService.decrementaQuantita(prodotto.getId(), quantitaOrdinata);
                } else if (acquistabile instanceof Pacchetto) {
                    Pacchetto pacchetto = (Pacchetto) acquistabile;
                    pacchettoService.decrementaQuantita(pacchetto.getId(), quantitaOrdinata);
                }
                // Altri tipi di acquistabili possono essere aggiunti qui in futuro

            } catch (Exception e) {
                // Log dell'errore ma continua con le altre righe
                System.err.println("Errore durante il decremento della quantità per l'acquistabile "
                        + acquistabile.getId() + ": " + e.getMessage());
                // In un'implementazione reale, qui si dovrebbe utilizzare un logger appropriato
            }
        }
    }

    /**
     * Getter per il ProdottoService (utile per il testing).
     * 
     * @return il ProdottoService utilizzato da questo handler
     */
    public ProdottoService getProdottoService() {
        return prodottoService;
    }

    /**
     * Getter per il PacchettoService (utile per il testing).
     * 
     * @return il PacchettoService utilizzato da questo handler
     */
    public PacchettoService getPacchettoService() {
        return pacchettoService;
    }
}
