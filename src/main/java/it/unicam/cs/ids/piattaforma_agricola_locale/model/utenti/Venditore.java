/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti;

import java.util.ArrayList;
import java.util.List;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import jakarta.persistence.*;

@Entity
@Table(name = "venditori")
public abstract class Venditore extends Utente {
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dati_azienda")
    private DatiAzienda datiAzienda;
    @OneToMany(mappedBy = "venditore", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Prodotto> prodottiOfferti;
    @Enumerated(EnumType.STRING)
    @Column(name = "stato_accreditamento")
    private StatoAccreditamento statoAccreditamento;

    protected Venditore() {
        super();
        // Default constructor for JPA
    }

    public Venditore(String nome, String cognome, String email, String passwordHash,
            String numeroTelefono, DatiAzienda datiAzienda, TipoRuolo tipoRuolo) {
        super(nome, cognome, email, passwordHash, numeroTelefono, tipoRuolo);
        this.datiAzienda = datiAzienda;
        this.prodottiOfferti = new ArrayList<>();
        this.statoAccreditamento = StatoAccreditamento.PENDING;
    }



    public DatiAzienda getDatiAzienda() {
        return datiAzienda;
    }

    public List<Prodotto> getProdottiOfferti() {
        return prodottiOfferti;
    }

    public void setDatiAzienda(DatiAzienda datiAzienda) {
        this.datiAzienda = datiAzienda;
    }

    public void aggiungiCertificazione(Certificazione certificazione) {
        this.getDatiAzienda().getCertificazioniAzienda().add(certificazione);
    }

    public void aggiungiProdottoOfferto(Prodotto prodotto) {
        if (prodotto != null && !this.prodottiOfferti.contains(prodotto)) {
            this.prodottiOfferti.add(prodotto);
        }
    }

    public StatoAccreditamento getStatoAccreditamento() {
        return statoAccreditamento;
    }

    public void setStatoAccreditamento(StatoAccreditamento statoAccreditamento) {
        this.statoAccreditamento = statoAccreditamento;
    }
}