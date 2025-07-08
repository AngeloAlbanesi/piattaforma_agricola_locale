package it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo;

import jakarta.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

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

    // Relazione con gli elementi del pacchetto tramite entita di associazione
    @OneToMany(mappedBy = "pacchetto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PacchettoElemento> pacchettoElementi = new java.util.ArrayList<>();

    // Lista transient per compatibilita con il codice esistente
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
        // Aggiungi alla lista transient per compatibilita
        this.getElementiInclusi().add(elemento);
        
        // Se e un prodotto, aggiungi anche alla relazione persistente
        if (elemento instanceof Prodotto) {
            PacchettoElemento pacchettoElemento = new PacchettoElemento(this, (Prodotto) elemento);
            this.pacchettoElementi.add(pacchettoElemento);
        }
        
        this.ricalcolaPrezzo();
    }

    public void rimuoviElemento(Acquistabile elemento) {
        this.getElementiInclusi().remove(elemento);
        
        // Se e un prodotto, rimuovi anche dalla relazione persistente
        if (elemento instanceof Prodotto) {
            this.pacchettoElementi.removeIf(pe -> pe.getProdotto().equals(elemento));
        }
        
        this.ricalcolaPrezzo();
    }

    public List<Acquistabile> getElementiInclusi() {
        if (elementiInclusi == null) {
            // Carica gli elementi dalla relazione persistente se disponibile
            if (pacchettoElementi != null && !pacchettoElementi.isEmpty()) {
                elementiInclusi = pacchettoElementi.stream()
                    .map(pe -> (Acquistabile) pe.getProdotto())
                    .collect(Collectors.toList());
            } else {
                elementiInclusi = new java.util.ArrayList<>();
            }
        }
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
        return this.getElementiInclusi();
    }

    /**
     * Ricalcola automaticamente il prezzo del pacchetto sommando i prezzi di tutti gli elementi inclusi.
     */
    private void ricalcolaPrezzo() {
        if (this.getElementiInclusi() != null && !this.getElementiInclusi().isEmpty()) {
            this.prezzoPacchetto = this.getElementiInclusi().stream()
                    .mapToDouble(Acquistabile::getPrezzo)
                    .sum();
        } else {
            this.prezzoPacchetto = 0.0;
        }
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