package it.unicam.cs.ids.piattaforma_agricola_locale.service.osm;

import org.springframework.stereotype.Service;

@Service
public class DistanceCalculationService {

    private static final int RAGGIO_TERRESTRE_KM = 6371;

    /**
     * Calcola la distanza in linea d'aria tra due punti geografici usando la formula di Haversine.
     * @return La distanza in chilometri.
     */
    public double calcolaDistanza(double lat1, double lon1, double lat2, double lon2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RAGGIO_TERRESTRE_KM * c;
    }
}