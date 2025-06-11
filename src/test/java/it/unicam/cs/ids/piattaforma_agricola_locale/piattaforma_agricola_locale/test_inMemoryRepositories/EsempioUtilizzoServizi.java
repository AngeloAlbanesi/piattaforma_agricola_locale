// package it.unicam.cs.ids.piattaforma_agricola_locale.piattaforma_agricola_locale.test_inMemoryRepositories;

// import java.util.ArrayList;
// import java.util.Optional;

// import it.unicam.cs.ids.piattaforma_agricola_locale.model.carrello.Carrello;
// import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
// import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
// import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;
// import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;
// import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DistributoreDiTipicita;
// import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.TipoRuolo;
// import it.unicam.cs.ids.piattaforma_agricola_locale.exception.OrdineException;
// import it.unicam.cs.ids.piattaforma_agricola_locale.service.impl.CarrelloService;
// import it.unicam.cs.ids.piattaforma_agricola_locale.service.impl.OrdineService;
// import it.unicam.cs.ids.piattaforma_agricola_locale.service.impl.PacchettoService;
// import it.unicam.cs.ids.piattaforma_agricola_locale.service.impl.ProdottoService;
// import it.unicam.cs.ids.piattaforma_agricola_locale.service.impl.RigaOrdineService;

// /**
//  * Test completo che dimostra l'utilizzo dei repository pattern implementati.
//  * 
//  * MODIFICHE APPORTATE:
//  * - Rimossa la creazione diretta di prodotti e pacchetti nel test
//  * - Integrati ProdottoService e PacchettoService per accedere ai repository
//  * esistenti
//  * - I prodotti e pacchetti vengono ora creati tramite i servizi che li salvano
//  * automaticamente nei repository
//  * - Implementato recupero di prodotti e pacchetti dai repository per l'utilizzo
//  * nel carrello
//  * - Aggiunta dimostrazione delle funzionalità di ricerca nei repository
//  * - Test esteso per includere sia prodotti che pacchetti nel carrello
//  * 
//  * Il test dimostra:
//  * 1. Popolamento dei repository tramite i servizi
//  * 2. Recupero di prodotti e pacchetti dai repository
//  * 3. Utilizzo dei dati recuperati per popolare i carrelli
//  * 4. Creazione di ordini basati sui contenuti del carrello
//  * 5. Funzionalità di ricerca per nome nei repository
//  */

// public class EsempioUtilizzoServizi {

//     public static void main(String[] args) {
//         System.out.println("=== Esempio Utilizzo Servizi ===\n");

//         // Inizializza i servizi
//         CarrelloService carrelloService = new CarrelloService();
//         OrdineService ordineService = new OrdineService();
//         RigaOrdineService rigaOrdineService = new RigaOrdineService();
//         ProdottoService prodottoService = new ProdottoService();
//         PacchettoService pacchettoService = new PacchettoService();

//         // Crea utenti di test
//         Acquirente acquirente = new Acquirente(1, "Mario", "Rossi", "mario.rossi@email.com",
//                 "hashedPassword", "123456789", TipoRuolo.ACQUIRENTE, true);

//         // Crea un DistributoreDiTipicita (classe concreta che estende Venditore)
//         DatiAzienda datiAzienda = new DatiAzienda(1,"Azienda Agricola", "12345678901",
//                 "Via Campagna 10", "Azienda agricola specializzata", "logo.jpg", "www.azienda.com");
//         DistributoreDiTipicita venditore = new DistributoreDiTipicita(1, "Nome", "Cognome",
//                 "farm@email.com", "hashedPassword", "987654321", datiAzienda,
//                 new ArrayList<>(), TipoRuolo.DISTRIBUTORE_DI_TIPICITA, true);

//         // === POPOLAMENTO REPOSITORY DI TEST ===
//         System.out.println("--- Popolamento Repository ---");

//         // Crea prodotti tramite ProdottoService (che li salva automaticamente nel
//         // repository)
//         prodottoService.creaProdotto("Pomodori Bio", "Pomodori biologici freschi", 4.50, 50, venditore);
//         prodottoService.creaProdotto("Insalata", "Insalata fresca di stagione", 2.30, 30, venditore);
//         prodottoService.creaProdotto("Salame", "Salame artigianale", 8.00, 20, venditore);

