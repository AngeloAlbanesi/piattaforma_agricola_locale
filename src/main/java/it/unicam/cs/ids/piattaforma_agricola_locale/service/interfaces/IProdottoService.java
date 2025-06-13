package it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

import java.util.Date;
import java.util.List;

public interface IProdottoService {
    Prodotto creaProdotto(String nome, String descrizione, double prezzo, int quantitaDisponibile, Venditore venditore);
    List<Prodotto> getProdottiOfferti(Venditore venditore);
    void rimuoviProdottoCatalogo(Venditore venditore, Prodotto prodotto);
    void aggiornaQuantitaProdotto(Venditore venditore, Prodotto prodotto, int nuovaQuantita);
    void aggiungiQuantitaProdotto(Venditore venditore, Prodotto prodotto, int quantitaAggiunta);
    void rimuoviQuantitaProdotto(Venditore venditore, Prodotto prodotto, int quantitaRimossa);

    // Nuovi metodi per la gestione delle certificazioni tramite ProdottoService
    Certificazione aggiungiCertificazioneAProdotto(Prodotto prodotto, String nomeCertificazione, String enteRilascio, Date dataRilascio, Date dataScadenza);
     void rimuoviCertificazioneDaProdotto(Prodotto prodotto, Long idCertificazione);
    List<Certificazione> getCertificazioniDelProdotto(Prodotto prodotto);

    void decrementaQuantita(Long idProdotto, int quantitaDaDecrementare);
    
}