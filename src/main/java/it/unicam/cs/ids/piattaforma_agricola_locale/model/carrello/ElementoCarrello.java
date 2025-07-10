package it.unicam.cs.ids.piattaforma_agricola_locale.model.carrello;

import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.Acquistabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.TipoAcquistabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.AcquistabileService;

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
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_acquistabile", nullable = false)
    private TipoAcquistabile tipoAcquistabile;
    
    @Column(name = "id_acquistabile", nullable = false)
    private Long idAcquistabile;
    
    @Transient
    private AcquistabileService acquistabileService;
    
    @Column(name = "quantita", nullable = false)
    private int quantita;
    
    @Column(name = "prezzo_unitario", nullable = false)
    private double prezzoUnitario;

    public ElementoCarrello() {}

    public ElementoCarrello(Carrello carrello, Acquistabile acquistabile, int quantita) {
        this.carrello = carrello;
        this.tipoAcquistabile = TipoAcquistabile.fromAcquistabile(acquistabile);
        this.idAcquistabile = acquistabile.getId();
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
        if (acquistabileService != null && tipoAcquistabile != null && idAcquistabile != null) {
            return acquistabileService.findByTipoAndId(tipoAcquistabile, idAcquistabile);
        }
        return null;
    }

    public void setAcquistabile(Acquistabile acquistabile) {
        if (acquistabile != null) {
            this.tipoAcquistabile = TipoAcquistabile.fromAcquistabile(acquistabile);
            this.idAcquistabile = acquistabile.getId();
            this.prezzoUnitario = acquistabile.getPrezzo();
        } else {
            this.tipoAcquistabile = null;
            this.idAcquistabile = null;
            this.prezzoUnitario = 0.0;
        }
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

    // Getters e setters per i nuovi campi
    public TipoAcquistabile getTipoAcquistabile() {
        return tipoAcquistabile;
    }

    public void setTipoAcquistabile(TipoAcquistabile tipoAcquistabile) {
        this.tipoAcquistabile = tipoAcquistabile;
    }

    public Long getIdAcquistabile() {
        return idAcquistabile;
    }

    public void setIdAcquistabile(Long idAcquistabile) {
        this.idAcquistabile = idAcquistabile;
    }

    public void setAcquistabileService(AcquistabileService acquistabileService) {
        this.acquistabileService = acquistabileService;
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
