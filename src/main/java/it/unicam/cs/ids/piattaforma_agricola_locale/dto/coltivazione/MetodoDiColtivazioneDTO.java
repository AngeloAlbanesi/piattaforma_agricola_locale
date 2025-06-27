/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.coltivazione;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for MetodoDiColtivazione entity.
 * Used to transfer cultivation method data between layers.
 */
public class MetodoDiColtivazioneDTO {
    
    private Long id;
    
    @NotBlank(message = "Il nome del metodo di coltivazione è obbligatorio")
    @Size(max = 255, message = "Il nome non può superare i 255 caratteri")
    private String nome;
    
    @Size(max = 1000, message = "La descrizione non può superare i 1000 caratteri")
    private String descrizioneDettagliata;
    
    @Size(max = 255, message = "La tecnica principale non può superare i 255 caratteri")
    private String tecnicaPrincipale;
    
    @Size(max = 255, message = "L'ambiente di coltivazione non può superare i 255 caratteri")
    private String ambienteColtivazione;

    public MetodoDiColtivazioneDTO() {
    }

    public MetodoDiColtivazioneDTO(Long id, String nome, String descrizioneDettagliata, 
                                 String tecnicaPrincipale, String ambienteColtivazione) {
        this.id = id;
        this.nome = nome;
        this.descrizioneDettagliata = descrizioneDettagliata;
        this.tecnicaPrincipale = tecnicaPrincipale;
        this.ambienteColtivazione = ambienteColtivazione;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizioneDettagliata() {
        return descrizioneDettagliata;
    }

    public void setDescrizioneDettagliata(String descrizioneDettagliata) {
        this.descrizioneDettagliata = descrizioneDettagliata;
    }

    public String getTecnicaPrincipale() {
        return tecnicaPrincipale;
    }

    public void setTecnicaPrincipale(String tecnicaPrincipale) {
        this.tecnicaPrincipale = tecnicaPrincipale;
    }

    public String getAmbienteColtivazione() {
        return ambienteColtivazione;
    }

    public void setAmbienteColtivazione(String ambienteColtivazione) {
        this.ambienteColtivazione = ambienteColtivazione;
    }
}