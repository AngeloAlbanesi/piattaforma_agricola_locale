package it.unicam.cs.ids.piattaforma_agricola_locale.dto.admin;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusUpdateDTO {
    @NotNull(message = "Lo stato non può essere nullo")
    private Boolean attivo;
}
