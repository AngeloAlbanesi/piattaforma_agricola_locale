package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import java.util.UUID;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Pacchetto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.StatoVerificaValori;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IPacchettoRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IVenditoreRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.PacchettoRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.VenditoreRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DistributoreDiTipicita;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IPacchettoService;
import it.unicam.cs.ids.piattaforma_agricola_locale.exception.QuantitaNonDisponibileException;

/**
 * Implementazione concreta di {@link IPacchettoService} che gestisce la logica
 * per la creazione, gestione e modifica dei pacchetti di prodotti nella
 * piattaforma agricola locale.
 *
 */
public class PacchettoService implements IPacchettoService {

    private final IPacchettoRepository pacchettoRepository;
    private final IVenditoreRepository venditoreRepository;

    public PacchettoService(IPacchettoRepository pacchettoRepository, IVenditoreRepository venditoreRepository) {
        this.pacchettoRepository = pacchettoRepository;
        this.venditoreRepository = venditoreRepository;
    }

    public PacchettoService() {
        this.pacchettoRepository = new PacchettoRepository();
        this.venditoreRepository = new VenditoreRepository();
    }

    @Override
    public void creaPacchetto(DistributoreDiTipicita distributore, String nome, String descrizione, int quantita,
            double prezzoPacchetto) {
        int idPacchetto = UUID.randomUUID().hashCode();
        Pacchetto pacchetto = new Pacchetto(distributore, nome, descrizione, quantita, prezzoPacchetto);
        distributore.getPacchettiOfferti().add(pacchetto);
        this.pacchettoRepository.salva(pacchetto);
        this.venditoreRepository.save(distributore);

    }

    @Override
    public void rimuoviPacchettoCatalogo(DistributoreDiTipicita distributore, Pacchetto pacchetto) {
        if (distributore != pacchetto.getDistributore()) {
            throw new IllegalArgumentException("Il distributore non possiede questo pacchetto");
        }
        if (pacchetto != null) {
            pacchetto.getDistributore().getPacchettiOfferti().remove(pacchetto);
            this.pacchettoRepository.deleteById(pacchetto.getId());
            this.venditoreRepository.save(distributore);
        }

    }

    @Override
    public void aggiungiProdottoAlPacchetto(DistributoreDiTipicita distributore, Pacchetto pacchetto,
            Prodotto prodotto) {
        if (pacchetto == null || prodotto == null || pacchetto.getElementiInclusi() == null) {
            throw new IllegalArgumentException("Errore nei parametri");
        }
        if (prodotto.getStatoVerifica() != StatoVerificaValori.APPROVATO) {
            throw new IllegalArgumentException("il prodotto non è stato approvato dal curatore");
        }

        if (distributore != pacchetto.getDistributore() || distributore != prodotto.getVenditore()) {
            throw new IllegalArgumentException("Il distributore non possiede questo pacchetto o prodotto");
        }

        pacchetto.aggiungiElemento(prodotto);
        this.pacchettoRepository.salva(pacchetto);

    }

    @Override
    public void rimuoviProdottoDalPacchetto(DistributoreDiTipicita distributore, Pacchetto pacchetto,
            Prodotto prodotto) {

        if (pacchetto == null)
            throw new IllegalArgumentException("Il pacchetto non puo essere null");
        if (prodotto == null)
            throw new IllegalArgumentException("Il prodotto non puo essere null");
        if (!pacchetto.getDistributore().equals(distributore))
            throw new IllegalArgumentException("Il distributore non possiede questo pacchetto");
        if (!pacchetto.getElementiInclusi().contains(prodotto))
            throw new IllegalArgumentException("il prodotto non fa parte di questo pacchetto");

        pacchetto.rimuoviElemento(prodotto);
        this.pacchettoRepository.salva(pacchetto);

    }

    public void aggiungiQuantitaPacchetto(DistributoreDiTipicita distributore, Pacchetto pacchetto,
            int quantitaAggiunta) {
        if (pacchetto == null)
            throw new IllegalArgumentException("Il pacchetto non puo essere null");
        if (!pacchetto.getDistributore().equals(distributore))
            throw new IllegalArgumentException("Il distributore non possiede questo pacchetto");
        if (quantitaAggiunta <= 0)
            throw new IllegalArgumentException("quantità errata");

        pacchetto.setQuantitaDisponibile(pacchetto.getQuantitaDisponibile() + quantitaAggiunta);
        this.pacchettoRepository.salva(pacchetto);

    }

    public void rimuoviQuantitaPacchetto(DistributoreDiTipicita distributore, Pacchetto pacchetto,
            int quantitaRimossa) {

        if (pacchetto == null)
            throw new IllegalArgumentException("Il pacchetto non puo essere null");
        if (!pacchetto.getDistributore().equals(distributore))
            throw new IllegalArgumentException("Il distributore non possiede questo pacchetto");

        if ((pacchetto.getQuantitaDisponibile() - quantitaRimossa) < 0)
            throw new IllegalArgumentException("Impossibile rimuovere " + quantitaRimossa);

        pacchetto.setQuantitaDisponibile(pacchetto.getQuantitaDisponibile() - quantitaRimossa);
        this.pacchettoRepository.salva(pacchetto);

    }

    @Override
    public void decrementaQuantita(Long idPacchetto, int quantitaDaDecrementare) {
        // Validazione parametri di input
        if (quantitaDaDecrementare <= 0) {
            throw new IllegalArgumentException("La quantità da decrementare deve essere maggiore di zero");
        }

        // Ricerca del pacchetto tramite repository
        Pacchetto pacchetto = this.pacchettoRepository.findById(idPacchetto);
        if (pacchetto == null) {
            throw new IllegalArgumentException("Pacchetto con ID " + idPacchetto + " non trovato");
        }

        // Verifica che ci sia abbastanza quantità disponibile
        int quantitaDisponibile = pacchetto.getQuantitaDisponibile();
        if (quantitaDaDecrementare > quantitaDisponibile) {
            throw new QuantitaNonDisponibileException(
                    (long) idPacchetto,
                    quantitaDaDecrementare,
                    quantitaDisponibile,
                    "Pacchetto");
        }

        // Decrementa la quantità e salva nel repository
        pacchetto.setQuantitaDisponibile(quantitaDisponibile - quantitaDaDecrementare);
        this.pacchettoRepository.salva(pacchetto);
    }

    public IPacchettoRepository getRepository() {
        return pacchettoRepository;
    }

}