package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CertificazioneRepository implements ICertificazioneRepository {
    private Map<Long, Certificazione> certificazioni = new HashMap<>();
    private Long nextId = 1L; // Semplice generatore di ID per l'esempio

    @Override
    public void save(Certificazione certificazione) {

        if (certificazione.getIdCertificazione() == null) {
            certificazione.SetIdCertificazione(nextId++);
        }
        certificazioni.put(certificazione.getIdCertificazione(), certificazione);
    }

    @Override
    public Certificazione findById(Long id) {
        return certificazioni.get(id);
    }

    @Override
    public List<Certificazione> findAll() {
        return new ArrayList<>(certificazioni.values());
    }

    @Override
    public List<Certificazione> findByProdottoId(Long idProdotto) {
        return certificazioni.values().stream()
                .filter(c -> c.getIdProdottoAssociato() != null && c.getIdProdottoAssociato() == idProdotto)
                .collect(Collectors.toList());
    }

    @Override
    public List<Certificazione> findByAziendaId(Long idAzienda) {
        return certificazioni.values().stream()
                .filter(c -> c.getIdAziendaAssociata() != null && c.getIdAziendaAssociata() == idAzienda)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        certificazioni.remove(id);
    }

    @Override
    public void deleteByProdottoId(Long idProdotto) {
        certificazioni.values().removeIf(c -> c.getIdProdottoAssociato() != null && c.getIdProdottoAssociato() == idProdotto);
    }

    @Override
    public void deleteByAziendaId(Long idAzienda) {
        certificazioni.values().removeIf(c -> c.getIdAziendaAssociata() != null && c.getIdAziendaAssociata() == idAzienda);
    }


}