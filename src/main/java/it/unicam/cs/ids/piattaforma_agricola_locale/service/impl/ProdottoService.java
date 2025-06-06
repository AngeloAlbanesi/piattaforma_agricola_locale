package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl; // o service.classes

import java.util.Date;
import java.util.List;
import java.util.UUID;

import it.unicam.cs.ids.piattaforma_agricola_locale.exception.QuantitaNonDisponibileException;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IProdottoRepository; // Usa interfaccia

import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IVenditoreRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.ProdottoRepository;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
// Assumendo che ICertificazioneService sia in service.interfaces
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.ICertificazioneService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IProdottoService;


public class ProdottoService implements IProdottoService {

    private final IProdottoRepository prodottoRepository; // Usa l'interfaccia
    private  ICertificazioneService certificazioneService; // Inietta il service delle certificazioni
    private IVenditoreRepository venditoreRepository;

    // Costruttore per l'iniezione delle dipendenze
    public ProdottoService(IProdottoRepository prodottoRepository, ICertificazioneService certificazioneService, IVenditoreRepository venditoreRepository) {
        this.prodottoRepository = prodottoRepository;
        this.certificazioneService = certificazioneService;
        this.venditoreRepository = venditoreRepository;
    }

    public ProdottoService(IProdottoRepository prodottoRepository) {
        this.prodottoRepository = prodottoRepository;
    }



    public ProdottoService() {
        this.prodottoRepository = new ProdottoRepository(); // Costruttore di default per compatibilità
    }


    @Override
    public Prodotto creaProdotto(String nome, String descrizione, double prezzo, int quantitaDisponibile,
                                 Venditore venditore) { // Modificato per ritornare Prodotto
        if (nome == null || descrizione == null || prezzo <= 0 || quantitaDisponibile <= 0 || venditore == null) {
           throw  new IllegalArgumentException("Errore nella creazione del prodotto");
        }
        int idProdotto = Math.abs(UUID.randomUUID().hashCode()); // Generazione ID
        Prodotto prodotto = new Prodotto(idProdotto, nome, descrizione, prezzo, quantitaDisponibile, venditore);

        venditore.getProdottiOfferti().add(prodotto); // Aggiunge alla lista del venditore
        prodottoRepository.save(prodotto); // Salva nel repository dei prodotti
        venditoreRepository.save(venditore);
        return prodotto;
    }

    @Override
    public List<Prodotto> getProdottiOfferti(Venditore venditore) {
        return prodottoRepository.findByVenditore(venditore);
    }

    @Override
    public void rimuoviProdottoCatalogo(Venditore venditore, Prodotto prodotto) {
        if(prodotto==null||venditore==null) throw new IllegalArgumentException("prodotto e venditore non puo essere null");

        if(!prodotto.getVenditore().equals(venditore))throw new IllegalArgumentException("permesso negato, il prodotto non appartiene al venditore");

        // 1. Rimuovere le certificazioni associate al prodotto
        List<Certificazione> certificazioniProdotto = certificazioneService.getCertificazioniProdotto(prodotto.getId());
        for (Certificazione cert : certificazioniProdotto) {
            // Rimuove la certificazione dal repository delle certificazioni
            // e implicitamente dalla lista interna del prodotto se il service lo gestisce,
            // o si potrebbe fare esplicitamente: prodotto.rimuoviCertificazione(cert);
            certificazioneService.rimuoviCertificazioneGlobale(cert.getIdCertificazione());
        }
        // Assicurati che la lista interna del prodotto sia pulita
        prodotto.getCertificazioni().clear();


        // 2. Rimuovere il prodotto dalla lista del venditore
        boolean rimossoDaVenditore = venditore.getProdottiOfferti().remove(prodotto);

        // 3. Rimuovere il prodotto dal repository dei prodotti
        if (rimossoDaVenditore) { // O anche se non era nella lista ma vogliamo comunque cancellarlo dal repo
            prodottoRepository.deleteById(prodotto.getId());

        }
    }

    @Override
    public void aggiornaQuantitaProdotto(Venditore venditore, Prodotto prodotto, int nuovaQuantita) {
        if(prodotto==null||venditore==null)
            throw new IllegalArgumentException("prodotto e venditore non puo essere null");

        if(!prodotto.getVenditore().equals(venditore))
            throw new IllegalArgumentException("permesso negato, il prodotto non appartiene al venditore");

        if (nuovaQuantita < 0)
            throw new IllegalArgumentException("la quantità non puo essere minore di 0");

        prodotto.setQuantitaDisponibile(nuovaQuantita);
        prodottoRepository.save(prodotto); // Salva le modifiche

    }

