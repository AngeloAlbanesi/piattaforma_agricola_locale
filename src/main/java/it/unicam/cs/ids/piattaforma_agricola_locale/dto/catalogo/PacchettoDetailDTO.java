/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.utente.UserPublicDTO;
import java.util.List;

/**
 * DTO for detailed package information, used in single package view.
 * Contains all package fields plus related entities like distributor and included items.
 */
public class PacchettoDetailDTO {
    
    private Long idPacchetto;
    private String nome;
    private String descrizione;
    private double prezzoPacchetto;
    private int quantitaDisponibile;
    private UserPublicDTO distributore;
    private List<ElementoPacchettoDTO> elementiInclusi;

    public PacchettoDetailDTO() {
    }

    public PacchettoDetailDTO(Long idPacchetto, String nome, String descrizione, 
                            double prezzoPacchetto, int quantitaDisponibile, 
                            UserPublicDTO distributore, List<ElementoPacchettoDTO> elementiInclusi) {
        this.idPacchetto = idPacchetto;
        this.nome = nome;
        this.descrizione = descrizione;
        this.prezzoPacchetto = prezzoPacchetto;
        this.quantitaDisponibile = quantitaDisponibile;
        this.distributore = distributore;
        this.elementiInclusi = elementiInclusi;
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

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
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

    public UserPublicDTO getDistributore() {
        return distributore;
    }

    public void setDistributore(UserPublicDTO distributore) {
        this.distributore = distributore;
    }

    public List<ElementoPacchettoDTO> getElementiInclusi() {
        return elementiInclusi;
    }

    public void setElementiInclusi(List<ElementoPacchettoDTO> elementiInclusi) {
        this.elementiInclusi = elementiInclusi;
    }

    /**
     * Gets the total number of items included in the package.
     * 
     * @return the total item count
     */
    public int getTotalElementi() {
        return elementiInclusi != null ? elementiInclusi.size() : 0;
    }
}

/**
 * DTO representing an item included in a package.
 */
class ElementoPacchettoDTO {
    private String tipoElemento; // "PRODOTTO", "EVENTO"
    private Long idElemento;
    private String nomeElemento;
    private String descrizioneElemento;
    private double prezzoElemento;

    public ElementoPacchettoDTO() {
    }

    public ElementoPacchettoDTO(String tipoElemento, Long idElemento, String nomeElemento, 
                              String descrizioneElemento, double prezzoElemento) {
        this.tipoElemento = tipoElemento;
        this.idElemento = idElemento;
        this.nomeElemento = nomeElemento;
        this.descrizioneElemento = descrizioneElemento;
        this.prezzoElemento = prezzoElemento;
    }

    public String getTipoElemento() {
        return tipoElemento;
    }

    public void setTipoElemento(String tipoElemento) {
        this.tipoElemento = tipoElemento;
    }

    public Long getIdElemento() {
        return idElemento;
    }

    public void setIdElemento(Long idElemento) {
        this.idElemento = idElemento;
    }

    public String getNomeElemento() {
        return nomeElemento;
    }

    public void setNomeElemento(String nomeElemento) {
        this.nomeElemento = nomeElemento;
    }

    public String getDescrizioneElemento() {
        return descrizioneElemento;
    }

    public void setDescrizioneElemento(String descrizioneElemento) {
        this.descrizioneElemento = descrizioneElemento;
    }

    public double getPrezzoElemento() {
        return prezzoElemento;
    }

    public void setPrezzoElemento(double prezzoElemento) {
        this.prezzoElemento = prezzoElemento;
    }
}