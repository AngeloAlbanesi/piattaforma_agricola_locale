package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import it.unicam.cs.ids.piattaforma_agricola_locale.exception.QuantitaNonDisponibileException;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.TipoOrigineProdotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IProdottoRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.VenditoreRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.ICertificazioneService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IProdottoService;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IVenditoreRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.ICertificazioneService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IProdottoService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IProcessoTrasformazioneService;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.processo.ProcessoTrasformazioneDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.*;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.mapper.ProcessoMapper;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.observer.IProdottoObservable;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.observer.ICuratoreObserver;

public class ProdottoService implements IProdottoService, IProdottoObservable {

    private final IProdottoRepository prodottoRepository;
    private ICertificazioneService certificazioneService;
    private IVenditoreRepository venditoreRepository;
    private final List<ICuratoreObserver> observers;

    // Costruttore per l'iniezione delle dipendenze complete
    public ProdottoService(IProdottoRepository prodottoRepository, ICertificazioneService certificazioneService,
                           IVenditoreRepository venditoreRepository) {
        this.prodottoRepository = prodottoRepository;
        this.certificazioneService = certificazioneService;
        this.venditoreRepository = venditoreRepository;
        this.observers = new ArrayList<>();
    }

    public ProdottoService(IProdottoRepository prodottoRepository) {
        this.prodottoRepository = prodottoRepository;
        this.observers = new ArrayList<>();
    }

    @Override
    public Prodotto creaProdotto(String nome, String descrizione, double prezzo, int quantitaDisponibile,
                                 Venditore venditore) {
        if (nome == null || descrizione == null || prezzo <= 0 || quantitaDisponibile <= 0 || venditore == null) {
            throw new IllegalArgumentException("Errore nella creazione del prodotto");
        }
        Prodotto prodotto = new Prodotto( nome, descrizione, prezzo, quantitaDisponibile, venditore);

        venditore.getProdottiOfferti().add(prodotto); // Aggiunge alla lista del venditore
        prodottoRepository.save(prodotto); // Salva nel repository dei prodotti
        venditoreRepository.save(venditore);
        
        // Notifica gli observer del nuovo prodotto creato
        notificaObservers(prodotto);
        
        return prodotto;
    }

    @Override
    public List<Prodotto> getProdottiOfferti(Venditore venditore) {
        return prodottoRepository.findByVenditore(venditore);
    }

    @Override
    public void rimuoviProdottoCatalogo(Venditore venditore, Prodotto prodotto) {
        if (prodotto == null || venditore == null)
            throw new IllegalArgumentException("prodotto e venditore non puo essere null");

        if (!prodotto.getVenditore().equals(venditore))
            throw new IllegalArgumentException("permesso negato, il prodotto non appartiene al venditore");

        // 1. Rimuovere le certificazioni associate al prodotto
        List<Certificazione> certificazioniProdotto = certificazioneService.getCertificazioniProdotto(prodotto.getId());
        for (Certificazione cert : certificazioniProdotto) {
            certificazioneService.rimuoviCertificazioneGlobale(cert.getIdCertificazione());
        }
        // Assicurati che la lista interna del prodotto sia pulita
        prodotto.getCertificazioni().clear();

        // 2. Rimuovere il prodotto dalla lista del venditore
        boolean rimossoDaVenditore = venditore.getProdottiOfferti().remove(prodotto);

        // 3. Rimuovere il prodotto dal repository dei prodotti
        if (rimossoDaVenditore) {
            prodottoRepository.deleteById(prodotto.getId());
        }
    }

    @Override
    public void aggiornaQuantitaProdotto(Venditore venditore, Prodotto prodotto, int nuovaQuantita) {
        if (prodotto == null || venditore == null)
            throw new IllegalArgumentException("prodotto e venditore non puo essere null");

        if (!prodotto.getVenditore().equals(venditore))
            throw new IllegalArgumentException("permesso negato, il prodotto non appartiene al venditore");

        if (nuovaQuantita < 0)
            throw new IllegalArgumentException("la quantità non puo essere minore di 0");

        prodotto.setQuantitaDisponibile(nuovaQuantita);
        prodottoRepository.save(prodotto); // Salva le modifiche
    }

