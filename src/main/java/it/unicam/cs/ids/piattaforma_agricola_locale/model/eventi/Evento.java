package it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.Acquistabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.AnimatoreDellaFiliera;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

@Entity
@Table(name = "eventi")
public class Evento implements Acquistabile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evento")
    private Long idEvento;
    
    @Column(name = "nome_evento", nullable = false)
    private String nomeEvento;
    
    @Column(name = "descrizione", columnDefinition = "TEXT")
    private String descrizione;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_ora_inizio", nullable = false)
    private Date DataOraInizio;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_ora_fine", nullable = false)
    private Date DataOraFine;
    
    @Column(name = "luogo_evento", nullable = false)
    private String luogoEvento;
    
    @Column(name = "capienza_massima", nullable = false)
    private int capienzaMassima;
    
    @Column(name = "posti_prenotati", nullable = false)
    private int postiAttualmentePrenotati;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "stato_evento", nullable = false)
    private StatoEventoValori statoEvento;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_organizzatore", nullable = false)
    private AnimatoreDellaFiliera organizzatore;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "eventi_aziende_partecipanti",
        joinColumns = @JoinColumn(name = "id_evento"),
        inverseJoinColumns = @JoinColumn(name = "id_venditore")
    )
    private List<Venditore> aziendePartecipanti;

    public Evento() {}

    public Evento(String nomeEvento, String descrizione, Date DataOraInizio, Date DataOraFine,
            String luogoEvento, int capienzaMassima, AnimatoreDellaFiliera organizzatore) {

        this.nomeEvento = nomeEvento;
        this.descrizione = descrizione;
        this.DataOraInizio = DataOraInizio;
        this.DataOraFine = DataOraFine;
        this.luogoEvento = luogoEvento;
        this.capienzaMassima = capienzaMassima;
        this.postiAttualmentePrenotati = 0;
        this.statoEvento = StatoEventoValori.IN_PROGRAMMA;
        this.organizzatore = organizzatore;
        this.aziendePartecipanti = new ArrayList<>();
    }


    public Long getId() {
        return idEvento;
    }
    public void setId(Long idEvento) {
        this.idEvento = idEvento;
    }

    public String getNome() {
        return nomeEvento;
    }

    public void setNome(String nomeEvento) {
        this.nomeEvento = nomeEvento;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Date getDataOraInizio() {
        return DataOraInizio;
    }

    public void setDataOraInizio(Date DataOraInizio) {
        this.DataOraInizio = DataOraInizio;
    }

    public Date getDataOraFine() {
        return DataOraFine;
    }

    public void setDataOraFine(Date DataOraFine) {
        this.DataOraFine = DataOraFine;
    }

    public String getLuogoEvento() {
        return luogoEvento;
    }

    public void setLuogoEvento(String luogoEvento) {
        this.luogoEvento = luogoEvento;
    }

    public int getCapienzaMassima() {
        return capienzaMassima;
    }

    public void setCapienzaMassima(int capienzaMassima) {
        this.capienzaMassima = capienzaMassima;
    }

    public int getPostiDisponibili() {
        return capienzaMassima - postiAttualmentePrenotati;
    }

    public AnimatoreDellaFiliera getOrganizzatore() {
        return organizzatore;
    }



    @Override
    public double getPrezzo() {
        return 0.0; // Gli eventi potrebbero essere gratuiti
    }

    @Override
    public Venditore getVenditore() {
        return null;
    }

    public void addAziendaPartecipante(Venditore venditore){
        aziendePartecipanti.add(venditore);
        }
    public void removeAziendaPartecipante(Venditore venditore) {
        aziendePartecipanti.remove(venditore);
    }
    public List<Venditore> getAziendePartecipanti() {
        return aziendePartecipanti;
    }
    public int getPostiAttualmentePrenotati() {
        return postiAttualmentePrenotati;
    }
    public void setPostiAttualmentePrenotati(int postiAttualmentePrenotati) {
        if (postiAttualmentePrenotati < 0) {
            throw new IllegalArgumentException("Il numero di posti prenotati non può essere negativo.");
        }
        if (postiAttualmentePrenotati > capienzaMassima) {
            throw new IllegalArgumentException("Il numero di posti prenotati non può superare la capienza massima.");
        }
        this.postiAttualmentePrenotati = postiAttualmentePrenotati;
    }

    public StatoEventoValori getStatoEvento() {
        return statoEvento;
    }
    public void setStatoEvento(StatoEventoValori statoEvento) {
        if (statoEvento == null) {
            throw new IllegalArgumentException("Lo stato dell'evento non può essere nullo.");
        }
        this.statoEvento = statoEvento;
    }

}

