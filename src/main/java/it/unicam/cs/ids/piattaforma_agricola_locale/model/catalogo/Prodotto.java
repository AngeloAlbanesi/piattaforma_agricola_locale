package it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo;

import java.util.ArrayList;
import java.util.List;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.Acquistabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.ElementoVerificabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.StatoVerificaValori;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
import jakarta.persistence.*;

@Entity
@Table(name = "prodotti")
public class Prodotto implements Acquistabile, ElementoVerificabile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prodotto")
    private Long idProdotto;
    @Column(name = "nome", nullable = false, length = 200)
    private String nome;
    @Column(name = "descrizione", columnDefinition = "TEXT")
    private String descrizione;
    @Column(name = "prezzo", nullable = false)
    private double prezzo;
    @Column(name = "quantita_disponibile", nullable = false)
    private int quantitaDisponibile;
    @Enumerated(EnumType.STRING)
    @Column(name = "stato_verifica", nullable = false)
    private StatoVerificaValori statoVerifica;
    @Column(name = "feedback_verifica", columnDefinition = "TEXT")
    private String feedbackVerifica;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venditore", nullable = false)
    private Venditore venditore;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_prodotto_associato")
    private List<Certificazione> certificazioniProdotto;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_origine", nullable = false)
    private TipoOrigineProdotto tipoOrigine;
    @Column(name = "id_processo_trasformazione_originario")
    private Long idProcessoTrasformazioneOriginario;
    @Column(name = "id_metodo_di_coltivazione")
    private Long idMetodoDiColtivazione;

    public Prodotto() {
        // Default constructor for JPA
        this.certificazioniProdotto = new ArrayList<>();
    }

    public Prodotto(String nome, String descrizione, double prezzo, int quantitaDisponibile,
            Venditore venditore) {

        this.nome = nome;
        this.descrizione = descrizione;
        this.prezzo = prezzo;
        this.quantitaDisponibile = quantitaDisponibile;
        this.venditore = venditore;
        this.statoVerifica = StatoVerificaValori.IN_REVISIONE;
        this.certificazioniProdotto = new ArrayList<>();
        this.tipoOrigine = TipoOrigineProdotto.COLTIVATO; // Default aggiornato da COLTIVATO_ALLEVATO a COLTIVATO
        this.idProcessoTrasformazioneOriginario = null;
    }

    /**
     * Costruttore per prodotti trasformati.
     *
     * @param nome                               Il nome del prodotto
     * @param descrizione                        La descrizione del prodotto
     * @param prezzo                             Il prezzo del prodotto
     * @param quantitaDisponibile                La quantità disponibile
     * @param venditore                          Il venditore del prodotto
     * @param idProcessoTrasformazioneOriginario L'ID del processo di trasformazione
     */
    public Prodotto(String nome, String descrizione, double prezzo, int quantitaDisponibile,
            Venditore venditore, Long idProcessoTrasformazioneOriginario) {
        this(nome, descrizione, prezzo, quantitaDisponibile, venditore);
        this.tipoOrigine = TipoOrigineProdotto.TRASFORMATO;
        this.idProcessoTrasformazioneOriginario = idProcessoTrasformazioneOriginario;
    }

    public Long getId() {
        return idProdotto;
    }

    public void setIdProdotto(Long idProdotto) {
        this.idProdotto = idProdotto;
    }

    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public double getPrezzo() {
        return prezzo;
    }

    public int getQuantitaDisponibile() {
        return quantitaDisponibile;
    }

    public void setStatoVerifica(StatoVerificaValori statoVerifica) {
        this.statoVerifica = statoVerifica;

    }

    public StatoVerificaValori getStatoVerifica() {
        return statoVerifica;
    }

    public String getFeedbackVerifica() {
        return feedbackVerifica;
    }

    public void setFeedbackVerifica(String feedbackVerifica) {
        this.feedbackVerifica = feedbackVerifica;
    }

    @Override
    public Venditore getVenditore() {
        return venditore;
    }

    public void setQuantitaDisponibile(int quantita) {
        this.quantitaDisponibile = quantita;
    }

    /**
     * Imposta il nome del prodotto.
     *
     * @param nome Il nome del prodotto
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Imposta la descrizione del prodotto.
     *
     * @param descrizione La descrizione del prodotto
     */
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    /**
     * Imposta il prezzo del prodotto.
     *
     * @param prezzo Il prezzo del prodotto
     */
    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }

    /**
     * Restituisce il tipo di origine del prodotto.
     *
     * @return Il tipo di origine del prodotto
     */
    public TipoOrigineProdotto getTipoOrigine() {
        return tipoOrigine;
    }

    /**
     * Imposta il tipo di origine del prodotto.
     *
     * @param tipoOrigine Il tipo di origine da impostare
     */
    public void setTipoOrigine(TipoOrigineProdotto tipoOrigine) {
        this.tipoOrigine = tipoOrigine;
    }

    /**
     * Restituisce l'ID del processo di trasformazione originario.
     *
     * @return L'ID del processo di trasformazione, null se non trasformato
     */
    public Long getIdProcessoTrasformazioneOriginario() {
        return idProcessoTrasformazioneOriginario;
    }

    /**
     * Imposta l'ID del processo di trasformazione originario.
     *
     * @param idProcessoTrasformazioneOriginario L'ID del processo da impostare
     */
    public void setIdProcessoTrasformazioneOriginario(Long idProcessoTrasformazioneOriginario) {
        this.idProcessoTrasformazioneOriginario = idProcessoTrasformazioneOriginario;
        // Se viene impostato un processo di trasformazione, il tipo diventa trasformato
        if (idProcessoTrasformazioneOriginario != null) {
            this.tipoOrigine = TipoOrigineProdotto.TRASFORMATO;
        }
    }

    /**
     * Imposta il processo di produzione (trasformazione) per questo prodotto.
     * Questo metodo aggiorna sia l'ID del processo che il tipo di origine.
     *
     * @param processo Il processo di trasformazione da associare al prodotto
     */
    public void setProcessoProduzione(
            it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.ProcessoTrasformazione processo) {
        if (processo != null) {
            this.idProcessoTrasformazioneOriginario = processo.getId();
            this.tipoOrigine = it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.TipoOrigineProdotto.TRASFORMATO;
        } else {
            this.idProcessoTrasformazioneOriginario = null;
            this.tipoOrigine = it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.TipoOrigineProdotto.COLTIVATO;
        }
    }

    /**
     * Restituisce il processo di produzione (trasformazione) di questo prodotto.
     * Nota: Questo metodo restituisce sempre null perché l'entity Prodotto
     * mantiene solo l'ID del processo, non il riferimento completo.
     * Per ottenere il processo completo, utilizzare il service appropriato.
     *
     * @return Sempre null (il processo deve essere recuperato tramite service)
     */
    public it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione.ProcessoTrasformazione getProcessoProduzione() {
        // L'entity Prodotto mantiene solo l'ID, non il riferimento completo
        // Il processo deve essere recuperato tramite service usando l'ID
        return null;
    }

    /**
     * Verifica se il prodotto è risultato di un processo di trasformazione.
     *
     * @return true se il prodotto è trasformato, false altrimenti
     */
    public boolean isTrasformato() {
        return tipoOrigine != null && tipoOrigine.isTrasformato();
    }

    /**
     * Verifica se il prodotto è di origine coltivata/allevata.
     *
     * @return true se il prodotto è coltivato/allevato, false altrimenti
     */
    public boolean isColtivato() {
        return tipoOrigine != null && tipoOrigine.isColtivato();
    }

    /**
     * Restituisce l'ID del metodo di coltivazione associato al prodotto.
     *
     * @return L'ID del metodo di coltivazione, null se non associato
     */
    public Long getIdMetodoDiColtivazione() {
        return idMetodoDiColtivazione;
    }

    /**
     * Imposta l'ID del metodo di coltivazione per il prodotto.
     *
     * @param idMetodoDiColtivazione L'ID del metodo di coltivazione da associare
     */
    public void setIdMetodoDiColtivazione(Long idMetodoDiColtivazione) {
        this.idMetodoDiColtivazione = idMetodoDiColtivazione;
    }

    @Override
    public String toString() {
        return "Prodotto{" +
                "idProdotto=" + idProdotto +
                ", nome='" + nome + '\'' +
                ", descrizione='" + descrizione + '\'' +
                ", prezzo=" + prezzo +
                ", quantitaDisponibile=" + quantitaDisponibile +
                ", statoVerifica=" + statoVerifica +
                ", feedbackVerifica='" + feedbackVerifica + '\'' +
                ", tipoOrigine=" + tipoOrigine +
                ", idProcessoTrasformazioneOriginario=" + idProcessoTrasformazioneOriginario +
                ", idMetodoDiColtivazione=" + idMetodoDiColtivazione +
                '}';
    }

    public List<Certificazione> getCertificazioni() {
        return certificazioniProdotto;
    }

    public void aggiungiCertificazione(Certificazione certificazione) {
        if (certificazione != null && certificazione.getIdProdottoAssociato() != null
                && certificazione.getIdProdottoAssociato().equals(this.idProdotto)) {
            this.certificazioniProdotto.add(certificazione);
        } else {
            // Gestire l'errore: la certificazione non appartiene a questo prodotto
            System.err.println("Errore: tentativo di aggiungere certificazione non pertinente al prodotto.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Prodotto prodotto = (Prodotto) o;
        return idProdotto != null && idProdotto.equals(prodotto.idProdotto);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}