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

public class Main {

    public static void main(String[] args) {
        // Dummy certificazioni e liste vuote
        List<Certificazione> certificazioni = new ArrayList<>();
        List<Prodotto> prodotti1 = new ArrayList<>();
        List<Prodotto> prodotti2 = new ArrayList<>();

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
        prodotti1.add(new Prodotto(
            1, "Olio EVO Marche", "Olio extravergine d'oliva marchigiano", 12.5, 100, distributore1));
        prodotti1.add(new Prodotto(
            2, "Vincisgrassi", "Pasta tipica marchigiana", 8.0, 50, distributore1));
        prodotti1.add(new Prodotto(
            3, "Ciauscolo", "Salame spalmabile IGP", 15.0, 30, distributore1));
        prodotti1.add(new Prodotto(
            4, "Vino Rosso Piceno", "Vino DOC delle Marche", 10.0, 60, distributore1));

        prodotti2.add(new Prodotto(
            5, "Olio EVO Toscano", "Olio extravergine d'oliva toscano", 13.0, 90, distributore2));
        prodotti2.add(new Prodotto(
            6, "Pecorino Toscano", "Formaggio DOP", 18.0, 40, distributore2));
        prodotti2.add(new Prodotto(
            7, "Finocchiona", "Salame tipico toscano", 14.0, 35, distributore2));
        prodotti2.add(new Prodotto(
            8, "Vino Chianti", "Vino DOCG della Toscana", 11.0, 70, distributore2));

        // Pacchetti con 2 prodotti ciascuno
        Pacchetto pacchetto1 =
            new Pacchetto(
                distributore1, 1, "Box Marche", "Selezione tipica marchigiana", 20.0);
        pacchetto1.aggiungiElemento(prodotti1.get(0));
        pacchetto1.aggiungiElemento(prodotti1.get(1));
        distributore1.aggiungiPacchettoCatalogo(pacchetto1);

        Pacchetto pacchetto2 =
            new Pacchetto(
                distributore2, 2, "Box Toscana", "Selezione tipica toscana", 22.0);
        pacchetto2.aggiungiElemento(prodotti2.get(0));
        pacchetto2.aggiungiElemento(prodotti2.get(1));
        distributore2.aggiungiPacchettoCatalogo(pacchetto2);

        // Stampa di verifica
        System.out.println("Distributore 1: " + distributore1.getDatiAzienda().getNomeAzienda());
        pacchettoService.mostraPacchetti(distributore1);
        System.out.println("Distributore 2: " + distributore2.getDatiAzienda().getNomeAzienda());
        pacchettoService.mostraPacchetti(distributore2);
    }

}
