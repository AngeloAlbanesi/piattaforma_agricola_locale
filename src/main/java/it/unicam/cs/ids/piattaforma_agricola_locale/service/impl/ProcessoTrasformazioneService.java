package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import java.util.Objects;
import java.util.Optional;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IProcessoTrasformazioneRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.FaseLavorazione;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.ProcessoTrasformazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Trasformatore;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IProcessoTrasformazioneService;

/**
 * Implementazione del servizio per la gestione dei processi di trasformazione.
 * Gestisce la logica business per la creazione, modifica e gestione dei
 * processi.
 */

public class ProcessoTrasformazioneService implements IProcessoTrasformazioneService {

    private final IProcessoTrasformazioneRepository processoRepository;

    public ProcessoTrasformazioneService(IProcessoTrasformazioneRepository processoRepository) {
        this.processoRepository = Objects.requireNonNull(processoRepository,
                "Il repository dei processi non può essere nullo");
    }

    @Override
    public ProcessoTrasformazione creaProcesso(String nome, String descrizione,
                                               Trasformatore trasformatore, String metodoProduzione) {
        // Validazione input
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome del processo non può essere nullo o vuoto");
        }
        if (descrizione == null || descrizione.trim().isEmpty()) {
            throw new IllegalArgumentException("La descrizione del processo non può essere nulla o vuota");
        }
        if (trasformatore == null) {
            throw new IllegalArgumentException("Il trasformatore non può essere nullo");
        }

        // Verifica che non esista già un processo con lo stesso nome per questo
        // trasformatore
        if (processoRepository.existsByNomeAndTrasformatore(nome.trim(), trasformatore)) {
            throw new IllegalArgumentException(
                    "Esiste già un processo con il nome '" + nome + "' per questo trasformatore");
        }

        // Crea il nuovo processo
        ProcessoTrasformazione nuovoProcesso = new ProcessoTrasformazione(
                nome.trim(),
                descrizione.trim(),
                trasformatore);

        if (metodoProduzione != null && !metodoProduzione.trim().isEmpty()) {
            nuovoProcesso.setMetodoProduzione(metodoProduzione.trim());
        }

        return processoRepository.save(nuovoProcesso);
    }

    @Override
    public ProcessoTrasformazione aggiornaProcesso(ProcessoTrasformazione processo) {
        if (processo == null) {
            throw new IllegalArgumentException("Il processo non può essere nullo");
        }

        if (processo.getId() == null) {
            throw new IllegalArgumentException("Il processo deve avere un ID per essere aggiornato");
        }

        // Verifica che il processo esista
        Optional<ProcessoTrasformazione> processoEsistente = processoRepository.findById(processo.getId());
        if (processoEsistente.isEmpty()) {
            throw new IllegalArgumentException("Il processo con ID " + processo.getId() + " non esiste");
        }

        return processoRepository.save(processo);
    }

    @Override
    public ProcessoTrasformazione aggiungiFaseAlProcesso(Long processoId, FaseLavorazione fase) {
        if (processoId == null) {
            throw new IllegalArgumentException("L'ID del processo non può essere nullo");
        }

        ProcessoTrasformazione processo = processoRepository.findById(processoId)
                .orElseThrow(() -> new IllegalArgumentException("Processo con ID " + processoId + " non trovato"));

        if (fase == null) {
            throw new IllegalArgumentException("La fase non può essere nulla");
        }

        processo.aggiungiFase(fase);
        return processoRepository.save(processo);
    }

    @Override
    public ProcessoTrasformazione rimuoviFaseDalProcesso(Long processoId, FaseLavorazione fase) {
        if (processoId == null) {
            throw new IllegalArgumentException("L'ID del processo non può essere nullo");
        }
        if (fase == null) {
            throw new IllegalArgumentException("La fase non può essere nulla");
        }

        ProcessoTrasformazione processo = processoRepository.findById(processoId)
                .orElseThrow(() -> new IllegalArgumentException("Processo con ID " + processoId + " non trovato"));

        processo.rimuoviFase(fase);
        return processoRepository.save(processo);
    }

    @Override
    public boolean eliminaProcesso(Long processoId, Trasformatore trasformatore) {
        if (processoId == null) {
            throw new IllegalArgumentException("L'ID del processo non può essere nullo");
        }
        if (trasformatore == null) {
            throw new IllegalArgumentException("Il trasformatore non può essere nullo");
        }

        Optional<ProcessoTrasformazione> processoOpt = processoRepository.findById(processoId);
        if (processoOpt.isEmpty()) {
            throw new IllegalArgumentException("Processo con ID " + processoId + " non trovato");
        }

        ProcessoTrasformazione processo = processoOpt.get();
        if (!trasformatore.equals(processo.getTrasformatore())) {
            throw new IllegalArgumentException("Non autorizzato");
        }

        return processoRepository.deleteById(processoId);
    }

}