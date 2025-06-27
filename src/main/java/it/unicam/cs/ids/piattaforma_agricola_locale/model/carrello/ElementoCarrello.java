package it.unicam.cs.ids.piattaforma_agricola_locale.model.carrello;

import jakarta.persistence.*;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.Acquistabile;

@Entity
@Table(name = "elementi_carrello")
public class ElementoCarrello {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_elemento")
    private Long idElemento;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carrello", nullable = false)
    private Carrello carrello;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_prodotto")
    private it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto prodotto;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pacchetto")
    private it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Pacchetto pacchetto;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_evento")
    private it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.Evento evento;
    
    @Column(name = "quantita", nullable = false)
    private int quantita;
    
    @Column(name = "prezzo_unitario", nullable = false)
    private double prezzoUnitario;

    public ElementoCarrello() {}

    public ElementoCarrello(Carrello carrello, Acquistabile acquistabile, int quantita) {
        this.carrello = carrello;
        
        if (acquistabile instanceof it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto) {
            this.prodotto = (it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto) acquistabile;
        } else if (acquistabile instanceof it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Pacchetto) {
            this.pacchetto = (it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Pacchetto) acquistabile;
        } else if (acquistabile instanceof it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.Evento) {
            this.evento = (it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.Evento) acquistabile;
        } else {
            throw new IllegalArgumentException("Unknown Acquistabile type");
        }
        
        this.quantita = quantita;
        this.prezzoUnitario = acquistabile.getPrezzo();
    }

    public Long getIdElemento() {
        return idElemento;
    }

    public void setIdElemento(Long idElemento) {
        this.idElemento = idElemento;
    }

    public Carrello getCarrello() {
        return carrello;
    }

    public void setCarrello(Carrello carrello) {
        this.carrello = carrello;
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
        
        this.prezzoUnitario = acquistabile.getPrezzo();
    }

    public int getQuantita() {
        return quantita;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    public double getPrezzoUnitario() {
        return prezzoUnitario;
    }

    public void setPrezzoUnitario(double prezzoUnitario) {
        this.prezzoUnitario = prezzoUnitario;
    }

    /**
     * Calcola il prezzo totale per questo elemento (quantità * prezzo unitario)
     * 
     * @return il prezzo totale dell'elemento
     */
    public double calcolaPrezzoTotale() {
        return quantita * prezzoUnitario;
    }

}
