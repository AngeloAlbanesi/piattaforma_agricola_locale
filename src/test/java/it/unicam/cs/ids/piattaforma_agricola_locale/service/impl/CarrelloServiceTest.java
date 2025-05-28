package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.carrello.Carrello;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.carrello.ElementoCarrello;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Pacchetto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.StatoVerificaValori;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.TipoRuolo;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Produttore;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DistributoreDiTipicita;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
import it.unicam.cs.ids.piattaforma_agricola_locale.exception.QuantitaNonDisponibileException;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.Evento;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.StatoEventoValori;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.AnimatoreDellaFiliera;

import java.util.Optional;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Test class per CarrelloService, verifica il corretto funzionamento
 * dell'aggiunta di elementi al carrello con controlli di approvazione
 */
class CarrelloServiceTest {

    private CarrelloService carrelloService;
    private Acquirente acquirente;
    private Prodotto prodottoApprovato;
    private Prodotto prodottoApprovato2; // Secondo prodotto approvato per i test
    private Prodotto prodottoInRevisione;
    private Prodotto prodottoRespinto;
    private Produttore venditore;
    private DistributoreDiTipicita distributore;
    private Evento evento;
    private AnimatoreDellaFiliera animatore;

    @BeforeEach
    void setUp() {
        carrelloService = new CarrelloService();

        // Crea un acquirente di test
        acquirente = new Acquirente(
                1,
                "Mario",
                "Rossi",
                "mario.rossi@test.com",
                "hashedPassword",
                "1234567890",
                TipoRuolo.ACQUIRENTE,
                true);

        // Crea un venditore di test
        DatiAzienda datiAzienda = new DatiAzienda(
                "Azienda Test",
                "12345678901",
                "Via Test 123",
                "Descrizione test",
                "logo.png",
                "www.test.com");

        venditore = new Produttore(
                2,
                "Giuseppe",
                "Verdi",
                "giuseppe.verdi@test.com",
                "hashedPassword",
                "0987654321",
                datiAzienda,
                null,
                TipoRuolo.PRODUTTORE,
                true);

        // Crea un distributore di test per i pacchetti
        DatiAzienda datiAziendaDistributore = new DatiAzienda(
                "Distributore Tipicità",
                "98765432109",
                "Via Tipicità 456",
                "Distributore locale di tipicità",
                "logo_distributore.png",
                "https://www.distributor.com");
        datiAziendaDistributore.setStatoVerifica(StatoVerificaValori.APPROVATO);

        distributore = new DistributoreDiTipicita(
                3,
                "Giuseppe",
                "Bianchi",
                "giuseppe.bianchi@test.com",
                "hashedPassword",
                "9876543210",
                datiAziendaDistributore,
                new ArrayList<Prodotto>(),
                TipoRuolo.DISTRIBUTORE_DI_TIPICITA,
                true);

        // Crea un animatore della filiera per gli eventi
        animatore = new AnimatoreDellaFiliera(
                4,
                "Luigi",
                "Verdi",
                "luigi.verdi@test.com",
                "hashedPassword",
                "5551234567",
                TipoRuolo.ANIMATORE_DELLA_FILIERA,
                true);

        // Crea un evento di test
        Date dataInizio = new Date(System.currentTimeMillis() + 86400000); // domani
        Date dataFine = new Date(System.currentTimeMillis() + 90000000); // dopo domani
        List<Venditore> aziendePartecipanti = new ArrayList<>();

        evento = new Evento(
                4,
                "Sagra del Pomodoro",
                "Evento dedicato ai prodotti tipici locali",
                dataInizio,
                dataFine,
                "Piazza del Paese",
                100, // capienza massima
                StatoEventoValori.IN_PROGRAMMA,
                animatore,
                aziendePartecipanti);

        // Crea prodotti con diversi stati di verifica
        prodottoApprovato = new Prodotto(1, "Prodotto Approvato", "Descrizione", 10.0, 100, venditore);
        prodottoApprovato.setStatoVerifica(StatoVerificaValori.APPROVATO);

        prodottoApprovato2 = new Prodotto(5, "Secondo Prodotto Approvato", "Altra descrizione", 25.0, 75, venditore);
        prodottoApprovato2.setStatoVerifica(StatoVerificaValori.APPROVATO);

        prodottoInRevisione = new Prodotto(2, "Prodotto In Revisione", "Descrizione", 15.0, 50, venditore);
        prodottoInRevisione.setStatoVerifica(StatoVerificaValori.IN_REVISIONE);

        prodottoRespinto = new Prodotto(3, "Prodotto Respinto", "Descrizione", 20.0, 25, venditore);
        prodottoRespinto.setStatoVerifica(StatoVerificaValori.RESPINTO);
    }

