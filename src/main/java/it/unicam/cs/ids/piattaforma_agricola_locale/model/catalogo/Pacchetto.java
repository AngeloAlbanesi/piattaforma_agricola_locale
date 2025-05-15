package it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo;

import java.util.List;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.Acquistabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DistributoreDiTipicita;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

public class Pacchetto implements Acquistabile {
    private int idPacchetto;
    private String nome;
    private String descrizione;
    private double prezzoPacchetto;
    private List <Acquistabile> elementiInclusi;
    private DistributoreDiTipicita distributore;

    public Pacchetto(DistributoreDiTipicita distributore, int idPacchetto, String nome, String descrizione, double prezzoPacchetto) {
        this.idPacchetto = idPacchetto;
        this.nome = nome;
        this.descrizione = descrizione;
        this.prezzoPacchetto = prezzoPacchetto;
        this.elementiInclusi = new java.util.ArrayList<>();
        this.distributore = distributore;
    }

    public int getId() {
        return idPacchetto;
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
    public  void aggiungiElemento(Acquistabile elemento) {
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

    public int numeroPacchettiTotali() {
        return this.elementiInclusi.size();
    }
}
