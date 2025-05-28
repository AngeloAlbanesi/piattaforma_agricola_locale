package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Pacchetto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.RigaOrdine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DistributoreDiTipicita;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Produttore;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.TipoRuolo;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

/**
 * Test per la classe VenditoreOrderHandlerService che implementa
 * VenditoreObserver
 * per gestire l'aggiornamento dell'inventario quando vengono creati nuovi
 * ordini.
 */
public class VenditoreObserverServiceTest {

    private VenditoreObserverService handlerService;
    private ProdottoService mockProdottoService;
    private PacchettoService mockPacchettoService;
    private OrdineService ordineService;

    private Venditore venditore;
    private Acquirente acquirente;
    private Prodotto prodotto;
    private Pacchetto pacchetto;

    @BeforeEach
    void setUp() {
        // Crea mock dei servizi
        mockProdottoService = mock(ProdottoService.class);
        mockPacchettoService = mock(PacchettoService.class);

        // Crea handler con mock services per il testing
        handlerService = new VenditoreObserverService(mockProdottoService, mockPacchettoService);

        // Crea OrdineService per test di integrazione
        ordineService = new OrdineService();

        // Crea dati azienda per il venditore
        DatiAzienda datiAzienda = new DatiAzienda("Azienda Test", "12345678901",
                "Via Test 1", "Descrizione Test", "logo.png", "www.test.it");

        // Crea venditore
        venditore = new Produttore(1, "Mario", "Rossi", "mario@test.it",
                "password", "1234567890", datiAzienda, new ArrayList<>(),
                TipoRuolo.PRODUTTORE, true);

        // Crea acquirente
        acquirente = new Acquirente(2, "Anna", "Bianchi", "anna@test.it",
                "password", "1111111111", TipoRuolo.ACQUIRENTE, true);

        // Crea prodotto
        prodotto = new Prodotto(1, "Pomodoro", "Pomodoro biologico",
                2.50, 100, venditore);

        // Crea distributore per il pacchetto
        DistributoreDiTipicita distributore = new DistributoreDiTipicita(3, "Giovanni", "Verdi", "giovanni@test.it",
                "password", "3333333333", datiAzienda, new ArrayList<>(),
                TipoRuolo.DISTRIBUTORE_DI_TIPICITA, true);

        // Crea pacchetto
        pacchetto = new Pacchetto(distributore, 2, "Pacchetto Verdure", "Mix di verdure fresche",
                5, 10.00);
    }


    @Test
    void testCostruttoreConParametri() {
        // Test costruttore con dependency injection
        assertNotNull(handlerService.getProdottoService());
        assertNotNull(handlerService.getPacchettoService());
        assertEquals(mockProdottoService, handlerService.getProdottoService());
        assertEquals(mockPacchettoService, handlerService.getPacchettoService());
    }

    @Test
    void testUpdateConOrdineNull() {
        // Test con ordine null
        List<RigaOrdine> righe = new ArrayList<>();

        assertThrows(IllegalArgumentException.class,
                () -> handlerService.update(null, righe));
    }

    @Test
    void testUpdateConRigheDiCompetenzaNull() {
        // Test con righe di competenza null
        Ordine ordine = new Ordine(1, new Date(), acquirente);

        assertThrows(IllegalArgumentException.class,
                () -> handlerService.update(ordine, null));
    }

    @Test
    void testUpdateConRigheVuote() {
        // Test con lista righe vuota
        Ordine ordine = new Ordine(1, new Date(), acquirente);
        List<RigaOrdine> righeVuote = new ArrayList<>();

        // Non dovrebbe lanciare eccezioni
        assertDoesNotThrow(() -> handlerService.update(ordine, righeVuote));

        // Nessun service dovrebbe essere chiamato
        verifyNoInteractions(mockProdottoService);
        verifyNoInteractions(mockPacchettoService);
    }

    @Test
    void testUpdateConProdotto() {
        // Test con riga ordine contenente un prodotto
        Ordine ordine = new Ordine(1, new Date(), acquirente);
        RigaOrdine rigaProdotto = new RigaOrdine(1, prodotto, 5, prodotto.getPrezzo());
        List<RigaOrdine> righe = List.of(rigaProdotto);

        // Esegui update
        handlerService.update(ordine, righe);

        // Verifica che il ProdottoService sia stato chiamato
        verify(mockProdottoService, times(1)).decrementaQuantita(prodotto.getId(), 5);
        verifyNoInteractions(mockPacchettoService);
    }

    @Test
    void testUpdateConPacchetto() {
        // Test con riga ordine contenente un pacchetto
        Ordine ordine = new Ordine(1, new Date(), acquirente);
        RigaOrdine rigaPacchetto = new RigaOrdine(2, pacchetto, 3, pacchetto.getPrezzo());
        List<RigaOrdine> righe = List.of(rigaPacchetto);

        // Esegui update
        handlerService.update(ordine, righe);

        // Verifica che il PacchettoService sia stato chiamato
        verify(mockPacchettoService, times(1)).decrementaQuantita(pacchetto.getId(), 3);
        verifyNoInteractions(mockProdottoService);
    }

