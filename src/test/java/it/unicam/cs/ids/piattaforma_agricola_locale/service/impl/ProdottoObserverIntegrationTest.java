// package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

// import static org.junit.jupiter.api.Assertions.*;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;

// import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
// import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.StatoVerificaValori;
// import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.*;
// import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;
// import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DistributoreDiTipicita;
// import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.TipoRuolo;
// import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.ICertificazioneService;

// import java.util.ArrayList;
// import java.util.List;

// /**
//  * Test di integrazione per verificare il corretto funzionamento
//  * del pattern Observer per la gestione automatica della revisione prodotti.
//  */
// public class ProdottoObserverIntegrationTest {

//     private ProdottoService prodottoService;
//     private CuratoreService curatoreService;
//     private CuratoreObserverService curatoreObserver;
//     private DistributoreDiTipicita venditore;

//     @BeforeEach
//     void setUp() {
//         // Inizializza i repository in memoria
//         IProdottoRepository prodottoRepository = new ProdottoRepository();
//         IVenditoreRepository venditoreRepository = new VenditoreRepository();
//         ICertificazioneRepository certificazioneRepository = new CertificazioneRepository();
//         IDatiAziendaRepository datiAziendaRepository = new DatiAziendaRepository();
        
//         ICertificazioneService certificazioneService = new CertificazioneService(
//             certificazioneRepository, datiAziendaRepository, prodottoRepository);
        
//         // Crea i servizi
//         prodottoService = new ProdottoService(prodottoRepository, certificazioneService, venditoreRepository);
//         curatoreService = new CuratoreService();
//         curatoreObserver = new CuratoreObserverService(curatoreService);
        
//         // Registra l'observer al ProdottoService
//         prodottoService.aggiungiObserver(curatoreObserver);
        
//         // Crea un venditore di test
//         DatiAzienda datiAzienda = new DatiAzienda(1, "Test Azienda", "IT12345678901", 
//                                                  "Via Test 1", "Descrizione test", "", "");
//         venditore = new DistributoreDiTipicita("Test", "Venditore", 
//                                               "test@email.com", "password", "1234567890", 
//                                               datiAzienda, TipoRuolo.DISTRIBUTORE_DI_TIPICITA);
//     }

//     @Test
//     void testProdottoAutomaticamenteAggiuntoCodaRevisione() {
//         // Given: Un sistema con observer registrato
//         assertTrue(curatoreService.getProdottiInAttesaRevisione().isEmpty(), 
//                   "La coda di revisione dovrebbe essere vuota inizialmente");

//         // When: Viene creato un nuovo prodotto
//         Prodotto prodotto = prodottoService.creaProdotto("Prodotto Test", "Descrizione test", 
//                                                         10.0, 5, venditore);

//         // Then: Il prodotto dovrebbe essere automaticamente aggiunto alla coda di revisione
//         List<Prodotto> prodottiInRevisione = curatoreService.getProdottiInAttesaRevisione();
        
//         assertEquals(1, prodottiInRevisione.size(), 
//                     "Dovrebbe esserci esattamente un prodotto in coda di revisione");
        
//         assertEquals(prodotto.getId(), prodottiInRevisione.get(0).getId(), 
//                     "Il prodotto in coda dovrebbe essere quello appena creato");
        
//         assertEquals(StatoVerificaValori.IN_REVISIONE, prodotto.getStatoVerifica(), 
//                     "Il prodotto dovrebbe essere in stato IN_REVISIONE");
//     }

//     @Test
//     void testProdottoRimossoDallaCodeDopoApprovazione() {
//         // Given: Un prodotto in coda di revisione
//         Prodotto prodotto = prodottoService.creaProdotto("Prodotto Test", "Descrizione test", 
//                                                         10.0, 5, venditore);
        
//         assertEquals(1, curatoreService.getProdottiInAttesaRevisione().size(), 
//                     "Il prodotto dovrebbe essere in coda");

//         // When: Il curatore approva il prodotto
//         curatoreService.approvaProdotto(prodotto, "Prodotto approvato");

//         // Then: Il prodotto dovrebbe essere rimosso dalla coda
//         assertTrue(curatoreService.getProdottiInAttesaRevisione().isEmpty(), 
//                   "La coda di revisione dovrebbe essere vuota dopo l'approvazione");
        
