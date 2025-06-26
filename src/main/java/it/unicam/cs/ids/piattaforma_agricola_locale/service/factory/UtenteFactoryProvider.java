package it.unicam.cs.ids.piattaforma_agricola_locale.service.factory;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IUtenteBaseRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.UtenteBaseRepository;

/**
 * Provider per le factory di utenti.
 * Questa classe implementa il pattern Singleton e fornisce un punto di accesso centralizzato
 * per ottenere istanze di UtenteFactory.
 */
public class UtenteFactoryProvider {
    
    private static UtenteFactoryProvider instance;
    private final IUtenteBaseRepository utenteRepository;
    
    /**
     * Costruttore privato per il pattern Singleton.
     */
    private UtenteFactoryProvider() {
        this.utenteRepository = new UtenteBaseRepository();
    }
    
    /**
     * Ottiene l'istanza singleton del provider.
     * 
     * @return L'istanza singleton del provider
     */
    public static synchronized UtenteFactoryProvider getInstance() {
        if (instance == null) {
            instance = new UtenteFactoryProvider();
        }
        return instance;
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