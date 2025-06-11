package it.unicam.cs.ids.piattaforma_agricola_locale.model.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.ProcessoTrasformazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Trasformatore;

/**
 * Implementazione in-memory del repository per i processi di trasformazione.
 * Utilizza una Map per simulare la persistenza dei dati.
 */
@Repository
public class ProcessoTrasformazioneRepository implements IProcessoTrasformazioneRepository {

    private final Map<Long, ProcessoTrasformazione> processi = new ConcurrentHashMap<>();
    private final AtomicLong contatore = new AtomicLong(1);

    @Override
    public ProcessoTrasformazione save(ProcessoTrasformazione processo) {
        if (processo == null) {
            throw new IllegalArgumentException("Il processo non può essere nullo");
        }

        if (processo.getId() == null) {
            processo.setId(contatore.getAndIncrement());
        }

        processi.put(processo.getId(), processo);
        return processo;
    }

    @Override
    public Optional<ProcessoTrasformazione> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(processi.get(id));
    }

    @Override
    public List<ProcessoTrasformazione> findAll() {
        return new ArrayList<>(processi.values());
    }

    @Override
    public List<ProcessoTrasformazione> findByTrasformatore(Trasformatore trasformatore) {
        if (trasformatore == null) {
            throw new IllegalArgumentException("Il trasformatore non può essere nullo");
        }

        return processi.values().stream()
                .filter(processo -> trasformatore.equals(processo.getTrasformatore()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProcessoTrasformazione> findActiveByTrasformatore(Trasformatore trasformatore) {
        if (trasformatore == null) {
            throw new IllegalArgumentException("Il trasformatore non può essere nullo");
        }

        return processi.values().stream()
                .filter(processo -> trasformatore.equals(processo.getTrasformatore()))

                .collect(Collectors.toList());
    }

    @Override
    public List<ProcessoTrasformazione> findByNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome di ricerca non può essere nullo o vuoto");
        }

        String nomeRicerca = nome.toLowerCase().trim();
        return processi.values().stream()
                .filter(processo -> processo.getNome() != null &&
                        processo.getNome().toLowerCase().contains(nomeRicerca))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProcessoTrasformazione> findByMetodoProduzione(String metodoProduzione) {
        if (metodoProduzione == null || metodoProduzione.trim().isEmpty()) {
            throw new IllegalArgumentException("Il metodo di produzione non può essere nullo o vuoto");
        }

        return processi.values().stream()
                .filter(processo -> metodoProduzione.trim().equals(processo.getMetodoProduzione()))
                .collect(Collectors.toList());
    }

    @Override
    public long countByTrasformatore(Trasformatore trasformatore) {
        if (trasformatore == null) {
            return 0;
        }

        return processi.values().stream()
                .filter(processo -> trasformatore.equals(processo.getTrasformatore()))
                .count();
    }

    @Override
    public boolean existsByNomeAndTrasformatore(String nome, Trasformatore trasformatore) {
        if (nome == null || nome.trim().isEmpty() || trasformatore == null) {
            return false;
        }

        return processi.values().stream()
                .anyMatch(processo -> trasformatore.equals(processo.getTrasformatore()) &&
                        nome.equals(processo.getNome()));
    }

    @Override
    public boolean deleteById(Long id) {
        if (id == null) {
            return false;
        }

        return processi.remove(id) != null;
    }

    @Override
    public int deleteAllByTrasformatore(Trasformatore trasformatore) {
        if (trasformatore == null) {
            return 0;
        }

        List<Long> idDaEliminare = processi.values().stream()
                .filter(processo -> trasformatore.equals(processo.getTrasformatore()))
                .map(ProcessoTrasformazione::getId)
                .collect(Collectors.toList());

        idDaEliminare.forEach(processi::remove);
        return idDaEliminare.size();
    }

    /**
     * Metodo di utilità per pulire tutti i dati (utile per test).
     */
    public void pulisciTutti() {
        processi.clear();
        contatore.set(1);
    }

    /**
     * Metodo di utilità per ottenere il numero totale di processi.
     *
     * @return Il numero totale di processi nel repository
     */
    public int dimensione() {
        return processi.size();
    }
}