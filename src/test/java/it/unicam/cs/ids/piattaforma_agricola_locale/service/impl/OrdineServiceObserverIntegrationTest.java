package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import it.unicam.cs.ids.piattaforma_agricola_locale.exception.OrdineException;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.carrello.Carrello;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.carrello.ElementoCarrello;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.RigaOrdine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.StatoCorrente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.*;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DistributoreDiTipicita;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.TipoRuolo;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.StatoVerificaValori;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test di integrazione per verificare che il pattern Observer funzioni
 * correttamente con il nuovo flusso di gestione degli ordini.
 */
public class OrdineServiceObserverIntegrationTest {

    private OrdineService ordineService;
    private CarrelloService carrelloService;
    private VenditoreOrderHandlerService venditoreHandler;

    // Repository utilizzati nel test
    private IProdottoRepository prodottoRepository; // Usiamo l'interfaccia
    private UtenteRepository utenteRepository;
    private IPacchettoRepository pacchettoRepository; // Per coerenza

    private Acquirente acquirente;
    private DistributoreDiTipicita venditore;
    private Prodotto prodotto;

    @BeforeEach
    void setUp() {
        // Inizializza i repository concreti
        prodottoRepository = new ProdottoRepository();
        utenteRepository = new UtenteRepository();
        pacchettoRepository = new PacchettoRepository(); // Inizializza repo pacchetti
        CarrelloRepository carrelloRepository = new CarrelloRepository();

        // Crea dati di test con costruttori corretti
        acquirente = new Acquirente(1, "Mario", "Rossi", "mario.rossi@email.com", "password123",
                "3334567890", TipoRuolo.ACQUIRENTE, true);

        DatiAzienda datiAzienda = new DatiAzienda(
                "Azienda Test",
                "12345678901",
                "Via Test 123",
                "Descrizione test",
                "logo.png",
                "www.test.com");
        venditore = new DistributoreDiTipicita(2, "Giuseppe", "Verdi", "giuseppe.verdi@email.com",
                "password456", "3335678901", datiAzienda,
                new ArrayList<>(), TipoRuolo.DISTRIBUTORE_DI_TIPICITA, true);

        prodotto = new Prodotto(
                1,
                "Pomodori Bio",
                "Pomodori biologici freschi",
                2.50,
                10,
                venditore);

        // Salva i dati nei repository
        utenteRepository.save(acquirente);
        utenteRepository.save(venditore);
        prodottoRepository.save(prodotto);

        // Approva il prodotto direttamente
        prodotto.setStatoVerifica(StatoVerificaValori.APPROVATO);
        prodotto.setFeedbackVerifica("Prodotto approvato per test");
        prodottoRepository.save(prodotto);

        // Inizializza i servizi
        carrelloService = new CarrelloService(carrelloRepository);
        ordineService = new OrdineService(carrelloService);

        // Inizializza ProdottoService e PacchettoService con i repository corretti
        ProdottoService ps = new ProdottoService(prodottoRepository); // Inietta sharedRepo
        PacchettoService pks = new PacchettoService(pacchettoRepository); // Inietta repo pacchetti


        // Crea il handler con i servizi che usano i repository condivisi
        venditoreHandler = new VenditoreOrderHandlerService(ps, pks);

        // Registra l'observer
        ordineService.aggiungiObserver(venditoreHandler);

        System.out.println("Stato prodotto dopo approvazione: " + prodotto.getStatoVerifica());
    }

    @Test
    void testFlussoCompletoConObserverPattern() throws Exception {
        // DEBUG: Verifica lo stato del prodotto prima di aggiungerlo al carrello
        System.out.println("Stato prodotto prima aggiunta al carrello: " + prodotto.getStatoVerifica());
        
        // 1. Aggiungi prodotto al carrello
        try {
            carrelloService.aggiungiElementoAlCarrello(acquirente, prodotto, 3);
            System.out.println("Prodotto aggiunto al carrello con successo");
        } catch (Exception e) {
            System.out.println("Errore nell'aggiunta al carrello: " + e.getMessage());
            throw e;
        }
        
        // Verifica che il carrello contenga il prodotto
        Carrello carrello = carrelloService.getCarrelloAcquirente(acquirente).orElse(null);
        assertNotNull(carrello);
        assertEquals(1, carrello.getElementiCarrello().size());
        
        ElementoCarrello elemento = carrello.getElementiCarrello().get(0);
        assertEquals(3, elemento.getQuantita());
        assertEquals(prodotto, elemento.getAcquistabile());
        
        // 2. Crea ordine dal carrello
        Ordine ordine = ordineService.creaOrdineDaCarrello(acquirente);
        
        // Verifica stato iniziale dell'ordine
        assertNotNull(ordine);
        assertEquals(StatoCorrente.ATTESA_PAGAMENTO, ordine.getStatoOrdine());
        assertEquals(7.50, ordine.getImportoTotale(), 0.01); // 3 * 2.50
        
        // Verifica che l'inventario NON sia ancora stato decrementato
        Prodotto prodottoAggiornato = prodottoRepository.findById(prodotto.getId());
        assertNotNull(prodottoAggiornato);
        assertEquals(10, prodottoAggiornato.getQuantitaDisponibile()); // Dovrebbe essere ancora 10
        
        // 3. Conferma il pagamento (questo dovrebbe attivare l'observer)
        ordineService.confermaPagamento(ordine);
        
        // Verifica che lo stato sia cambiato
        assertEquals(StatoCorrente.PRONTO_PER_LAVORAZIONE, ordine.getStatoOrdine());
        
        // La chiamata diretta a venditoreHandler.update() è rimossa.
        // La notifica dovrebbe avvenire automaticamente tramite ordineService.confermaPagamento -> notificaObservers.
        
        // Verifica che l'observer abbia decrementato l'inventario
        prodottoAggiornato = prodottoRepository.findById(prodotto.getId());
        assertNotNull(prodottoAggiornato);
        assertEquals(7, prodottoAggiornato.getQuantitaDisponibile()); // Dovrebbe essere 10 - 3 = 7
        
        // Verifica che il carrello sia stato svuotato
        carrello = carrelloService.getCarrelloAcquirente(acquirente).orElse(null);
        assertNotNull(carrello);
        assertTrue(carrello.getElementiCarrello().isEmpty());
    }

    @Test
    void testConfermaPagamentoConStatoNonValido() {
        // Crea un ordine già processato
        Ordine ordine = new Ordine(999, new java.util.Date(), acquirente);
        ordine.paga(); // Questo lo porta a PRONTO_PER_LAVORAZIONE

        // Tenta di confermare nuovamente il pagamento
        OrdineException exception = assertThrows(OrdineException.class, () -> {
            ordineService.confermaPagamento(ordine);
        });

        // Verifica che l'eccezione contenga un messaggio relativo allo stato non valido
        assertTrue(exception.getMessage().toLowerCase().contains("non è in attesa") ||
                exception.getMessage().toLowerCase().contains("attesa di pagamento"));
    }

    @Test
    void testConfermaPagamentoConOrdineNull() {
        // Tenta di confermare il pagamento di un ordine null
        OrdineException exception = assertThrows(OrdineException.class, () -> {
            ordineService.confermaPagamento(null);
        });

        assertTrue(exception.getMessage().contains("l'ordine non può essere null"));
    }
}
