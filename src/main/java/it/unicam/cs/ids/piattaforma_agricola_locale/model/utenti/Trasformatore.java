 /*
  *   Copyright (c) 2025
  *   All rights reserved.
  */
 package it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti;

 import java.util.List;

 import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;


 public class Trasformatore extends Venditore {
//  Costruttore overload per factory (solo parametri base Venditore)
     public Trasformatore(String idUtente, String nome, String cognome, String email, String passwordHash,
            String numeroTelefono, DatiAzienda datiAzienda,
           java.util.List<Prodotto> prodottiOfferti, TipoRuolo tipoRuolo, boolean isAttivo) {
        super(idUtente, nome, cognome, email, passwordHash, numeroTelefono, datiAzienda, prodottiOfferti, tipoRuolo, isAttivo);

    }

 }