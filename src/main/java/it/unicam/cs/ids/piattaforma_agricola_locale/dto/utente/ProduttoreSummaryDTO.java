package it.unicam.cs.ids.piattaforma_agricola_locale.dto.utente;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO semplificato per la lista dei produttori.
 * Usato per selezionare un produttore come fonte di materia prima.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProduttoreSummaryDTO {
    private Long id;
    private String nome;
    private String cognome;
    private String nomeAzienda;

    /**
     * Restituisce il nome completo del produttore.
     */
    public String getNomeCompleto() {
        return nome + " " + cognome;
    }

    /**
     * Restituisce una descrizione completa: nome + azienda.
     */
    public String getDescrizioneCompleta() {
        String nomeCompletoStr = getNomeCompleto();
        String azienda = (nomeAzienda != null && !nomeAzienda.isEmpty()) ? nomeAzienda : "N/D";
        return nomeCompletoStr + " - " + azienda;
    }
}
