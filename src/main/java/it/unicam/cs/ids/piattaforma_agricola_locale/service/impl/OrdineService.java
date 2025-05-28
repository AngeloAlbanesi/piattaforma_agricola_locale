package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import it.unicam.cs.ids.piattaforma_agricola_locale.exception.CarrelloVuotoException;
import it.unicam.cs.ids.piattaforma_agricola_locale.exception.OrdineException;
import it.unicam.cs.ids.piattaforma_agricola_locale.exception.QuantitaNonDisponibileAlCheckoutException;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.carrello.Carrello;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.carrello.ElementoCarrello;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Pacchetto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.Acquistabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.RigaOrdine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.StatoCorrente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.OrdineRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.RigaOrdineRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IOrdineService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.observer.IOrdineObservable;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.observer.IVenditoreObserver;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.pagamento.IMetodoPagamentoStrategy;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.pagamento.PagamentoException;

public class OrdineService implements IOrdineService, IOrdineObservable {

    private OrdineRepository ordineRepository = new OrdineRepository();
    private RigaOrdineRepository rigaOrdineRepository = new RigaOrdineRepository();
    private CarrelloService carrelloService = new CarrelloService();

    // Gestione Observer Pattern
    private final List<IVenditoreObserver> observers;

    public OrdineService() {
        this.observers = new ArrayList<>();
    }

    /**
     * Costruttore con CarrelloService esterno per testing e dependency injection
     * 
     * @param carrelloService il servizio carrello da utilizzare
     */
    public OrdineService(CarrelloService carrelloService) {
        this.observers = new ArrayList<>();
        this.carrelloService = carrelloService;
    }

    @Override
    public void creaNuovoOrdine(Acquirente acquirente) throws OrdineException {
        if (acquirente == null) {
            throw new OrdineException("Impossibile creare un ordine: l'acquirente non può essere null");
        }

        try {
            int idOrdine = UUID.randomUUID().hashCode();
            Date dataOrdine = new Date(); // mette anche l'ora
            // Non è più necessario passare lo stato come parametro,
            // viene inizializzato automaticamente a "in attesa di pagamento"
            Ordine ordine = new Ordine(idOrdine, dataOrdine, acquirente);
            ordineRepository.save(ordine);

            // Notifica gli observer dopo la creazione dell'ordine
            // In questo caso l'ordine è vuoto (senza righe), quindi notifica solo se
            // necessario
            if (!ordine.getRigheOrdine().isEmpty()) {
                notificaObservers(ordine, null);
            }
        } catch (Exception e) {
            throw new OrdineException("Errore durante la creazione dell'ordine", e);
        }
    }

    @Override
    public void calcolaPrezzoOrdine(Ordine ordine) throws OrdineException {
        if (ordine == null) {
            throw new OrdineException("Impossibile calcolare il prezzo: l'ordine non può essere null");
        }

        if (ordine.getRigheOrdine() == null || ordine.getRigheOrdine().isEmpty()) {
            throw new OrdineException("Impossibile calcolare il prezzo: l'ordine non contiene righe");
        }

        try {
            double prezzoTotOrdine = 0;
            for (RigaOrdine r : ordine.getRigheOrdine()) {
                double prezzoRiga = r.getPrezzoUnitario() * r.getQuantitaOrdinata();
                prezzoTotOrdine += prezzoRiga;
            }
            ordine.setImportoTotale(prezzoTotOrdine);
            ordineRepository.update(ordine);
        } catch (Exception e) {
            throw new OrdineException("Errore durante il calcolo del prezzo dell'ordine", e);
        }
    }

    /**
     * Trova un ordine per ID
     * 
     * @param idOrdine l'ID dell'ordine
     * @return l'ordine se trovato
     */
    public Optional<Ordine> findOrdineById(int idOrdine) {
        return ordineRepository.findById(idOrdine);
    }

    /**
     * Ottiene tutti gli ordini
     * 
     * @return lista di tutti gli ordini
     */
    public List<Ordine> getTuttiGliOrdini() {
        return ordineRepository.findAll();
    }

    /**
     * Ottiene gli ordini di un acquirente
     * 
     * @param acquirente l'acquirente
     * @return lista degli ordini dell'acquirente
     */
    public List<Ordine> getOrdiniAcquirente(Acquirente acquirente) {
        return ordineRepository.findByAcquirente(acquirente);
    }

    /**
     * Ottiene gli ordini per stato
     * 
     * @param stato lo stato degli ordini
     * @return lista degli ordini con lo stato specificato
     */
    public List<Ordine> getOrdiniPerStato(StatoCorrente stato) {
        return ordineRepository.findByStato(stato);
    }

