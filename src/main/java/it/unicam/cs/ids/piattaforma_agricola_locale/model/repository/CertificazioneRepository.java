package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CertificazioneRepository implements ICertificazioneRepository {
    private Map<Integer, Certificazione> certificazioni = new HashMap<>();
    private int nextId = 1; // Semplice generatore di ID per l'esempio

    @Override
    public void save(Certificazione certificazione) {
        // Se l'ID è 0 o non presente, assegna un nuovo ID (per nuove certificazioni)
        // Altrimenti, si assume un aggiornamento (anche se qui sovrascrive sempre)
        if (certificazione.getIdCertificazione() == 0) {
            // Questo non va bene con i costruttori attuali, l'ID viene passato.
            // Dovresti avere un modo per generare l'ID nel service prima di chiamare il costruttore.
            // Per ora, assumiamo che l'ID sia già impostato correttamente prima di save.
        }
        certificazioni.put(certificazione.getIdCertificazione(), certificazione);
    }

    @Override
    public Certificazione findById(int id) {
        return certificazioni.get(id);
    }

    @Override
    public List<Certificazione> findAll() {
        return new ArrayList<>(certificazioni.values());
    }

    @Override
    public List<Certificazione> findByProdottoId(int idProdotto) {
        return certificazioni.values().stream()
                .filter(c -> c.getIdProdottoAssociato() != null && c.getIdProdottoAssociato() == idProdotto)
                .collect(Collectors.toList());
    }

    @Override
    public List<Certificazione> findByAziendaId(int idAzienda) {
        return certificazioni.values().stream()
                .filter(c -> c.getIdAziendaAssociata() != null && c.getIdAziendaAssociata() == idAzienda)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(int id) {
        certificazioni.remove(id);
    }

    @Override
    public void deleteByProdottoId(int idProdotto) {
        certificazioni.values().removeIf(c -> c.getIdProdottoAssociato() != null && c.getIdProdottoAssociato() == idProdotto);
    }

    @Override
    public void deleteByAziendaId(int idAzienda) {
        certificazioni.values().removeIf(c -> c.getIdAziendaAssociata() != null && c.getIdAziendaAssociata() == idAzienda);
    }

    // Metodo di utilità per generare ID univoci, da usare nel Service
    public int getNextId() {
        return nextId++;
    }
}