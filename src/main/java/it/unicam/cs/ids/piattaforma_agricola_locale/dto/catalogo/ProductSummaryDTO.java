/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.TipoOrigineProdotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.StatoVerificaValori;

/**
 * DTO for product summary information, used in product lists.
 * Contains only essential fields for displaying products in a catalog view.
 */
public class ProductSummaryDTO {
    
    private Long idProdotto;
    private String nome;
    private double prezzo;
    private int quantitaDisponibile;
    private StatoVerificaValori statoVerifica;
    private TipoOrigineProdotto tipoOrigine;
    private String nomeVenditore;
    private Long idVenditore;

    public ProductSummaryDTO() {
    }

    public ProductSummaryDTO(Long idProdotto, String nome, double prezzo, int quantitaDisponibile, 
                           StatoVerificaValori statoVerifica, TipoOrigineProdotto tipoOrigine, 
                           String nomeVenditore, Long idVenditore) {
        this.idProdotto = idProdotto;
        this.nome = nome;
        this.prezzo = prezzo;
        this.quantitaDisponibile = quantitaDisponibile;
        this.statoVerifica = statoVerifica;
        this.tipoOrigine = tipoOrigine;
        this.nomeVenditore = nomeVenditore;
        this.idVenditore = idVenditore;
    }

    public Long getIdProdotto() {
        return idProdotto;
    }

    public void setIdProdotto(Long idProdotto) {
        this.idProdotto = idProdotto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }

    public int getQuantitaDisponibile() {
        return quantitaDisponibile;
    }

    public void setQuantitaDisponibile(int quantitaDisponibile) {
        this.quantitaDisponibile = quantitaDisponibile;
    }

    public StatoVerificaValori getStatoVerifica() {
        return statoVerifica;
    }

    public void setStatoVerifica(StatoVerificaValori statoVerifica) {
        this.statoVerifica = statoVerifica;
    }

    public TipoOrigineProdotto getTipoOrigine() {
        return tipoOrigine;
    }

    public void setTipoOrigine(TipoOrigineProdotto tipoOrigine) {
        this.tipoOrigine = tipoOrigine;
    }

    public String getNomeVenditore() {
        return nomeVenditore;
    }

    public void setNomeVenditore(String nomeVenditore) {
        this.nomeVenditore = nomeVenditore;
    }

    public Long getIdVenditore() {
        return idVenditore;
    }

    public void setIdVenditore(Long idVenditore) {
        this.idVenditore = idVenditore;
    }
}