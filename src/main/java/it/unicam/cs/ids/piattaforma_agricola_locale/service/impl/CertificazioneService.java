package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.*;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.ICertificazioneService;

@Service
public class CertificazioneService implements ICertificazioneService {

    private final IProdottoRepository prodottoRepository;
    private final ICertificazioneRepository certificazioneRepository;
    private final IDatiAziendaRepository datiAziendaRepository;

    @Autowired
    public CertificazioneService(ICertificazioneRepository certificazioneRepository, IDatiAziendaRepository datiAziendaRepository, IProdottoRepository prodottoRepository) {
        this.certificazioneRepository = certificazioneRepository;
        this.datiAziendaRepository = datiAziendaRepository;
        this.prodottoRepository = prodottoRepository;
    }

    // Metodo per generare ID univoci. Potrebbe stare nel repository o essere un servizio a parte.



    @Override
    public Certificazione creaCertificazionePerProdotto(String nome, String ente, Date rilascio, Date scadenza, Prodotto prodotto) {
        if (prodotto == null || nome == null || nome.trim().isEmpty()) {
            // Lanciare eccezione o ritornare null
            return null;
        }

        // Crea la certificazione associata al prodotto
        Certificazione cert = new Certificazione(nome, ente, rilascio, scadenza, prodotto.getId());
        
        // ✅ CORREZIONE: Associa l'azienda usando l'ID dei DatiAzienda, non del Venditore
        if (prodotto.getVenditore() != null && prodotto.getVenditore().getDatiAzienda() != null) {
            cert.setIdAziendaAssociata(prodotto.getVenditore().getDatiAzienda().getId());
        }
        
        certificazioneRepository.save(cert);
        prodotto.aggiungiCertificazione(cert); // Aggiunge alla lista interna del prodotto
        // Se ProdottoRepository gestisce la persistenza di Prodotto, potresti dover salvare Prodotto qui
        prodottoRepository.save(prodotto);
        return cert;
    }

    @Override
    public Certificazione creaCertificazionePerAzienda(String nome, String ente, Date rilascio, Date scadenza, DatiAzienda azienda) {
        if (azienda == null || nome == null || nome.trim().isEmpty()) {
            return null;
        }
        // Assumendo che DatiAzienda abbia un getIdAzienda()
        Certificazione cert = new Certificazione( nome, ente, rilascio, scadenza, azienda.getId(), true);
        certificazioneRepository.save(cert);
        azienda.aggiungiCertificazione(cert);
        datiAziendaRepository.save(azienda);
        return cert;
    }

    @Override
    public Certificazione getCertificazioneById(Long idCertificazione) {
        return certificazioneRepository.findById(idCertificazione).orElse(null);
    }

    @Override
    public List<Certificazione> getCertificazioniProdotto(Long idProdotto) {
        return certificazioneRepository.findByIdProdottoAssociato(idProdotto);
    }

    @Override
    public List<Certificazione> getCertificazioniAzienda(Long idAzienda) {
        return certificazioneRepository.findByIdAziendaAssociata(idAzienda);
    }

    @Override
    public boolean rimuoviCertificazione(Long idCertificazione, Prodotto prodotto) {
        Certificazione cert = certificazioneRepository.findById(idCertificazione).orElse(null);
        if (cert != null && cert.getIdProdottoAssociato() != null && cert.getIdProdottoAssociato().equals(prodotto.getId())) {
            prodotto.getCertificazioni().remove(cert); // Rimuovi dalla lista interna
             prodottoRepository.save(prodotto); // Se necessario
            certificazioneRepository.deleteById(idCertificazione);
            return true;
        }
        return false;
    }

    @Override
    public boolean rimuoviCertificazione(Long idCertificazione, DatiAzienda azienda) {
        Certificazione cert = certificazioneRepository.findById(idCertificazione).orElse(null);
        if (cert != null && cert.getIdAziendaAssociata() != null && cert.getIdAziendaAssociata().equals(azienda.getId())) {
            azienda.getCertificazioniAzienda().remove(cert);
            datiAziendaRepository.save(azienda); // Se necessario
            certificazioneRepository.deleteById(idCertificazione);
            return true;
        }
        return false;
    }

    @Override
    public void rimuoviCertificazioneGlobale(Long idCertificazione) {
        // Qui devi anche rimuovere la certificazione dalle liste interne
        // di Prodotto o DatiAzienda se vuoi mantenere la consistenza
        // Questo metodo è più per pulizia se l'oggetto padre è già stato cancellato.
        Certificazione cert = certificazioneRepository.findById(idCertificazione).orElse(null);
        if (cert == null) return;

        // Logica opzionale per rimuovere dalle liste dei proprietari
        // if (cert.getIdProdottoAssociato() != null) {
        //     Prodotto p = prodottoRepository.findById(cert.getIdProdottoAssociato());
        //     if (p != null) p.getCertificazioniProdotto().removeIf(c -> c.getIdCertificazione() == idCertificazione);
        // } else if (cert.getIdAziendaAssociata() != null) {
        //     DatiAzienda da = //... recupera DatiAzienda
        //     if (da != null) da.getCertificazioniAzienda().removeIf(c -> c.getIdCertificazione() == idCertificazione);
        // }
        certificazioneRepository.deleteById(idCertificazione);
    }


    @Override
    public Certificazione aggiornaCertificazione(Long idCertificazione, String nuovoNome, String nuovoEnte, Date nuovaDataRilascio, Date nuovaDataScadenza) {
        Certificazione cert = certificazioneRepository.findById(idCertificazione).orElse(null);
        if (cert != null) {
            if (nuovoNome != null) cert.setNomeCertificazione(nuovoNome);
            if (nuovoEnte != null) cert.setEnteRilascio(nuovoEnte);
            if (nuovaDataRilascio != null) cert.setDataRilascio(nuovaDataRilascio);
            if (nuovaDataScadenza != null) cert.setDataScadenza(nuovaDataScadenza);
            certificazioneRepository.save(cert); // L'implementazione di save deve gestire l'aggiornamento
        }
        return cert;
    }
}