    /**
     * Ottiene gli ordini relativi a un venditore
     * 
     * @param venditore il venditore
     * @return lista degli ordini che contengono prodotti del venditore
     */
    public List<Ordine> getOrdiniVenditore(Venditore venditore) {
        return ordineRepository.findByVenditore(venditore);
    }

    /**
     * Salva un ordine
     * 
     * @param ordine l'ordine da salvare
     */
    public void salvaOrdine(Ordine ordine) throws OrdineException {
        if (ordine == null) {
            throw new OrdineException("Impossibile salvare un ordine null");
        }

        try {
            ordineRepository.save(ordine);
            // Salva anche tutte le righe ordine
            for (RigaOrdine riga : ordine.getRigheOrdine()) {
                rigaOrdineRepository.save(riga);
            }

            // Notifica gli observer dopo il salvataggio
            notificaObservers(ordine, null);
        } catch (Exception e) {
            throw new OrdineException("Errore durante il salvataggio dell'ordine", e);
        }
    }

    /**
     * Aggiorna un ordine
     * 
     * @param ordine l'ordine da aggiornare
     */
    public void aggiornaOrdine(Ordine ordine) throws OrdineException {
        if (ordine == null) {
            throw new OrdineException("Impossibile aggiornare un ordine null");
        }

        try {
            ordineRepository.update(ordine);

            // Notifica gli observer dopo l'aggiornamento
            notificaObservers(ordine, null);
        } catch (Exception e) {
            throw new OrdineException("Errore durante l'aggiornamento dell'ordine", e);
        }
    }

    /**
     * Elimina un ordine
     * 
     * @param idOrdine l'ID dell'ordine da eliminare
     */
    public void eliminaOrdine(int idOrdine) throws OrdineException {
        try {
            Optional<Ordine> ordineOpt = ordineRepository.findById(idOrdine);
            if (ordineOpt.isPresent()) {
                rigaOrdineRepository.deleteByOrdine(ordineOpt.get());
                ordineRepository.deleteById(idOrdine);
            } else {
                throw new OrdineException("Ordine con ID " + idOrdine + " non trovato");
            }
        } catch (Exception e) {
            if (e instanceof OrdineException) {
                throw e;
            }
            throw new OrdineException("Errore durante l'eliminazione dell'ordine", e);
        }
    }

    @Override
    public Ordine creaOrdineDaCarrello(Acquirente acquirente)
            throws CarrelloVuotoException, QuantitaNonDisponibileAlCheckoutException, OrdineException {
        if (acquirente == null) {
            throw new OrdineException("Impossibile creare un ordine: l'acquirente non può essere null");
        }

        try {
            // 1. Recupera il carrello dell'acquirente
            Optional<Carrello> carrelloOpt = carrelloService.getCarrelloAcquirente(acquirente);
            if (carrelloOpt.isEmpty()) {
                throw new CarrelloVuotoException(acquirente.getId());
            }

            Carrello carrello = carrelloOpt.get();
            List<ElementoCarrello> elementi = carrello.getElementiCarrello();

            // 2. Verifica che il carrello non sia vuoto
            if (elementi.isEmpty()) {
                throw new CarrelloVuotoException("Il carrello dell'acquirente " + acquirente.getNome() + " è vuoto");
            }

            // 3. Crea un nuovo ordine
            int idOrdine = UUID.randomUUID().hashCode();
            Date dataOrdine = new Date();
            Ordine ordine = new Ordine(idOrdine, dataOrdine, acquirente);

            double importoTotale = 0.0;

            // 4. Per ogni elemento del carrello, verifica disponibilità e crea riga ordine
            for (ElementoCarrello elemento : elementi) {
                Acquistabile acquistabile = elemento.getAcquistabile();
                int quantitaRichiesta = elemento.getQuantita();

                // Verifica disponibilità finale
                int quantitaDisponibile = 0;
                if (acquistabile instanceof Prodotto) {
                    Prodotto prodotto = (Prodotto) acquistabile;
                    quantitaDisponibile = prodotto.getQuantitaDisponibile();
                } else if (acquistabile instanceof Pacchetto) {
                    Pacchetto pacchetto = (Pacchetto) acquistabile;
                    quantitaDisponibile = pacchetto.getQuantitaDisponibile();
                }

                if (quantitaRichiesta > quantitaDisponibile) {
                    throw new QuantitaNonDisponibileAlCheckoutException(
                            acquistabile.getNome(),
                            quantitaRichiesta,
                            quantitaDisponibile);
                }

                // Crea riga ordine
                int idRiga = UUID.randomUUID().hashCode();
                RigaOrdine rigaOrdine = new RigaOrdine(
                        idRiga,
                        acquistabile,
                        quantitaRichiesta,
                        acquistabile.getPrezzo());

                // Aggiunge la riga direttamente alla lista delle righe ordine
                ordine.getRigheOrdine().add(rigaOrdine);

                // Calcola importo parziale
                importoTotale += acquistabile.getPrezzo() * quantitaRichiesta;

                // RIMOSSO: La gestione dell'inventario è ora delegata al pattern Observer
                // Il decremento delle quantità avverrà tramite VenditoreOrderHandlerService
                // quando l'ordine transita allo stato PRONTO_PER_LAVORAZIONE dopo il pagamento
            }

            // 5. Imposta l'importo totale
            ordine.setImportoTotale(importoTotale);

            // 6. L'ordine è già inizializzato con stato "in attesa di pagamento" dal
            // costruttore

            // 7. Salva l'ordine e le righe nei repository
            ordineRepository.save(ordine);
            for (RigaOrdine riga : ordine.getRigheOrdine()) {
                rigaOrdineRepository.save(riga);
            }

            // 8. Svuota il carrello dopo aver creato l'ordine
            carrelloService.svuotaCarrello(acquirente);

            // NOTA: La notifica agli observer avverrà automaticamente quando
            // l'ordine transiterà allo stato PRONTO_PER_LAVORAZIONE dopo il pagamento
            // tramite il metodo paga() / processa() dell'ordine

            return ordine;

        } catch (CarrelloVuotoException | QuantitaNonDisponibileAlCheckoutException e) {
            // Rilancia le eccezioni specifiche senza modificarle
            throw e;
        } catch (Exception e) {
            throw new OrdineException("Errore imprevisto durante la creazione dell'ordine dal carrello", e);
        }
    }

