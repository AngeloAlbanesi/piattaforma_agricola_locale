package it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda; // o l'ID dell'azienda
import java.util.Date;
import java.util.List;

public interface ICertificazioneService {
    Certificazione creaCertificazionePerProdotto(String nome, String ente, Date rilascio, Date scadenza, Prodotto prodotto);
    Certificazione creaCertificazionePerAzienda(String nome, String ente, Date rilascio, Date scadenza, DatiAzienda azienda); // o int idAzienda
    Certificazione getCertificazioneById(int idCertificazione);
    List<Certificazione> getCertificazioniProdotto(int idProdotto);
    List<Certificazione> getCertificazioniAzienda(int idAzienda);
    boolean rimuoviCertificazione(int idCertificazione, Prodotto prodotto); // Rimuove da prodotto e da repo
    boolean rimuoviCertificazione(int idCertificazione, DatiAzienda azienda); // Rimuove da azienda e da repo
    void rimuoviCertificazioneGlobale(int idCertificazione); // Rimuove solo dal repo, utile se l'oggetto associato è già stato rimosso
    Certificazione aggiornaCertificazione(int idCertificazione, String nuovoNome, String nuovoEnte, Date nuovaDataRilascio, Date nuovaDataScadenza);
}