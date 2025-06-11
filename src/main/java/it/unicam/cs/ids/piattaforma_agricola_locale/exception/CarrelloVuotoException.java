package it.unicam.cs.ids.piattaforma_agricola_locale.exception;

/**
 * Eccezione lanciata quando si tenta di creare un ordine da un carrello vuoto o
 * inesistente
 */
public class CarrelloVuotoException extends RuntimeException {

    private final Long acquirenteId;

    /**
     * Costruttore dell'eccezione con solo messaggio
     * 
     * @param message il messaggio di errore
     */
    public CarrelloVuotoException(String message) {
        super(message);
        this.acquirenteId = null;
    }

    /**
     * Costruttore dell'eccezione
     * 
     * @param acquirenteId l'ID dell'acquirente con carrello vuoto
     * @param message      il messaggio di errore
     */
    public CarrelloVuotoException(Long acquirenteId, String message) {
        super(message);
        this.acquirenteId = acquirenteId;
    }

    /**
     * Costruttore dell'eccezione con messaggio predefinito
     * 
     * @param acquirenteId l'ID dell'acquirente con carrello vuoto
     */
    public CarrelloVuotoException(Long acquirenteId) {
        super("Il carrello dell'acquirente con ID " + acquirenteId + " è vuoto o non esiste");
        this.acquirenteId = acquirenteId;
    }

    /**
     * Restituisce l'ID dell'acquirente
     * 
     * @return l'ID dell'acquirente
     */
    public Long getAcquirenteId() {
        return acquirenteId;
    }
}
