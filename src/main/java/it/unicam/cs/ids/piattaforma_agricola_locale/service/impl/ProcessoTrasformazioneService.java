package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IProcessoTrasformazioneRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IProdottoRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.FaseLavorazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.ProcessoTrasformazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Trasformatore;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IProcessoTrasformazioneService;

/**
 * Implementazione del servizio per la gestione dei processi di trasformazione.
 * Gestisce la logica business per la creazione, modifica e gestione dei
 * processi.
 */
@Service
public class ProcessoTrasformazioneService implements IProcessoTrasformazioneService {

    private final IProcessoTrasformazioneRepository processoRepository;
    private final IProdottoRepository prodottoRepository;

    @Autowired
    public ProcessoTrasformazioneService(IProcessoTrasformazioneRepository processoRepository,
            IProdottoRepository prodottoRepository) {
        this.processoRepository = Objects.requireNonNull(processoRepository,
                "Il repository dei processi non può essere nullo");
        this.prodottoRepository = Objects.requireNonNull(prodottoRepository,
                "Il repository dei prodotti non può essere nullo");
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
    public ProcessoTrasformazione aggiornaProcesso(Long processoId, String nuovoNome, String nuovaDescrizione,
            String nuovoMetodoProduzione, Trasformatore trasformatore) {
        if (processoId == null) {
            throw new IllegalArgumentException("L'ID del processo non può essere nullo");
        }
        if (trasformatore == null) {
            throw new IllegalArgumentException("Il trasformatore non può essere nullo");
        }

        // Recupera il processo esistente dal database
        ProcessoTrasformazione processoEsistente = processoRepository.findById(processoId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Il processo con ID " + processoId + " non esiste"));

        // Verifica autorizzazione
        if (!trasformatore.equals(processoEsistente.getTrasformatore())) {
            throw new IllegalArgumentException("Non autorizzato ad aggiornare questo processo");
        }

        // Aggiorna il nome se fornito
        if (nuovoNome != null && !nuovoNome.trim().isEmpty()) {
            // Verifica che non esista già un altro processo con lo stesso nome per questo
            // trasformatore
            if (!nuovoNome.trim().equals(processoEsistente.getNome()) &&
                    processoRepository.existsByNomeAndTrasformatore(nuovoNome.trim(), trasformatore)) {
                throw new IllegalArgumentException(
                        "Esiste già un processo con il nome '" + nuovoNome + "' per questo trasformatore");
            }
            processoEsistente.setNome(nuovoNome.trim());
        }

        // Aggiorna la descrizione se fornita
        if (nuovaDescrizione != null && !nuovaDescrizione.trim().isEmpty()) {
            processoEsistente.setDescrizione(nuovaDescrizione.trim());
        }

        // Aggiorna il metodo di produzione se fornito (può essere null per rimuoverlo)
        if (nuovoMetodoProduzione != null) {
            processoEsistente
                    .setMetodoProduzione(nuovoMetodoProduzione.trim().isEmpty() ? null : nuovoMetodoProduzione.trim());
        }

        return processoRepository.save(processoEsistente);
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

        // Verifica se esistono prodotti che fanno riferimento a questo processo
        if (prodottoRepository.existsByProcessoId(processoId)) {
            throw new IllegalStateException(
                    "Impossibile eliminare il processo: esistono prodotti che fanno riferimento a questo processo di trasformazione");
        }

        processoRepository.deleteById(processoId);
        return true;
    }

    /**
     * Aggiorna il nome di un processo esistente
     */
    public ProcessoTrasformazione aggiornaNomeProcesso(Long processoId, String nuovoNome, Trasformatore trasformatore) {
        if (processoId == null) {
            throw new IllegalArgumentException("L'ID del processo non può essere nullo");
        }
        if (nuovoNome == null || nuovoNome.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nuovo nome non può essere nullo o vuoto");
        }
        if (trasformatore == null) {
            throw new IllegalArgumentException("Il trasformatore non può essere nullo");
        }

        ProcessoTrasformazione processo = processoRepository.findById(processoId)
                .orElseThrow(() -> new IllegalArgumentException("Processo con ID " + processoId + " non trovato"));

        // Verifica autorizzazione
        if (!trasformatore.equals(processo.getTrasformatore())) {
            throw new IllegalArgumentException("Non autorizzato ad aggiornare questo processo");
        }

        // Verifica unicità del nome
        if (processoRepository.existsByNomeAndTrasformatore(nuovoNome.trim(), trasformatore) &&
                !nuovoNome.trim().equals(processo.getNome())) {
            throw new IllegalArgumentException("Esiste già un processo con il nome '" + nuovoNome + "'");
        }

        processo.setNome(nuovoNome.trim());
        return processoRepository.save(processo);
    }

    /**
     * Aggiorna la descrizione di un processo esistente
     */
    public ProcessoTrasformazione aggiornaDescrizioneProcesso(Long processoId, String nuovaDescrizione,
            Trasformatore trasformatore) {
        if (processoId == null) {
            throw new IllegalArgumentException("L'ID del processo non può essere nullo");
        }
        if (nuovaDescrizione == null || nuovaDescrizione.trim().isEmpty()) {
            throw new IllegalArgumentException("La nuova descrizione non può essere nulla o vuota");
        }
        if (trasformatore == null) {
            throw new IllegalArgumentException("Il trasformatore non può essere nullo");
        }

        ProcessoTrasformazione processo = processoRepository.findById(processoId)
                .orElseThrow(() -> new IllegalArgumentException("Processo con ID " + processoId + " non trovato"));

        // Verifica autorizzazione
        if (!trasformatore.equals(processo.getTrasformatore())) {
            throw new IllegalArgumentException("Non autorizzato ad aggiornare questo processo");
        }

        processo.setDescrizione(nuovaDescrizione.trim());
        return processoRepository.save(processo);
    }

    /**
     * Aggiorna il metodo di produzione di un processo esistente
     */
    public ProcessoTrasformazione aggiornaMetodoProduzione(Long processoId, String nuovoMetodo,
            Trasformatore trasformatore) {
        if (processoId == null) {
            throw new IllegalArgumentException("L'ID del processo non può essere nullo");
        }
        if (trasformatore == null) {
            throw new IllegalArgumentException("Il trasformatore non può essere nullo");
        }

        ProcessoTrasformazione processo = processoRepository.findById(processoId)
                .orElseThrow(() -> new IllegalArgumentException("Processo con ID " + processoId + " non trovato"));

        // Verifica autorizzazione
        if (!trasformatore.equals(processo.getTrasformatore())) {
            throw new IllegalArgumentException("Non autorizzato ad aggiornare questo processo");
        }

        processo.setMetodoProduzione(nuovoMetodo != null ? nuovoMetodo.trim() : null);
        return processoRepository.save(processo);
    }

}