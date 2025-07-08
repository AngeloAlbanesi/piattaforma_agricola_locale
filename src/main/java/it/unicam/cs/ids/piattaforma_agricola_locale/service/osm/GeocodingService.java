package it.unicam.cs.ids.piattaforma_agricola_locale.service.osm;

// src/main/java/com/tuoprogetto/service/GeocodingService.java
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.osm.CoordinateDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import java.net.URLDecoder; // Importa il decoder
import java.nio.charset.StandardCharsets; // Importa il set di caratteri

@Service
public class GeocodingService {
    private static final String NOMINATIM_API_URL_TEMPLATE = "https://nominatim.openstreetmap.org/search?q={address}&format=json&limit=1";
    private final RestTemplate restTemplate = new RestTemplate();

    public CoordinateDTO getCoordinates(String address) {

        System.out.println("Indirizzo ricevuto da GeocodingService: '" + address + "'");

        try {
            // Non usiamo più UriComponentsBuilder.
            // Invochiamo RestTemplate passandogli un template di URL e i valori
            // per i segnaposto. Sarà RestTemplate a fare la codifica, una sola volta.

            JsonNode[] response = restTemplate.getForObject(
                    NOMINATIM_API_URL_TEMPLATE,
                    JsonNode[].class,
                    address // Passiamo l'indirizzo pulito come valore per il segnaposto {address}
            );

            if (response != null && response.length > 0) {
                double lat = response[0].get("lat").asDouble();
                double lon = response[0].get("lon").asDouble();
                System.out.println("Coordinate trovate: " + lat + ", " + lon);
                return new CoordinateDTO(lat, lon);
            } else {
                System.out.println("Nominatim ha risposto, ma con un array vuoto. Indirizzo non trovato.");
            }
        } catch (Exception e) {
            System.err.println("Errore GRAVE durante la chiamata a Nominatim: " + e.getMessage());
            // Stampa lo stack trace completo per vedere l'errore in dettaglio
            e.printStackTrace();
        }
        return null;
    }
}