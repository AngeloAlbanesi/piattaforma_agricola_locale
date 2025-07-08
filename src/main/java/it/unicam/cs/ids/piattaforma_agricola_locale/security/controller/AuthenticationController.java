package it.unicam.cs.ids.piattaforma_agricola_locale.security.controller;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.utente.UserDetailDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.utente.UserUpdateDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.security.dto.AuthenticationResponse;
import it.unicam.cs.ids.piattaforma_agricola_locale.security.dto.LoginRequest;
import it.unicam.cs.ids.piattaforma_agricola_locale.security.dto.RegisterRequest;
import it.unicam.cs.ids.piattaforma_agricola_locale.security.service.AuthenticationService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IUtenteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final IUtenteService utenteService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    )
    {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDetailDTO> getProfile(Authentication authentication) {
        String email = authentication.getName();
        UserDetailDTO userProfile = utenteService.getUtenteDetailByEmail(email);
        return ResponseEntity.ok(userProfile);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserDetailDTO> updateProfile(
            @Valid @RequestBody UserUpdateDTO updateRequest,
            Authentication authentication) {
        String email = authentication.getName();
        UserDetailDTO updatedProfile = utenteService.updateUtente(email, updateRequest);
        return ResponseEntity.ok(updatedProfile);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Logout successful. Please discard your JWT token.");
    }
}