    @Override
    public void aggiungiQuantitaProdotto(Venditore venditore, Prodotto prodotto, int quantitaAggiunta) {
        if (prodotto == null || venditore == null)
            throw new IllegalArgumentException("prodotto e venditore non puo essere null");

        if (!prodotto.getVenditore().equals(venditore))
            throw new IllegalArgumentException("permesso negato, il prodotto non appartiene al venditore");

        if (quantitaAggiunta <= 0)
            throw new IllegalArgumentException("non possono essere aggiunti un numero <=0 di elementi");
        prodotto.setQuantitaDisponibile(prodotto.getQuantitaDisponibile() + quantitaAggiunta);
        prodottoRepository.save(prodotto); // Salva le modifiche
    }

    @Override
    public void rimuoviQuantitaProdotto(Venditore venditore, Prodotto prodotto, int quantitaRimossa) {
        if (prodotto == null || venditore == null)
            throw new IllegalArgumentException("prodotto e venditore non puo essere null");

        if (!prodotto.getVenditore().equals(venditore))
            throw new IllegalArgumentException("permesso negato, il prodotto non appartiene al venditore");

        if ((prodotto.getQuantitaDisponibile() - quantitaRimossa) <= 0)
            throw new IllegalArgumentException("Impossibile rimuovere questa quantità di prodotto");
        prodotto.setQuantitaDisponibile(prodotto.getQuantitaDisponibile() - quantitaRimossa);
        prodottoRepository.save(prodotto); // Salva le modifiche
    }

    // Metodo per aggiungere una certificazione a un prodotto (usando il
    // CertificazioneService)
    public Certificazione aggiungiCertificazioneAProdotto(Prodotto prodotto, String nomeCertificazione,
                                                          String enteRilascio, Date dataRilascio, Date dataScadenza) {
        if (prodotto == null || certificazioneService == null) {
            System.err.println("Prodotto o servizio certificazioni non disponibile.");
            return null;
        }
        return certificazioneService.creaCertificazionePerProdotto(nomeCertificazione, enteRilascio, dataRilascio,
                dataScadenza, prodotto);
    }