    @Test
    void testAggiungiProdottoApprovato() {
        // Test: aggiunta di un prodotto approvato deve funzionare
        assertDoesNotThrow(() -> {
            carrelloService.aggiungiElementoAlCarrello(acquirente, prodottoApprovato, 2);
        });

        // Verifica che il carrello sia stato creato e contenga l'elemento
        Optional<Carrello> carrelloOpt = carrelloService.getCarrelloAcquirente(acquirente);
        assertTrue(carrelloOpt.isPresent());

        Carrello carrello = carrelloOpt.get();
        assertEquals(1, carrello.getElementiCarrello().size());

        ElementoCarrello elemento = carrello.getElementiCarrello().get(0);
        assertEquals(prodottoApprovato.getId(), elemento.getAcquistabile().getId());
        assertEquals(2, elemento.getQuantita());
    }

    @Test
    void testAggiungiProdottoInRevisione() {
        // Test: aggiunta di un prodotto in revisione deve lanciare eccezione
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            carrelloService.aggiungiElementoAlCarrello(acquirente, prodottoInRevisione, 1);
        });

        assertEquals("Non è possibile aggiungere al carrello un prodotto non approvato", exception.getMessage());
    }

    @Test
    void testAggiungiProdottoRespinto() {
        // Test: aggiunta di un prodotto respinto deve lanciare eccezione
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            carrelloService.aggiungiElementoAlCarrello(acquirente, prodottoRespinto, 1);
        });

        assertEquals("Non è possibile aggiungere al carrello un prodotto non approvato", exception.getMessage());
    }

    @Test
    void testAggiungiQuantitaNegativa() {
        // Test: aggiunta con quantità negativa deve lanciare eccezione
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            carrelloService.aggiungiElementoAlCarrello(acquirente, prodottoApprovato, -1);
        });

        assertEquals("La quantità deve essere maggiore di zero", exception.getMessage());
    }

    @Test
    void testAggiungiQuantitaZero() {
        // Test: aggiunta con quantità zero deve lanciare eccezione
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            carrelloService.aggiungiElementoAlCarrello(acquirente, prodottoApprovato, 0);
        });

        assertEquals("La quantità deve essere maggiore di zero", exception.getMessage());
    }

    @Test
    void testAggiungiElementoEsistente() {
        // Test: aggiunta dello stesso prodotto deve aggiornare la quantità
        carrelloService.aggiungiElementoAlCarrello(acquirente, prodottoApprovato, 2);
        carrelloService.aggiungiElementoAlCarrello(acquirente, prodottoApprovato, 3);

        Optional<Carrello> carrelloOpt = carrelloService.getCarrelloAcquirente(acquirente);
        assertTrue(carrelloOpt.isPresent());

        Carrello carrello = carrelloOpt.get();
        assertEquals(1, carrello.getElementiCarrello().size());

        ElementoCarrello elemento = carrello.getElementiCarrello().get(0);
        assertEquals(prodottoApprovato.getId(), elemento.getAcquistabile().getId());
        assertEquals(5, elemento.getQuantita()); // 2 + 3 = 5
    }

    @Test
    void testAggiungiProdottiDiversi() {
        // Test: aggiunta di prodotti diversi deve creare elementi separati
        Prodotto altroProdotto = new Prodotto(4, "Altro Prodotto", "Descrizione", 25.0, 30, venditore);
        altroProdotto.setStatoVerifica(StatoVerificaValori.APPROVATO);

        carrelloService.aggiungiElementoAlCarrello(acquirente, prodottoApprovato, 2);
        carrelloService.aggiungiElementoAlCarrello(acquirente, altroProdotto, 1);

        Optional<Carrello> carrelloOpt = carrelloService.getCarrelloAcquirente(acquirente);
        assertTrue(carrelloOpt.isPresent());

        Carrello carrello = carrelloOpt.get();
        assertEquals(2, carrello.getElementiCarrello().size());
    }

    // *** TEST PER IL CONTROLLO DI DISPONIBILITÀ ***

    @Test
    void testAggiungiQuantitaSuperioreADisponibile() {
        // Test: aggiunta di quantità superiore a quella disponibile deve lanciare
        // QuantitaNonDisponibileException
        // Il prodotto ha 100 unità disponibili, tentiamo di aggiungerne 150
        QuantitaNonDisponibileException exception = assertThrows(QuantitaNonDisponibileException.class, () -> {
            carrelloService.aggiungiElementoAlCarrello(acquirente, prodottoApprovato, 150);
        });

        // Verifica i dettagli dell'eccezione
        assertEquals((long) prodottoApprovato.getId(), exception.getProdottoId());
        assertEquals(150, exception.getQuantitaRichiesta());
        assertEquals(100, exception.getQuantitaDisponibile());
        assertEquals("Prodotto", exception.getTipoProdotto());
        assertTrue(exception.getMessage().contains("Quantità non disponibile"));
    }

    @Test
    void testAggiungiQuantitaEsattamenteUgualeADisponibile() {
        // Test: aggiunta di quantità esattamente uguale a quella disponibile deve
        // funzionare
        // Il prodotto ha 100 unità disponibili
        assertDoesNotThrow(() -> {
            carrelloService.aggiungiElementoAlCarrello(acquirente, prodottoApprovato, 100);
        });

        Optional<Carrello> carrelloOpt = carrelloService.getCarrelloAcquirente(acquirente);
        assertTrue(carrelloOpt.isPresent());

        Carrello carrello = carrelloOpt.get();
        assertEquals(1, carrello.getElementiCarrello().size());
        assertEquals(100, carrello.getElementiCarrello().get(0).getQuantita());
    }

    @Test
    void testAggiungiQuantitaIncrementaleSuperioreADisponibile() {
        // Test: aggiunta incrementale che supera la disponibilità deve lanciare
        // eccezione
        // Prima aggiunta: 60 unità (ok)
        carrelloService.aggiungiElementoAlCarrello(acquirente, prodottoApprovato, 60);

        // Seconda aggiunta: 50 unità (totale 110, supera le 100 disponibili)
        QuantitaNonDisponibileException exception = assertThrows(QuantitaNonDisponibileException.class, () -> {
            carrelloService.aggiungiElementoAlCarrello(acquirente, prodottoApprovato, 50);
        });

        assertEquals(110, exception.getQuantitaRichiesta()); // Quantità totale richiesta
        assertEquals(100, exception.getQuantitaDisponibile());

        // Verifica che il carrello contenga ancora solo la prima aggiunta
        Optional<Carrello> carrelloOpt = carrelloService.getCarrelloAcquirente(acquirente);
        assertTrue(carrelloOpt.isPresent());
        assertEquals(60, carrelloOpt.get().getElementiCarrello().get(0).getQuantita());
    }

    @Test
    void testAggiungiQuantitaIncrementaleValidaDopoPrimaAggiunta() {
        // Test: aggiunta incrementale valida dopo una prima aggiunta
        // Prima aggiunta: 30 unità
        carrelloService.aggiungiElementoAlCarrello(acquirente, prodottoApprovato, 30);

        // Seconda aggiunta: 40 unità (totale 70, entro le 100 disponibili)
        assertDoesNotThrow(() -> {
            carrelloService.aggiungiElementoAlCarrello(acquirente, prodottoApprovato, 40);
        });

        Optional<Carrello> carrelloOpt = carrelloService.getCarrelloAcquirente(acquirente);
        assertTrue(carrelloOpt.isPresent());
        assertEquals(70, carrelloOpt.get().getElementiCarrello().get(0).getQuantita());
    }

    @Test
    void testControlloDisponibilitaConPacchetto() {
        // Test: controllo disponibilità per un Pacchetto
        Pacchetto pacchetto = new Pacchetto(distributore, 5, "Pacchetto Test", "Descrizione pacchetto", 20, 50.0);

        // Tentativo di aggiungere quantità superiore alla disponibilità del pacchetto
        QuantitaNonDisponibileException exception = assertThrows(QuantitaNonDisponibileException.class, () -> {
            carrelloService.aggiungiElementoAlCarrello(acquirente, pacchetto, 25);
        });

        assertEquals("Pacchetto", exception.getTipoProdotto());
        assertEquals(25, exception.getQuantitaRichiesta());
        assertEquals(20, exception.getQuantitaDisponibile());
    }

    @Test
    void testControlloDisponibilitaConPacchettoQuantitaValida() {
        // Test: aggiunta valida per un Pacchetto
        Pacchetto pacchetto = new Pacchetto(distributore, 6, "Pacchetto Valido", "Descrizione", 15, 40.0);

        assertDoesNotThrow(() -> {
            carrelloService.aggiungiElementoAlCarrello(acquirente, pacchetto, 10);
        });

        Optional<Carrello> carrelloOpt = carrelloService.getCarrelloAcquirente(acquirente);
        assertTrue(carrelloOpt.isPresent());
        assertEquals(1, carrelloOpt.get().getElementiCarrello().size());
        assertEquals(10, carrelloOpt.get().getElementiCarrello().get(0).getQuantita());
    }

    @Test
    void testMultipliProdottiConControlloDisponibilita() {
        // Test: aggiunta di più prodotti diversi con controllo di disponibilità
        Prodotto prodotto2 = new Prodotto(7, "Prodotto 2", "Descrizione", 15.0, 50, venditore);
        prodotto2.setStatoVerifica(StatoVerificaValori.APPROVATO);

        // Prima aggiunta: prodotto1 con 80 unità (su 100 disponibili)
        assertDoesNotThrow(() -> {
            carrelloService.aggiungiElementoAlCarrello(acquirente, prodottoApprovato, 80);
        });

        // Seconda aggiunta: prodotto2 con 30 unità (su 50 disponibili)
        assertDoesNotThrow(() -> {
            carrelloService.aggiungiElementoAlCarrello(acquirente, prodotto2, 30);
        });

        // Terza aggiunta: tentativo di aggiungere 25 unità al prodotto1 (totale 105,
        // supera 100)
        QuantitaNonDisponibileException exception = assertThrows(QuantitaNonDisponibileException.class, () -> {
            carrelloService.aggiungiElementoAlCarrello(acquirente, prodottoApprovato, 25);
        });

        assertEquals(105, exception.getQuantitaRichiesta());

        // Verifica che il carrello contenga 2 elementi con le quantità corrette
        Optional<Carrello> carrelloOpt = carrelloService.getCarrelloAcquirente(acquirente);
        assertTrue(carrelloOpt.isPresent());
        assertEquals(2, carrelloOpt.get().getElementiCarrello().size());
    }

    @Test
    void testAggiungiEventoAlCarrelloConSuccesso() {
        // Testa l'aggiunta di un evento con posti disponibili
        int quantitaDaAggiungere = 5;

        assertDoesNotThrow(() -> {
            carrelloService.aggiungiElementoAlCarrello(acquirente, evento, quantitaDaAggiungere);
        });

        Optional<Carrello> carrelloOpt = carrelloService.getCarrelloAcquirente(acquirente);
        assertTrue(carrelloOpt.isPresent());

        Carrello carrello = carrelloOpt.get();
        assertEquals(1, carrello.getElementiCarrello().size());

        ElementoCarrello elemento = carrello.getElementiCarrello().get(0);
        assertEquals(evento.getId(), elemento.getAcquistabile().getId());
        assertEquals(quantitaDaAggiungere, elemento.getQuantita());
    }

    @Test
    void testAggiungiEventoAlCarrelloQuantitaEccessiva() {
        // Testa il lancio dell'eccezione quando si richiede più posti di quelli
        // disponibili
        int quantitaEccessiva = 150; // più della capienza massima di 100

        QuantitaNonDisponibileException exception = assertThrows(
                QuantitaNonDisponibileException.class,
                () -> carrelloService.aggiungiElementoAlCarrello(acquirente, evento, quantitaEccessiva));

        assertEquals(evento.getId(), exception.getProdottoId());
        assertEquals(quantitaEccessiva, exception.getQuantitaRichiesta());
        assertEquals(100, exception.getQuantitaDisponibile());
        assertEquals("Evento", exception.getTipoProdotto());
        assertTrue(exception.getMessage().contains("Quantità non disponibile"));
        assertTrue(exception.getMessage().contains("richieste"));
    }

    @Test
    void testAggiungiEventoAlCarrelloPostiEsatti() {
        // Testa l'aggiunta di un numero di posti uguale alla capienza massima
        int quantitaEsatta = 100;

        assertDoesNotThrow(() -> {
            carrelloService.aggiungiElementoAlCarrello(acquirente, evento, quantitaEsatta);
        });

        Optional<Carrello> carrelloOpt = carrelloService.getCarrelloAcquirente(acquirente);
        assertTrue(carrelloOpt.isPresent());

        Carrello carrello = carrelloOpt.get();
        assertEquals(1, carrello.getElementiCarrello().size());

        ElementoCarrello elemento = carrello.getElementiCarrello().get(0);
        assertEquals(evento.getId(), elemento.getAcquistabile().getId());
        assertEquals(quantitaEsatta, elemento.getQuantita());
    }

    @Test
    void testAggiungiEventoAlCarrelloIncrementale() {
        // Testa l'aggiunta incrementale di posti
        int primaAggiunta = 30;
        int secondaAggiunta = 40;
        int terzaAggiunta = 31; // Questa dovrebbe fallire perché supererebbe i 100 posti disponibili

        // Prima aggiunta
        assertDoesNotThrow(() -> {
            carrelloService.aggiungiElementoAlCarrello(acquirente, evento, primaAggiunta);
        });

        // Seconda aggiunta
        assertDoesNotThrow(() -> {
            carrelloService.aggiungiElementoAlCarrello(acquirente, evento, secondaAggiunta);
        });

        // Verifica che ora ci siano 70 posti nel carrello
        Optional<Carrello> carrelloOpt = carrelloService.getCarrelloAcquirente(acquirente);
        assertTrue(carrelloOpt.isPresent());
        Carrello carrello = carrelloOpt.get();
        assertEquals(1, carrello.getElementiCarrello().size());
        assertEquals(70, carrello.getElementiCarrello().get(0).getQuantita());

        // Terza aggiunta che dovrebbe fallire
        QuantitaNonDisponibileException exception = assertThrows(
                QuantitaNonDisponibileException.class,
                () -> carrelloService.aggiungiElementoAlCarrello(acquirente, evento, terzaAggiunta));

        assertEquals(evento.getId(), exception.getProdottoId());
        assertEquals(70 + terzaAggiunta, exception.getQuantitaRichiesta()); // totale richiesto
        assertEquals(100, exception.getQuantitaDisponibile());
        assertEquals("Evento", exception.getTipoProdotto());
    }

    @Test
    void testAggiungiEventoConPostiGiaPrenotati() {
        // Simula la prenotazione di alcuni posti
        evento.incrementaPostiPrenotati(20);

        // Ora sono disponibili solo 80 posti
        int quantitaDaAggiungere = 85; // Più dei posti disponibili rimanenti

        QuantitaNonDisponibileException exception = assertThrows(
                QuantitaNonDisponibileException.class,
                () -> carrelloService.aggiungiElementoAlCarrello(acquirente, evento, quantitaDaAggiungere));

        assertEquals(evento.getId(), exception.getProdottoId());
        assertEquals(quantitaDaAggiungere, exception.getQuantitaRichiesta());
        assertEquals(80, exception.getQuantitaDisponibile()); // 100 - 20 già prenotati
        assertEquals("Evento", exception.getTipoProdotto());
    }

    /**
     * Test per il metodo rimuoviElementoDalCarrello
     */
    @Test
    void testRimuoviElementoDalCarrello_ConElementoEsistente() {
        // Aggiungi elementi al carrello
        carrelloService.aggiungiElementoAlCarrello(acquirente, prodottoApprovato, 3);

        // Verifica che l'elemento sia stato aggiunto
        var carrello = carrelloService.getCarrelloAcquirente(acquirente);
        assertTrue(carrello.isPresent());
        assertEquals(1, carrello.get().getElementiCarrello().size());

        // Ottieni l'elemento da rimuovere
        ElementoCarrello elementoDaRimuovere = carrello.get().getElementiCarrello().get(0);

        // Rimuovi l'elemento
        carrelloService.rimuoviElementoDalCarrello(acquirente, elementoDaRimuovere);

        // Verifica che l'elemento sia stato rimosso
        var carrelloAggiornato = carrelloService.getCarrelloAcquirente(acquirente);
        assertTrue(carrelloAggiornato.isPresent());
        assertEquals(0, carrelloAggiornato.get().getElementiCarrello().size());
    }

    @Test
    void testRimuoviElementoDalCarrello_ConAcquirenteNull() {
        ElementoCarrello elemento = new ElementoCarrello(prodottoApprovato, 1);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> carrelloService.rimuoviElementoDalCarrello(null, elemento));

        assertEquals("L'acquirente non può essere null", exception.getMessage());
    }

    @Test
    void testRimuoviElementoDalCarrello_ConElementoNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> carrelloService.rimuoviElementoDalCarrello(acquirente, null));

        assertEquals("L'elemento da rimuovere non può essere null", exception.getMessage());
    }

    @Test
    void testRimuoviElementoDalCarrello_CarrelloNonEsistente() {
        ElementoCarrello elemento = new ElementoCarrello(prodottoApprovato, 1);

        // Non dovrebbe lanciare eccezioni anche se il carrello non esiste
        assertDoesNotThrow(() -> carrelloService.rimuoviElementoDalCarrello(acquirente, elemento));
    }

    @Test
    void testRimuoviElementoDalCarrello_ElementoNonEsistente() {
        // Crea un carrello con un elemento
        carrelloService.aggiungiElementoAlCarrello(acquirente, prodottoApprovato, 2);

        // Crea un elemento diverso non presente nel carrello
        ElementoCarrello elementoNonEsistente = new ElementoCarrello(prodottoInRevisione, 1);

        // Non dovrebbe lanciare eccezioni anche se l'elemento non esiste nel carrello
        assertDoesNotThrow(() -> carrelloService.rimuoviElementoDalCarrello(acquirente, elementoNonEsistente));

        // Verifica che l'elemento originale sia ancora presente
        var carrello = carrelloService.getCarrelloAcquirente(acquirente);
        assertTrue(carrello.isPresent());
        assertEquals(1, carrello.get().getElementiCarrello().size());
    }

    /**
     * Test per il metodo svuotaCarrello
     */
    @Test
    void testSvuotaCarrello_ConCarrelloConElementi() {
        // Aggiungi più elementi al carrello
        carrelloService.aggiungiElementoAlCarrello(acquirente, prodottoApprovato, 2);
        carrelloService.aggiungiElementoAlCarrello(acquirente, prodottoApprovato2, 1);

        // Verifica che gli elementi siano stati aggiunti
        var carrello = carrelloService.getCarrelloAcquirente(acquirente);
        assertTrue(carrello.isPresent());
        assertEquals(2, carrello.get().getElementiCarrello().size());

        // Svuota il carrello
        carrelloService.svuotaCarrello(acquirente);

        // Verifica che il carrello sia vuoto
        var carrelloSvuotato = carrelloService.getCarrelloAcquirente(acquirente);
        assertTrue(carrelloSvuotato.isPresent());
        assertEquals(0, carrelloSvuotato.get().getElementiCarrello().size());
    }

    @Test
    void testSvuotaCarrello_ConAcquirenteNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> carrelloService.svuotaCarrello(null));

        assertEquals("L'acquirente non può essere null", exception.getMessage());
    }

    @Test
    void testSvuotaCarrello_CarrelloNonEsistente() {
        // Non dovrebbe lanciare eccezioni anche se il carrello non esiste
        assertDoesNotThrow(() -> carrelloService.svuotaCarrello(acquirente));
    }

    @Test
    void testSvuotaCarrello_CarrelloGiaVuoto() {
        // Crea un carrello vuoto
        carrelloService.creaNuovoCarrello(acquirente);

        // Verifica che sia vuoto
        var carrello = carrelloService.getCarrelloAcquirente(acquirente);
        assertTrue(carrello.isPresent());
        assertEquals(0, carrello.get().getElementiCarrello().size());

        // Svuota il carrello già vuoto
        assertDoesNotThrow(() -> carrelloService.svuotaCarrello(acquirente));

        // Verifica che sia ancora vuoto
        var carrelloDopoSvuotamento = carrelloService.getCarrelloAcquirente(acquirente);
        assertTrue(carrelloDopoSvuotamento.isPresent());
        assertEquals(0, carrelloDopoSvuotamento.get().getElementiCarrello().size());
    }

    /**
     * Test di integrazione per verificare il flusso completo
     */
    @Test
    void testFlussoContinuo_AggiungiRimuoviSvuota() {
        // Aggiungi elementi
        carrelloService.aggiungiElementoAlCarrello(acquirente, prodottoApprovato, 3);
        carrelloService.aggiungiElementoAlCarrello(acquirente, prodottoApprovato2, 2);

        var carrello = carrelloService.getCarrelloAcquirente(acquirente);
        assertTrue(carrello.isPresent());
        assertEquals(2, carrello.get().getElementiCarrello().size());

        // Rimuovi un elemento specifico
        ElementoCarrello elementoDaRimuovere = carrello.get().getElementiCarrello().get(0);
        carrelloService.rimuoviElementoDalCarrello(acquirente, elementoDaRimuovere);

        var carrelloDopoRimozione = carrelloService.getCarrelloAcquirente(acquirente);
        assertTrue(carrelloDopoRimozione.isPresent());
        assertEquals(1, carrelloDopoRimozione.get().getElementiCarrello().size());

        // Svuota completamente
        carrelloService.svuotaCarrello(acquirente);

        var carrelloSvuotato = carrelloService.getCarrelloAcquirente(acquirente);
        assertTrue(carrelloSvuotato.isPresent());
        assertEquals(0, carrelloSvuotato.get().getElementiCarrello().size());
    }
}
