package it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

import java.util.Date;
import java.util.List;

public interface IProdottoService {
    Prodotto creaProdotto(String nome, String descrizione, double prezzo, int quantitaDisponibile, Venditore venditore);
    List<Prodotto> getProdottiOfferti(Venditore venditore);
    boolean rimuoviProdottoCatalogo(Venditore venditore, Prodotto prodotto);
    boolean aggiornaQuantitaProdotto(Venditore venditore, Prodotto prodotto, int nuovaQuantita);
    boolean aggiungiQuantitaProdotto(Venditore venditore, Prodotto prodotto, int quantitaAggiunta);
    boolean rimuoviQuantitaProdotto(Venditore venditore, Prodotto prodotto, int quantitaRimossa);

    // Nuovi metodi per la gestione delle certificazioni tramite ProdottoService
    Certificazione aggiungiCertificazioneAProdotto(Prodotto prodotto, String nomeCertificazione, String enteRilascio, Date dataRilascio, Date dataScadenza);
    boolean rimuoviCertificazioneDaProdotto(Prodotto prodotto, int idCertificazione);
    List<Certificazione> getCertificazioniDelProdotto(Prodotto prodotto);
    void stampaCertificazioni(Prodotto prodotto); // Già presente, ma ora usa il service
    void mostraProdotti(Venditore venditore); // Già presente

    void decrementaQuantita(int idProdotto, int quantitaDaDecrementare);
    
}