package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
@Service
public class CuratoreObserverService implements ICuratoreObserver {

    private final CuratoreService curatoreService;

    /**
     * Costruttore che inietta il CuratoreService per gestire la coda di revisione.
     * 
     * @param curatoreService il servizio del curatore che gestisce la coda di
     *                        revisione
     * @throws IllegalArgumentException se curatoreService è null
     */
    @Autowired
    public CuratoreObserverService(CuratoreService curatoreService) {
        if (curatoreService == null) {
            throw new IllegalArgumentException("CuratoreService non può essere null");
        }
        this.curatoreService = curatoreService;
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
                curatoreService.aggiungiProdottoALlaCodeDiRevisione(prodotto);
            } catch (Exception e) {
                e.printStackTrace();
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