//         // Crea pacchetti tramite PacchettoService (che li salva automaticamente nel
//         // repository)
//         pacchettoService.creaPacchetto(venditore, "Pacchetto Verdure", "Assortimento di verdure fresche", 5, 12.00);
//         pacchettoService.creaPacchetto(venditore, "Pacchetto Salumi", "Assortimento di salumi locali", 3, 25.00);

//         System.out.println("✓ Repository popolati con prodotti e pacchetti");

//         // === RECUPERO PRODOTTI DAI REPOSITORY ===
//         System.out.println("--- Recupero da Repository ---");

//         // Recupera tutti i prodotti dal repository
//         var prodottiDisponibili = prodottoService.getProdottoRepository().mostraTuttiIProdotti();
//         System.out.println("✓ Prodotti trovati nel repository: " + prodottiDisponibili.size());

//         // Recupera tutti i pacchetti dal repository
//         var pacchettiDisponibili = pacchettoService.getRepository().mostraTuttiIPacchetti();
//         System.out.println("✓ Pacchetti trovati nel repository: " + pacchettiDisponibili.size());

//         // Seleziona alcuni prodotti per il test (primi 2)
//         Prodotto primoP = !prodottiDisponibili.isEmpty() ? prodottiDisponibili.get(0) : null;
//         Prodotto secondoP = prodottiDisponibili.size() > 1 ? prodottiDisponibili.get(1) : null;

//         if (primoP != null) {
//             System.out.println("✓ Primo prodotto: " + primoP.getNome() + " - €" + primoP.getPrezzo());
//         }
//         if (secondoP != null) {
//             System.out.println("✓ Secondo prodotto: " + secondoP.getNome() + " - €" + secondoP.getPrezzo());
//         }

//         System.out.println("\n--- Test CarrelloService ---");

//         // Crea un carrello per l'acquirente
//         carrelloService.creaNuovoCarrello(acquirente);
//         System.out.println("✓ Carrello creato per: " + acquirente.getNome());

//         // Aggiungi prodotti al carrello usando i prodotti dal repository
//         if (primoP != null) {
//             carrelloService.aggiungiElementoAlCarrello(acquirente, primoP, 3);
//             System.out.println("✓ Aggiunto al carrello: " + primoP.getNome() + " x3");
//         }
//         if (secondoP != null) {
//             carrelloService.aggiungiElementoAlCarrello(acquirente, secondoP, 2);
//             System.out.println("✓ Aggiunto al carrello: " + secondoP.getNome() + " x2");
//         }

//         // Calcola il prezzo totale del carrello
//         double prezzoTotaleCarrello = carrelloService.calcolaPrezzoTotaleCarrello(acquirente);
//         System.out.println("✓ Prezzo totale carrello: €" + String.format("%.2f", prezzoTotaleCarrello));

//         // Mostra contenuto carrello
//         Optional<Carrello> carrelloOpt = carrelloService.getCarrelloAcquirente(acquirente);
//         if (carrelloOpt.isPresent()) {
//             Carrello c = carrelloOpt.get();
//             System.out.println("✓ Elementi nel carrello: " + c.getElementiCarrello().size());
//         }

//         System.out.println("\n--- Test OrdineService ---");

//         // Crea un nuovo ordine
//         try {
//             ordineService.creaNuovoOrdine(acquirente);
//             System.out.println("✓ Nuovo ordine creato per: " + acquirente.getNome());
//         } catch (OrdineException e) {
//             System.err.println("✗ Errore durante la creazione dell'ordine: " + e.getMessage());
//             return;
//         }

//         // Recupera l'ordine creato (simuliamo che sia il primo)
//         if (!ordineService.getTuttiGliOrdini().isEmpty()) {
//             Ordine ordine = ordineService.getTuttiGliOrdini().get(0);

//             // Aggiungi righe all'ordine dal carrello
//             if (carrelloOpt.isPresent()) {
//                 Carrello c = carrelloOpt.get();
//                 c.getElementiCarrello().forEach(elemento -> {
//                     rigaOrdineService.creaRigaOrdine(ordine, elemento.getAcquistabile(), elemento.getQuantita());
//                 });
//                 System.out.println("✓ Righe ordine create dal carrello");
//             }

