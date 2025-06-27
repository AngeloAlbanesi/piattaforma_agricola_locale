package it.unicam.cs.ids.piattaforma_agricola_locale.security.config;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IUtenteBaseRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Utente; // Assicurati che l'import sia corretto
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails; // Import corretto
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final IUtenteBaseRepository utenteRepository;

    /**
     * Definisce come caricare i dettagli di un utente.
     * Spring Security userà questo bean ogni volta che dovrà trovare un utente
     * dato il suo username (in questo caso, l'email).
     *
     * @return un'implementazione di UserDetailsService.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        // Questa implementazione è più esplicita e risolve l'errore di tipo.
        // Cerca l'utente nel repository. Poiché Utente implementerà UserDetails,
        // possiamo restituirlo direttamente.
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return utenteRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato con email: " + username));
            }
        };
    }

    /**
     * Definisce l'algoritmo di hashing per le password.
     * Useremo BCrypt, che è lo standard de-facto per la memorizzazione sicura delle password.
     *
     * @return un'istanza di BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Definisce il provider di autenticazione.
     * Questo componente è responsabile di recuperare i dettagli dell'utente (tramite UserDetailsService)
     * e verificare la password (tramite PasswordEncoder).
     *
     * @return un'istanza di DaoAuthenticationProvider configurata.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Espone l'AuthenticationManager come bean.
     * L'AuthenticationManager è il componente principale che processa una richiesta di autenticazione.
     * Lo useremo direttamente nel nostro servizio di autenticazione per il login.
     *
     * @param config l'oggetto di configurazione dell'autenticazione di Spring.
     * @return l'AuthenticationManager.
     * @throws Exception se si verifica un errore durante la configurazione.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}