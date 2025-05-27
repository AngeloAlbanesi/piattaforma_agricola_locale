package it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces;



import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.ICertificazioneRepository;


import java.util.Date;
import java.util.UUID;

public class CertificazioneService implements ICertificazioneService {

    private final ICertificazioneRepository repository;

    public CertificazioneService(ICertificazioneRepository repository) {
        this.repository = repository;
    }

    @Override
    public Certificazione creaCertificazione(String nome, String enteRilascio, Date dataRilascio, Date dataScadenza) {
        int id = UUID.randomUUID().hashCode();
        Certificazione certificazione = new Certificazione(id, nome, enteRilascio, dataRilascio, dataScadenza);
        repository.salva(certificazione);
        return certificazione;
    }

    @Override
    public Certificazione trovaCertificazione(int id) {
        return repository.trovaPerId(id).orElseThrow(() ->
                new IllegalArgumentException("Certificazione non trovata con ID: " + id));
    }

    @Override
    public void rimuoviCertificazione(int id) {
        repository.rimuovi(id);
    }
}
