package it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo;

import jakarta.persistence.*;

/**
 * Entità di associazione tra Pacchetto e Prodotto per persistere gli elementi inclusi nel pacchetto.
 */
@Entity
@Table(name = "pacchetto_elementi")
public class PacchettoElemento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pacchetto", nullable = false)
    private Pacchetto pacchetto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_prodotto", nullable = false)
    private Prodotto prodotto;

    public PacchettoElemento() {
    }

    public PacchettoElemento(Pacchetto pacchetto, Prodotto prodotto) {
        this.pacchetto = pacchetto;
        this.prodotto = prodotto;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pacchetto getPacchetto() {
        return pacchetto;
    }

    public void setPacchetto(Pacchetto pacchetto) {
        this.pacchetto = pacchetto;
    }

    public Prodotto getProdotto() {
        return prodotto;
    }

    public void setProdotto(Prodotto prodotto) {
        this.prodotto = prodotto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PacchettoElemento that = (PacchettoElemento) o;
        return pacchetto != null && prodotto != null && 
               pacchetto.equals(that.pacchetto) && prodotto.equals(that.prodotto);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}