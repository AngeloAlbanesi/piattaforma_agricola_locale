/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.TipoOrigineProdotto;
import jakarta.validation.constraints.*;

/**
 * DTO for product creation requests.
 * Contains validation annotations for input validation.
 */
public class CreateProductRequestDTO {
    
    @NotBlank(message = "Il nome del prodotto è obbligatorio")
    @Size(max = 200, message = "Il nome del prodotto non può superare i 200 caratteri")
    private String nome;
    
    @Size(max = 1000, message = "La descrizione non può superare i 1000 caratteri")
    private String descrizione;
    
    @NotNull(message = "Il prezzo è obbligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "Il prezzo deve essere maggiore di 0")
    private Double prezzo;
    
    @NotNull(message = "La quantità disponibile è obbligatoria")
    @Min(value = 0, message = "La quantità deve essere non negativa")
    private Integer quantitaDisponibile;
    
    @NotNull(message = "Il tipo di origine è obbligatorio")
    private TipoOrigineProdotto tipoOrigine;
    
    private Long idProcessoTrasformazioneOriginario;
    private Long idMetodoDiColtivazione;

    public CreateProductRequestDTO() {
    }

    public CreateProductRequestDTO(String nome, String descrizione, Double prezzo, 
                                 Integer quantitaDisponibile, TipoOrigineProdotto tipoOrigine, 
                                 Long idProcessoTrasformazioneOriginario, Long idMetodoDiColtivazione) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.prezzo = prezzo;
        this.quantitaDisponibile = quantitaDisponibile;
        this.tipoOrigine = tipoOrigine;
        this.idProcessoTrasformazioneOriginario = idProcessoTrasformazioneOriginario;
        this.idMetodoDiColtivazione = idMetodoDiColtivazione;
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

    public Double getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(Double prezzo) {
        this.prezzo = prezzo;
    }

    public Integer getQuantitaDisponibile() {
        return quantitaDisponibile;
    }

    public void setQuantitaDisponibile(Integer quantitaDisponibile) {
        this.quantitaDisponibile = quantitaDisponibile;
    }

    public TipoOrigineProdotto getTipoOrigine() {
        return tipoOrigine;
    }

    public void setTipoOrigine(TipoOrigineProdotto tipoOrigine) {
        this.tipoOrigine = tipoOrigine;
    }

    public Long getIdProcessoTrasformazioneOriginario() {
        return idProcessoTrasformazioneOriginario;
    }

    public void setIdProcessoTrasformazioneOriginario(Long idProcessoTrasformazioneOriginario) {
        this.idProcessoTrasformazioneOriginario = idProcessoTrasformazioneOriginario;
    }

    public Long getIdMetodoDiColtivazione() {
        return idMetodoDiColtivazione;
    }

    public void setIdMetodoDiColtivazione(Long idMetodoDiColtivazione) {
        this.idMetodoDiColtivazione = idMetodoDiColtivazione;
    }
}