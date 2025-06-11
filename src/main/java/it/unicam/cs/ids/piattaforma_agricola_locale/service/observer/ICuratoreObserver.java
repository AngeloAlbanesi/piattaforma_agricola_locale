package it.unicam.cs.ids.piattaforma_agricola_locale.service.observer;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;

/**
 * Interfaccia che definisce il contratto per un observer
 * nel pattern Observer per la gestione delle notifiche di creazione prodotti.
 * 
 * Implementa il lato Observer del pattern, definendo il metodo
 * di callback che viene invocato quando un nuovo prodotto viene creato
 * e necessita di essere assegnato al curatore per la revisione.
 */
public interface ICuratoreObserver {
    
    /**
     * Metodo di callback invocato quando un nuovo prodotto viene creato
     * e necessita di essere aggiunto alla coda di revisione del curatore.
     * 
     * Questo metodo viene chiamato automaticamente dal Subject
     * (ProdottoObservable) quando si verifica la creazione di un nuovo prodotto
     * con stato IN_REVISIONE.
     * 
     * @param prodotto il prodotto appena creato che necessita di revisione.
     *                 Il prodotto avrà sempre stato StatoVerificaValori.IN_REVISIONE
     *                 al momento della notifica.
     * @throws IllegalArgumentException se il prodotto è null
     */
    void onProdottoCreato(Prodotto prodotto);
}