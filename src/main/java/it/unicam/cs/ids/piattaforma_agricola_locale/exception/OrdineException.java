package it.unicam.cs.ids.piattaforma_agricola_locale.exception;

/**
 * Eccezione custom per il modulo ordini.
 * Rappresenta errori generici che possono verificarsi durante le operazioni
 * sugli ordini.
 * Può essere utilizzata come classe base per altre eccezioni specifiche del
 * modulo ordini
 * o per gestire errori generici non coperti da eccezioni più specifiche.
 * 
 * @author Team IDS
 */
public class OrdineException extends Exception {

    /**
     * Costruttore che crea un'eccezione con il messaggio specificato.
     * 
     * @param message il messaggio di dettaglio dell'eccezione
     */
    public OrdineException(String message) {
        super(message);
    }

    /**
     * Costruttore che crea un'eccezione con il messaggio e la causa specificati.
     * 
     * @param message il messaggio di dettaglio dell'eccezione
     * @param cause   la causa dell'eccezione
     */
    public OrdineException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Costruttore che crea un'eccezione con la causa specificata.
     * 
     * @param cause la causa dell'eccezione
     */
    public OrdineException(Throwable cause) {
        super(cause);
    }
}
