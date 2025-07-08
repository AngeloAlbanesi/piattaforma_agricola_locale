package it.unicam.cs.ids.piattaforma_agricola_locale.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disabilita CSRF (Cross-Site Request Forgery) siccome usiamo JWT stateless
                .csrf(AbstractHttpConfigurer::disable)

                // Definisce le regole di autorizzazione per le richieste HTTP
                .authorizeHttpRequests(auth -> auth
                        // Permetti a chiunque di accedere agli endpoint di autenticazione, CONSOLE H2,
                        // SWAGGER e endpoint pubblici delle aziende
                        .requestMatchers(
                                new AntPathRequestMatcher("/api/auth/**"),
                                new AntPathRequestMatcher("/api/azienda/{id}/geocode"),
                                new AntPathRequestMatcher("/api/azienda/{id}/distanza"),
                                new AntPathRequestMatcher("/api/azienda/tutte"),
                                new AntPathRequestMatcher("/api/azienda/cercaAzienda"),
                                new AntPathRequestMatcher("/api/azienda/*/prodotti"),
                                new AntPathRequestMatcher("/api/azienda/*/certificazioni"),
                                new AntPathRequestMatcher("/api/azienda/*"),
                                new AntPathRequestMatcher("/h2-console/**"),
                                new AntPathRequestMatcher("/swagger-ui/**"),
                                new AntPathRequestMatcher("/swagger-ui.html"),
                                new AntPathRequestMatcher("/v3/api-docs/**"),
                                new AntPathRequestMatcher("/swagger-resources/**"),
                                new AntPathRequestMatcher("/webjars/**"))
                        .permitAll()
                        // Qualsiasi altra richiesta deve essere autenticata
                        .anyRequest().authenticated())

                // Configura la gestione della sessione come STATELESS
                // Spring non creerà né userà sessioni HTTP
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Imposta il provider di autenticazione che abbiamo configurato in
                // ApplicationConfig
                .authenticationProvider(authenticationProvider)

                // Aggiunge il nostro filtro JWT prima del filtro standard di Spring
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

        return http.build();
    }
}