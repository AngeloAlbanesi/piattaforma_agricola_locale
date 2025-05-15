package it.unicam.cs.ids.piattaforma_agricola_locale;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DistributoreDiTipicita;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.TipoRuolo;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Pacchetto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;
import java.util.List;
import java.util.ArrayList;

import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.PacchettoService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.ProdottoService;

public class Main {

    public static void main(String[] args) {
        // Dummy certificazioni e liste vuote
        List<Certificazione> certificazioni = new ArrayList<>();
        List<Prodotto> prodotti1 = new ArrayList<>();
        List<Prodotto> prodotti2 = new ArrayList<>();
        ProdottoService prodottoService = new ProdottoService();
        PacchettoService pacchettoService = new PacchettoService();

        // DatiAzienda per i distributori
        DatiAzienda datiAzienda1 = new DatiAzienda(
            "Terre Marche", "IT12345678901", "Via Marche 1, Ancona", "Azienda di prodotti tipici marchigiani",
            "", "", "APPROVATO", "", certificazioni
        );
        DatiAzienda datiAzienda2 = new DatiAzienda(
            "Sapori Toscani", "IT98765432109", "Via Toscana 10, Firenze", "Azienda di prodotti tipici toscani",
            "", "", "APPROVATO", "", certificazioni
        );

        // Distributori
        DistributoreDiTipicita distributore1 =
            new DistributoreDiTipicita(
                "distr1", "Mario", "Rossi", "mario.rossi@marche.it", "pw1", "3331112222",
                datiAzienda1, prodotti1,
                TipoRuolo.DISTRIBUTORE_DI_TIPICITA, true
            );
        DistributoreDiTipicita distributore2 =
            new DistributoreDiTipicita(
                "distr2", "Luca", "Bianchi", "luca.bianchi@toscana.it", "pw2", "3332223333",
                datiAzienda2, prodotti2,
                TipoRuolo.DISTRIBUTORE_DI_TIPICITA, true
            );

        // 4 prodotti per ciascun distributore
        prodottoService.creaProdotto("Olio EVO Marche", "Olio extravergine d'oliva marchigiano", 12.5, 100, distributore1);
        prodottoService.creaProdotto("Vincisgrassi", "Pasta tipica marchigiana", 8.0, 50, distributore1);
        prodottoService.creaProdotto("Ciauscolo", "Salame spalmabile IGP", 15.0, 30, distributore1);
        prodottoService.creaProdotto( "Vino Rosso Piceno", "Vino DOC delle Marche", 10.0, 60, distributore1);

        prodottoService.creaProdotto("Olio EVO Toscano", "Olio extravergine d'oliva toscano", 13.0, 90, distributore2);
        prodottoService.creaProdotto("Pecorino Toscano", "Formaggio DOP", 18.0, 40, distributore2);
        prodottoService.creaProdotto("Finocchiona", "Salame tipico toscano", 14.0, 35, distributore2);
        prodottoService.creaProdotto("Vino Chianti", "Vino DOCG della Toscana", 11.0, 70, distributore2);

        // Pacchetti con 2 prodotti ciascuno
        pacchettoService.creaPacchetto(distributore1,"Box Marche", "Selezione tipica Marchigiana",3,20.0);
        pacchettoService.creaPacchetto(distributore2,"Box Toscano", "Selezione tipica Toscana",2,25.0);

        pacchettoService.aggiungiProdottoAlPacchetto(distributore1.getPacchettiOfferti().get(0),distributore1.getProdottiOfferti().get(0) );
        pacchettoService.aggiungiProdottoAlPacchetto(distributore1.getPacchettiOfferti().get(0),distributore1.getProdottiOfferti().get(1) );
        pacchettoService.aggiungiProdottoAlPacchetto(distributore1.getPacchettiOfferti().get(0),distributore1.getProdottiOfferti().get(2) );


        pacchettoService.aggiungiProdottoAlPacchetto(distributore2.getPacchettiOfferti().get(0),distributore2.getProdottiOfferti().get(0) );
        pacchettoService.aggiungiProdottoAlPacchetto(distributore2.getPacchettiOfferti().get(0),distributore2.getProdottiOfferti().get(1) );
        pacchettoService.aggiungiProdottoAlPacchetto(distributore2.getPacchettiOfferti().get(0),distributore2.getProdottiOfferti().get(2) );



        // Stampa di verifica
        System.out.println("Distributore 1: " + distributore1.getDatiAzienda().getNomeAzienda());
        pacchettoService.mostraPacchetti(distributore1);
        System.out.println(" ");
        System.out.println("Distributore 2: " + distributore2.getDatiAzienda().getNomeAzienda());
        pacchettoService.mostraPacchetti(distributore2);
        System.out.println(" ");

        pacchettoService.aggiungiQuantitaPacchetto(distributore1.getPacchettiOfferti().get(0),5);
        System.out.println("Distributore 1: " + distributore1.getDatiAzienda().getNomeAzienda());
        pacchettoService.mostraPacchetti(distributore1);
        /*
        pacchettoService.rimuoviProdottoDalPacchetto(distributore1.getPacchettiOfferti().get(0),distributore1.getProdottiOfferti().get(0) );
        System.out.println("Distributore 1: " + distributore1.getDatiAzienda().getNomeAzienda());
        pacchettoService.mostraPacchetti(distributore1);

        pacchettoService.rimuoviPacchettoCatalogo(distributore2.getPacchettiOfferti().get(0));
        System.out.println("Distributore 2: " + distributore2.getDatiAzienda().getNomeAzienda());
        pacchettoService.mostraPacchetti(distributore2);
        System.out.println(" ");
        */

       /*
        prodottoService.mostraProdotti(distributore1);
        prodottoService.aggiornaQuantitaProdotto(distributore1.getProdottiOfferti().get(0),19);
        prodottoService.aggiungiQuantitaProdotto(distributore1.getProdottiOfferti().get(1),3);
        prodottoService.rimuoviQuantitaProdotto(distributore1.getProdottiOfferti().get(2),60);
        prodottoService.rimuoviQuantitaProdotto(distributore1.getProdottiOfferti().get(3),51);

        System.out.println(" ");
        prodottoService.mostraProdotti(distributore1);
        */




    }


}
