package it.unicam.cs.ids.piattaforma_agricola_locale.piattaforma_agricola_locale.test_inMemoryRepositories;

import java.util.ArrayList;
import java.util.Optional;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.carrello.carrello;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DistributoreDiTipicita;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.TipoRuolo;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.impl.CarrelloService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.impl.OrdineService;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.impl.RigaOrdineService;


public class EsempioUtilizzoServizi {

    public static void main(String[] args) {
        System.out.println("=== Esempio Utilizzo Servizi ===\n");

        // Inizializza i servizi
        CarrelloService carrelloService = new CarrelloService();
        OrdineService ordineService = new OrdineService();
        RigaOrdineService rigaOrdineService = new RigaOrdineService();

        // Crea utenti di test
        Acquirente acquirente = new Acquirente(1, "Mario", "Rossi", "mario.rossi@email.com",
                "hashedPassword", "123456789", TipoRuolo.ACQUIRENTE, true);

        // Crea un DistributoreDiTipicita (classe concreta che estende Venditore)
        DatiAzienda datiAzienda = new DatiAzienda("Azienda Agricola", "12345678901",
                "Via Campagna 10", "Azienda agricola specializzata", "logo.jpg", "www.azienda.com");
        DistributoreDiTipicita venditore = new DistributoreDiTipicita(1, "Nome", "Cognome",
                "farm@email.com", "hashedPassword", "987654321", datiAzienda,
                new ArrayList<>(), TipoRuolo.DISTRIBUTORE_DI_TIPICITA, true);

        // Crea prodotti di test
        Prodotto pomodori = new Prodotto(1, "Pomodori Bio", "Pomodori biologici freschi", 4.50, 50, venditore);
        Prodotto insalata = new Prodotto(2, "Insalata", "Insalata fresca di stagione", 2.30, 30, venditore);
        Prodotto salame = new Prodotto(3, "Salame", "Salame artigianale", 8.00, 20, venditore);

        System.out.println("--- Test CarrelloService ---");

        // Crea un carrello per l'acquirente
        carrello carrelloAcquirente = carrelloService.creaNuovoCarrello(acquirente);
        System.out.println("✓ Carrello creato per: " + acquirente.getNome());

        // Aggiungi prodotti al carrello
        carrelloService.aggiungiElementoAlCarrello(acquirente, pomodori, 3);
        carrelloService.aggiungiElementoAlCarrello(acquirente, insalata, 2);
        //carrelloService.aggiungiElementoAlCarrello(acquirente, salame, 1);
        System.out.println("✓ Prodotti aggiunti al carrello");

        // Calcola il prezzo totale del carrello
        double prezzoTotaleCarrello = carrelloService.calcolaPrezzoTotaleCarrello(acquirente);
        System.out.println("✓ Prezzo totale carrello: €" + String.format("%.2f", prezzoTotaleCarrello));

        // Mostra contenuto carrello
        Optional<carrello> carrelloOpt = carrelloService.getCarrelloAcquirente(acquirente);
        if (carrelloOpt.isPresent()) {
            carrello c = carrelloOpt.get();
            System.out.println("✓ Elementi nel carrello: " + c.getElementiCarrello().size());
        }

        System.out.println("\n--- Test OrdineService ---");

        // Crea un nuovo ordine
        ordineService.creaNuovoOrdine(acquirente);
        System.out.println("✓ Nuovo ordine creato per: " + acquirente.getNome());

        // Recupera l'ordine creato (simuliamo che sia il primo)
        if (!ordineService.getTuttiGliOrdini().isEmpty()) {
            Ordine ordine = ordineService.getTuttiGliOrdini().get(0);

            // Aggiungi righe all'ordine dal carrello
            if (carrelloOpt.isPresent()) {
                carrello c = carrelloOpt.get();
                c.getElementiCarrello().forEach(elemento -> {
                    rigaOrdineService.creaRigaOrdine(ordine, elemento.getAcquistabile(), elemento.getQuantita());
                });
                System.out.println("✓ Righe ordine create dal carrello");
            }

            // Calcola il prezzo dell'ordine
            ordineService.calcolaPrezzoOrdine(ordine);
            System.out.println("✓ Prezzo ordine calcolato: €" + String.format("%.2f", ordine.getImportoTotale()));

            // Mostra stato ordine
            System.out.println("✓ Stato ordine: " + ordine.getStatoOrdine());

            // Mostra righe ordine
            System.out.println("✓ Numero righe ordine: " + ordine.getRigheOrdine().size());
        }

        System.out.println("\n--- Test Statistiche ---");

        // Mostra statistiche finali
        System.out.println("• Totale carrelli: " + carrelloService.getTuttiICarrelli().size());
        System.out.println("• Totale ordini: " + ordineService.getTuttiGliOrdini().size());
        System.out.println("• Totale righe ordine: " + rigaOrdineService.getTutteLeRigheOrdine().size());

        // Test pulizia carrello
        System.out.println("\n--- Test Pulizia Carrello ---");
        carrelloService.svuotaCarrello(acquirente);
        System.out.println("✓ Carrello svuotato");

        Optional<carrello> carrelloVuoto = carrelloService.getCarrelloAcquirente(acquirente);
        if (carrelloVuoto.isPresent()) {
            System.out.println(
                    "✓ Elementi nel carrello dopo pulizia: " + carrelloVuoto.get().getElementiCarrello().size());
        }

        System.out.println("\n=== Test Completato ===");
    }
}