    /**
     * Conferma il pagamento di un ordine utilizzando la strategia di pagamento specificata
     * e gestisce la transizione di stato. Questo metodo attiva il pattern Observer quando
     * l'ordine transisce allo stato PRONTO_PER_LAVORAZIONE.
     *
     * @param ordine l'ordine di cui confermare il pagamento
     * @param strategiaPagamento la strategia di pagamento da utilizzare
     * @throws OrdineException se si verifica un errore durante la conferma del pagamento
     * @throws PagamentoException se si verifica un errore durante l'elaborazione del pagamento
     */
    public void confermaPagamento(Ordine ordine, IMetodoPagamentoStrategy strategiaPagamento) throws OrdineException, PagamentoException {
        // Validazione parametri
        if (ordine == null) {
            throw new OrdineException("Impossibile confermare il pagamento: l'ordine non può essere null");
        }
        
        if (strategiaPagamento == null) {
            throw new OrdineException("Impossibile confermare il pagamento: la strategia di pagamento non può essere null");
        }

        // Verifica che l'ordine sia nello stato corretto per il pagamento
        if (ordine.getStatoOrdine() != StatoCorrente.ATTESA_PAGAMENTO) {
            throw new OrdineException("L'ordine non è in attesa di pagamento e non può essere processato");
        }

        try {
            // Elabora il pagamento utilizzando la strategia fornita
            boolean successo = strategiaPagamento.elaboraPagamento(ordine);
            
            if (successo) {
                // Se il pagamento è andato a buon fine:
                // 1. Effettua la transizione di stato tramite il pattern State
                // Questo cambierà lo stato da ATTESA_PAGAMENTO a PRONTO_PER_LAVORAZIONE
                ordine.paga();

                // 2. Aggiorna l'ordine nel repository con il nuovo stato
                ordineRepository.update(ordine);

                // 3. Notifica gli observer dopo la conferma del pagamento e l'update
                notificaObservers(ordine, null);
            } else {
                // Se il pagamento non è andato a buon fine, lancia PagamentoException
                throw new PagamentoException("Il pagamento dell'ordine ID " + ordine.getIdOrdine() + " non è andato a buon fine");
            }

        } catch (PagamentoException e) {
            // Rilancia le PagamentoException senza modificarle
            throw e;
        } catch (UnsupportedOperationException e) {
            // Questa eccezione proviene dal pattern State se si tenta un'operazione non
            // valida sullo stato corrente
            throw new OrdineException("Operazione di pagamento non permessa per lo stato corrente dell'ordine: "
                    + ordine.getStatoOrdine() + ". Dettagli: " + e.getMessage(), e);
        } catch (Exception e) {
            // Per altre eccezioni impreviste durante paga(), update() o notificaObservers()
            throw new OrdineException(
                    "Errore imprevisto durante la conferma del pagamento dell'ordine ID " + ordine.getIdOrdine(), e);
        }
    }