    @Override
    public void aggiungiQuantitaProdotto(Venditore venditore, Prodotto prodotto, int quantitaAggiunta) {
        if(prodotto==null||venditore==null)
            throw new IllegalArgumentException("prodotto e venditore non puo essere null");

        if(!prodotto.getVenditore().equals(venditore))
            throw new IllegalArgumentException("permesso negato, il prodotto non appartiene al venditore");

        if (quantitaAggiunta <= 0)
            throw new IllegalArgumentException("non possono essere aggiunti un numero <=0 di elementi");
        prodotto.setQuantitaDisponibile(prodotto.getQuantitaDisponibile() + quantitaAggiunta);
        prodottoRepository.save(prodotto); // Salva le modifiche

    }

    @Override
    public void rimuoviQuantitaProdotto(Venditore venditore, Prodotto prodotto, int quantitaRimossa) {
        if(prodotto==null||venditore==null)
            throw new IllegalArgumentException("prodotto e venditore non puo essere null");

        if(!prodotto.getVenditore().equals(venditore))
            throw new IllegalArgumentException("permesso negato, il prodotto non appartiene al venditore");

        if ((prodotto.getQuantitaDisponibile()-quantitaRimossa) <= 0)
            throw new IllegalArgumentException("Impossibile rimuovere questa quantità di prodotto");
        prodotto.setQuantitaDisponibile(prodotto.getQuantitaDisponibile() - quantitaRimossa);
        prodottoRepository.save(prodotto); // Salva le modifiche

    }



    // Metodo per aggiungere una certificazione a un prodotto (usando il CertificazioneService)
    public Certificazione aggiungiCertificazioneAProdotto(Prodotto prodotto, String nomeCertificazione, String enteRilascio, Date dataRilascio, Date dataScadenza) {
        if (prodotto == null || certificazioneService == null) {
            System.err.println("Prodotto o servizio certificazioni non disponibile.");
            return null;
        }
        // Il CertificazioneService si occupa di creare, salvare la certificazione
        // e di associarla correttamente al prodotto (aggiornando anche la lista interna del prodotto).
        return certificazioneService.creaCertificazionePerProdotto(nomeCertificazione, enteRilascio, dataRilascio, dataScadenza, prodotto);
    }

    // Metodo per rimuovere una certificazione da un prodotto
    public void rimuoviCertificazioneDaProdotto(Prodotto prodotto, int idCertificazione) {
        if(prodotto==null)
            throw new IllegalArgumentException("prodotto non puo essere null");
        if(certificazioneService.getCertificazioneById(idCertificazione) == null)
            throw new IllegalArgumentException("Certificazione non esistente");

        boolean successo = certificazioneService.rimuoviCertificazione(idCertificazione, prodotto);
        if(!successo) throw new IllegalArgumentException("Impossibile rimuovere questa certificazione dal prodotto");
    }




    public IProdottoRepository getProdottoRepository() {
        return prodottoRepository;
    }

    // Metodo per ottenere le certificazioni di un prodotto
    public List<Certificazione> getCertificazioniDelProdotto(Prodotto prodotto) {
        if (prodotto == null || certificazioneService == null) {
            return List.of(); // Ritorna lista vuota se non è possibile procedere
        }
        return certificazioneService.getCertificazioniProdotto(prodotto.getId());
    }
    

    @Override
    public void decrementaQuantita(int idProdotto, int quantitaDaDecrementare) {
        // Validazione parametri di input
        if (quantitaDaDecrementare <= 0) {
            throw new IllegalArgumentException("La quantità da decrementare deve essere maggiore di zero");
        }

        // Ricerca del prodotto tramite repository
        Prodotto prodotto = this.prodottoRepository.findById(idProdotto);
        if (prodotto == null) {
            throw new IllegalArgumentException("Prodotto con ID " + idProdotto + " non trovato");
        }

        // Verifica che ci sia abbastanza quantità disponibile
        int quantitaDisponibile = prodotto.getQuantitaDisponibile();
        if (quantitaDaDecrementare > quantitaDisponibile) {
            throw new QuantitaNonDisponibileException(
                    (long) idProdotto,
                    quantitaDaDecrementare,
                    quantitaDisponibile,
                    "Prodotto");
        }

        // Decrementa la quantità e salva nel repository
        prodotto.setQuantitaDisponibile(quantitaDisponibile - quantitaDaDecrementare);
        this.prodottoRepository.save(prodotto);
    }


}