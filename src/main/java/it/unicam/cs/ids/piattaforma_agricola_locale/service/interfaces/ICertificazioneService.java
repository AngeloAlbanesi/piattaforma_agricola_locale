package it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda; // o l'ID dell'azienda
import java.util.Date;
import java.util.List;

public interface ICertificazioneService {
    Certificazione creaCertificazionePerProdotto(String nome, String ente, Date rilascio, Date scadenza, Prodotto prodotto);
    Certificazione creaCertificazionePerAzienda(String nome, String ente, Date rilascio, Date scadenza, DatiAzienda azienda); // o int idAzienda
    Certificazione getCertificazioneById(Long idCertificazione);
    List<Certificazione> getCertificazioniProdotto(Long idProdotto);
    List<Certificazione> getCertificazioniAzienda(Long idAzienda);
    boolean rimuoviCertificazione(Long idCertificazione, Prodotto prodotto); // Rimuove da prodotto e da repo
    boolean rimuoviCertificazione(Long idCertificazione, DatiAzienda azienda); // Rimuove da azienda e da repo
    void rimuoviCertificazioneGlobale(Long idCertificazione); // Rimuove solo dal repo, utile se l'oggetto associato è già stato rimosso
    Certificazione aggiornaCertificazione(Long idCertificazione, String nuovoNome, String nuovoEnte, Date nuovaDataRilascio, Date nuovaDataScadenza);
}