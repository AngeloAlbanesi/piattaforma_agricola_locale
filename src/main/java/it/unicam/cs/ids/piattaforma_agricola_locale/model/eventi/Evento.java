package it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi;

import java.util.Date;
import java.util.List;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.Acquistabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.AnimatoreDellaFiliera;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

public class Evento implements Acquistabile {
    private int idEvento;
    private String nomeEvento;
    private String descrizione;
    private Date DataOraInizio;
    private Date DataOraFine;
    private String luogoEvento;
    private int capienzaMassima;
    private int postiAttualmentePrenotati;
    private StatoEventoValori statoEvento;
    private AnimatoreDellaFiliera organizzatore;
    private List<Venditore> aziendePartecipanti;

    public Evento(int idEvento, String nomeEvento, String descrizione, Date DataOraInizio, Date DataOraFine,
            String luogoEvento, int capienzaMassima, StatoEventoValori statoEvento,
            AnimatoreDellaFiliera organizzatore, List<Venditore> aziendePartecipanti) {
        this.idEvento = idEvento;
        this.nomeEvento = nomeEvento;
        this.descrizione = descrizione;
        this.DataOraInizio = DataOraInizio;
        this.DataOraFine = DataOraFine;
        this.luogoEvento = luogoEvento;
        this.capienzaMassima = capienzaMassima;
        this.postiAttualmentePrenotati = 0;
        this.statoEvento = statoEvento;
        this.organizzatore = organizzatore;
        this.aziendePartecipanti = aziendePartecipanti;
    }

    public int getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(int idEvento) {
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

    public boolean incrementaPostiPrenotati(int quantita) {
        if (quantita > capienzaMassima - postiAttualmentePrenotati) {
            return false;
        }
        this.postiAttualmentePrenotati += quantita;
        return true;
    }

    public boolean decrementaPostiPrenotati(int quantita) {
        if (quantita > postiAttualmentePrenotati) {
            return false;
        }
        this.postiAttualmentePrenotati -= quantita;
        return true;
    }

    public int getPostiDisponibili() {
        return capienzaMassima - postiAttualmentePrenotati;
    }

    public AnimatoreDellaFiliera getOrganizzatore() {
        return organizzatore;
    }

    @Override
    public int getId() {
        return idEvento;
    }

    @Override
    public double getPrezzo() {
        return 0.0; // Gli eventi potrebbero essere gratuiti
    }

    @Override
    public Venditore getVenditore() {
        return null;
    }
}
