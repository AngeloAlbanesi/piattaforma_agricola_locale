/*
 *   Copyright (c) 2025 
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;

public abstract class Venditore extends Utente {
    private StatoAccreditamento statoAccreditamento;
    private DatiAzienda datiAzienda;
    private List<Prodotto> prodottiOfferti;

    public Venditore(String nome, String cognome, String email, String passwordHash,
            String numeroTelefono, DatiAzienda datiAzienda, TipoRuolo tipoRuolo ) {
        super(nome, cognome, email, passwordHash, numeroTelefono, tipoRuolo);
        statoAccreditamento = StatoAccreditamento.PENDING;
        this.datiAzienda = datiAzienda;
        this.prodottiOfferti = new ArrayList<>();
    }


    public void stampaDatiAzienda() {
        System.out.println(datiAzienda.getNomeAzienda() + " " + datiAzienda.getDescrizioneAzienda() + " "
                + datiAzienda.getIndirizzoAzienda());
        List<Certificazione> certificazioniAzienda = datiAzienda.getCertificazioniAzienda();
        for (Certificazione c : certificazioniAzienda) {
            c.stampaCertificazione();
        }
    }

    public DatiAzienda getDatiAzienda() {
        return datiAzienda;
    }

    public List<Prodotto> getProdottiOfferti() {
        return prodottiOfferti;
    }

    public boolean gestisciOrdineRicevuto() {
        return true;// TODO:Completa metodo con anche i parametri del metodo
    }


    public void setDatiAzienda(DatiAzienda datiAzienda) {
        this.datiAzienda = datiAzienda;
    }

    public void aggiungiCertificazione(Certificazione certificazione) {
        this.getDatiAzienda().getCertificazioniAzienda().add(certificazione);
    }

    public StatoAccreditamento getStatoAccreditamento() {
        return statoAccreditamento;
    }
    public void setStatoAccreditamento(StatoAccreditamento statoAccreditamento) {
        this.statoAccreditamento = statoAccreditamento;
    }
}