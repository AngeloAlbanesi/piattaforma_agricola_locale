package it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine;

import jakarta.persistence.*;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.Acquistabile;

@Entity
@Table(name = "righe_ordine")
public class RigaOrdine {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_riga")
    private Long idRiga;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ordine", nullable = false)
    private Ordine ordine;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_prodotto")
    private it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto prodotto;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pacchetto")
    private it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Pacchetto pacchetto;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_evento")
    private it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.Evento evento;
    
    @Column(name = "quantita_ordinata", nullable = false)
    private int quantitaOrdinata;
    
    @Column(name = "prezzo_unitario", nullable = false)
    private double prezzoUnitario;

    public RigaOrdine() {}

    public RigaOrdine(Ordine ordine, Acquistabile acquistabile, int quantitaOrdinata, double prezzoUnitario) {
        this.ordine = ordine;
        
        if (acquistabile instanceof it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto) {
            this.prodotto = (it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto) acquistabile;
        } else if (acquistabile instanceof it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Pacchetto) {
            this.pacchetto = (it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Pacchetto) acquistabile;
        } else if (acquistabile instanceof it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.Evento) {
            this.evento = (it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.Evento) acquistabile;
        } else {
            throw new IllegalArgumentException("Unknown Acquistabile type");
        }
        
        this.quantitaOrdinata = quantitaOrdinata;
        this.prezzoUnitario = prezzoUnitario;
    }

    public Long getIdRiga() {
        return idRiga;
    }

    public void setIdRiga(Long idRiga) {
        this.idRiga = idRiga;
    }

    public Ordine getOrdine() {
        return ordine;
    }

    public void setOrdine(Ordine ordine) {
        this.ordine = ordine;
    }

    public Acquistabile getAcquistabile() {
        if (prodotto != null) {
            return prodotto;
        } else if (pacchetto != null) {
            return pacchetto;
        } else if (evento != null) {
            return evento;
        }
        return null;
    }

    public void setAcquistabile(Acquistabile acquistabile) {
        // Reset all references
        this.prodotto = null;
        this.pacchetto = null;
        this.evento = null;
        
        if (acquistabile instanceof it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto) {
            this.prodotto = (it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto) acquistabile;
        } else if (acquistabile instanceof it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Pacchetto) {
            this.pacchetto = (it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Pacchetto) acquistabile;
        } else if (acquistabile instanceof it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.Evento) {
            this.evento = (it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.Evento) acquistabile;
        }
    }

    public int getQuantitaOrdinata() {
        return quantitaOrdinata;
    }

    public void setQuantitaOrdinata(int quantitaOrdinata) {
        this.quantitaOrdinata = quantitaOrdinata;
    }

    public double getPrezzoUnitario() {
        return prezzoUnitario;
    }

    public void setPrezzoUnitario(double prezzoUnitario) {
        this.prezzoUnitario = prezzoUnitario;
    }

    @Override
    public String toString() {
        Acquistabile acq = getAcquistabile();
        return "RigaOrdine{" +
                "idRiga=" + idRiga +
                ", acquistabile=" + (acq != null ? acq.getNome() : "null") +
                ", quantitaOrdinata=" + quantitaOrdinata +
                ", prezzoUnitario=" + prezzoUnitario +
                '}';
    }
}
