package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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
    public VenditoreService(ICertificazioneService certificazioneService, IVenditoreRepository venditoreRepository, IDatiAziendaRepository datiAziendaRepository) {
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
    public DatiAzienda aggiungiDatiAzienda(Venditore venditore, String nomeAzienda, String partitaIva, String indirizzoAzienda,
                                           String descrizioneAzienda, String logoUrl, String sitoWebUrl) { // Modificato per ritornare DatiAzienda

        if(datiAziendaRepository.findByPartitaIva(partitaIva).isEmpty()) {
            // Genera un ID per DatiAzienda se non fornito o gestito diversamente
            Long idVenditore = venditore.getId();
            DatiAzienda datiAzienda = new DatiAzienda( nomeAzienda, partitaIva, indirizzoAzienda, descrizioneAzienda,
                    logoUrl, sitoWebUrl);
            venditore.setDatiAzienda(datiAzienda);
            // Se il venditore deve essere salvato dopo questa modifica:

            datiAziendaRepository.save(datiAzienda);
            return datiAzienda;
        }else
        {
            throw new IllegalStateException("DatiAzienda gia presenti");
        }
    }

    // Metodo per aggiungere una certificazione ai DatiAzienda di un Venditore
    @Override
    public Certificazione aggiungiCertificazioneAdAzienda(Venditore venditore, String nomeCertificazione, String enteRilascio, Date dataRilascio, Date dataScadenza) {
        if (venditore == null || venditore.getDatiAzienda() == null || certificazioneService == null) {
            System.err.println("Venditore, dati azienda o servizio certificazioni non disponibile.");
            return null;
        }
        DatiAzienda datiAzienda = venditore.getDatiAzienda();
        return certificazioneService.creaCertificazionePerAzienda(nomeCertificazione, enteRilascio, dataRilascio, dataScadenza, datiAzienda);
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



    // Metodo per stampare le certificazioni dell'azienda (simile a quello per prodotto)
    public void stampaCertificazioniAzienda(Venditore venditore) {
        if (venditore == null || venditore.getDatiAzienda() == null) return;

        List<Certificazione> certificazioni = getCertificazioniAzienda(venditore);

        if (certificazioni.isEmpty()) {
            System.out.println("  Nessuna certificazione per questa azienda.");
        } else {
            System.out.println("  Certificazioni dell'azienda ("+ venditore.getDatiAzienda().getNomeAzienda() +"):");
            for (Certificazione c : certificazioni) {
                c.stampaCertificazione();
            }
        }
    }
    
    @Override
    public Page<DatiAzienda> searchAziende(String query, Pageable pageable) {
        // Implementazione semplificata: cerca per nome azienda
        // In una implementazione reale, si userebbe un repository con query pi� complesse
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
}