package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;

import java.util.*;

public class CertificazioneRepository implements ICertificazioneRepository {

    private final Map<Integer, Certificazione> certificazioni = new HashMap<>();

    public void salva(Certificazione certificazione) {
        certificazioni.put(certificazione.getIdCertificazione(), certificazione);
    }

    public Optional<Certificazione> trovaPerId(int id) {
        return Optional.ofNullable(certificazioni.get(id));
    }

    public List<Certificazione> trovaTutte() {
        return new ArrayList<>(certificazioni.values());
    }

    public void rimuovi(int id) {
        certificazioni.remove(id);
    }

    public void rimuovi(Certificazione certificazione) {
        certificazioni.remove(certificazione.getIdCertificazione());
    }
}
