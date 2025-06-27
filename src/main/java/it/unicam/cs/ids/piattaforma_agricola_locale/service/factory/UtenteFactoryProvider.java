package it.unicam.cs.ids.piattaforma_agricola_locale.service.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IUtenteBaseRepository;

/**
 * Provider per le factory di utenti.
 * Questa classe fornisce un punto di accesso centralizzato
 * per ottenere istanze di UtenteFactory.
 */
@Component
public class UtenteFactoryProvider {
    
    private final IUtenteBaseRepository utenteRepository;
    
    /**
     * Costruttore per l'iniezione delle dipendenze.
     * 
     * @param utenteRepository Repository degli utenti
     */
    @Autowired
    public UtenteFactoryProvider(IUtenteBaseRepository utenteRepository) {
        this.utenteRepository = utenteRepository;
    }
    
    /**
     * Ottiene una factory di utenti standard.
     * 
     * @return Una factory di utenti standard
     */
    public UtenteFactory getUtenteFactory() {
        return new SimpleUtenteFactory(utenteRepository);
    }
    
    /**
     * Ottiene una factory di utenti pronta per Spring Boot.
     * 
     * @return Una factory di utenti pronta per Spring Boot
     */
    public UtenteFactory getSpringReadyUtenteFactory() {
        return new SpringReadyUtenteFactory(utenteRepository);
    }
    
    /**
     * Ottiene una factory di utenti personalizzata con un repository specifico.
     * 
     * @param repository Repository da utilizzare
     * @return Una factory di utenti personalizzata
     */
    public UtenteFactory getUtenteFactory(IUtenteBaseRepository repository) {
        return new SimpleUtenteFactory(repository);
    }
    
    /**
     * Ottiene il repository di utenti utilizzato dal provider.
     * 
     * @return Il repository di utenti
     */
    public IUtenteBaseRepository getUtenteRepository() {
        return utenteRepository;
    }
}