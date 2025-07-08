package it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo;

import jakarta.persistence.*;
import java.util.List;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.Acquistabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DistributoreDiTipicita;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

@Entity
@Table(name = "pacchetti")
public class Pacchetto implements Acquistabile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pacchetto")
    private Long idPacchetto;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "descrizione", columnDefinition = "TEXT")
    private String descrizione;

    @Column(name = "quantita_disponibile", nullable = false)
    private int quantitaDisponibile;

    @Column(name = "prezzo_pacchetto", nullable = false)
    private double prezzoPacchetto;

    // Note: This needs to be handled differently since we can't map directly to
    // interface
    // We'll need to create a separate entity for PacchettoElemento or use
    // repository methods
    @Transient
    private List<Acquistabile> elementiInclusi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_distributore", nullable = false)
    private DistributoreDiTipicita distributore;

    public Pacchetto() {
    }

    public Pacchetto(DistributoreDiTipicita distributore, String nome, String descrizione, int quantita,
            double prezzoPacchetto) {

        this.nome = nome;
        this.descrizione = descrizione;
        this.prezzoPacchetto = prezzoPacchetto;
        this.quantitaDisponibile = quantita;
        this.elementiInclusi = new java.util.ArrayList<>();
        this.distributore = distributore;
    }

    public Long getId() {
        return idPacchetto;
    }

    public void SetId(Long idPacchetto) {
        this.idPacchetto = idPacchetto;
    }

    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public double getPrezzo() {
        return prezzoPacchetto;
    }

    @Override
    public Venditore getVenditore() {
        return distributore;
    }

    public void aggiungiElemento(Acquistabile elemento) {
        this.elementiInclusi.add(elemento);
    }

    public void rimuoviElemento(Acquistabile elemento) {
        this.elementiInclusi.remove(elemento);
    }

    public List<Acquistabile> getElementiInclusi() {
        return elementiInclusi;
    }

    public DistributoreDiTipicita getDistributore() {
        return distributore;
    }

    public int getQuantitaDisponibile() {
        return quantitaDisponibile;
    }

    public void setQuantitaDisponibile(int quantitaDisponibile) {
        this.quantitaDisponibile = quantitaDisponibile;
    }

    /**
     * Imposta il nome del pacchetto.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Imposta la descrizione del pacchetto.
     */
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    /**
     * Imposta il prezzo totale del pacchetto.
     */
    public void setPrezzoTotale(double prezzoTotale) {
        this.prezzoPacchetto = prezzoTotale;
    }

    /**
     * Restituisce il prezzo totale del pacchetto.
     */
    public double getPrezzoTotale() {
        return this.prezzoPacchetto;
    }

    /**
     * Restituisce gli elementi inclusi nel pacchetto.
     */
    public List<Acquistabile> getElementi() {
        return this.elementiInclusi;
    }

    @Override
    public String toString() {
        return "Pacchetto{" +
                "idPacchetto=" + idPacchetto +
                ", nome='" + nome + '\'' +
                ", descrizione='" + descrizione + '\'' +
                ", quantitaDisponibile=" + quantitaDisponibile +
                ", prezzoPacchetto=" + prezzoPacchetto +
                ", elementiInclusi=" + elementiInclusi +
                ", distributore=" + distributore.getId() +
                '}';
    }
}