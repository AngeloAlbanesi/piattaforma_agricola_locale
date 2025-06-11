package it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.FaseLavorazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.ProcessoTrasformazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Trasformatore;

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
     * @param processo Il processo da aggiornare
     * @return Il processo aggiornato
     * @throws IllegalArgumentException se il processo è nullo o non valido
     */
    ProcessoTrasformazione aggiornaProcesso(ProcessoTrasformazione processo);

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

}