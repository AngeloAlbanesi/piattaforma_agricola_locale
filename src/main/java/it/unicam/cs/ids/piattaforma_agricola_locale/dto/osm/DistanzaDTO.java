package it.unicam.cs.ids.piattaforma_agricola_locale.dto.osm;

import lombok.Data;

@Data // Aggiunge getters, setters, etc.
public class DistanzaDTO {

    private String indirizzoPartenza;
    private String indirizzoAzienda;
    private double distanzaInKm;

    public DistanzaDTO(String indirizzoPartenza, String indirizzoAzienda, double distanzaInKm) {
        this.indirizzoPartenza = indirizzoPartenza;
        this.indirizzoAzienda = indirizzoAzienda;
        // Arrotondiamo la distanza a due cifre decimali per una migliore leggibilità
        this.distanzaInKm = Math.round(distanzaInKm * 100.0) / 100.0;
    }
}