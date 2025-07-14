package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.osm.CoordinateDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.osm.DistanzaDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.osm.DistanceCalculationService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.osm.GeocodingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.StatoVerificaValori;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IDatiAziendaRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IVenditoreRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.ICertificazioneService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IVenditoreService;

@Service
public class VenditoreService implements IVenditoreService {

    private final IVenditoreRepository venditoreRepository;
    private final ICertificazioneService certificazioneService;
    private final IDatiAziendaRepository datiAziendaRepository;

    @Autowired
    private GeocodingService geocodingService;
    @Autowired
    private DistanceCalculationService distanceCalculationService;

    @Autowired
    public VenditoreService(ICertificazioneService certificazioneService, IVenditoreRepository venditoreRepository,
            IDatiAziendaRepository datiAziendaRepository) {
        this.certificazioneService = certificazioneService;
        this.venditoreRepository = venditoreRepository;
        this.datiAziendaRepository = datiAziendaRepository;
    }

    @Override
    public Optional<Venditore> getVenditoreById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return venditoreRepository.findById(id);
    }

    @Override
    public void aggiornaDatiAzienda(Venditore venditore, DatiAzienda datiAggiornati) {
        if (venditore == null || venditore.getDatiAzienda() == null || datiAggiornati == null) {
            System.err.println("Impossibile aggiornare i dati azienda: venditore o dati non validi.");
            return;
        }
        DatiAzienda datiEsistenti = venditore.getDatiAzienda();
        datiEsistenti.setNomeAzienda(datiAggiornati.getNomeAzienda());
        datiEsistenti.setDescrizioneAzienda(datiAggiornati.getDescrizioneAzienda());
        datiEsistenti.setIndirizzoAzienda(datiAggiornati.getIndirizzoAzienda());
        datiEsistenti.setLogoUrl(datiAggiornati.getLogoUrl());
        datiEsistenti.setSitoWebUrl(datiAggiornati.getSitoWebUrl());
        datiEsistenti.setStatoVerifica(StatoVerificaValori.IN_REVISIONE);
        // Non aggiorniamo la Partita IVA o l'ID qui, solitamente sono più stabili.
        // Se il venditore deve essere salvato dopo questa modifica:
        datiAziendaRepository.save(datiEsistenti);
    }

    @Override
    public DatiAzienda aggiungiDatiAzienda(Venditore venditore, String nomeAzienda, String partitaIva,
            String indirizzoAzienda,
            String descrizioneAzienda, String logoUrl, String sitoWebUrl) { // Modificato per ritornare DatiAzienda

        if (datiAziendaRepository.findByPartitaIva(partitaIva).isEmpty()) {
            // Genera un ID per DatiAzienda se non fornito o gestito diversamente
            Long idVenditore = venditore.getId();
            DatiAzienda datiAzienda = new DatiAzienda(nomeAzienda, partitaIva, indirizzoAzienda, descrizioneAzienda,
                    logoUrl, sitoWebUrl);
            venditore.setDatiAzienda(datiAzienda);
            // Se il venditore deve essere salvato dopo questa modifica:

            datiAziendaRepository.save(datiAzienda);
            return datiAzienda;
        } else {
            throw new IllegalStateException("DatiAzienda gia presenti");
        }
    }

    // Metodo per aggiungere una certificazione ai DatiAzienda di un Venditore
    @Override
    public Certificazione aggiungiCertificazioneAdAzienda(Venditore venditore, String nomeCertificazione,
            String enteRilascio, Date dataRilascio, Date dataScadenza) {
        if (venditore == null || venditore.getDatiAzienda() == null || certificazioneService == null) {
            System.err.println("Venditore, dati azienda o servizio certificazioni non disponibile.");
            return null;
        }
        DatiAzienda datiAzienda = venditore.getDatiAzienda();
        return certificazioneService.creaCertificazionePerAzienda(nomeCertificazione, enteRilascio, dataRilascio,
                dataScadenza, datiAzienda);
    }

    // Metodo per rimuovere una certificazione dai DatiAzienda di un Venditore
    public boolean rimuoviCertificazioneDaAzienda(Venditore venditore, Long idCertificazione) {
        if (venditore == null || venditore.getDatiAzienda() == null || certificazioneService == null) {
            System.err.println("Venditore, dati azienda o servizio certificazioni non disponibile.");
            return false;
        }
        DatiAzienda datiAzienda = venditore.getDatiAzienda();
        return certificazioneService.rimuoviCertificazione(idCertificazione, datiAzienda);
    }

    // Metodo per ottenere le certificazioni dei DatiAzienda di un Venditore
    public List<Certificazione> getCertificazioniAzienda(Venditore venditore) {
        if (venditore == null || venditore.getDatiAzienda() == null || certificazioneService == null) {
            return List.of(); // Ritorna lista vuota
        }
        return certificazioneService.getCertificazioniAzienda(venditore.getDatiAzienda().getId());
    }

    @Override
    public Page<DatiAzienda> getAllAziende(Pageable pageable) {
        // Restituisce tutte le aziende approvate
        return datiAziendaRepository.findByStatoVerifica(StatoVerificaValori.APPROVATO, pageable);
    }

    @Override
    public Page<DatiAzienda> searchAziende(String query, Pageable pageable) {
        // Implementazione semplificata: cerca per nome azienda
        // In una implementazione reale, si userebbe un repository con query pi�
        // complesse
        // o un motore di ricerca come Elasticsearch
        return datiAziendaRepository.findByNomeAziendaContainingIgnoreCase(query, pageable);
    }

    @Override
    public Venditore updateVenditore(Venditore venditore) {
        if (venditore == null) {
            throw new IllegalArgumentException("Venditore non pu� essere null");
        }
        return venditoreRepository.save(venditore);
    }

    @Override
    public Optional<CoordinateDTO> getCoordinatePerAziendaId(Long id) {
        // 1. Trova l'azienda nel database
        Optional<DatiAzienda> aziendaOpt = datiAziendaRepository.findById(id);

        if (aziendaOpt.isEmpty()) {
            return Optional.empty(); // L'azienda non è stata trovata
        }

        // 2. Prendi l'indirizzo dall'azienda trovata
        String indirizzo = aziendaOpt.get().getIndirizzo();
        if (indirizzo == null || indirizzo.isBlank()) {
            return Optional.empty(); // Nessun indirizzo da geocodificare
        }

        // 3. Chiama il GeocodingService e ritorna il risultato
        CoordinateDTO coordinate = geocodingService.getCoordinates(indirizzo);
        return Optional.ofNullable(coordinate);
    }

    public Optional<DistanzaDTO> calcolaDistanzaDaAzienda(Long aziendaId, String indirizzoPartenza) {
        // 1. Trova l'azienda e il suo indirizzo
        Optional<DatiAzienda> aziendaOpt = datiAziendaRepository.findById(aziendaId);
        if (aziendaOpt.isEmpty() || aziendaOpt.get().getIndirizzoAzienda() == null) {
            return Optional.empty(); // Azienda non trovata o senza indirizzo
        }
        String indirizzoAzienda = aziendaOpt.get().getIndirizzoAzienda();

        // 2. Geocodifica l'indirizzo di partenza
        CoordinateDTO coordPartenza = geocodingService.getCoordinates(indirizzoPartenza);
        if (coordPartenza == null) {
            System.err.println("Impossibile geocodificare l'indirizzo di partenza: " + indirizzoPartenza);
            return Optional.empty(); // Indirizzo di partenza non valido
        }

        // 3. Geocodifica l'indirizzo dell'azienda (destinazione)
        CoordinateDTO coordAzienda = geocodingService.getCoordinates(indirizzoAzienda);
        if (coordAzienda == null) {
            System.err.println("Impossibile geocodificare l'indirizzo dell'azienda: " + indirizzoAzienda);
            return Optional.empty(); // Indirizzo dell'azienda non valido
        }

        // 4. Se abbiamo entrambe le coordinate, calcola la distanza
        double distanza = distanceCalculationService.calcolaDistanza(
                coordPartenza.getLatitudine(),
                coordPartenza.getLongitudine(),
                coordAzienda.getLatitudine(),
                coordAzienda.getLongitudine());

        // 5. Crea e restituisci il DTO con tutte le informazioni
        DistanzaDTO risultato = new DistanzaDTO(indirizzoPartenza, indirizzoAzienda, distanza);
        return Optional.of(risultato);
    }
}