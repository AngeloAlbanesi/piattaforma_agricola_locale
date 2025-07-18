package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.unicam.cs.ids.piattaforma_agricola_locale.exception.QuantitaNonDisponibileException;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.carrello.Carrello;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.carrello.ElementoCarrello;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Pacchetto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.Acquistabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.ICarrelloRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.ICarrelloService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.AcquistabileService;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.TipoAcquistabile;

@Service
public class CarrelloService implements ICarrelloService {

    private final ICarrelloRepository carrelloRepository;
    private final AcquistabileService acquistabileService;

    @Autowired
    public CarrelloService(ICarrelloRepository carrelloRepository, AcquistabileService acquistabileService) {
        this.carrelloRepository = carrelloRepository;
        this.acquistabileService = acquistabileService;
    }

    /**
     * Crea un nuovo carrello per un acquirente
     * 
     * @param acquirente l'acquirente
     * @return il carrello creato
     */
    public Carrello creaNuovoCarrello(Acquirente acquirente) {
        int idCarrello = java.util.UUID.randomUUID().hashCode();
        Carrello nuovoCarrello = new Carrello(acquirente);
        carrelloRepository.save(nuovoCarrello);
        return nuovoCarrello;
    }

    /**
     * Ottiene il carrello di un acquirente
     * 
     * @param acquirente l'acquirente
     * @return il carrello se esiste
     */
    public Optional<Carrello> getCarrelloAcquirente(Acquirente acquirente) {
        Optional<Carrello> carrelloOpt = carrelloRepository.findByAcquirente(acquirente);
        
        // Inietta l'AcquistabileService negli elementi del carrello per permettere il recupero dei dettagli
        if (carrelloOpt.isPresent()) {
            Carrello carrello = carrelloOpt.get();
            carrello.getElementiCarrello().forEach(elemento -> 
                elemento.setAcquistabileService(acquistabileService));
        }
        
        return carrelloOpt;
    }

    /**
     * Aggiunge un elemento al carrello con controllo preliminare di disponibilità
     * 
     * @param acquirente   l'acquirente
     * @param acquistabile l'acquistabile da aggiungere
     * @param quantita     la quantità
     * @throws IllegalArgumentException        se l'acquistabile non è approvato o
     *                                         la quantità non è valida
     * @throws QuantitaNonDisponibileException se la quantità richiesta supera
     *                                         quella disponibile
     */
    public void aggiungiElementoAlCarrello(Acquirente acquirente, Acquistabile acquistabile, int quantita) {
        // Verifica che la quantità sia valida
        if (quantita <= 0) {
            throw new IllegalArgumentException("La quantità deve essere maggiore di zero");
        }

        // Verifica che l'acquistabile sia approvato (solo per i Prodotti)
        if (acquistabile instanceof Prodotto) {
            Prodotto prodotto = (Prodotto) acquistabile;
            if (prodotto
                    .getStatoVerifica() != it.unicam.cs.ids.piattaforma_agricola_locale.model.common.StatoVerificaValori.APPROVATO) {
                throw new IllegalArgumentException("Non è possibile aggiungere al carrello un prodotto non approvato");
            }
        }

        // Ottieni o crea il carrello dell'acquirente
        Optional<Carrello> carrelloOpt = carrelloRepository.findByAcquirente(acquirente);
        Carrello carrelloAcquirente;

        if (carrelloOpt.isPresent()) {
            carrelloAcquirente = carrelloOpt.get();
        } else {
            carrelloAcquirente = creaNuovoCarrello(acquirente);
        }

        // Verifica se l'elemento esiste già nel carrello
        // Prima inietta il service in tutti gli elementi esistenti per permettere il confronto
        carrelloAcquirente.getElementiCarrello().forEach(elemento -> elemento.setAcquistabileService(acquistabileService));
        
        Optional<ElementoCarrello> elementoEsistente = carrelloAcquirente.getElementiCarrello().stream()
                .filter(elemento -> {
                    // Verifica sia il tipo che l'ID per evitare conflitti tra tabelle diverse
                    return elemento.getTipoAcquistabile().equals(TipoAcquistabile.fromAcquistabile(acquistabile))
                            && elemento.getIdAcquistabile().equals(acquistabile.getId());
                })
                .findFirst();

        // Calcola la quantità totale che si vuole avere nel carrello
        int quantitaTotaleRichiesta = quantita;
        if (elementoEsistente.isPresent()) {
            quantitaTotaleRichiesta += elementoEsistente.get().getQuantita();
        }

        // *** CONTROLLO PRELIMINARE DI DISPONIBILITÀ ***
        verificaDisponibilita(acquistabile, quantitaTotaleRichiesta);

        // Se il controllo di disponibilità passa, procedi con l'aggiunta/aggiornamento
        if (elementoEsistente.isPresent()) {
            // Se l'elemento esiste già, aggiorna la quantità
            ElementoCarrello elemento = elementoEsistente.get();
            elemento.setQuantita(elemento.getQuantita() + quantita);
            // Assicurati che l'elemento abbia accesso al service
            elemento.setAcquistabileService(acquistabileService);
        } else {
            // Se l'elemento non esiste, creane uno nuovo e aggiungilo
            ElementoCarrello nuovoElemento = new ElementoCarrello(carrelloAcquirente, acquistabile, quantita);
            nuovoElemento.setAcquistabileService(acquistabileService);
            carrelloAcquirente.aggiungiElemento(nuovoElemento);
        }

        carrelloRepository.save(carrelloAcquirente);
    }

