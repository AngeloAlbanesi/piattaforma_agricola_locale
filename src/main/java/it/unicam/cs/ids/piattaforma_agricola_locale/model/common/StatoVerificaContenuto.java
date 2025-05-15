package it.unicam.cs.ids.piattaforma_agricola_locale.model.common;

public interface StatoVerificaContenuto {
    public void approva(ElementoVerificabile elemento);
    public void respingi (ElementoVerificabile elemento);
    public void richiediModifica (ElementoVerificabile elemento);
    public StatoVerificaValori getNomeStato();

}