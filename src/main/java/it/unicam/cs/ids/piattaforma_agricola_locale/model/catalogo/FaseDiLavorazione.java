package it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo;

import java.util.ArrayList;
import java.util.List;

public class FaseDiLavorazione {
    private String nome;
    private String descrizione;
    private int ordineFase;
    private List<Ingrediente> ingredienti;
    private Prodotto prodottoRisultante;
}