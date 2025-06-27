/*
 *   Copyright (c) 2025
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.dto.catalogo;

/**
 * DTO representing an item included in a package.
 * This represents polymorphic Acquistabile items (Prodotto, Evento) in packages.
 */
public class ElementoPacchettoDTO {
    
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