    /**
     * Verifica la disponibilità di un acquistabile per la quantità richiesta.
     * Questo controllo è preliminare e migliora l'UX, ma la verifica finale
     * deve comunque avvenire al momento del checkout.
     * 
     * @param acquistabile      l'acquistabile da verificare
     * @param quantitaRichiesta la quantità totale richiesta
     * @throws QuantitaNonDisponibileException se la quantità richiesta supera
     *                                         quella disponibile
     */
    private void verificaDisponibilita(Acquistabile acquistabile, int quantitaRichiesta) {
        int quantitaDisponibile = 0;
        String tipoProdotto = "";

        // Determina la quantità disponibile in base al tipo di acquistabile
        if (acquistabile instanceof Prodotto) {
            Prodotto prodotto = (Prodotto) acquistabile;
            quantitaDisponibile = prodotto.getQuantitaDisponibile();
            tipoProdotto = "Prodotto";
        } else if (acquistabile instanceof Pacchetto) {
            Pacchetto pacchetto = (Pacchetto) acquistabile;
            quantitaDisponibile = pacchetto.getQuantitaDisponibile();
            tipoProdotto = "Pacchetto";
        } else if (acquistabile instanceof it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.Evento) {
            it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.Evento evento = (it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.Evento) acquistabile;
            quantitaDisponibile = evento.getPostiDisponibili();
            tipoProdotto = "Evento";
        } else {
            // Per altri tipi di acquistabili, assumiamo disponibilità illimitata
            // o gestiamo come caso generico
            return;
        }

        // Verifica che la quantità richiesta non superi quella disponibile
        if (quantitaRichiesta > quantitaDisponibile) {
            throw new QuantitaNonDisponibileException(
                    (long) acquistabile.getId(),
                    quantitaRichiesta,
                    quantitaDisponibile,
                    tipoProdotto);
        }
    }

    /**
     * Rimuove un elemento dal carrello
     * 
     * @param acquirente l'acquirente proprietario del carrello
     * @param elemento   l'elemento da rimuovere dal carrello
     * @throws IllegalArgumentException se l'acquirente o l'elemento sono null
     */
    @Transactional
    public void rimuoviElementoDalCarrello(Acquirente acquirente, ElementoCarrello elemento) {
        // Validazione input
        if (acquirente == null) {
            throw new IllegalArgumentException("L'acquirente non può essere null");
        }
        if (elemento == null) {
            throw new IllegalArgumentException("L'elemento da rimuovere non può essere null");
        }

        Optional<Carrello> carrelloOpt = carrelloRepository.findByAcquirente(acquirente);
        if (carrelloOpt.isPresent()) {
            Carrello carrelloAcquirente = carrelloOpt.get();

            // Trova l'elemento per ID invece di usare contains()
            ElementoCarrello elementoDaRimuovere = null;
            for (ElementoCarrello elem : carrelloAcquirente.getElementiCarrello()) {
                if (elem.getIdElemento() != null && elem.getIdElemento().equals(elemento.getIdElemento())) {
                    elementoDaRimuovere = elem;
                    break;
                }
            }

            if (elementoDaRimuovere != null) {
                carrelloAcquirente.getElementiCarrello().remove(elementoDaRimuovere);
                carrelloAcquirente.setUltimaModifica(new Date());
                carrelloRepository.save(carrelloAcquirente);
            }
            // Se l'elemento non esiste, non è necessario lanciare un'eccezione
            // poiché il risultato desiderato (elemento non presente) è già raggiunto
        }
        // Se il carrello non esiste, non c'è nulla da rimuovere
    }

    /**
     * Svuota il carrello di un acquirente rimuovendo tutti gli elementi presenti
     * 
     * @param acquirente l'acquirente proprietario del carrello da svuotare
     * @throws IllegalArgumentException se l'acquirente è null
     */
    @Transactional
    public void svuotaCarrello(Acquirente acquirente) {
        // Validazione input
        if (acquirente == null) {
            throw new IllegalArgumentException("L'acquirente non può essere null");
        }

        Optional<Carrello> carrelloOpt = carrelloRepository.findByAcquirente(acquirente);
        if (carrelloOpt.isPresent()) {
            Carrello carrelloAcquirente = carrelloOpt.get();
            
            // Svuota la lista degli elementi e aggiorna la data di modifica
            carrelloAcquirente.getElementiCarrello().clear();
            carrelloAcquirente.setUltimaModifica(new Date());
            
            // Salva le modifiche
            carrelloRepository.save(carrelloAcquirente);
        }
        // Se il carrello non esiste, non c'è nulla da svuotare
    }

    /**
     * Calcola il prezzo totale del carrello
     * 
     * @param acquirente l'acquirente
     * @return il prezzo totale
     */
    public double calcolaPrezzoTotaleCarrello(Acquirente acquirente) {
        Optional<Carrello> carrelloOpt = carrelloRepository.findByAcquirente(acquirente);
        if (carrelloOpt.isPresent()) {
            return carrelloOpt.get().calcolaPrezzoTotale();
        }
        return 0.0;
    }

    /**
     * Ottiene tutti i carrelli
     * 
     * @return lista di tutti i carrelli
     */
    public List<Carrello> getTuttiICarrelli() {
        return carrelloRepository.findAll();
    }

    /**
     * Ottiene il repository dei carrelli
     * 
     * @return il repository dei carrelli
     */
    public ICarrelloRepository getRepository() {
        return carrelloRepository;
    }

    /**
     * Ottiene il service per gli acquistabili
     * 
     * @return il service per gli acquistabili
     */
    public AcquistabileService getAcquistabileService() {
        return acquistabileService;
    }

}