    @Test
    void testUpdateConProdottoEPacchetto() {
        // Test con più righe ordine contenenti sia prodotti che pacchetti
        Ordine ordine = new Ordine(1, new Date(), acquirente);
        RigaOrdine rigaProdotto = new RigaOrdine(1, prodotto, 2, prodotto.getPrezzo());
        RigaOrdine rigaPacchetto = new RigaOrdine(2, pacchetto, 1, pacchetto.getPrezzo());
        List<RigaOrdine> righe = List.of(rigaProdotto, rigaPacchetto);

        // Esegui update
        handlerService.update(ordine, righe);

        // Verifica che entrambi i servizi siano stati chiamati
        verify(mockProdottoService, times(1)).decrementaQuantita(prodotto.getId(), 2);
        verify(mockPacchettoService, times(1)).decrementaQuantita(pacchetto.getId(), 1);
    }

    @Test
    void testUpdateConRigaNull() {
        // Test con riga null nella lista
        Ordine ordine = new Ordine(1, new Date(), acquirente);
        RigaOrdine rigaProdotto = new RigaOrdine(1, prodotto, 2, prodotto.getPrezzo());
        List<RigaOrdine> righe = new ArrayList<>();
        righe.add(null);
        righe.add(rigaProdotto);

        // Dovrebbe gestire la riga null senza errori
        assertDoesNotThrow(() -> handlerService.update(ordine, righe));

        // Solo la riga valida dovrebbe essere processata
        verify(mockProdottoService, times(1)).decrementaQuantita(prodotto.getId(), 2);
    }

    @Test
    void testUpdateConRigaConAcquistabileNull() {
        // Test con riga con acquistabile null
        Ordine ordine = new Ordine(1, new Date(), acquirente);
        RigaOrdine rigaInvalida = new RigaOrdine(1, null, 2, 5.0);
        RigaOrdine rigaValida = new RigaOrdine(2, prodotto, 3, prodotto.getPrezzo());
        List<RigaOrdine> righe = List.of(rigaInvalida, rigaValida);

        // Dovrebbe gestire la riga invalida senza errori
        assertDoesNotThrow(() -> handlerService.update(ordine, righe));

        // Solo la riga valida dovrebbe essere processata
        verify(mockProdottoService, times(1)).decrementaQuantita(prodotto.getId(), 3);
    }

    @Test
    void testUpdateConEccezioneNelService() {
        // Test gestione eccezioni dal service
        Ordine ordine = new Ordine(1, new Date(), acquirente);
        RigaOrdine rigaProdotto = new RigaOrdine(1, prodotto, 5, prodotto.getPrezzo());
        List<RigaOrdine> righe = List.of(rigaProdotto);

        // Mock per simulare eccezione
        doThrow(new RuntimeException("Errore test")).when(mockProdottoService)
                .decrementaQuantita(prodotto.getId(), 5);

        // L'handler dovrebbe gestire l'eccezione senza propagarla
        assertDoesNotThrow(() -> handlerService.update(ordine, righe));

        // Verifica che il service sia stato chiamato
        verify(mockProdottoService, times(1)).decrementaQuantita(prodotto.getId(), 5);
    }

    @Test
    void testIntegrazioneConOrdineService() {
        // Test di integrazione: registra l'handler come observer
        ordineService.aggiungiObserver(handlerService);

        // Crea ordine con righe
        Ordine ordine = new Ordine(1, new Date(), acquirente);
        RigaOrdine rigaProdotto = new RigaOrdine(1, prodotto, 2, prodotto.getPrezzo());
        ordine.getRigheOrdine().add(rigaProdotto);

        // La notifica dovrebbe chiamare l'handler
        assertDoesNotThrow(() -> ordineService.notificaObservers(ordine, null));

        // Verifica che l'handler sia registrato correttamente
        ordineService.rimuoviObserver(handlerService);
        assertDoesNotThrow(() -> ordineService.notificaObservers(ordine, null));
    }

    @Test
    void testRegistrazioneMultipliHandler() {
        // Test registrazione di più handler per lo stesso venditore
        VenditoreObserverService handler2 = new VenditoreObserverService(mockProdottoService, mockPacchettoService);

        ordineService.aggiungiObserver(handlerService);
        ordineService.aggiungiObserver(handler2);

        // Entrambi dovrebbero essere registrati
        assertDoesNotThrow(() -> {
            ordineService.rimuoviObserver(handlerService);
            ordineService.rimuoviObserver(handler2);
        });
    }
}
