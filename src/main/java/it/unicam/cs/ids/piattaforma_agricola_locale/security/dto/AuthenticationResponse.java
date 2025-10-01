package it.unicam.cs.ids.piattaforma_agricola_locale.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    @JsonProperty("token")
    private String accessToken;
    
    @JsonProperty("id")
    private Long idUtente;
    
    @JsonProperty("username")
    private String username;
    
    private String email;
    
    private String[] roles;
    
    @JsonProperty("type")
    private String tokenType;

}