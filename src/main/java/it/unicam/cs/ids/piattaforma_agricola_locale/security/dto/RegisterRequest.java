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

    // Dati aziendali mappati dal frontend
    @JsonProperty("datiAzienda")
    private DatiAzienda datiAzienda;

    // Manteniamo per compatibilità con il codice esistente
    private TipoRuolo tipoRuolo;

    // Metodo per convertire la stringa del ruolo in TipoRuolo
    public TipoRuolo getTipoRuolo() {
        if (ruoloString != null) {
            return TipoRuolo.valueOf(ruoloString);
        }
        return tipoRuolo;
    }

    // Metodo per creare DatiAzienda dai campi del form se necessario
    public DatiAzienda getDatiAzienda() {
        if (datiAzienda == null) {
            return null;
        }
        return datiAzienda;
    }
}