//         assertEquals(StatoVerificaValori.APPROVATO, prodotto.getStatoVerifica(), 
//                     "Il prodotto dovrebbe essere in stato APPROVATO");
//     }

//     @Test
//     void testProdottoRimossoDallaCodeDopoRespinta() {
//         // Given: Un prodotto in coda di revisione
//         Prodotto prodotto = prodottoService.creaProdotto("Prodotto Test", "Descrizione test", 
//                                                         10.0, 5, venditore);
        
//         assertEquals(1, curatoreService.getProdottiInAttesaRevisione().size(), 
//                     "Il prodotto dovrebbe essere in coda");

//         // When: Il curatore respinge il prodotto
//         curatoreService.respingiProdotto(prodotto, "Prodotto respinto");

//         // Then: Il prodotto dovrebbe essere rimosso dalla coda
//         assertTrue(curatoreService.getProdottiInAttesaRevisione().isEmpty(), 
//                   "La coda di revisione dovrebbe essere vuota dopo la respinta");
        
//         assertEquals(StatoVerificaValori.RESPINTO, prodotto.getStatoVerifica(), 
//                     "Il prodotto dovrebbe essere in stato RESPINTO");
//     }

//     @Test
//     void testMultipliProdottiInCodeDiRevisione() {
//         // Given: Nessun prodotto in coda
//         assertTrue(curatoreService.getProdottiInAttesaRevisione().isEmpty());

//         // When: Vengono creati più prodotti
//         Prodotto prodotto1 = prodottoService.creaProdotto("Prodotto 1", "Descrizione 1", 
//                                                          10.0, 5, venditore);
//         Prodotto prodotto2 = prodottoService.creaProdotto("Prodotto 2", "Descrizione 2", 
//                                                          15.0, 3, venditore);
//         Prodotto prodotto3 = prodottoService.creaProdotto("Prodotto 3", "Descrizione 3", 
//                                                          20.0, 7, venditore);

//         // Then: Tutti i prodotti dovrebbero essere in coda
//         List<Prodotto> prodottiInRevisione = curatoreService.getProdottiInAttesaRevisione();
        
//         assertEquals(3, prodottiInRevisione.size(), 
//                     "Dovrebbero esserci 3 prodotti in coda di revisione");
        
//         assertTrue(prodottiInRevisione.stream().anyMatch(p -> p.getId() == prodotto1.getId()));
//         assertTrue(prodottiInRevisione.stream().anyMatch(p -> p.getId() == prodotto2.getId()));
//         assertTrue(prodottiInRevisione.stream().anyMatch(p -> p.getId() == prodotto3.getId()));
//     }

//     @Test
//     void testObserverNonDuplicaProdottiInCoda() {
//         // Given: Un prodotto già creato
//         Prodotto prodotto = prodottoService.creaProdotto("Prodotto Test", "Descrizione test", 
//                                                         10.0, 5, venditore);
        
//         assertEquals(1, curatoreService.getProdottiInAttesaRevisione().size());

//         // When: Si tenta di aggiungere manualmente lo stesso prodotto alla coda
//         curatoreService.aggiungiProdottoALlaCodeDiRevisione(prodotto);

//         // Then: Non dovrebbero esserci duplicati
//         assertEquals(1, curatoreService.getProdottiInAttesaRevisione().size(), 
//                     "Non dovrebbero esserci duplicati nella coda");
//     }

//     @Test
//     void testRimozioneObserver() {
//         // Given: Un observer registrato
//         assertEquals(0, curatoreService.getProdottiInAttesaRevisione().size());

//         // When: L'observer viene rimosso e si crea un prodotto
//         prodottoService.rimuoviObserver(curatoreObserver);
//         prodottoService.creaProdotto("Prodotto Test", "Descrizione test", 10.0, 5, venditore);

//         // Then: Il prodotto NON dovrebbe essere aggiunto automaticamente alla coda
//         assertTrue(curatoreService.getProdottiInAttesaRevisione().isEmpty(), 
//                   "Il prodotto non dovrebbe essere in coda dopo la rimozione dell'observer");
//     }
// }