    // Implementazione dei metodi dell'interfaccia OrdineObservable

    @Override
    public void aggiungiObserver(IVenditoreObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("L'observer non può essere null");
        }
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void rimuoviObserver(IVenditoreObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("L'observer non può essere null");
        }
        observers.remove(observer);
    }

    @Override
    public void notificaObservers(Ordine ordine, Venditore venditoreSpecifico) {
        if (ordine == null) {
            throw new IllegalArgumentException("L'ordine non può essere null");
        }

        if (venditoreSpecifico != null) {
            // Notifica solo il venditore specifico
            notificaVenditoreSpecifico(ordine, venditoreSpecifico);
        } else {
            // Identifica e notifica tutti i venditori coinvolti nell'ordine
            notificaTuttiIVenditoriCoinvolti(ordine);
        }
    }

    /**
     * Notifica un venditore specifico con le righe d'ordine di sua competenza.
     */
    private void notificaVenditoreSpecifico(Ordine ordine, Venditore venditore) {
        // Filtra le righe d'ordine che contengono prodotti del venditore
        List<RigaOrdine> righeDiCompetenza = ordine.getRigheOrdine().stream()
                .filter(riga -> riga.getAcquistabile().getVenditore() != null &&
                        riga.getAcquistabile().getVenditore().getId() == venditore.getId())
                .collect(Collectors.toList());

        if (!righeDiCompetenza.isEmpty()) {
            observers.forEach(obs -> {
                if (obs instanceof VenditoreObserverService) {
                    try {
                        obs.update(ordine, righeDiCompetenza);
                    } catch (Exception e) {
                        System.err.println(
                                "Errore durante la notifica dell'observer (VenditoreOrderHandlerService) per il venditore specifico "
                                        +
                                        venditore.getDatiAzienda().getNomeAzienda() + ": " + e.getMessage());
                    }
                } else if (obs instanceof Venditore && obs instanceof IVenditoreObserver &&
                        ((Venditore) obs).getId() == venditore.getId()) {
                    try {
                        ((IVenditoreObserver) obs).update(ordine, righeDiCompetenza);
                    } catch (Exception e) {
                        System.err.println("Errore durante la notifica dell'observer (Venditore) " +
                                ((Venditore) obs).getDatiAzienda().getNomeAzienda() + ": " + e.getMessage());
                    }
                }
            });
        }
    }

    public List<IVenditoreObserver> getObservers() {
        return observers;
    }

    /**
     * Identifica tutti i venditori coinvolti nell'ordine e li notifica.
     */
    private void notificaTuttiIVenditoriCoinvolti(Ordine ordine) {
        // Mappa per raggruppare le righe d'ordine per venditore
        Map<Venditore, List<RigaOrdine>> righePeerVenditore = new HashMap<>();

        // Raggruppa le righe d'ordine per venditore
        for (RigaOrdine riga : ordine.getRigheOrdine()) {
            Acquistabile acquistabile = riga.getAcquistabile();
            Venditore venditore = acquistabile.getVenditore();

            if (venditore != null) {
                righePeerVenditore.computeIfAbsent(venditore, k -> new ArrayList<>()).add(riga);
            }
        }

        // Notifica ogni venditore con le sue righe di competenza
        for (Map.Entry<Venditore, List<RigaOrdine>> entry : righePeerVenditore.entrySet()) {
            Venditore venditore = entry.getKey();
            List<RigaOrdine> righeDiCompetenza = entry.getValue();

            observers.forEach(obs -> {
                if (obs instanceof VenditoreObserverService) {
                    try {
                        obs.update(ordine, righeDiCompetenza);
                    } catch (Exception e) {
                        System.err.println(
                                "Errore durante la notifica dell'observer (VenditoreOrderHandlerService) per il venditore "
                                        +
                                        venditore.getDatiAzienda().getNomeAzienda() + ": " + e.getMessage());
                    }
                } else if (obs instanceof Venditore && obs instanceof IVenditoreObserver &&
                        ((Venditore) obs).getId() == venditore.getId()) {
                    try {
                        ((IVenditoreObserver) obs).update(ordine, righeDiCompetenza);
                    } catch (Exception e) {
                        System.err.println("Errore durante la notifica dell'observer (Venditore) " +
                                ((Venditore) obs).getDatiAzienda().getNomeAzienda() + ": " + e.getMessage());
                    }
                }
            });
        }
    }

    public OrdineRepository getOrdineRepository() {
        return ordineRepository;
    }
}
