package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.observer.ICuratoreObserver;

/**
 * Implementazione concreta dell'observer per la gestione automatica
 * della coda di revisione dei prodotti da parte del curatore.
 * 
 * Questa classe implementa il pattern Observer per ricevere notifiche
 * quando vengono creati nuovi prodotti che necessitano di revisione,
 * e li aggiunge automaticamente alla coda del CuratoreService.
 */
public class CuratoreObserverService implements ICuratoreObserver {

    private final CuratoreService curatoreService;

    /**
     * Costruttore che inietta il CuratoreService per gestire la coda di revisione.
     * 
     * @param curatoreService il servizio del curatore che gestisce la coda di
     *                        revisione
     * @throws IllegalArgumentException se curatoreService è null
     */
    public CuratoreObserverService(CuratoreService curatoreService) {
        if (curatoreService == null) {
            throw new IllegalArgumentException("CuratoreService non può essere null");
        }
        this.curatoreService = curatoreService;
    }

    /**
     * Costruttore di default che crea una nuova istanza di CuratoreService.
     * Utile per test e per utilizzi semplificati.
     */
    public CuratoreObserverService() {
        this.curatoreService = new CuratoreService();
    }

    @Override
    public void onProdottoCreato(Prodotto prodotto) {
        if (prodotto == null) {
            throw new IllegalArgumentException("Il prodotto non può essere null");
        }

        // Verifica che il prodotto sia effettivamente in stato IN_REVISIONE
        if (prodotto
                .getStatoVerifica() == it.unicam.cs.ids.piattaforma_agricola_locale.model.common.StatoVerificaValori.IN_REVISIONE) {
            try {
                // Aggiunge il prodotto alla coda di revisione interna del curatore
                curatoreService.aggiungiProdottoALlaCodeDiRevisione(prodotto);

                // Log di conferma (opzionale)
                System.out.println("Prodotto '" + prodotto.getNome() + "' (ID: " + prodotto.getId() +
                        ") aggiunto automaticamente alla coda di revisione del curatore");

            } catch (Exception e) {
                // Gestione errori senza propagare l'eccezione per non bloccare altri observer
                System.err.println("Errore durante l'aggiunta del prodotto alla coda di revisione: " + e.getMessage());
            }
        }
    }

    /**
     * Getter per il CuratoreService utilizzato da questo observer.
     * Utile per test e debugging.
     * 
     * @return il CuratoreService associato a questo observer
     */
    public CuratoreService getCuratoreService() {
        return curatoreService;
    }
}