package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

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
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.stateOrdine.IStatoOrdine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.stateOrdine.StatoOrdineAnnullato;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.stateOrdine.StatoOrdineConsegnato;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.stateOrdine.StatoOrdineInLavorazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.stateOrdine.StatoOrdineNuovoInAttesaDiPagamento;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.stateOrdine.StatoOrdinePagatoProntoPerLavorazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.stateOrdine.StatoOrdineSpedito;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IOrdineRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IProdottoRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IPacchettoRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IRigaOrdineRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IOrdineService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.observer.IOrdineObservable;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.observer.IVenditoreObserver;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.pagamento.IMetodoPagamentoStrategy;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.pagamento.PagamentoException;

@Service
@Slf4j
public class OrdineService implements IOrdineService, IOrdineObservable {

    private final IOrdineRepository ordineRepository;
    private final IRigaOrdineRepository rigaOrdineRepository;
    private final CarrelloService carrelloService;

    // Gestione Observer Pattern
    private final List<IVenditoreObserver> observers;

    private final IProdottoRepository prodottoRepository;
    private final IPacchettoRepository pacchettoRepository;

    @Autowired
    public OrdineService(IOrdineRepository ordineRepository, IRigaOrdineRepository rigaOrdineRepository,
            CarrelloService carrelloService, IProdottoRepository prodottoRepository,
            IPacchettoRepository pacchettoRepository) {
        this.ordineRepository = ordineRepository;
        this.rigaOrdineRepository = rigaOrdineRepository;
        this.carrelloService = carrelloService;
        this.prodottoRepository = prodottoRepository;
        this.pacchettoRepository = pacchettoRepository;
        this.observers = new ArrayList<>();
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
            Ordine ordine = new Ordine(dataOrdine, acquirente);
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
            ordineRepository.save(ordine);
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
    public Optional<Ordine> findOrdineById(Long idOrdine) {
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
     * NUOVO: Ottiene gli ordini relativi a un venditore specifico.
     * Usa la relazione diretta venditore-ordine invece di cercare attraverso i
     * prodotti.
     * 
     * @param venditore il venditore
     * @return lista degli ordini del venditore
     */
    public List<Ordine> getOrdiniVenditore(Venditore venditore) {
        if (venditore == null) {
            return new ArrayList<>();
        }

        // NUOVA LOGICA: Cerca direttamente gli ordini per venditore
        List<Ordine> ordini = ordineRepository.findByVenditore(venditore);

        // DEBUG: Log detailed information about returned orders
        log.info("DEBUG getOrdiniVenditore - Vendor: {}, Found {} orders",
                venditore.getIdUtente(), ordini.size());

        ordini.forEach(ordine -> {
            Long ordineVendorId = ordine.getVenditore() != null ? ordine.getVenditore().getIdUtente() : null;
            log.info("DEBUG Order: {} - DirectVendor: {}, RequestedVendor: {}, Match: {}",
                    ordine.getIdOrdine(),
                    ordineVendorId,
                    venditore.getIdUtente(),
                    ordineVendorId != null && ordineVendorId.equals(venditore.getIdUtente()));
        });

        return ordini;
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
            ordineRepository.save(ordine);

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
    public void eliminaOrdine(Long idOrdine) throws OrdineException {
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

    @Deprecated
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

            Date dataOrdine = new Date();
            Ordine ordine = new Ordine(dataOrdine, acquirente);

            double importoTotale = 0.0;

            // 4. Inietta il service negli elementi del carrello per permettere il recupero
            // degli acquistabili
            elementi.forEach(elemento -> elemento.setAcquistabileService(carrelloService.getAcquistabileService()));

            // 5. Per ogni elemento del carrello, verifica disponibilità e crea riga ordine
            for (ElementoCarrello elemento : elementi) {
                Acquistabile acquistabile = elemento.getAcquistabile();
                if (acquistabile == null) {
                    throw new OrdineException(
                            "Impossibile recuperare l'acquistabile per l'elemento con ID: " + elemento.getIdElemento());
                }
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

                RigaOrdine rigaOrdine = new RigaOrdine(
                        ordine,
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

            // 6. Imposta l'importo totale
            ordine.setImportoTotale(importoTotale);

            // 7. L'ordine è già inizializzato con stato "in attesa di pagamento" dal
            // costruttore

            // 8. Salva l'ordine e le righe nei repository
            ordineRepository.save(ordine);
            for (RigaOrdine riga : ordine.getRigheOrdine()) {
                // Inietta l'AcquistabileService nelle righe ordine
                riga.setAcquistabileService(carrelloService.getAcquistabileService());
                rigaOrdineRepository.save(riga);
            }

            // 9. Svuota il carrello dopo aver creato l'ordine
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

    @Override
    public List<Ordine> creaOrdiniDaCarrello(Acquirente acquirente)
            throws CarrelloVuotoException, QuantitaNonDisponibileAlCheckoutException, OrdineException {
        if (acquirente == null) {
            throw new OrdineException("Impossibile creare ordini: l'acquirente non può essere null");
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

            // 3. Inietta il service negli elementi del carrello per permettere il recupero
            // degli acquistabili
            elementi.forEach(elemento -> elemento.setAcquistabileService(carrelloService.getAcquistabileService()));

            // 4. Raggruppa gli elementi del carrello per venditore
            Map<Venditore, List<ElementoCarrello>> elementiPerVenditore = new HashMap<>();

            for (ElementoCarrello elemento : elementi) {
                Acquistabile acquistabile = elemento.getAcquistabile();
                if (acquistabile == null) {
                    throw new OrdineException(
                            "Impossibile recuperare l'acquistabile per l'elemento con ID: " + elemento.getIdElemento());
                }

                // Ottieni il venditore dell'acquistabile
                Venditore venditore = null;
                if (acquistabile instanceof Prodotto) {
                    venditore = ((Prodotto) acquistabile).getVenditore();
                } else if (acquistabile instanceof Pacchetto) {
                    venditore = ((Pacchetto) acquistabile).getVenditore();
                }

                if (venditore == null) {
                    throw new OrdineException(
                            "Impossibile determinare il venditore per l'acquistabile: " + acquistabile.getNome());
                }

                // Aggiungi l'elemento alla lista del venditore
                elementiPerVenditore.computeIfAbsent(venditore, k -> new ArrayList<>()).add(elemento);
            }

            Date dataOrdine = new Date();
            List<Ordine> ordiniCreati = new ArrayList<>();

            // 5. Crea un ordine separato per ogni venditore
            for (Map.Entry<Venditore, List<ElementoCarrello>> entry : elementiPerVenditore.entrySet()) {
                Venditore venditore = entry.getKey();
                List<ElementoCarrello> elementiVenditore = entry.getValue();

                // Crea nuovo ordine per questo venditore
                Ordine ordine = new Ordine(dataOrdine, acquirente, venditore);
                double importoTotale = 0.0;

                // Per ogni elemento del venditore, verifica disponibilità e crea riga ordine
                for (ElementoCarrello elemento : elementiVenditore) {
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
                    RigaOrdine rigaOrdine = new RigaOrdine(
                            ordine,
                            acquistabile,
                            quantitaRichiesta,
                            acquistabile.getPrezzo());

                    // Aggiunge la riga direttamente alla lista delle righe ordine
                    ordine.getRigheOrdine().add(rigaOrdine);

                    // Calcola importo parziale
                    importoTotale += acquistabile.getPrezzo() * quantitaRichiesta;
                }

                // Imposta l'importo totale per questo ordine
                ordine.setImportoTotale(importoTotale);

                // Salva l'ordine e le righe nei repository
                ordineRepository.save(ordine);
                for (RigaOrdine riga : ordine.getRigheOrdine()) {
                    // Inietta l'AcquistabileService nelle righe ordine
                    riga.setAcquistabileService(carrelloService.getAcquistabileService());
                    rigaOrdineRepository.save(riga);
                }

                ordiniCreati.add(ordine);
            }

            // 6. Svuota il carrello dopo aver creato tutti gli ordini
            carrelloService.svuotaCarrello(acquirente);

            return ordiniCreati;

        } catch (CarrelloVuotoException | QuantitaNonDisponibileAlCheckoutException e) {
            // Rilancia le eccezioni specifiche senza modificarle
            throw e;
        } catch (Exception e) {
            throw new OrdineException("Errore imprevisto durante la creazione degli ordini dal carrello", e);
        }
    }

    @Deprecated
    /**
     * Conferma il pagamento di un ordine utilizzando la strategia di pagamento
     * specificata
     * e gestisce la transizione di stato. Questo metodo attiva il pattern Observer
     * quando
     * l'ordine transisce allo stato PRONTO_PER_LAVORAZIONE.
     *
     * @param ordine             l'ordine di cui confermare il pagamento
     * @param strategiaPagamento la strategia di pagamento da utilizzare
     * @throws OrdineException    se si verifica un errore durante la conferma del
     *                            pagamento
     * @throws PagamentoException se si verifica un errore durante l'elaborazione
     *                            del pagamento
     */
    public void confermaPagamento(Ordine ordine, IMetodoPagamentoStrategy strategiaPagamento)
            throws OrdineException, PagamentoException {
        // Validazione parametri
        if (ordine == null) {
            throw new OrdineException("Impossibile confermare il pagamento: l'ordine non può essere null");
        }

        if (strategiaPagamento == null) {
            throw new OrdineException(
                    "Impossibile confermare il pagamento: la strategia di pagamento non può essere null");
        }

        // Assicurati che lo stato sia inizializzato correttamente
        if (ordine.getStato() == null) {
            // Inizializza lo stato in base allo statoCorrente
            if (ordine.getStatoOrdine() == StatoCorrente.ATTESA_PAGAMENTO) {
                ordine.setStato(new StatoOrdineNuovoInAttesaDiPagamento());
            } else {
                // Usa il metodo createStateFromEnum che abbiamo aggiunto a Ordine
                // Questo viene fatto tramite reflection per evitare di duplicare la logica
                try {
                    java.lang.reflect.Method createStateMethod = Ordine.class.getDeclaredMethod("createStateFromEnum",
                            StatoCorrente.class);
                    createStateMethod.setAccessible(true);
                    IStatoOrdine nuovoStato = (IStatoOrdine) createStateMethod.invoke(ordine, ordine.getStatoOrdine());
                    ordine.setStato(nuovoStato);
                } catch (Exception e) {
                    // Fallback: imposta lo stato in base all'enum
                    switch (ordine.getStatoOrdine()) {
                        case PRONTO_PER_LAVORAZIONE:
                            ordine.setStato(new StatoOrdinePagatoProntoPerLavorazione());
                            break;
                        case IN_LAVORAZIONE:
                            ordine.setStato(new StatoOrdineInLavorazione());
                            break;
                        case SPEDITO:
                            ordine.setStato(new StatoOrdineSpedito());
                            break;
                        case CONSEGNATO:
                            ordine.setStato(new StatoOrdineConsegnato());
                            break;
                        case ANNULLATO:
                            ordine.setStato(new StatoOrdineAnnullato());
                            break;
                        default:
                            ordine.setStato(new StatoOrdineNuovoInAttesaDiPagamento());
                    }
                }
            }
        }

        // Verifica che l'ordine sia nello stato corretto per il pagamento
        if (ordine.getStatoOrdine() != StatoCorrente.ATTESA_PAGAMENTO) {
            throw new OrdineException("L'ordine non è in attesa di pagamento e non può essere processato");
        }

        try {
            boolean successo = strategiaPagamento.elaboraPagamento(ordine);

            if (successo) {
                try {
                    ordine.paga();
                    ordineRepository.save(ordine);

                    try {
                        notificaObservers(ordine, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            } else {
                throw new PagamentoException(
                        "Il pagamento dell'ordine ID " + ordine.getIdOrdine() + " non è andato a buon fine");
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

        try {
            if (venditoreSpecifico != null) {
                notificaVenditoreSpecifico(ordine, venditoreSpecifico);
            } else {
                notificaTuttiIVenditoriCoinvolti(ordine);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                        e.printStackTrace();
                    }
                } else if (obs instanceof Venditore && obs instanceof IVenditoreObserver &&
                        ((Venditore) obs).getId() == venditore.getId()) {
                    try {
                        ((IVenditoreObserver) obs).update(ordine, righeDiCompetenza);
                    } catch (Exception e) {
                        e.printStackTrace();
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
        try {
            Map<Venditore, List<RigaOrdine>> righePeerVenditore = new HashMap<>();

            if (ordine.getRigheOrdine() == null) {
                return;
            }

            for (RigaOrdine riga : ordine.getRigheOrdine()) {
                try {
                    if (riga == null) {
                        continue;
                    }

                    Acquistabile acquistabile = riga.getAcquistabile();
                    if (acquistabile == null) {
                        continue;
                    }

                    Venditore venditore = acquistabile.getVenditore();
                    if (venditore == null) {
                        continue;
                    }

                    righePeerVenditore.computeIfAbsent(venditore, k -> new ArrayList<>()).add(riga);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (Map.Entry<Venditore, List<RigaOrdine>> entry : righePeerVenditore.entrySet()) {
                try {
                    Venditore venditore = entry.getKey();
                    List<RigaOrdine> righeDiCompetenza = entry.getValue();

                    if (observers.isEmpty()) {
                        continue;
                    }

                    observers.forEach(obs -> {
                        try {
                            if (obs == null) {
                                return;
                            }

                            if (obs instanceof VenditoreObserverService) {
                                try {
                                    obs.update(ordine, righeDiCompetenza);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (obs instanceof Venditore && obs instanceof IVenditoreObserver &&
                                    ((Venditore) obs).getId() == venditore.getId()) {
                                try {
                                    ((IVenditoreObserver) obs).update(ordine, righeDiCompetenza);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public IOrdineRepository getOrdineRepository() {
        return ordineRepository;
    }

    /**
     * Ottiene il servizio per i carrelli
     * 
     * @return il servizio per i carrelli
     */
    public CarrelloService getCarrelloService() {
        return carrelloService;
    }

    @Override
    public Ordine avanzaStatoOrdine(Ordine ordine, Venditore venditore)
            throws OrdineException, IllegalStateException {

        if (ordine == null) {
            throw new OrdineException("Ordine non può essere null");
        }

        if (venditore == null) {
            throw new OrdineException("Venditore non può essere null");
        }

        // Verifica che il venditore abbia prodotti in questo ordine
        boolean hasProductsInOrder = ordine.getRigheOrdine().stream()
                .anyMatch(riga -> {
                    if (riga.getAcquistabile() == null)
                        return false;
                    Venditore venditoreProdotto = riga.getAcquistabile().getVenditore();
                    return venditoreProdotto != null &&
                            venditoreProdotto.getIdUtente().equals(venditore.getIdUtente());
                });

        if (!hasProductsInOrder) {
            throw new OrdineException("Il venditore non ha prodotti in questo ordine");
        }

        // Ottieni lo stato corrente e determina l'azione da eseguire
        StatoCorrente statoCorrente = ordine.getStatoOrdine();

        try {
            switch (statoCorrente) {
                case PRONTO_PER_LAVORAZIONE:
                    ordine.processa();
                    break;

                case IN_LAVORAZIONE:
                    ordine.spedisci();
                    break;

                case SPEDITO:
                    ordine.consegna();
                    break;

                case CONSEGNATO:
                    throw new IllegalStateException("L'ordine è già stato consegnato e non può avanzare ulteriormente");

                case ANNULLATO:
                    throw new IllegalStateException("L'ordine è stato annullato e non può avanzare");

                case ATTESA_PAGAMENTO:
                    throw new IllegalStateException(
                            "L'ordine è in attesa di pagamento. Il venditore non può farlo avanzare.");

                default:
                    throw new IllegalStateException("Stato ordine non riconosciuto: " + statoCorrente);
            }

            // Aggiorna l'ordine nel database
            ordineRepository.save(ordine);

            return ordine;

        } catch (UnsupportedOperationException e) {
            throw new IllegalStateException(
                    "Impossibile avanzare l'ordine dallo stato " + statoCorrente + ": " + e.getMessage());
        }
    }

}
