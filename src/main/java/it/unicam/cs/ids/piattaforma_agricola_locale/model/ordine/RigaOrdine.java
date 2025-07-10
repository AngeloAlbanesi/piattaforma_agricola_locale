package it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine;

import jakarta.persistence.*;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.Acquistabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.TipoAcquistabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.AcquistabileService;

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
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_acquistabile", nullable = false)
    private TipoAcquistabile tipoAcquistabile;
    
    @Column(name = "id_acquistabile", nullable = false)
    private Long idAcquistabile;
    
    @Transient
    private AcquistabileService acquistabileService;
    
    @Column(name = "quantita_ordinata", nullable = false)
    private int quantitaOrdinata;
    
    @Column(name = "prezzo_unitario", nullable = false)
    private double prezzoUnitario;

    public RigaOrdine() {}

    public RigaOrdine(Ordine ordine, Acquistabile acquistabile, int quantitaOrdinata, double prezzoUnitario) {
        this.ordine = ordine;
        this.tipoAcquistabile = TipoAcquistabile.fromAcquistabile(acquistabile);
        this.idAcquistabile = acquistabile.getId();
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
        if (acquistabileService != null && tipoAcquistabile != null && idAcquistabile != null) {
            return acquistabileService.findByTipoAndId(tipoAcquistabile, idAcquistabile);
        }
        return null;
    }

    public void setAcquistabile(Acquistabile acquistabile) {
        if (acquistabile != null) {
            this.tipoAcquistabile = TipoAcquistabile.fromAcquistabile(acquistabile);
            this.idAcquistabile = acquistabile.getId();
        } else {
            this.tipoAcquistabile = null;
            this.idAcquistabile = null;
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
