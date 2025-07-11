package it.unicam.cs.ids.piattaforma_agricola_locale.security.service;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IUtenteBaseRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Utente;
import it.unicam.cs.ids.piattaforma_agricola_locale.security.dto.AuthenticationResponse;
import it.unicam.cs.ids.piattaforma_agricola_locale.security.dto.LoginRequest;
import it.unicam.cs.ids.piattaforma_agricola_locale.security.dto.RegisterRequest;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.factory.UtenteFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UtenteFactory utenteFactory;
    private final IUtenteBaseRepository utenteRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        Utente utente = utenteFactory.creaUtente(
                request.getTipoRuolo(),
                request.getNome(),
                request.getCognome(),
                request.getEmail(),
                request.getPassword(),
                request.getNumeroTelefono(),
                request.getDatiAzienda()
        );

        var jwtToken = jwtService.generateToken(utente);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .idUtente(utente.getIdUtente())
                .build();
    }

    public AuthenticationResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var utente = utenteRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalStateException("Utente non trovato dopo autenticazione riuscita."));

        var jwtToken = jwtService.generateToken(utente);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .idUtente(utente.getIdUtente())
                .build();
    }
}