package it.unicam.cs.ids.piattaforma_agricola_locale.dto.social;

import lombok.Data;

@Data
public class ShareResponseDTO {
    private String messaggioDiCondivisione;

    public ShareResponseDTO(String messaggioDiCondivisione) {
        this.messaggioDiCondivisione = messaggioDiCondivisione;
    }
}
