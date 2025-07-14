package it.unicam.cs.ids.piattaforma_agricola_locale.service.osm;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.osm.CoordinateDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Service
public class GeocodingService {
    private static final String NOMINATIM_API_URL_TEMPLATE = "https://nominatim.openstreetmap.org/search?q={address}&format=json&limit=1";
    private final RestTemplate restTemplate = new RestTemplate();

    public CoordinateDTO getCoordinates(String address) {
        try {
            JsonNode[] response = restTemplate.getForObject(
                    NOMINATIM_API_URL_TEMPLATE,
                    JsonNode[].class,
                    address);

            if (response != null && response.length > 0) {
                double lat = response[0].get("lat").asDouble();
                double lon = response[0].get("lon").asDouble();
                return new CoordinateDTO(lat, lon);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}