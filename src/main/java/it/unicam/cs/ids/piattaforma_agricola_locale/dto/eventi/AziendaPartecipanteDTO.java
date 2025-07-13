/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.eventi;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo.CertificazioneDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for company data in event participants.
 * Contains company information without sensitive fields like logoUrl, statoVerifica, feedbackVerifica.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AziendaPartecipanteDTO {
    
    private Long id;
    private String nomeAzienda;
    private String partitaIva;
    private String indirizzoAzienda;
    private String descrizioneAzienda;
    private String sitoWebUrl;
    private List<CertificazioneDTO> certificazioniAzienda;
    
    // Alias methods for compatibility
    public String getNome() {
        return nomeAzienda;
    }
    
    public String getIndirizzo() {
        return indirizzoAzienda;
    }
    
    public String getTelefono() {
        return "N/A"; // Campo non presente nel modello
    }
    
    public String getEmail() {
        return "N/A"; // Campo non presente nel modello
    }
    
    public String getSitoWeb() {
        return sitoWebUrl;
    }

    // Alias setter methods for JSON compatibility
    public void setNome(String nome) {
        this.nomeAzienda = nome;
    }
    
    public void setIndirizzo(String indirizzo) {
        this.indirizzoAzienda = indirizzo;
    }
    
    public void setSitoWeb(String sitoWeb) {
        this.sitoWebUrl = sitoWeb;
    }
    
    // Note: setTelefono and setEmail are not implemented as these fields
    // return hardcoded values and are not stored in the model
}