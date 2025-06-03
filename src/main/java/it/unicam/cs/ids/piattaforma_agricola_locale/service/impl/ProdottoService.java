package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl; // o service.classes

import java.util.Date;
import java.util.List;
import java.util.UUID;

import it.unicam.cs.ids.piattaforma_agricola_locale.exception.QuantitaNonDisponibileException;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IProdottoRepository; // Usa interfaccia
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.ProdottoRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
// Assumendo che ICertificazioneService sia in service.interfaces
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.ICertificazioneService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IProdottoService;


public class ProdottoService implements IProdottoService {

    private IProdottoRepository prodottoRepository; // Usa l'interfaccia
    private ICertificazioneService certificazioneService; // Inietta il service delle certificazioni

    // Costruttore per l'iniezione delle dipendenze
    public ProdottoService(IProdottoRepository prodottoRepository, ICertificazioneService certificazioneService) {
        this.prodottoRepository = prodottoRepository;
        this.certificazioneService = certificazioneService;
    }

    public ProdottoService(IProdottoRepository prodottoRepository) {
        this.prodottoRepository = prodottoRepository;
    }

    // Costruttore precedente (per compatibilità temporanea, ma da rimuovere in favore dell'iniezione)
    // public ProdottoService() {
    //     this.prodottoRepository = new ProdottoRepository();
    //     // this.certificazioneService = new CertificazioneService(new CertificazioneRepository()); // Esempio di istanziazione diretta
    // }

    public ProdottoService() {
        this.prodottoRepository = new ProdottoRepository(); // Costruttore di default per compatibilità
    }


    @Override
    public Prodotto creaProdotto(String nome, String descrizione, double prezzo, int quantitaDisponibile,
                                 Venditore venditore) { // Modificato per ritornare Prodotto
        if (nome == null || descrizione == null || prezzo <= 0 || quantitaDisponibile <= 0 || venditore == null) {
            // Considera di lanciare IllegalArgumentException invece di ritornare null/void
            System.err.println("Dati per la creazione del prodotto non validi.");
            return null;
        }
        int idProdotto = Math.abs(UUID.randomUUID().hashCode()); // Generazione ID
        Prodotto prodotto = new Prodotto(idProdotto, nome, descrizione, prezzo, quantitaDisponibile, venditore);

        venditore.getProdottiOfferti().add(prodotto); // Aggiunge alla lista del venditore
        prodottoRepository.save(prodotto); // Salva nel repository dei prodotti
        return prodotto;
    }

    @Override
    public List<Prodotto> getProdottiOfferti(Venditore venditore) {
        // Potrebbe essere più corretto chiedere al repository se la lista del venditore non è la "source of truth"
        // return prodottoRepository.findByVenditoreId(venditore.getIdUtente()); // Assumendo che Venditore abbia getIdUtente()
        return venditore.getProdottiOfferti(); // Mantenuto come prima per ora
    }

    @Override
    public boolean rimuoviProdottoCatalogo(Venditore venditore, Prodotto prodotto) {
        if (prodotto == null || venditore == null || !prodotto.getVenditore().equals(venditore)) {
            // Lanciare eccezione per "prodotto non trovato" o "permesso negato"
            return false;
        }

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
            return true;
        }
        return false; // Se non è stato rimosso dalla lista del venditore (es. non c'era)
    }

    @Override
    public boolean aggiornaQuantitaProdotto(Venditore venditore, Prodotto prodotto, int nuovaQuantita) {
        if (prodotto == null || venditore == null || !prodotto.getVenditore().equals(venditore)) return false;
        if (nuovaQuantita < 0) { // Quantità non può essere negativa
            return false;
        }
        prodotto.setQuantitaDisponibile(nuovaQuantita);
        prodottoRepository.save(prodotto); // Salva le modifiche
        return true;
    }

    @Override
    public boolean aggiungiQuantitaProdotto(Venditore venditore, Prodotto prodotto, int quantitaAggiunta) {
        if (prodotto == null || venditore == null || !prodotto.getVenditore().equals(venditore)) return false;
        if (quantitaAggiunta <= 0) {
            return false;
        }
        prodotto.setQuantitaDisponibile(prodotto.getQuantitaDisponibile() + quantitaAggiunta);
        prodottoRepository.save(prodotto); // Salva le modifiche
        return true;
    }

    @Override
    public boolean rimuoviQuantitaProdotto(Venditore venditore, Prodotto prodotto, int quantitaRimossa) {
        if (prodotto == null || venditore == null || !prodotto.getVenditore().equals(venditore)) return false;
        if (quantitaRimossa <= 0 || prodotto.getQuantitaDisponibile() - quantitaRimossa < 0) {
            return false;
        }
        prodotto.setQuantitaDisponibile(prodotto.getQuantitaDisponibile() - quantitaRimossa);
        prodottoRepository.save(prodotto); // Salva le modifiche
        return true;
    }

    public void mostraProdotti(Venditore venditore) {
        if (venditore == null) return;
        for (Prodotto p : getProdottiOfferti(venditore)) { // Usa il metodo del service
            System.out.println(p.getNome() + " - " + p.getPrezzo() + "€ - disponibili:" + p.getQuantitaDisponibile());
            stampaCertificazioni(p);
        }
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
    public boolean rimuoviCertificazioneDaProdotto(Prodotto prodotto, int idCertificazione) {
        if (prodotto == null || certificazioneService == null) {
            System.err.println("Prodotto o servizio certificazioni non disponibile.");
            return false;
        }
        return certificazioneService.rimuoviCertificazione(idCertificazione, prodotto);
    }


    public void stampaCertificazioni(Prodotto prodotto) {
        if (prodotto == null) return;
        // Recupera le certificazioni tramite il service o direttamente dalla lista del prodotto
        // se si è sicuri che sia sempre aggiornata. Chiedere al service è più robusto.
        List<Certificazione> certificazioni = certificazioneService.getCertificazioniProdotto(prodotto.getId());
        // Oppure: List<Certificazione> certificazioni = prodotto.getCertificazioniProdotto();

        if (certificazioni.isEmpty()) {
            System.out.println("  Nessuna certificazione per questo prodotto.");
        } else {
            System.out.println("  Certificazioni del prodotto:");
            for (Certificazione c : certificazioni) {
                c.stampaCertificazione(); // Assumendo che Certificazione abbia un metodo stampa
            }
        }
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