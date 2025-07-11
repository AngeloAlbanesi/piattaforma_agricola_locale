package it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine;

import jakarta.persistence.*;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.stateOrdine.*;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "ordini")
public class Ordine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ordine")
    private Long idOrdine;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_ordine", nullable = false)
    private Date dataOrdine;

    @Column(name = "importo_totale", nullable = false)
    private double importoTotale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_acquirente", nullable = false)
    private Acquirente acquirente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venditore", nullable = false)
    private Venditore venditore;

    @OneToMany(mappedBy = "ordine", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RigaOrdine> righeOrdine;

    @Enumerated(EnumType.STRING)
    @Column(name = "stato_corrente", nullable = false)
    private StatoCorrente statoCorrente;

    @Transient
    private IStatoOrdine stato;

    public Ordine() {
    }

    /**
     * Metodo chiamato dopo il caricamento dell'entità dal database
     * per inizializzare lo stato transient basato sullo stato persistente
     */
    @PostLoad
    private void initializeState() {
        this.stato = createStateFromEnum(this.statoCorrente);
    }

    public Ordine(Date dataOrdine, Acquirente acquirente) {
        this.dataOrdine = dataOrdine;
        this.importoTotale = 0.0;
        this.acquirente = acquirente;
        this.righeOrdine = new ArrayList<>();
        this.statoCorrente = StatoCorrente.ATTESA_PAGAMENTO;
        this.stato = new StatoOrdineNuovoInAttesaDiPagamento();
    }

    /**
     * Nuovo costruttore per ordini indipendenti per venditore
     */
    public Ordine(Date dataOrdine, Acquirente acquirente, Venditore venditore) {
        this.dataOrdine = dataOrdine;
        this.importoTotale = 0.0;
        this.acquirente = acquirente;
        this.venditore = venditore;
        this.righeOrdine = new ArrayList<>();
        this.statoCorrente = StatoCorrente.ATTESA_PAGAMENTO;
        this.stato = new StatoOrdineNuovoInAttesaDiPagamento();
    }

    public Long getIdOrdine() {
        return idOrdine;
    }

    public void setIdOrdine(Long idOrdine) {
        this.idOrdine = idOrdine;
    }

    public double getImportoTotale() {
        return importoTotale;
    }

    public void setImportoTotale(double importoTotale) {
        this.importoTotale = importoTotale;
    }

    public Date getDataOrdine() {
        return dataOrdine;
    }

    public void setDataOrdine(Date dataOrdine) {
        this.dataOrdine = dataOrdine;
    }

    public Acquirente getAcquirente() {
        return acquirente;
    }

    public void setAcquirente(Acquirente acquirente) {
        this.acquirente = acquirente;
    }

    /**
     * Restituisce la rappresentazione descrittiva dello stato corrente
     * 
     * @return l'enum StatoCorrente corrispondente allo stato attuale
     */
    public StatoCorrente getStatoOrdine() {
        return statoCorrente;
    }

    public void setStatoCorrente(StatoCorrente statoCorrente) {
        this.statoCorrente = statoCorrente;
    }

    /**
     * Restituisce l'oggetto stato corrente (per il pattern State)
     * 
     * @return l'istanza di IStatoOrdine che rappresenta lo stato corrente
     */
    public IStatoOrdine getStato() {
        return stato;
    }

    /**
     * Imposta un nuovo stato per l'ordine (per il pattern State)
     * 
     * @param nuovoStato il nuovo stato da impostare
     */
    public void setStato(IStatoOrdine nuovoStato) {
        this.stato = nuovoStato;
    }

    public List<RigaOrdine> getRigheOrdine() {
        return righeOrdine;
    }

    public void setRigheOrdine(List<RigaOrdine> righeOrdine) {
        this.righeOrdine = righeOrdine;
    }

    /**
     * Restituisce il venditore associato a questo ordine
     * 
     * @return il venditore dell'ordine
     */
    public Venditore getVenditore() {
        return venditore;
    }

    /**
     * Imposta il venditore per questo ordine
     * 
     * @param venditore il venditore da associare all'ordine
     */
    public void setVenditore(Venditore venditore) {
        this.venditore = venditore;
    }

    // Metodi per gestire lo stato dell'ordine (delegano al pattern State)
    public void processa() {
        if (stato == null) {
            stato = createStateFromEnum(statoCorrente);
        }
        stato.processaOrdine(this);
    }

    public void spedisci() {
        if (stato == null) {
            stato = createStateFromEnum(statoCorrente);
        }
        stato.spedisciOrdine(this);
    }

    public void annulla() {
        if (stato == null) {
            stato = createStateFromEnum(statoCorrente);
        }
        stato.annullaOrdine(this);
    }

    public void consegna() {
        if (stato == null) {
            stato = createStateFromEnum(statoCorrente);
        }
        stato.consegnaOrdine(this);
    }

    /**
     * Metodo per effettuare il pagamento dell'ordine
     */
    public void paga() {
        // Assicurati che lo stato sia inizializzato
        if (stato == null) {
            stato = createStateFromEnum(statoCorrente);
        }
        // Questo metodo potrebbe essere aggiunto per gestire il pagamento
        // In questo caso, la transizione da "in attesa di pagamento" a "pagato"
        // sar� gestita tramite il metodo processa() che nel caso dello stato
        // "in attesa di pagamento" dovrebbe gestire il pagamento
        processa();
    }

    /**
     * Crea un'istanza dello stato appropriato basato sull'enum StatoCorrente
     */
    private IStatoOrdine createStateFromEnum(StatoCorrente statoCorrente) {
        if (statoCorrente == null) {
            return new StatoOrdineNuovoInAttesaDiPagamento();
        }

        switch (statoCorrente) {
            case ATTESA_PAGAMENTO:
                return new StatoOrdineNuovoInAttesaDiPagamento();
            case PRONTO_PER_LAVORAZIONE:
                return new StatoOrdinePagatoProntoPerLavorazione();
            case IN_LAVORAZIONE:
                return new StatoOrdineInLavorazione();
            case SPEDITO:
                return new StatoOrdineSpedito();
            case CONSEGNATO:
                return new StatoOrdineConsegnato();
            case ANNULLATO:
                return new StatoOrdineAnnullato();
            default:
                return new StatoOrdineNuovoInAttesaDiPagamento();
        }
    }

}
