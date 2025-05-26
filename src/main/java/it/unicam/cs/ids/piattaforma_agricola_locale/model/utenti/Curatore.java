/*
 *   Copyright (c) 2025 
 *   All rights reserved.
 */
package it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Ingrediente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.StatoVerificaValori;

import java.util.Scanner;

public class Curatore extends Utente {
    public Curatore(int idUtente, String nome, String cognome, String email, String passwordHash, String numeroTelefono,
            TipoRuolo tipoRuolo, boolean isAttivo) {
        super(idUtente,nome, cognome, email, passwordHash, numeroTelefono, tipoRuolo, isAttivo);
    }

    // METODO PER LA CLI da eliminare
    /*
     * public void revisionaProdotto(Prodotto prodotto) {
     * System.out.println(prodotto.getNome() +" "+prodotto.getDescrizione() +" "
     * +prodotto.getStatoVerifica());
     * System.out.println("Approvare questo prodotto? (S/N)");
     * Scanner scanner = new Scanner(System.in);
     * String conferma = scanner.nextLine();
     * if (conferma.equals("s")){
     * prodotto.setStatoVerifica(StatoVerificaValori.APPROVATO);
     * }else{
     * prodotto.setStatoVerifica(StatoVerificaValori.RESPINTO);
     * }
     * }
     */
    
}