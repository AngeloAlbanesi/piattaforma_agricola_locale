package it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo;

/**
 * Enumerazione che rappresenta il tipo di origine di un prodotto.
 * Permette di distinguere tra prodotti coltivati, allevati
 * e prodotti risultanti da processi di trasformazione.
 */
public enum TipoOrigineProdotto {

    /**
     * Prodotto coltivato o allevato direttamente da un produttore
     * senza processi di trasformazione significativi.
     * Mantenuto per compatibilità con codice esistente.
     */
    COLTIVATO_ALLEVATO("Coltivato/Allevato"),

    /**
     * Prodotto coltivato direttamente da un produttore agricolo.
     */
    COLTIVATO("Coltivato"),

    /**
     * Prodotto derivante da allevamento.
     */
    ALLEVATO("Allevato"),

    /**
     * Prodotto risultante da un processo di trasformazione
     * di materie prime o semilavorati.
     */
    TRASFORMATO("Trasformato");

    private final String descrizione;

    /**
     * Costruttore dell'enumerazione.
     *
     * @param descrizione La descrizione testuale del tipo di origine
     */
    TipoOrigineProdotto(String descrizione) {
        this.descrizione = descrizione;
    }

    /**
     * Restituisce la descrizione testuale del tipo di origine.
     *
     * @return La descrizione del tipo di origine
     */
    public String getDescrizione() {
        return descrizione;
    }

    /**
     * Verifica se il prodotto è di origine trasformata.
     *
     * @return true se il prodotto è trasformato, false altrimenti
     */
    public boolean isTrasformato() {
        return this == TRASFORMATO;
    }

    /**
     * Verifica se il prodotto è di origine coltivata/allevata.
     *
     * @return true se il prodotto è di tipo coltivato o allevato (qualsiasi)
     */
    public boolean isMateriaPrima() {
        return this == COLTIVATO_ALLEVATO || this == COLTIVATO || this == ALLEVATO;
    }

    /**
     * Verifica se il prodotto è specificamente coltivato.
     *
     * @return true se il prodotto è di tipo COLTIVATO
     */
    public boolean isColtivato() {
        return this == COLTIVATO || this == COLTIVATO_ALLEVATO;
    }

    /**
     * Verifica se il prodotto è specificamente allevato.
     *
     * @return true se il prodotto è di tipo ALLEVATO
     */
    public boolean isAllevato() {
        return this == ALLEVATO || this == COLTIVATO_ALLEVATO;
    }
}