package it.unicam.cs.ids.piattaforma_agricola_locale.service.factory;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IUtenteBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Provider per le factory di utenti.
 * Questa classe fornisce un punto di accesso centralizzato
 * per ottenere istanze di UtenteFactory.
 */
@Component
public class UtenteFactoryProvider {

    private final IUtenteBaseRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Costruttore per l'iniezione delle dipendenze.
     *
     * @param utenteRepository Repository degli utenti
     * @param passwordEncoder  Encoder per le password
     */
    @Autowired
    public UtenteFactoryProvider(IUtenteBaseRepository utenteRepository, PasswordEncoder passwordEncoder) {
        this.utenteRepository = utenteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Ottiene una factory di utenti standard.
     *
     * @return Una factory di utenti standard
     */
    public UtenteFactory getUtenteFactory() {
        return getSpringReadyUtenteFactory();
    }

    /**
     * Ottiene una factory di utenti pronta per Spring Boot.
     *
     * @return Una factory di utenti pronta per Spring Boot
     */
    public UtenteFactory getSpringReadyUtenteFactory() {

        return new SpringReadyUtenteFactory(utenteRepository, passwordEncoder);
    }

    /**
     * Ottiene una factory di utenti personalizzata con un repository specifico.
     *
     * @param repository Repository da utilizzare
     * @return Una factory di utenti personalizzata
     */
    public UtenteFactory getUtenteFactory(IUtenteBaseRepository repository) {
        return new SpringReadyUtenteFactory(repository, passwordEncoder);
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