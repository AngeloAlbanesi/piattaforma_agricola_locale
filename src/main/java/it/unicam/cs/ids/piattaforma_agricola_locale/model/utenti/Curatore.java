/*
 *   Copyright (c) 2025 
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Ingrediente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;

public class Curatore extends Utente {
    public Curatore(String nome, String cognome, String email, String passwordHash, String numeroTelefono,
            String idUtente, TipoRuolo tipoRuolo, boolean isAttivo) {
        super(nome, cognome, email, passwordHash, numeroTelefono, idUtente, tipoRuolo, isAttivo);
    }

    public void revisionaDatiAzienda(DatiAzienda datiAzienda) {
        //TODO: Implementa la logica per revisionare i dati dell'azienda
    }
    
    public void revisionaProdotto(Prodotto prodotto) {
        //TODO
    }

    public void revisionaCertificazione(Certificazione certificazione) {
        //TODO  
    }

    public void verificaIngredientiLocaliTrasformatore(Ingrediente ingrediente) {
        //TODO: Implementa la logica per verificare gli ingredienti locali
    }
}