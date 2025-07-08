package it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.social.ShareRequestDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.social.ShareResponseDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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
    
    // Public catalog methods
    Page<Prodotto> getAllProdotti(Pageable pageable);
    Optional<Prodotto> getProdottoById(Long id);
    List<Prodotto> searchProdottiByNome(String nome);
    List<Prodotto> getProdottiByVenditore(Long venditorId);
    
    /**
     * Ottiene una pagina di prodotti di un venditore specifico.
     * 
     * @param venditore Il venditore
     * @param pageable Parametri di paginazione
     * @return Una pagina di prodotti
     */
    Page<Prodotto> getProdottiByVenditore(Venditore venditore, Pageable pageable);


    Optional<ShareResponseDTO> condividiProdotto(Long id, ShareRequestDTO request);

    
    /**
     * Salva o aggiorna un prodotto nel repository.
     * 
     * @param prodotto Il prodotto da salvare
     * @return Il prodotto salvato
     */
    Prodotto salvaProdotto(Prodotto prodotto);

}