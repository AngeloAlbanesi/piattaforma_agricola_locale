package it.unicam.cs.ids.piattaforma_agricola_locale.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import it.unicam.cs.ids.piattaforma_agricola_locale.service.impl.OrdineService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.impl.VenditoreObserverService;
import jakarta.annotation.PostConstruct;

/**
 * Configurazione per la registrazione degli observer nel sistema.
 * 
 * Questa classe si occupa di registrare automaticamente gli observer
 * necessari durante l'inizializzazione dell'applicazione.
 */
@Configuration
public class ObserverConfig {

    private final OrdineService ordineService;
    private final VenditoreObserverService venditoreObserverService;

    @Autowired
    public ObserverConfig(OrdineService ordineService, VenditoreObserverService venditoreObserverService) {
        this.ordineService = ordineService;
        this.venditoreObserverService = venditoreObserverService;
    }

    /**
     * Registra tutti gli observer necessari dopo l'inizializzazione dei bean.
     * 
     * Questo metodo viene chiamato automaticamente da Spring dopo che tutti
     * i bean sono stati creati e le dipendenze iniettate.
     */
    @PostConstruct
    public void registerObservers() {
        // Registra il VenditoreObserverService come observer dell'OrdineService
        // Questo permetterà di scalare automaticamente le quantità dei prodotti
        // quando vengono creati nuovi ordini
        ordineService.aggiungiObserver(venditoreObserverService);
        
        System.out.println("Observer registrati con successo:");
        System.out.println("- VenditoreObserverService registrato in OrdineService");
    }
}