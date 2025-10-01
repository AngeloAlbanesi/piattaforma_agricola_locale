package it.unicam.cs.ids.piattaforma_agricola_locale.security.dto;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.TipoRuolo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String nome;
    private String cognome;
    private String email;
    private String password;
    
    @JsonProperty("username")
    private String username;
    
    @JsonProperty("telefono")
    private String numeroTelefono;
    
    @JsonProperty("ruolo")
    private String ruoloString;
    
    @JsonProperty("indirizzo")
    private String indirizzo;
    
    // Manteniamo per compatibilità con il codice esistente
    private TipoRuolo tipoRuolo;
    private DatiAzienda datiAzienda;
    
    // Metodo per convertire la stringa del ruolo in TipoRuolo
    public TipoRuolo getTipoRuolo() {
        if (ruoloString != null) {
            return TipoRuolo.valueOf(ruoloString);
        }
        return tipoRuolo;
    }
}