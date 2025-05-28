package it.unicam.cs.ids.piattaforma_agricola_locale.service.pagamento;

/**
 * Eccezione specifica per gli errori relativi ai processi di pagamento.
 * Viene lanciata quando si verificano problemi durante l'elaborazione
 * di un pagamento tramite qualsiasi strategia di pagamento.
 */
public class PagamentoException extends Exception {

    /**
     * Costruttore con messaggio di errore.
     * 
     * @param message il messaggio di errore
     */
    public PagamentoException(String message) {
        super(message);
    }

    /**
     * Costruttore con messaggio di errore e causa dell'eccezione.
     * 
     * @param message il messaggio di errore
     * @param cause   la causa dell'eccezione
     */
    public PagamentoException(String message, Throwable cause) {
        super(message, cause);
    }
}
