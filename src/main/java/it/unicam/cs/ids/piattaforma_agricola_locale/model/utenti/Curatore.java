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
    public Curatore(String idUtente, String nome, String cognome, String email, String passwordHash, String numeroTelefono, TipoRuolo tipoRuolo, boolean isAttivo) {
        super(nome, cognome, email, passwordHash, numeroTelefono, idUtente, tipoRuolo, isAttivo);
    }
    public void approvaAzienda(Venditore venditore,String feedbackVerifica){
        if(venditore.getDatiAzienda().getStatoVerifica()==StatoVerificaValori.IN_REVISIONE){
            venditore.getDatiAzienda().setStatoVerifica(StatoVerificaValori.APPROVATO);
            venditore.getDatiAzienda() .setFeedbackVerifica(feedbackVerifica);
        }
    }

    public void respingiAzienda (Venditore venditore,String feedbackVerifica) {
        if (venditore.getDatiAzienda().getStatoVerifica() == StatoVerificaValori.IN_REVISIONE) {
           venditore.getDatiAzienda().setStatoVerifica(StatoVerificaValori.RESPINTO);
            venditore.getDatiAzienda().setFeedbackVerifica(feedbackVerifica);
        }
    }

    //METODO PER LA CLI da eliminare
    /*
    public void revisionaProdotto(Prodotto prodotto) {
        System.out.println(prodotto.getNome() +" "+prodotto.getDescrizione() +" " +prodotto.getStatoVerifica());
        System.out.println("Approvare questo prodotto? (S/N)");
        Scanner scanner = new Scanner(System.in);
        String conferma = scanner.nextLine();
        if (conferma.equals("s")){
            prodotto.setStatoVerifica(StatoVerificaValori.APPROVATO);
        }else{
            prodotto.setStatoVerifica(StatoVerificaValori.RESPINTO);
        }
    } */
    public void approvaProdotto(Prodotto prodotto,String feedbackVerifica){
        if(prodotto.getStatoVerifica()==StatoVerificaValori.IN_REVISIONE){
            prodotto.setStatoVerifica(StatoVerificaValori.APPROVATO);
            prodotto.setFeedbackVerifica(feedbackVerifica);
        }
    }

    public void respingiProdotto (Prodotto prodotto,String feedbackVerifica) {
        if (prodotto.getStatoVerifica() == StatoVerificaValori.IN_REVISIONE) {
            prodotto.setStatoVerifica(StatoVerificaValori.RESPINTO);
            prodotto.setFeedbackVerifica(feedbackVerifica);
        }
    }



    public void verificaIngredientiLocaliTrasformatore(Ingrediente ingrediente) {
        //TODO: Implementa la logica per verificare gli ingredienti locali
    }
}