package it.unicam.cs.ids.piattaforma_agricola_locale.exception;

/**
 * Eccezione lanciata quando si tenta di aggiungere al carrello una quantità
 * superiore a quella disponibile per un articolo acquistabile.
 * 
 * Questa eccezione fornisce un controllo preliminare di disponibilità per
 * migliorare
 * l'esperienza utente, mentre la verifica finale avviene comunque al momento
 * del checkout.
 * 
 * @author Sistema Piattaforma Agricola Locale
 * @version 1.0
 */
public class QuantitaNonDisponibileException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final Long prodottoId;
    private final Integer quantitaRichiesta;
    private final Integer quantitaDisponibile;
    private final String tipoProdotto;

    /**
     * Costruisce una nuova eccezione per quantità non disponibile.
     *
     * @param prodottoId          L'ID del prodotto per cui la quantità non è
     *                            disponibile
     * @param quantitaRichiesta   La quantità richiesta dall'utente
     * @param quantitaDisponibile La quantità effettivamente disponibile
     * @param tipoProdotto        Il tipo di prodotto (Prodotto, Pacchetto, Evento)
     */
    public QuantitaNonDisponibileException(Long prodottoId, Integer quantitaRichiesta,
            Integer quantitaDisponibile, String tipoProdotto) {
        super(String.format("Quantità non disponibile per %s ID %d: richieste %d unità ma disponibili solo %d",
                tipoProdotto, prodottoId, quantitaRichiesta, quantitaDisponibile));
        this.prodottoId = prodottoId;
        this.quantitaRichiesta = quantitaRichiesta;
        this.quantitaDisponibile = quantitaDisponibile;
        this.tipoProdotto = tipoProdotto;
    }

    /**
     * Costruisce una nuova eccezione per quantità non disponibile con messaggio
     * personalizzato.
     *
     * @param message             Il messaggio di errore personalizzato
     * @param prodottoId          L'ID del prodotto per cui la quantità non è
     *                            disponibile
     * @param quantitaRichiesta   La quantità richiesta dall'utente
     * @param quantitaDisponibile La quantità effettivamente disponibile
     * @param tipoProdotto        Il tipo di prodotto (Prodotto, Pacchetto, Evento)
     */
    public QuantitaNonDisponibileException(String message, Long prodottoId, Integer quantitaRichiesta,
            Integer quantitaDisponibile, String tipoProdotto) {
        super(message);
        this.prodottoId = prodottoId;
        this.quantitaRichiesta = quantitaRichiesta;
        this.quantitaDisponibile = quantitaDisponibile;
        this.tipoProdotto = tipoProdotto;
    }

    /**
     * Costruisce una nuova eccezione per quantità non disponibile con causa.
     *
     * @param message             Il messaggio di errore
     * @param cause               La causa dell'eccezione
     * @param prodottoId          L'ID del prodotto per cui la quantità non è
     *                            disponibile
     * @param quantitaRichiesta   La quantità richiesta dall'utente
     * @param quantitaDisponibile La quantità effettivamente disponibile
     * @param tipoProdotto        Il tipo di prodotto (Prodotto, Pacchetto, Evento)
     */
    public QuantitaNonDisponibileException(String message, Throwable cause, Long prodottoId,
            Integer quantitaRichiesta, Integer quantitaDisponibile,
            String tipoProdotto) {
        super(message, cause);
        this.prodottoId = prodottoId;
        this.quantitaRichiesta = quantitaRichiesta;
        this.quantitaDisponibile = quantitaDisponibile;
        this.tipoProdotto = tipoProdotto;
    }

    // Getters per accedere alle informazioni dettagliate dell'errore

    /**
     * @return L'ID del prodotto per cui la quantità non è disponibile
     */
    public Long getProdottoId() {
        return prodottoId;
    }

    /**
     * @return La quantità richiesta dall'utente
     */
    public Integer getQuantitaRichiesta() {
        return quantitaRichiesta;
    }

    /**
     * @return La quantità effettivamente disponibile
     */
    public Integer getQuantitaDisponibile() {
        return quantitaDisponibile;
    }

    /**
     * @return Il tipo di prodotto (Prodotto, Pacchetto, Evento)
     */
    public String getTipoProdotto() {
        return tipoProdotto;
    }
}
