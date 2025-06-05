package it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo;

import java.util.ArrayList;
import java.util.List;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.Acquistabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.ElementoVerificabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.StatoVerificaValori;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

public class Prodotto implements Acquistabile, ElementoVerificabile {

    private int idProdotto;
    private String nome;
    private String descrizione;
    private double prezzo;
    private int quantitaDisponibile;
    private StatoVerificaValori statoVerifica;
    private String feedbackVerifica;
    private Venditore venditore;
    private List<Certificazione> certificazioniProdotto;
    private Integer idProcessoTrasforamzioneOriginiario;
    private TipoOrigineProdotto tipoOrigineProdotto;

    public Prodotto(int idProdotto, String nome, String descrizione, double prezzo, int quantitaDisponibile,
            Venditore venditore,int idProcessoTrasforamzioneOriginiario, TipoOrigineProdotto tipoOrigineProdotto) {
        this.idProdotto = idProdotto;
        this.nome = nome;
        this.descrizione = descrizione;
        this.prezzo = prezzo;
        this.quantitaDisponibile = quantitaDisponibile;
        this.venditore = venditore;
        this.statoVerifica = StatoVerificaValori.IN_REVISIONE;
        this.certificazioniProdotto = new ArrayList<>();
        this.idProcessoTrasforamzioneOriginiario = idProcessoTrasforamzioneOriginiario;
        this.tipoOrigineProdotto = tipoOrigineProdotto;
    }

    public int getId() {
        return idProdotto;
    }

    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public double getPrezzo() {
        return prezzo;
    }

    public int getQuantitaDisponibile() {
        return quantitaDisponibile;
    }

    public void setStatoVerifica(StatoVerificaValori statoVerifica) {
        this.statoVerifica = statoVerifica;

    }

    public StatoVerificaValori getStatoVerifica() {
        return statoVerifica;
    }

    public String getFeedbackVerifica() {
        return feedbackVerifica;
    }

    public void setFeedbackVerifica(String feedbackVerifica) {
        this.feedbackVerifica = feedbackVerifica;
    }

    @Override
    public Venditore getVenditore() {
        return venditore;
    }

    public void setQuantitaDisponibile(int quantita) {
        this.quantitaDisponibile = quantita;
    }

    @Override
    public String toString() {
        return "Prodotto{" +
                "idProdotto=" + idProdotto +
                ", nome='" + nome + '\'' +
                ", descrizione='" + descrizione + '\'' +
                ", prezzo=" + prezzo +
                ", quantitaDisponibile=" + quantitaDisponibile +
                ", statoVerifica=" + statoVerifica +
                ", feedbackVerifica='" + feedbackVerifica + '\'' +
                '}';
    }

    public List<Certificazione> getCertificazioni() {
        return certificazioniProdotto;
    }

    public void aggiungiCertificazione(Certificazione certificazione) {
        if (certificazione != null && certificazione.getIdProdottoAssociato() != null && certificazione.getIdProdottoAssociato().equals(this.idProdotto)) {
            this.certificazioniProdotto.add(certificazione);
        } else {
            // Gestire l'errore: la certificazione non appartiene a questo prodotto
            System.err.println("Errore: tentativo di aggiungere certificazione non pertinente al prodotto.");
        }
    }

    public Integer getIdProcessoTrasforamzioneOriginiario() {
        return idProcessoTrasforamzioneOriginiario;
    }
    public void setIdProcessoTrasforamzioneOriginiario(Integer idProcessoTrasforamzioneOriginiario) {
        this.idProcessoTrasforamzioneOriginiario = idProcessoTrasforamzioneOriginiario;
    }
    public TipoOrigineProdotto getTipoOrigineProdotto() {
        return tipoOrigineProdotto;
    }
    public void setTipoOrigineProdotto(TipoOrigineProdotto tipoOrigineProdotto) {
        this.tipoOrigineProdotto = tipoOrigineProdotto;
    }



}