//             // Calcola il prezzo dell'ordine
//             try {
//                 ordineService.calcolaPrezzoOrdine(ordine);
//                 System.out.println("✓ Prezzo ordine calcolato: €" + String.format("%.2f", ordine.getImportoTotale()));
//             } catch (OrdineException e) {
//                 System.err.println("✗ Errore durante il calcolo del prezzo: " + e.getMessage());
//             }

//             // Mostra stato ordine
//             System.out.println("✓ Stato ordine: " + ordine.getStatoOrdine());

//             // Mostra righe ordine
//             System.out.println("✓ Numero righe ordine: " + ordine.getRigheOrdine().size());
//         }

//         System.out.println("\n--- Test Statistiche ---");

//         // Mostra statistiche finali
//         System.out.println("• Totale carrelli: " + carrelloService.getTuttiICarrelli().size());
//         System.out.println("• Totale ordini: " + ordineService.getTuttiGliOrdini().size());
//         System.out.println("• Totale righe ordine: " + rigaOrdineService.getTutteLeRigheOrdine().size());
//         System.out.println("• Totale prodotti in repository: "
//                 + prodottoService.getProdottoRepository().mostraTuttiIProdotti().size());
//         System.out.println(
//                 "• Totale pacchetti in repository: " + pacchettoService.getRepository().mostraTuttiIPacchetti().size());

//         System.out.println("\n--- Dettagli Repository ---");
//         System.out.println("=== Prodotti disponibili ===");
//         prodottoService.getProdottoRepository().mostraTuttiIProdotti().forEach(p -> System.out
//                 .println("• " + p.getNome() + " - €" + p.getPrezzo() + " (Qta: " + p.getQuantitaDisponibile() + ")"));

//         System.out.println("\n=== Pacchetti disponibili ===");
//         pacchettoService.getRepository().mostraTuttiIPacchetti().forEach(pac -> System.out.println(
//                 "• " + pac.getNome() + " - €" + pac.getPrezzo() + " (Qta: " + pac.getQuantitaDisponibile() + ")"));

//         // Test pulizia carrello
//         System.out.println("\n--- Test Pulizia Carrello ---");
//         carrelloService.svuotaCarrello(acquirente);
//         System.out.println("✓ Carrello svuotato");

//         Optional<Carrello> carrelloVuoto = carrelloService.getCarrelloAcquirente(acquirente);
//         if (carrelloVuoto.isPresent()) {
//             System.out.println(
//                     "✓ Elementi nel carrello dopo pulizia: " + carrelloVuoto.get().getElementiCarrello().size());
//         }

//         // === TEST AVANZATO CON PACCHETTI ===
//         System.out.println("\n--- Test Avanzato: Carrello con Pacchetti ---");

//         // Recupera un pacchetto dal repository e aggiungilo al carrello
//         if (!pacchettiDisponibili.isEmpty()) {
//             var primoPacchetto = pacchettiDisponibili.get(0);
//             carrelloService.aggiungiElementoAlCarrello(acquirente, primoPacchetto, 1);
//             System.out.println("✓ Aggiunto pacchetto al carrello: " + primoPacchetto.getNome() + " - €"
//                     + primoPacchetto.getPrezzo());

//             // Verifica il nuovo totale del carrello
//             double nuovoTotale = carrelloService.calcolaPrezzoTotaleCarrello(acquirente);
//             System.out.println("✓ Nuovo prezzo totale carrello: €" + String.format("%.2f", nuovoTotale));
//         }

//         // === RICERCA PER NOME NEI REPOSITORY ===
//         System.out.println("\n--- Test Ricerca nei Repository ---");

//         // Cerca prodotti per nome
//         var prodottiPomodori = prodottoService.getProdottoRepository().findByNome("Pomodori Bio");
//         System.out.println("✓ Prodotti trovati con nome 'Pomodori Bio': " + prodottiPomodori.size());

//         // Cerca pacchetti per nome
//         var pacchettiVerdure = pacchettoService.getRepository().findByNome("Pacchetto Verdure");
//         System.out.println("✓ Pacchetti trovati con nome 'Pacchetto Verdure': " + pacchettiVerdure.size());

//         System.out.println("\n=== Test Completato con Successo! ===");
//     }
// }
