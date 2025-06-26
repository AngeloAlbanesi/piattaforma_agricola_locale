package it.unicam.cs.ids.piattaforma_agricola_locale.model.trasformazione;

import jakarta.persistence.*;
import java.util.Objects;

/**
 * Classe che rappresenta una singola fase di lavorazione all'interno di un
 * processo di trasformazione.
 * Ogni fase è caratterizzata da una descrizione, una materia prima utilizzata
 * e la sua fonte (interna o esterna).
 */
@Entity
@Table(name = "fasi_lavorazione")
public class FaseLavorazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "nome", nullable = false)
    private String nome;
    
    @Column(name = "descrizione", columnDefinition = "TEXT")
    private String descrizione;
    
    @Column(name = "ordine_esecuzione", nullable = false)
    private int ordineEsecuzione;
    
    @Column(name = "materia_prima_utilizzata", nullable = false)
    private String materiaPrimaUtilizzata;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_processo_trasformazione")
    private ProcessoTrasformazione processoTrasformazione;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_fonte_materia_prima")
    private FonteMateriaPrima fonte;
    
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    public FaseLavorazione() {}

    /**
     * Costruttore per una fase di lavorazione.
     *
     * @param nome                   Il nome della fase di lavorazione.
     * @param descrizione            La descrizione dettagliata della fase.
     * @param ordineEsecuzione       L'ordine di esecuzione della fase nel processo.
     * @param materiaPrimaUtilizzata La descrizione testuale della materia prima.
     * @param fonte                  L'oggetto che rappresenta la fonte della
     *                               materia prima.
     */
    public FaseLavorazione(String nome, String descrizione, int ordineEsecuzione,
                           String materiaPrimaUtilizzata, FonteMateriaPrima fonte) {
        this.nome = validaNome(nome);
        this.descrizione = Objects.requireNonNull(descrizione, "La descrizione non può essere nulla");
        this.ordineEsecuzione = validaOrdineEsecuzione(ordineEsecuzione);
        this.materiaPrimaUtilizzata = Objects.requireNonNull(materiaPrimaUtilizzata,
                "La materia prima utilizzata non può essere nulla");
        this.fonte = Objects.requireNonNull(fonte, "La fonte non può essere nulla");
    }

    private String validaNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della fase non può essere nullo o vuoto");
        }
        return nome.trim();
    }

    private int validaOrdineEsecuzione(int ordine) {
        if (ordine < 0) {
            throw new IllegalArgumentException("L'ordine di esecuzione non può essere negativo");
        }
        return ordine;
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

    public int getOrdineEsecuzione() {
        return ordineEsecuzione;
    }

    public String getMateriaPrimaUtilizzata() {
        return materiaPrimaUtilizzata;
    }

    public FonteMateriaPrima getFonte() {
        return fonte;
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

    public void setOrdineEsecuzione(int ordineEsecuzione) {
        this.ordineEsecuzione = validaOrdineEsecuzione(ordineEsecuzione);
    }

    public void setMateriaPrimaUtilizzata(String materiaPrimaUtilizzata) {
        this.materiaPrimaUtilizzata = Objects.requireNonNull(materiaPrimaUtilizzata,
                "La materia prima utilizzata non può essere nulla");
    }

    public void setFonte(FonteMateriaPrima fonte) {
        this.fonte = Objects.requireNonNull(fonte, "La fonte non può essere nulla");
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        FaseLavorazione that = (FaseLavorazione) o;
        return ordineEsecuzione == that.ordineEsecuzione &&
                Objects.equals(id, that.id) &&
                Objects.equals(nome, that.nome) &&
                Objects.equals(descrizione, that.descrizione);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, descrizione, ordineEsecuzione);
    }

    @Override
    public String toString() {
        return String.format("FaseLavorazione{id=%d, nome='%s', ordine=%d}",
                id, nome, ordineEsecuzione);
    }
}