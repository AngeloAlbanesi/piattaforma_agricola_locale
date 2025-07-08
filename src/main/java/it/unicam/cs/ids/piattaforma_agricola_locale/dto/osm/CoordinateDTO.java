package it.unicam.cs.ids.piattaforma_agricola_locale.dto.osm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoordinateDTO {
    private double latitudine;
    private double longitudine;
}