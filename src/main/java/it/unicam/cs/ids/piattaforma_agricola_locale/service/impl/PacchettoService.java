package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Pacchetto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.Acquistabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.StatoVerificaValori;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IPacchettoRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.PacchettoRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DistributoreDiTipicita;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
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

    public PacchettoService(IPacchettoRepository pacchettoRepository) {
        this.pacchettoRepository = pacchettoRepository;
    }

    public PacchettoService() {
        this.pacchettoRepository = new PacchettoRepository(); // Costruttore di default per compatibilità
    }

    @Override
    public void creaPacchetto(DistributoreDiTipicita distributore, String nome, String descrizione, int quantita,
            double prezzoPacchetto) {
        int idPacchetto = UUID.randomUUID().hashCode();
        Pacchetto pacchetto = new Pacchetto(distributore, idPacchetto, nome, descrizione, quantita, prezzoPacchetto);
        distributore.getPacchettiOfferti().add(pacchetto);
        pacchettoRepository.salva(pacchetto);

    }

    @Override
    public boolean rimuoviPacchettoCatalogo(DistributoreDiTipicita distributore, Pacchetto pacchetto) {
        if (distributore != pacchetto.getDistributore()) {
            return false;
        }
        if (pacchetto != null) {
            pacchetto.getDistributore().getPacchettiOfferti().remove(pacchetto);
            return true;
        }
        return false;
    }

    @Override
    public boolean aggiungiProdottoAlPacchetto(DistributoreDiTipicita distributore, Pacchetto pacchetto,
            Prodotto prodotto) {
        if (prodotto.getStatoVerifica() != StatoVerificaValori.APPROVATO) {
            return false;
        }

        if (distributore != pacchetto.getDistributore() || distributore != prodotto.getVenditore()) {
            return false;
        }
        if (pacchetto != null && prodotto != null && pacchetto.getElementiInclusi() != null) {
            pacchetto.getElementiInclusi().add(prodotto);
            return true;
        }
        return false;
    }

    @Override
    public boolean rimuoviProdottoDalPacchetto(DistributoreDiTipicita distributore, Pacchetto pacchetto,
            Prodotto prodotto) {
        if (pacchetto != null && prodotto != null && pacchetto.getElementiInclusi() != null) {
            return pacchetto.getElementiInclusi().remove(prodotto);
        }
        return false;
    }

    @Override
    public void mostraPacchetti(DistributoreDiTipicita distributore) {
        for (Pacchetto pacchetto : distributore.getPacchettiOfferti()) {
            System.out.println("Pacchetto: " + pacchetto.getNome() + ", Prezzo: " + pacchetto.getPrezzo()
                    + " Disponibili: " + pacchetto.getQuantitaDisponibile());
            stampaProdottiPacchetto(pacchetto);
        }
    }

    public void aggiungiQuantitaPacchetto(DistributoreDiTipicita distributore, Pacchetto pacchetto,
            int quantitaAggiunta) {
        if (pacchetto == null || quantitaAggiunta <= 0) {
            throw new IllegalArgumentException("quantità errata");
        }
        pacchetto.setQuantitaDisponibile(pacchetto.getQuantitaDisponibile() + quantitaAggiunta);

    }

    public void rimuoviQuantitaPacchetto(DistributoreDiTipicita distributore, Pacchetto pacchetto,
            int quantitaRimossa) {
        if (pacchetto == null || pacchetto.getQuantitaDisponibile() - quantitaRimossa < 0) {
            throw new IllegalArgumentException("Impossibile rimuovere " + quantitaRimossa);
        }
        pacchetto.setQuantitaDisponibile(pacchetto.getQuantitaDisponibile() - quantitaRimossa);

    }

    // Metodo per stampare a video i prodotti che sono dentro un pacchetto
    public void stampaProdottiPacchetto(Pacchetto pacchetto) {
        System.out.println("Prodotti nel pacchetto " + pacchetto.getNome() + ":");
        for (Acquistabile elemento : pacchetto.getElementiInclusi()) {
            if (elemento instanceof Prodotto) {
                Prodotto prodotto = (Prodotto) elemento;
                System.out.println(
                        "- " + prodotto.getNome() + " (ID: " + prodotto.getId() + ") " + prodotto.getStatoVerifica());
            }
        }
    }

    @Override
    public void decrementaQuantita(int idPacchetto, int quantitaDaDecrementare) {
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