    // Metodo per rimuovere una certificazione da un prodotto
    public void rimuoviCertificazioneDaProdotto(Prodotto prodotto, Long idCertificazione) {
        if (prodotto == null)
            throw new IllegalArgumentException("prodotto non puo essere null");
        if (certificazioneService.getCertificazioneById(idCertificazione) == null)
            throw new IllegalArgumentException("Certificazione non esistente");

        boolean successo = certificazioneService.rimuoviCertificazione(idCertificazione, prodotto);
        if (!successo)
            throw new IllegalArgumentException("Impossibile rimuovere questa certificazione dal prodotto");
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
    public void decrementaQuantita(Long idProdotto, int quantitaDaDecrementare) {
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

    /**
     * Crea e aggiunge un prodotto trasformato al catalogo del venditore.
     * Questo metodo gestisce la creazione di prodotti derivanti da processi di
     * trasformazione,
     * impostando automaticamente il tipo di origine e l'ID del processo.
     *
     * @param nome                     Il nome del prodotto trasformato
     * @param descrizione              La descrizione del prodotto
     * @param prezzo                   Il prezzo del prodotto
     * @param quantitaDisponibile      La quantità inizialmente disponibile
     * @param venditore                Il venditore (deve essere un Trasformatore)
     * @param idProcessoTrasformazione L'ID del processo di trasformazione
     *                                 utilizzato
     * @return Il prodotto trasformato creato
     * @throws IllegalArgumentException se i parametri sono invalidi o se il
     *                                  venditore non è un trasformatore
     */
    public Prodotto aggiungiProdottoTrasformato(String nome, String descrizione, double prezzo,
                                                int quantitaDisponibile, Venditore venditore,
                                                Long idProcessoTrasformazione) {
        // Validazione parametri di input
        if (idProcessoTrasformazione == null) {
            throw new IllegalArgumentException("L'ID del processo di trasformazione non può essere nullo");
        }

        // Verifica che il venditore sia effettivamente un trasformatore
        if (!(venditore instanceof it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Trasformatore)) {
            throw new IllegalArgumentException("Solo i trasformatori possono creare prodotti trasformati");
        }

        // Crea il prodotto base utilizzando il metodo esistente (che già notifica gli observer)
        Prodotto prodotto = creaProdotto(nome, descrizione, prezzo, quantitaDisponibile, venditore);

        // Imposta le proprietà specifiche per i prodotti trasformati
        prodotto.setTipoOrigine(TipoOrigineProdotto.TRASFORMATO);
        prodotto.setIdProcessoTrasformazioneOriginario(idProcessoTrasformazione);

        // Salva le modifiche al repository (il prodotto e il venditore sono già stati
        // salvati in creaProdotto)
        prodottoRepository.save(prodotto);

        return prodotto;
    }

    /**
     * Aggiunge un prodotto già esistente come prodotto trasformato.
     * Utile quando si ha già un'istanza di prodotto e si vuole convertirla in
     * prodotto trasformato.
     *
     * @param prodotto                 Il prodotto esistente da convertire
     * @param venditore                Il venditore proprietario del prodotto
     * @param idProcessoTrasformazione L'ID del processo di trasformazione
     * @throws IllegalArgumentException se i parametri sono invalidi
     */
    public void impostaProdottoComeTrasformato(Prodotto prodotto, Venditore venditore,
                                               Long idProcessoTrasformazione) {
        // Validazioni
        if (prodotto == null) {
            throw new IllegalArgumentException("Il prodotto non può essere nullo");
        }
        if (venditore == null) {
            throw new IllegalArgumentException("Il venditore non può essere nullo");
        }
        if (idProcessoTrasformazione == null) {
            throw new IllegalArgumentException("L'ID del processo di trasformazione non può essere nullo");
        }
        if (!prodotto.getVenditore().equals(venditore)) {
            throw new IllegalArgumentException("Il prodotto non appartiene al venditore specificato");
        }

        // Imposta come prodotto trasformato
        prodotto.setTipoOrigine(TipoOrigineProdotto.TRASFORMATO);
        prodotto.setIdProcessoTrasformazioneOriginario(idProcessoTrasformazione);

        // Aggiunge alla lista del venditore se non è già presente
        if (!venditore.getProdottiOfferti().contains(prodotto)) {
            venditore.getProdottiOfferti().add(prodotto);
        }

        // Salva le modifiche
        prodottoRepository.save(prodotto);
        venditoreRepository.save(venditore);
    }

    // ===== IMPLEMENTAZIONE PATTERN OBSERVER =====
    
    @Override
    public void aggiungiObserver(ICuratoreObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("L'observer non può essere null");
        }
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void rimuoviObserver(ICuratoreObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("L'observer non può essere null");
        }
        observers.remove(observer);
    }

    @Override
    public void notificaObservers(Prodotto prodotto) {
        if (prodotto == null) {
            throw new IllegalArgumentException("Il prodotto non può essere null");
        }
        
        // Notifica solo se il prodotto è in stato IN_REVISIONE
        if (prodotto.getStatoVerifica() == it.unicam.cs.ids.piattaforma_agricola_locale.model.common.StatoVerificaValori.IN_REVISIONE) {
            for (ICuratoreObserver observer : observers) {
                try {
                    observer.onProdottoCreato(prodotto);
                } catch (Exception e) {
                    // Log dell'errore ma continua con gli altri observer
                    System.err.println("Errore durante la notifica dell'observer: " + e.getMessage());
                }
            }
        }
    }

}