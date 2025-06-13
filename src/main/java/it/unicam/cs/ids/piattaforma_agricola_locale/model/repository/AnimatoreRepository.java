package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.AnimatoreDellaFiliera;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.StatoAccreditamento;

import java.util.*;
import java.util.stream.Collectors;

public class AnimatoreRepository implements IAnimatoreRepository {
    private final Map<Long, AnimatoreDellaFiliera> animatoriMap = new HashMap<>();
    private Long nextId = 1L; // Semplice generazione ID

    @Override
    public void save(AnimatoreDellaFiliera animatore) {
        if (animatore.getId() == null) {
            animatore.setId(nextId++); // Assumendo che Utente abbia setId
        }
        animatoriMap.put(animatore.getId(), animatore);

    }

    @Override
    public Optional<AnimatoreDellaFiliera> findById(Long id) {
        return Optional.ofNullable(animatoriMap.get(id));
    }

    @Override
    public List<AnimatoreDellaFiliera> findAll() {
        return new ArrayList<>(animatoriMap.values());
    }

    @Override
    public List<AnimatoreDellaFiliera> findByStatoAccreditamento(StatoAccreditamento stato) {
        return animatoriMap.values().stream()
                .filter(v -> v.getStatoAccreditamento() == stato)
                .collect(Collectors.toList());
    }
}
