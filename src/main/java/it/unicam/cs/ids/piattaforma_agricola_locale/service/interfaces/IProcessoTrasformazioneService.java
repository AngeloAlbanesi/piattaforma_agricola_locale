package it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.FaseLavorazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.ProcessoTrasformazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Trasformatore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Interfaccia del servizio per la gestione dei processi di trasformazione.
 * Definisce le operazioni business per la gestione completa dei processi di
 * trasformazione.
 */
public interface IProcessoTrasformazioneService {

    /**
     * Crea un nuovo processo di trasformazione.
     *
     * @param nome             Il nome del processo
     * @param descrizione      La descrizione del processo
     * @param trasformatore    Il trasformatore responsabile
     * @param metodoProduzione Il metodo di produzione (opzionale)
     * @return Il processo creato
     * @throws IllegalArgumentException se i parametri obbligatori sono nulli o non
     *                                  validi
     */
    ProcessoTrasformazione creaProcesso(String nome, String descrizione, Trasformatore trasformatore,
            String metodoProduzione);

    /**
     * Aggiorna un processo di trasformazione esistente.
     *
     * @param processoId            L'ID del processo da aggiornare
     * @param nuovoNome             La nuova denominazione (può essere null se non
     *                              si vuole cambiare)
     * @param nuovaDescrizione      La nuova descrizione (può essere null se non si
     *                              vuole cambiare)
     * @param nuovoMetodoProduzione Il nuovo metodo di produzione (può essere null)
     * @param trasformatore         Il trasformatore che richiede l'aggiornamento
     * @return Il processo aggiornato
     * @throws IllegalArgumentException se il processo non esiste o il trasformatore
     *                                  non è autorizzato
     */
    ProcessoTrasformazione aggiornaProcesso(Long processoId, String nuovoNome, String nuovaDescrizione,
            String nuovoMetodoProduzione, Trasformatore trasformatore);

    /**
     * Aggiunge una fase di lavorazione a un processo.
     *
     * @param processoId L'ID del processo
     * @param fase       La fase da aggiungere
     * @return Il processo aggiornato
     * @throws IllegalArgumentException se il processo non esiste o la fase non è
     *                                  valida
     */
    ProcessoTrasformazione aggiungiFaseAlProcesso(Long processoId, FaseLavorazione fase);

    /**
     * Rimuove una fase di lavorazione da un processo.
     *
     * @param processoId L'ID del processo
     * @param fase       La fase da rimuovere
     * @return Il processo aggiornato
     * @throws IllegalArgumentException se il processo non esiste
     */
    ProcessoTrasformazione rimuoviFaseDalProcesso(Long processoId, FaseLavorazione fase);

    /**
     * Elimina un processo di trasformazione.
     *
     * @param processoId    L'ID del processo da eliminare
     * @param trasformatore Il trasformatore che richiede l'eliminazione (per
     *                      verifica autorizzazione)
     * @return true se il processo è stato eliminato, false altrimenti
     * @throws IllegalArgumentException se il trasformatore non è autorizzato
     */
    boolean eliminaProcesso(Long processoId, Trasformatore trasformatore);

    /**
     * Aggiorna il nome di un processo esistente.
     *
     * @param processoId    L'ID del processo da aggiornare
     * @param nuovoNome     Il nuovo nome del processo
     * @param trasformatore Il trasformatore che richiede l'aggiornamento
     * @return Il processo aggiornato
     * @throws IllegalArgumentException se il processo non esiste, il nome non è
     *                                  valido
     *                                  o il trasformatore non è autorizzato
     */
    ProcessoTrasformazione aggiornaNomeProcesso(Long processoId, String nuovoNome, Trasformatore trasformatore);

    /**
     * Aggiorna la descrizione di un processo esistente.
     *
     * @param processoId       L'ID del processo da aggiornare
     * @param nuovaDescrizione La nuova descrizione del processo
     * @param trasformatore    Il trasformatore che richiede l'aggiornamento
     * @return Il processo aggiornato
     * @throws IllegalArgumentException se il processo non esiste, la descrizione
     *                                  non è valida
     *                                  o il trasformatore non è autorizzato
     */
    ProcessoTrasformazione aggiornaDescrizioneProcesso(Long processoId, String nuovaDescrizione,
            Trasformatore trasformatore);

    /**
     * Aggiorna il metodo di produzione di un processo esistente.
     *
     * @param processoId    L'ID del processo da aggiornare
     * @param nuovoMetodo   Il nuovo metodo di produzione (può essere null)
     * @param trasformatore Il trasformatore che richiede l'aggiornamento
     * @return Il processo aggiornato
     * @throws IllegalArgumentException se il processo non esiste o il trasformatore
     *                                  non è autorizzato
     */
    ProcessoTrasformazione aggiornaMetodoProduzione(Long processoId, String nuovoMetodo, Trasformatore trasformatore);

    /**
     * Aggiorna un processo usando un DTO di aggiornamento.
     * 
     * @param processoId    L'ID del processo da aggiornare
     * @param aggiornamento I dati di aggiornamento
     * @param trasformatore Il trasformatore che richiede l'aggiornamento
     * @return Il processo aggiornato
     * @throws IllegalArgumentException se il processo non esiste o il trasformatore
     *                                  non è autorizzato
     */
    // ProcessoTrasformazione aggiornaProcessoConDTO(Long processoId,
    // ProcessoAggiornamentoDTO aggiornamento, Trasformatore trasformatore);

    // ===== PUBLIC CATALOG METHODS =====
    
    /**
     * Recupera tutti i processi di trasformazione con paginazione.
     * 
     * @param pageable parametri di paginazione
     * @return pagina di processi
     */
    Page<ProcessoTrasformazione> getAllProcessi(Pageable pageable);
    
    /**
     * Recupera un processo di trasformazione per ID.
     * 
     * @param id l'ID del processo
     * @return il processo se trovato
     */
    Optional<ProcessoTrasformazione> getProcessoById(Long id);
    
    /**
     * Recupera tutti i processi di un trasformatore.
     * 
     * @param trasformatore il trasformatore
     * @return lista dei processi
     */
    List<ProcessoTrasformazione> getProcessiByTrasformatore(Trasformatore trasformatore);
    
    /**
     * Cerca processi per nome.
     * 
     * @param nome il nome da cercare
     * @return lista dei processi trovati
     */
    List<ProcessoTrasformazione> searchProcessiByNome(String nome);
    
    /**
     * Ottiene le informazioni di tracciabilità per un processo di trasformazione.
     * 
     * @param processoId l'ID del processo
     * @return le informazioni di tracciabilità se il processo esiste
     */
    Optional<ProcessoTrasformazione> getProcessoTracciabilita(Long processoId);
    
    /**
     * Collega un processo di trasformazione a un prodotto finale in modo bidirezionale.
     * 
     * @param processoId l'ID del processo di trasformazione
     * @param prodottoId l'ID del prodotto finale
     * @return il processo aggiornato
     */
    ProcessoTrasformazione collegaProcessoAProdotto(Long processoId, Long prodottoId);
    
    /**
     * Rimuove il collegamento tra un processo di trasformazione e un prodotto.
     * 
     * @param processoId l'ID del processo di trasformazione
     * @param prodottoId l'ID del prodotto finale
     * @return il processo aggiornato
     */
    ProcessoTrasformazione scollegaProcessoDaProdotto(Long processoId, Long prodottoId);

}