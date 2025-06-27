package it.unicam.cs.ids.piattaforma_agricola_locale.security.dto;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.TipoRuolo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String nome;
    private String cognome;
    private String email;
    private String password;
    private String numeroTelefono;
    private TipoRuolo tipoRuolo;
    private DatiAzienda datiAzienda;
}