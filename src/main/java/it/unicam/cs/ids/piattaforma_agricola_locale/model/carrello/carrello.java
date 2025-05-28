package it.unicam.cs.ids.piattaforma_agricola_locale.model.carrello;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;

public class Carrello {
    private int idCarrello;
    private Acquirente acquirente;
    private List<ElementoCarrello> elementiCarrello;
    private Date ultimaModifica;

    public Carrello(int idCarrello, Acquirente acquirente) {
        this.idCarrello = idCarrello;
        this.acquirente = acquirente;
        this.elementiCarrello = new ArrayList<>();
        this.ultimaModifica = new Date();
    }

    public int getIdCarrello() {
        return idCarrello;
    }

    public void setIdCarrello(int idCarrello) {
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
