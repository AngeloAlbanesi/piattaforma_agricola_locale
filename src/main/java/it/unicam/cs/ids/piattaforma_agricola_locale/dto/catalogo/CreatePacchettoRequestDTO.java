/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo;

import jakarta.validation.constraints.*;
import java.util.List;

/**
 * DTO for package creation requests.
 * Contains validation annotations for input validation.
 */
public class CreatePacchettoRequestDTO {
    
    @NotBlank(message = "Il nome del pacchetto è obbligatorio")
    @Size(max = 200, message = "Il nome del pacchetto non può superare i 200 caratteri")
    private String nome;
    
    @Size(max = 1000, message = "La descrizione non può superare i 1000 caratteri")
    private String descrizione;
    
    @NotNull(message = "Il prezzo del pacchetto è obbligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "Il prezzo deve essere maggiore di 0")
    private Double prezzoPacchetto;
    
    @NotNull(message = "La quantità disponibile è obbligatoria")
    @Min(value = 0, message = "La quantità deve essere non negativa")
    private Integer quantitaDisponibile;
    
    @NotEmpty(message = "Il pacchetto deve contenere almeno un elemento")
    private List<ElementoPacchettoRequestDTO> elementiInclusi;

    public CreatePacchettoRequestDTO() {
    }

    public CreatePacchettoRequestDTO(String nome, String descrizione, Double prezzoPacchetto, 
                                   Integer quantitaDisponibile, List<ElementoPacchettoRequestDTO> elementiInclusi) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.prezzoPacchetto = prezzoPacchetto;
        this.quantitaDisponibile = quantitaDisponibile;
        this.elementiInclusi = elementiInclusi;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Double getPrezzoPacchetto() {
        return prezzoPacchetto;
    }

    public void setPrezzoPacchetto(Double prezzoPacchetto) {
        this.prezzoPacchetto = prezzoPacchetto;
    }

    public Integer getQuantitaDisponibile() {
        return quantitaDisponibile;
    }

    public void setQuantitaDisponibile(Integer quantitaDisponibile) {
        this.quantitaDisponibile = quantitaDisponibile;
    }

    public List<ElementoPacchettoRequestDTO> getElementiInclusi() {
        return elementiInclusi;
    }

    public void setElementiInclusi(List<ElementoPacchettoRequestDTO> elementiInclusi) {
        this.elementiInclusi = elementiInclusi;
    }
}

/**
 * DTO for specifying elements to include in a package.
 */
class ElementoPacchettoRequestDTO {
    
    @NotBlank(message = "Il tipo di elemento è obbligatorio")
    private String tipoElemento; // "PRODOTTO", "EVENTO"
    
    @NotNull(message = "L'ID dell'elemento è obbligatorio")
    private Long idElemento;

    public ElementoPacchettoRequestDTO() {
    }

    public ElementoPacchettoRequestDTO(String tipoElemento, Long idElemento) {
        this.tipoElemento = tipoElemento;
        this.idElemento = idElemento;
    }

    public String getTipoElemento() {
        return tipoElemento;
    }

    public void setTipoElemento(String tipoElemento) {
        this.tipoElemento = tipoElemento;
    }

    public Long getIdElemento() {
        return idElemento;
    }

    public void setIdElemento(Long idElemento) {
        this.idElemento = idElemento;
    }
}