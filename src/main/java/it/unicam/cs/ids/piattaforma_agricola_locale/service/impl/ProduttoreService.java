package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.coltivazione.MetodoDiColtivazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IDatiAziendaRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IMetodoDiColtivazioneRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IProdottoRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IVenditoreRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.ICertificazioneService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IProduttoreService;

@Service
public class ProduttoreService extends VenditoreService implements IProduttoreService {

    private final IMetodoDiColtivazioneRepository metodiRepository;
    private final IProdottoRepository prodottoRepository;
    private final IVenditoreRepository venditoreRepository;
    private Long nextId = 1L;

    @Autowired
    public ProduttoreService(ICertificazioneService certificazioneService,
            IVenditoreRepository venditoreRepository,
            IDatiAziendaRepository datiAziendaRepository,
            IMetodoDiColtivazioneRepository metodiRepository,
            IProdottoRepository prodottoRepository) {
        super(certificazioneService, venditoreRepository, datiAziendaRepository);
        this.metodiRepository = metodiRepository;
        this.prodottoRepository = prodottoRepository;
        this.venditoreRepository = venditoreRepository;
    }

    @Override
    public MetodoDiColtivazione creaMetodoDiColtivazione(Long idProduttore, Long idProdotto,
            MetodoDiColtivazione metodoDiColtivazione) {
        Prodotto prodotto = validaProdottoEProduttore(idProduttore, idProdotto);

        if (!prodotto.isColtivato()) {
            throw new IllegalArgumentException(
                    "Non è possibile associare un metodo di coltivazione a un prodotto trasformato");
        }

        if (prodotto.getIdMetodoDiColtivazione() != null) {
            return aggiornaMetodoDiColtivazione(idProduttore, idProdotto, metodoDiColtivazione);
        }

        metodoDiColtivazione.setId(nextId++);
        metodiRepository.save(metodoDiColtivazione);

        prodotto.setIdMetodoDiColtivazione(metodoDiColtivazione.getId());
        prodottoRepository.save(prodotto);

        return metodoDiColtivazione;
    }

    @Override
    public MetodoDiColtivazione aggiornaMetodoDiColtivazione(Long idProduttore, Long idProdotto,
            MetodoDiColtivazione metodoDiColtivazione) {
        Prodotto prodotto = validaProdottoEProduttore(idProduttore, idProdotto);

        Long idMetodoEsistente = prodotto.getIdMetodoDiColtivazione();
        if (idMetodoEsistente == null) {
            throw new IllegalArgumentException("Nessun metodo di coltivazione associato a questo prodotto");
        }

        metodoDiColtivazione.setId(idMetodoEsistente);
        metodiRepository.save(metodoDiColtivazione);

        return metodoDiColtivazione;
    }

    @Override
    public void eliminaMetodoDiColtivazione(Long idProduttore, Long idProdotto) {
        Prodotto prodotto = validaProdottoEProduttore(idProduttore, idProdotto);

        Long idMetodo = prodotto.getIdMetodoDiColtivazione();
        if (idMetodo != null) {
            metodiRepository.deleteById(idMetodo);
            prodotto.setIdMetodoDiColtivazione(null);
            prodottoRepository.save(prodotto);
        }
    }

    @Override
    public MetodoDiColtivazione getMetodoDiColtivazioneByProdotto(Long idProdotto) {
        Prodotto prodotto = prodottoRepository.findById(idProdotto).orElse(null);
        if (prodotto == null) {
            return null;
        }

        Long idMetodo = prodotto.getIdMetodoDiColtivazione();
        if (idMetodo == null) {
            return null;
        }

        return metodiRepository.findById(idMetodo).orElse(null);
    }

    private Prodotto validaProdottoEProduttore(Long idProduttore, Long idProdotto) {
        Optional<Venditore> produttoreOpt = venditoreRepository.findById(idProduttore);
        if (!produttoreOpt.isPresent()) {
            throw new IllegalArgumentException("Produttore non trovato");
        }
        Venditore produttore = produttoreOpt.get();

        Prodotto prodotto = prodottoRepository.findById(idProdotto).orElse(null);
        if (prodotto == null) {
            throw new IllegalArgumentException("Prodotto non trovato");
        }

        if (!prodotto.getVenditore().equals(produttore)) {
            throw new IllegalArgumentException("Il produttore non possiede questo prodotto");
        }

        return prodotto;
    }
}