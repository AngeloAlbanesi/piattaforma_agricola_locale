package it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Trasformatore;

/**
 * Classe che rappresenta un processo di trasformazione completo.
 * Un processo è caratterizzato da più fasi di lavorazione sequenziali
 * che trasformano materie prime in prodotti finiti.
 */
@Entity
@Table(name = "processi_trasformazione")
public class ProcessoTrasformazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "nome", nullable = false)
    private String nome;
    
    @Column(name = "descrizione", columnDefinition = "TEXT")
    private String descrizione;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_trasformatore", nullable = false)
    private Trasformatore trasformatore;
    
    @OneToMany(mappedBy = "processoTrasformazione", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FaseLavorazione> fasiLavorazione;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_prodotto_finale")
    private Prodotto prodottoFinale;

    @Column(name = "metodo_produzione")
    private String metodoProduzione;
    
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    public ProcessoTrasformazione() {}

    /**
     * Costruttore con parametri principali.
     *
     * @param nome          Il nome del processo di trasformazione
     * @param descrizione   La descrizione del processo
     * @param trasformatore Il trasformatore responsabile del processo
     */
    public ProcessoTrasformazione(String nome, String descrizione, Trasformatore trasformatore) {
        this.fasiLavorazione = new ArrayList<>();
        this.nome = validaNome(nome);
        this.descrizione = Objects.requireNonNull(descrizione, "La descrizione non può essere nulla");
        this.trasformatore = Objects.requireNonNull(trasformatore, "Il trasformatore non può essere nullo");
    }

    /**
     * Costruttore completo.
     *
     * @param nome             Il nome del processo di trasformazione
     * @param descrizione      La descrizione del processo
     * @param trasformatore    Il trasformatore responsabile del processo
     * @param prodottoFinale   Il prodotto risultante dal processo
     * @param metodoProduzione Il metodo di produzione utilizzato
     */
    public ProcessoTrasformazione(String nome, String descrizione, Trasformatore trasformatore,
            Prodotto prodottoFinale, String metodoProduzione) {
        this(nome, descrizione, trasformatore);
        this.prodottoFinale = prodottoFinale;
        this.metodoProduzione = metodoProduzione;
    }

    /**
     * Valida il nome del processo.
     *
     * @param nome Il nome da validare
     * @return Il nome validato
     * @throws IllegalArgumentException se il nome è nullo o vuoto
     */
    private String validaNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome del processo non può essere nullo o vuoto");
        }
        return nome.trim();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public Trasformatore getTrasformatore() {
        return trasformatore;
    }

    public List<FaseLavorazione> getFasiLavorazione() {
        return fasiLavorazione.stream()
                .sorted(Comparator.comparingInt(FaseLavorazione::getOrdineEsecuzione))
                .collect(Collectors.toList());
    }

    /**
     * Restituisce le fasi del processo.
     * Alias per getFasiLavorazione() per compatibilità con i test.
     *
     * @return Lista delle fasi di lavorazione ordinate per esecuzione
     */
    public List<FaseLavorazione> getFasi() {
        return getFasiLavorazione();
    }

    /**
     * Restituisce il responsabile del processo.
     * Alias per getTrasformatore() per compatibilità con i test.
     *
     * @return Il trasformatore responsabile del processo
     */
    public Trasformatore getResponsabile() {
        return getTrasformatore();
    }

    public Prodotto getProdottoFinale() {
        return prodottoFinale;
    }

    public String getMetodoProduzione() {
        return metodoProduzione;
    }

    public String getNote() {
        return note;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = validaNome(nome);

    }

    public void setDescrizione(String descrizione) {
        this.descrizione = Objects.requireNonNull(descrizione, "La descrizione non può essere nulla");

    }

    public void setTrasformatore(Trasformatore trasformatore) {
        this.trasformatore = Objects.requireNonNull(trasformatore, "Il trasformatore non può essere nullo");

    }

    public void setProdottoFinale(Prodotto prodottoFinale) {
        this.prodottoFinale = prodottoFinale;

    }

    public void setMetodoProduzione(String metodoProduzione) {
        this.metodoProduzione = metodoProduzione;

    }

    public void setNote(String note) {
        this.note = note;

    }

    /**
     * Aggiunge una fase di lavorazione al processo.
     *
     * @param fase La fase da aggiungere
     * @throws IllegalArgumentException se la fase è nulla
     */
    public void aggiungiFase(FaseLavorazione fase) {
        Objects.requireNonNull(fase, "La fase non può essere nulla");

        fasiLavorazione.add(fase);
    }

    /**
     * Rimuove una fase di lavorazione dal processo.
     *
     * @param fase La fase da rimuovere
     * @return true se la fase è stata rimossa, false altrimenti
     */
    public boolean rimuoviFase(FaseLavorazione fase) {
        boolean rimossa = fasiLavorazione.remove(fase);

        return rimossa;
    }

    /**
     * Restituisce il numero totale di fasi nel processo.
     *
     * @return Il numero di fasi
     */
    public int getNumeroFasi() {
        return fasiLavorazione.size();
    }

    /**
     * Verifica se il processo è completo (ha almeno una fase).
     *
     * @return true se il processo è completo, false altrimenti
     */
    public boolean isCompleto() {
        return !fasiLavorazione.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ProcessoTrasformazione that = (ProcessoTrasformazione) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(nome, that.nome) &&
                Objects.equals(trasformatore, that.trasformatore);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, trasformatore);
    }

    @Override
    public String toString() {
        return String.format("ProcessoTrasformazione{id=%d, nome='%s', trasformatore='%s', fasi=%d, attivo=%s}",
                id, nome,
                trasformatore != null ? trasformatore.getNome() : "N/A",
                fasiLavorazione.size());
    }
}