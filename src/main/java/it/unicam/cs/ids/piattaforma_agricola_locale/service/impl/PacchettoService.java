package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.unicam.cs.ids.piattaforma_agricola_locale.exception.QuantitaNonDisponibileException;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Pacchetto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.StatoVerificaValori;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IPacchettoRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IPacchettoElementoRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IVenditoreRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DistributoreDiTipicita;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IPacchettoService;

/**
 * Implementazione concreta di {@link IPacchettoService} che gestisce la logica
 * per la creazione, gestione e modifica dei pacchetti di prodotti nella
 * piattaforma agricola locale.
 *
 */
@Service
public class PacchettoService implements IPacchettoService {

    private final IPacchettoRepository pacchettoRepository;
    private final IPacchettoElementoRepository pacchettoElementoRepository;
    private final IVenditoreRepository venditoreRepository;

    public PacchettoService(IPacchettoRepository pacchettoRepository,
            IPacchettoElementoRepository pacchettoElementoRepository,
            IVenditoreRepository venditoreRepository) {
        this.pacchettoRepository = pacchettoRepository;
        this.pacchettoElementoRepository = pacchettoElementoRepository;
        this.venditoreRepository = venditoreRepository;
    }

    @Override
    public void creaPacchetto(DistributoreDiTipicita distributore, String nome, String descrizione, int quantita,
            double prezzoPacchetto) {

        // Il prezzo iniziale del pacchetto è sempre 0.0, verrà calcolato
        // automaticamente quando si aggiungono prodotti
        Pacchetto pacchetto = new Pacchetto(distributore, nome, descrizione, quantita, 0.0);

        distributore.getPacchettiOfferti().add(pacchetto);
        this.pacchettoRepository.save(pacchetto);
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
        if (pacchetto == null || prodotto == null) {
            throw new IllegalArgumentException("Errore nei parametri");
        }
        if (prodotto.getStatoVerifica() != StatoVerificaValori.APPROVATO) {
            throw new IllegalArgumentException("il prodotto non è stato approvato dal curatore");
        }

        if (distributore != pacchetto.getDistributore() || distributore != prodotto.getVenditore()) {
            throw new IllegalArgumentException("Il distributore non possiede questo pacchetto o prodotto");
        }

        pacchetto.aggiungiElemento(prodotto);
        this.pacchettoRepository.save(pacchetto);

    }

    @Override
    @Transactional
    public void rimuoviProdottoDalPacchetto(DistributoreDiTipicita distributore, Pacchetto pacchetto,
            Prodotto prodotto) {

        if (pacchetto == null)
            throw new IllegalArgumentException("Il pacchetto non puo essere null");
        if (prodotto == null)
            throw new IllegalArgumentException("Il prodotto non puo essere null");
        if (!pacchetto.getDistributore().equals(distributore))
            throw new IllegalArgumentException("Il distributore non possiede questo pacchetto");

        // Verifica che il prodotto faccia parte del pacchetto confrontando gli ID
        boolean prodottoTrovato = pacchetto.getElementiInclusi().stream()
                .anyMatch(elemento -> elemento.getId().equals(prodotto.getId()));

        if (!prodottoTrovato)
            throw new IllegalArgumentException("il prodotto non fa parte di questo pacchetto");

        // Rimuovi l'elemento dal pacchetto
        pacchetto.rimuoviElemento(prodotto);

        // Rimuovi esplicitamente dalla tabella di associazione
        this.pacchettoElementoRepository.deleteByPacchettoAndProdotto(pacchetto, prodotto);

        // Salva il pacchetto
        this.pacchettoRepository.save(pacchetto);
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
        this.pacchettoRepository.save(pacchetto);

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
        this.pacchettoRepository.save(pacchetto);

    }

    @Override
    public void decrementaQuantita(Long idPacchetto, int quantitaDaDecrementare) {
        // Validazione parametri di input
        if (quantitaDaDecrementare <= 0) {
            throw new IllegalArgumentException("La quantità da decrementare deve essere maggiore di zero");
        }

        // Ricerca del pacchetto tramite repository
        Pacchetto pacchetto = this.pacchettoRepository.findById(idPacchetto)
                .orElseThrow(() -> new IllegalArgumentException("Pacchetto con ID " + idPacchetto + " non trovato"));

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
        this.pacchettoRepository.save(pacchetto);
    }

    public IPacchettoRepository getRepository() {
        return pacchettoRepository;
    }

    // ===== PUBLIC CATALOG METHODS =====

    @Override
    public Page<Pacchetto> getAllPacchetti(Pageable pageable) {
        return pacchettoRepository.findAll(pageable);
    }

    @Override
    public Optional<Pacchetto> getPacchettoById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID pacchetto non può essere null");
        }
        return pacchettoRepository.findById(id);
    }

    @Override
    public List<Pacchetto> searchPacchettiByNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome di ricerca non può essere vuoto");
        }
        return pacchettoRepository.findByNomeContainingIgnoreCase(nome.trim());
    }

    @Override
    public List<Pacchetto> getPacchettiByDistributore(Long distributoreId) {
        if (distributoreId == null) {
            throw new IllegalArgumentException("ID distributore non può essere null");
        }
        DistributoreDiTipicita distributore = (DistributoreDiTipicita) venditoreRepository.findById(distributoreId)
                .orElseThrow(
                        () -> new IllegalArgumentException("Distributore con ID " + distributoreId + " non trovato"));
        return pacchettoRepository.findByDistributore(distributore);
    }

}