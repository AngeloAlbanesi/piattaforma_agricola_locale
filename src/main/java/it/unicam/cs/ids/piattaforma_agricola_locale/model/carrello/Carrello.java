package it.unicam.cs.ids.piattaforma_agricola_locale.model.carrello;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;

@Entity
@Table(name = "carrelli")
public class Carrello {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrello")
    private Long idCarrello;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_acquirente", nullable = false, unique = true)
    private Acquirente acquirente;
    
    @OneToMany(mappedBy = "carrello", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ElementoCarrello> elementiCarrello;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ultima_modifica", nullable = false)
    private Date ultimaModifica;

    public Carrello() {}

    public Carrello(Acquirente acquirente) {
        this.acquirente = acquirente;
        this.elementiCarrello = new ArrayList<>();
        this.ultimaModifica = new Date();
    }

    public Long getIdCarrello() {
        return idCarrello;
    }

    public void setIdCarrello(Long idCarrello) {
        this.idCarrello = idCarrello;
    }

    public Acquirente getAcquirente() {
        return acquirente;
    }

    public void setAcquirente(Acquirente acquirente) {
        this.acquirente = acquirente;
    }

    public List<ElementoCarrello> getElementiCarrello() {
        return elementiCarrello;
    }

    public void setElementiCarrello(List<ElementoCarrello> elementiCarrello) {
        this.elementiCarrello = elementiCarrello;
        this.ultimaModifica = new Date();
    }

    public Date getUltimaModifica() {
        return ultimaModifica;
    }

    public void setUltimaModifica(Date ultimaModifica) {
        this.ultimaModifica = ultimaModifica;
    }

    /**
     * Aggiunge un elemento al carrello
     * 
     * @param elemento l'elemento da aggiungere
     */
    public void aggiungiElemento(ElementoCarrello elemento) {
        this.elementiCarrello.add(elemento);
        this.ultimaModifica = new Date();
    }

    /**
     * Rimuove un elemento dal carrello
     * 
     * @param elemento l'elemento da rimuovere
     */
    public void rimuoviElemento(ElementoCarrello elemento) {
        this.elementiCarrello.remove(elemento);
        this.ultimaModifica = new Date();
    }

    /**
     * Svuota il carrello
     */
    public void svuota() {
        this.elementiCarrello.clear();
        this.ultimaModifica = new Date();
    }

    /**
     * Calcola il prezzo totale del carrello
     * 
     * @return il prezzo totale
     */
    public double calcolaPrezzoTotale() {
        return elementiCarrello.stream()
                .mapToDouble(elemento -> elemento.getPrezzoUnitario() * elemento.getQuantita())
                .sum();
    }

}
