package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.Acquistabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.TipoAcquistabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IProdottoRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IPacchettoRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IEventoRepository;

@Service
public class AcquistabileService implements it.unicam.cs.ids.piattaforma_agricola_locale.service.AcquistabileService {

    private final IProdottoRepository prodottoRepository;
    private final IPacchettoRepository pacchettoRepository;
    private final IEventoRepository eventoRepository;

    @Autowired
    public AcquistabileService(IProdottoRepository prodottoRepository, 
                              IPacchettoRepository pacchettoRepository,
                              IEventoRepository eventoRepository) {
        this.prodottoRepository = prodottoRepository;
        this.pacchettoRepository = pacchettoRepository;
        this.eventoRepository = eventoRepository;
    }

    @Override
    public Acquistabile findByTipoAndId(TipoAcquistabile tipo, Long id) {
        if (tipo == null || id == null) {
            return null;
        }

        switch (tipo) {
            case PRODOTTO:
                return prodottoRepository.findById(id).orElse(null);
            case PACCHETTO:
                return pacchettoRepository.findById(id).orElse(null);
            case EVENTO:
                return eventoRepository.findById(id).orElse(null);
            default:
                throw new IllegalArgumentException("Tipo acquistabile non supportato: " + tipo);
        }
    }

    @Override
    public Acquistabile findByTipoStringAndId(String tipoString, Long id) {
        if (tipoString == null || id == null) {
            return null;
        }

        try {
            TipoAcquistabile tipo = TipoAcquistabile.fromString(tipoString);
            return findByTipoAndId(tipo, id);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}