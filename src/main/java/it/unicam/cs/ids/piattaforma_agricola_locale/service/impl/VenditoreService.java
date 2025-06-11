package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl; // o service.classes

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.StatoVerificaValori;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.DatiAziendaRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IDatiAziendaRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.StatoVerificaValori;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IVenditoreRepository;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
// Assumendo che IVenditoreRepository e ICertificazioneService siano in service.interfaces
// import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IVenditoreRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.ICertificazioneService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IVenditoreService;

import java.util.Date;
import java.util.List;
import java.util.UUID; // Per generare ID DatiAzienda di esempio

public class VenditoreService implements IVenditoreService {

    private IVenditoreRepository venditoreRepository; // Se necessario per salvare il venditore
    private ICertificazioneService certificazioneService;
    private IDatiAziendaRepository datiAziendaRepository;

    // Costruttore per l'iniezione delle dipendenze
    public VenditoreService(ICertificazioneService certificazioneService , IVenditoreRepository venditoreRepository, IDatiAziendaRepository datiAziendaRepository) {
        this.certificazioneService = certificazioneService;
        this.venditoreRepository = venditoreRepository;
        this.datiAziendaRepository = datiAziendaRepository;

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
        datiEsistenti.setStatoVerifica(StatoVerificaValori.IN_REVISIONE); // Imposta lo stato di verifica a IN_ATTESA dopo l'aggiornamento
        // Non aggiorniamo la Partita IVA o l'ID qui, solitamente sono più stabili.
        // Se il venditore deve essere salvato dopo questa modifica:
        datiAziendaRepository.save(datiEsistenti);
    }

    @Override
    public DatiAzienda aggiungiDatiAzienda(Venditore venditore, String nomeAzienda, String partitaIva, String indirizzoAzienda,
                                           String descrizioneAzienda, String logoUrl, String sitoWebUrl) { // Modificato per ritornare DatiAzienda

        if(datiAziendaRepository.findByPartitaIva(partitaIva).isEmpty()) {
            // Genera un ID per DatiAzienda se non fornito o gestito diversamente
            int idAzienda = Math.abs(UUID.randomUUID().hashCode()); // da eliminare
            DatiAzienda datiAzienda = new DatiAzienda(idAzienda, nomeAzienda, partitaIva, indirizzoAzienda, descrizioneAzienda,
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
    public boolean rimuoviCertificazioneDaAzienda(Venditore venditore, int idCertificazione) {
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
        return certificazioneService.getCertificazioniAzienda(venditore.getDatiAzienda().getIdAzienda());
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
}