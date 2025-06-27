package it.unicam.cs.ids.piattaforma_agricola_locale.model.common;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

public interface Acquistabile {
    public Long getId();
    public String getNome();
    public String getDescrizione();
    public double getPrezzo();
    public Venditore getVenditore();
}