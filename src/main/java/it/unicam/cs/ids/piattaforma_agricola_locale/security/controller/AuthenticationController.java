package it.unicam.cs.ids.piattaforma_agricola_locale.security.controller;

import it.unicam.cs.ids.piattaforma_agricola_locale.security.dto.AuthenticationResponse;
import it.unicam.cs.ids.piattaforma_agricola_locale.security.dto.LoginRequest;
import it.unicam.cs.ids.piattaforma_agricola_locale.security.dto.RegisterRequest;
import it.unicam.cs.ids.piattaforma_agricola_locale.security.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(authenticationService.login(request));
    }
}