package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import java.util.List;
import java.util.Optional;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.carrello.ElementoCarrello;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.carrello.carrello;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.Acquistabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.CarrelloRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.ICarrelloService;

public class CarrelloService implements ICarrelloService {

    private CarrelloRepository carrelloRepository = new CarrelloRepository();

    /**
     * Crea un nuovo carrello per un acquirente
     * 
     * @param acquirente l'acquirente
     * @return il carrello creato
     */
    public carrello creaNuovoCarrello(Acquirente acquirente) {
        int idCarrello = java.util.UUID.randomUUID().hashCode();
        carrello nuovoCarrello = new carrello(idCarrello, acquirente);
        carrelloRepository.save(nuovoCarrello);
        return nuovoCarrello;
    }

    /**
     * Ottiene il carrello di un acquirente
     * 
     * @param acquirente l'acquirente
     * @return il carrello se esiste
     */
    public Optional<carrello> getCarrelloAcquirente(Acquirente acquirente) {
        return carrelloRepository.findByAcquirente(acquirente);
    }

    /**
     * Aggiunge un elemento al carrello
     * 
     * @param acquirente   l'acquirente
     * @param acquistabile l'acquistabile da aggiungere
     * @param quantita     la quantità
     */
    public void aggiungiElementoAlCarrello(Acquirente acquirente, Acquistabile acquistabile, int quantita) {
        Optional<carrello> carrelloOpt = carrelloRepository.findByAcquirente(acquirente);
        carrello carrelloAcquirente;

        if (carrelloOpt.isPresent()) {
            carrelloAcquirente = carrelloOpt.get();
        } else {
            carrelloAcquirente = creaNuovoCarrello(acquirente);
        }

        ElementoCarrello elemento = new ElementoCarrello(acquistabile, quantita);
        carrelloAcquirente.aggiungiElemento(elemento);
        carrelloRepository.save(carrelloAcquirente);
    }

    /**
     * Rimuove un elemento dal carrello
     * 
     * @param acquirente l'acquirente
     * @param elemento   l'elemento da rimuovere
     */
    public void rimuoviElementoDalCarrello(Acquirente acquirente, ElementoCarrello elemento) {
        Optional<carrello> carrelloOpt = carrelloRepository.findByAcquirente(acquirente);
        if (carrelloOpt.isPresent()) {
            carrello carrelloAcquirente = carrelloOpt.get();
            carrelloAcquirente.rimuoviElemento(elemento);
            carrelloRepository.save(carrelloAcquirente);
        }
    }

    /**
     * Svuota il carrello di un acquirente
     * 
     * @param acquirente l'acquirente
     */
    public void svuotaCarrello(Acquirente acquirente) {
        Optional<carrello> carrelloOpt = carrelloRepository.findByAcquirente(acquirente);
        if (carrelloOpt.isPresent()) {
            carrello carrelloAcquirente = carrelloOpt.get();
            carrelloAcquirente.svuota();
            carrelloRepository.save(carrelloAcquirente);
        }
    }

    /**
     * Calcola il prezzo totale del carrello
     * 
     * @param acquirente l'acquirente
     * @return il prezzo totale
     */
    public double calcolaPrezzoTotaleCarrello(Acquirente acquirente) {
        Optional<carrello> carrelloOpt = carrelloRepository.findByAcquirente(acquirente);
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
    public List<carrello> getTuttiICarrelli() {
        return carrelloRepository.findAll();
    }

}
