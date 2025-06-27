/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo;

/**
 * DTO for package summary information, used in package lists.
 * Contains only essential fields for displaying packages in a catalog view.
 */
public class PacchettoSummaryDTO {
    
    private Long idPacchetto;
    private String nome;
    private double prezzoPacchetto;
    private int quantitaDisponibile;
    private String nomeDistributore;
    private Long idDistributore;
    private int numeroElementi;

    public PacchettoSummaryDTO() {
    }

    public PacchettoSummaryDTO(Long idPacchetto, String nome, double prezzoPacchetto, 
                             int quantitaDisponibile, String nomeDistributore, 
                             Long idDistributore, int numeroElementi) {
        this.idPacchetto = idPacchetto;
        this.nome = nome;
        this.prezzoPacchetto = prezzoPacchetto;
        this.quantitaDisponibile = quantitaDisponibile;
        this.nomeDistributore = nomeDistributore;
        this.idDistributore = idDistributore;
        this.numeroElementi = numeroElementi;
    }

    public Long getIdPacchetto() {
        return idPacchetto;
    }

    public void setIdPacchetto(Long idPacchetto) {
        this.idPacchetto = idPacchetto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getPrezzoPacchetto() {
        return prezzoPacchetto;
    }

    public void setPrezzoPacchetto(double prezzoPacchetto) {
        this.prezzoPacchetto = prezzoPacchetto;
    }

    public int getQuantitaDisponibile() {
        return quantitaDisponibile;
    }

    public void setQuantitaDisponibile(int quantitaDisponibile) {
        this.quantitaDisponibile = quantitaDisponibile;
    }

    public String getNomeDistributore() {
        return nomeDistributore;
    }

    public void setNomeDistributore(String nomeDistributore) {
        this.nomeDistributore = nomeDistributore;
    }

    public Long getIdDistributore() {
        return idDistributore;
    }

    public void setIdDistributore(Long idDistributore) {
        this.idDistributore = idDistributore;
    }

    public int getNumeroElementi() {
        return numeroElementi;
    }

    public void setNumeroElementi(int numeroElementi) {
        this.numeroElementi = numeroElementi;
    }
}