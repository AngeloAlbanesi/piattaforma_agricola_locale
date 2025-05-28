package it.unicam.cs.ids.piattaforma_agricola_locale.exception;

/**
 * Eccezione lanciata quando la quantità richiesta di un prodotto non è
 * disponibile
 * al momento del checkout/creazione ordine
 */
public class QuantitaNonDisponibileAlCheckoutException extends RuntimeException {

    private final Long prodottoId;
    private final int quantitaRichiesta;
    private final int quantitaDisponibile;

    /**
     * Costruttore dell'eccezione
     * 
     * @param prodottoId          l'ID del prodotto con quantità insufficiente
     * @param quantitaRichiesta   la quantità richiesta nel carrello
     * @param quantitaDisponibile la quantità effettivamente disponibile
     * @param message             il messaggio di errore
     */
    public QuantitaNonDisponibileAlCheckoutException(Long prodottoId, int quantitaRichiesta,
            int quantitaDisponibile, String message) {
        super(message);
        this.prodottoId = prodottoId;
        this.quantitaRichiesta = quantitaRichiesta;
        this.quantitaDisponibile = quantitaDisponibile;
    }

    /**
     * Costruttore dell'eccezione con messaggio predefinito
     * 
     * @param prodottoId          l'ID del prodotto con quantità insufficiente
     * @param quantitaRichiesta   la quantità richiesta nel carrello
     * @param quantitaDisponibile la quantità effettivamente disponibile
     */
    public QuantitaNonDisponibileAlCheckoutException(Long prodottoId, int quantitaRichiesta,
            int quantitaDisponibile) {
        super(String.format("Quantità insufficiente per il prodotto ID %d. Richiesta: %d, Disponibile: %d",
                prodottoId, quantitaRichiesta, quantitaDisponibile));
        this.prodottoId = prodottoId;
        this.quantitaRichiesta = quantitaRichiesta;
        this.quantitaDisponibile = quantitaDisponibile;
    }

    /**
     * Costruttore dell'eccezione con nome del prodotto
     * 
     * @param nomeProdotto        il nome del prodotto con quantità insufficiente
     * @param quantitaRichiesta   la quantità richiesta nel carrello
     * @param quantitaDisponibile la quantità effettivamente disponibile
     */
    public QuantitaNonDisponibileAlCheckoutException(String nomeProdotto, int quantitaRichiesta,
            int quantitaDisponibile) {
        super(String.format("Quantità insufficiente per il prodotto '%s'. Richiesta: %d, Disponibile: %d",
                nomeProdotto, quantitaRichiesta, quantitaDisponibile));
        this.prodottoId = null;
        this.quantitaRichiesta = quantitaRichiesta;
        this.quantitaDisponibile = quantitaDisponibile;
    }

    /**
     * Restituisce l'ID del prodotto
     * 
     * @return l'ID del prodotto
     */
    public Long getProdottoId() {
        return prodottoId;
    }

    /**
     * Restituisce la quantità richiesta
     * 
     * @return la quantità richiesta
     */
    public int getQuantitaRichiesta() {
        return quantitaRichiesta;
    }

    /**
     * Restituisce la quantità disponibile
     * 
     * @return la quantità disponibile
     */
    public int getQuantitaDisponibile() {
        return quantitaDisponibile;
    }
}
