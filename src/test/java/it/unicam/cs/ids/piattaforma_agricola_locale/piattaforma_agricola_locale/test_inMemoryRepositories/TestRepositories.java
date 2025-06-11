// package it.unicam.cs.ids.piattaforma_agricola_locale.piattaforma_agricola_locale.test_inMemoryRepositories;

// import java.util.ArrayList;
// import java.util.Date;
// import java.util.List;
// import java.util.Optional;

// import it.unicam.cs.ids.piattaforma_agricola_locale.model.carrello.Carrello;
// import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
// import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
// import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.RigaOrdine;
// import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.CarrelloRepository;
// import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.OrdineRepository;
// import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.RigaOrdineRepository;
// import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;
// import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;
// import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DistributoreDiTipicita;
// import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.TipoRuolo;

// /**
//  * Classe di test per dimostrare il funzionamento dei repository implementati
//  */
// public class TestRepositories {

//     public static void main(String[] args) {
//         System.out.println("=== Test Repository In-Memory ===\n");

//         // Test CarrelloRepository
//         testCarrelloRepository();

//         // Test OrdineRepository
//         testOrdineRepository();

//         // Test RigaOrdineRepository
//         testRigaOrdineRepository();
//     }

//     private static void testCarrelloRepository() {
//         System.out.println("--- Test CarrelloRepository ---");

//         CarrelloRepository carrelloRepo = new CarrelloRepository();

//         // Crea un acquirente di test
//         Acquirente acquirente = new Acquirente(1, "Mario", "Rossi", "mario.rossi@email.com",
//                 "hashedPassword", "123456789", TipoRuolo.ACQUIRENTE, true);

//         // Crea un carrello
//         Carrello carrelloTest = new Carrello(1, acquirente);

//         // Salva il carrello
//         carrelloRepo.save(carrelloTest);
//         System.out.println("✓ Carrello salvato con ID: " + carrelloTest.getIdCarrello());

//         // Cerca il carrello per ID
//         Optional<Carrello> carrelloTrovato = carrelloRepo.findById(1);
//         if (carrelloTrovato.isPresent()) {
//             System.out.println("✓ Carrello trovato per ID: " + carrelloTrovato.get().getIdCarrello());
//         }

//         // Cerca il carrello per acquirente
//         Optional<Carrello> carrelloPerAcquirente = carrelloRepo.findByAcquirente(acquirente);
//         if (carrelloPerAcquirente.isPresent()) {
//             System.out.println(
//                     "✓ Carrello trovato per acquirente: " + carrelloPerAcquirente.get().getAcquirente().getNome());
//         }

//         // Lista tutti i carrelli
//         List<Carrello> tuttiCarrelli = carrelloRepo.findAll();
//         System.out.println("✓ Numero totale carrelli: " + tuttiCarrelli.size());

//         System.out.println();
//     }

//     private static void testOrdineRepository() {
//         System.out.println("--- Test OrdineRepository ---");

//         OrdineRepository ordineRepo = new OrdineRepository();

//         // Crea un acquirente di test
//         Acquirente acquirente = new Acquirente(1, "Mario", "Rossi", "mario.rossi@email.com",
//                 "hashedPassword", "123456789", TipoRuolo.ACQUIRENTE, true);

//         // Crea un ordine
//         Ordine ordineTest = new Ordine(1, new Date(), acquirente);
//         ordineTest.setImportoTotale(50.0);

//         // Salva l'ordine
//         ordineRepo.save(ordineTest);
//         System.out.println("✓ Ordine salvato con ID: " + ordineTest.getIdOrdine());

//         // Cerca l'ordine per ID
//         Optional<Ordine> ordineTrovato = ordineRepo.findById(1);
//         if (ordineTrovato.isPresent()) {
//             System.out.println("✓ Ordine trovato per ID: " + ordineTrovato.get().getIdOrdine() +
//                     ", Importo: €" + ordineTrovato.get().getImportoTotale());
//         }

//         // Cerca ordini per acquirente
//         List<Ordine> ordiniAcquirente = ordineRepo.findByAcquirente(acquirente);
//         System.out.println("✓ Numero ordini per acquirente: " + ordiniAcquirente.size());

//         // Lista tutti gli ordini
//         List<Ordine> tuttiOrdini = ordineRepo.findAll();
//         System.out.println("✓ Numero totale ordini: " + tuttiOrdini.size());

//         System.out.println();
//     }

//     private static void testRigaOrdineRepository() {
//         System.out.println("--- Test RigaOrdineRepository ---");

//         RigaOrdineRepository rigaRepo = new RigaOrdineRepository();

//         // Crea dati di test
//         Acquirente acquirente = new Acquirente(1, "Mario", "Rossi", "mario.rossi@email.com",
//                 "hashedPassword", "123456789", TipoRuolo.ACQUIRENTE, true);

//         // Crea un DistributoreDiTipicita (classe concreta che estende Venditore)
//         DatiAzienda datiAzienda = new DatiAzienda(1, "Azienda Test", "12345678901",
//                 "Via Test 123", "Descrizione test", "logo.jpg", "www.test.com");
//         DistributoreDiTipicita venditore = new DistributoreDiTipicita(1, "Nome", "Cognome",
//                 "venditore@email.com", "hashedPassword", "987654321", datiAzienda,
//                 new ArrayList<>(), TipoRuolo.DISTRIBUTORE_DI_TIPICITA, true);
//         Prodotto prodotto = new Prodotto(1, "Pomodori", "Pomodori freschi", 3.50, 100, venditore);

//         Ordine ordine = new Ordine(1, new Date(), acquirente);

//         // Crea una riga ordine
//         RigaOrdine rigaTest = new RigaOrdine(1, prodotto, 5, 3.50);
//         ordine.getRigheOrdine().add(rigaTest);

//         // Salva la riga ordine
//         rigaRepo.save(rigaTest);
//         System.out.println("✓ Riga ordine salvata con ID: " + rigaTest.getIdRiga());

//         // Cerca la riga per ID
//         Optional<RigaOrdine> rigaTrovata = rigaRepo.findById(1);
//         if (rigaTrovata.isPresent()) {
//             System.out.println("✓ Riga ordine trovata per ID: " + rigaTrovata.get().getIdRiga() +
//                     ", Prodotto: " + rigaTrovata.get().getAcquistabile().getNome() +
//                     ", Quantità: " + rigaTrovata.get().getQuantitaOrdinata());
//         }

//         // Cerca righe per acquistabile
//         List<RigaOrdine> righePerProdotto = rigaRepo.findByAcquistabile(prodotto);
//         System.out.println("✓ Numero righe per prodotto: " + righePerProdotto.size());

//         // Lista tutte le righe ordine
//         List<RigaOrdine> tutteRighe = rigaRepo.findAll();
//         System.out.println("✓ Numero totale righe ordine: " + tutteRighe.size());

//         System.out.println();
//     }
// }
