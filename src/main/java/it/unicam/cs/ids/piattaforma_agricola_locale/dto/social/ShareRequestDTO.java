package it.unicam.cs.ids.piattaforma_agricola_locale.dto.social;

import lombok.Data;

@Data
public class ShareRequestDTO {
    private String socialNetwork; // ad esempio "Instagram", "Facebook", "X"
    private String nickname;
    private String messaggio;
}