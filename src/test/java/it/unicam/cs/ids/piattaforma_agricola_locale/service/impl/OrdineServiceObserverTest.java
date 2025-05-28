package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.RigaOrdine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Produttore;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.TipoRuolo;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.observer.IVenditoreObserver;

/**
 * Test per verificare l'implementazione del pattern Observer
 * nell'OrdineService per le notifiche ai venditori.
 */
public class OrdineServiceObserverTest {

    private OrdineService ordineService;
    private Venditore venditore1;
    private Venditore venditore2;
    private Acquirente acquirente;
    private Prodotto prodotto1;
    private Prodotto prodotto2;
    private MockVenditoreObserver observer1;
    private MockVenditoreObserver observer2;

    @BeforeEach
    void setUp() {
        ordineService = new OrdineService();

        // Crea dati azienda per i venditori
        DatiAzienda datiAzienda1 = new DatiAzienda("Azienda 1", "12345678901",
                "Via Test 1", "Descrizione 1", "logo1.png", "www.azienda1.it");
        DatiAzienda datiAzienda2 = new DatiAzienda("Azienda 2", "12345678902",
                "Via Test 2", "Descrizione 2", "logo2.png", "www.azienda2.it");

        // Crea venditori
        venditore1 = new Produttore(1, "Mario", "Rossi", "mario@test.it",
                "password", "1234567890", datiAzienda1, new ArrayList<>(),
                TipoRuolo.PRODUTTORE, true);

        venditore2 = new Produttore(2, "Luigi", "Verdi", "luigi@test.it",
                "password", "0987654321", datiAzienda2, new ArrayList<>(),
                TipoRuolo.PRODUTTORE, true);

        // Crea acquirente
        acquirente = new Acquirente(3, "Anna", "Bianchi", "anna@test.it",
                "password", "1111111111", TipoRuolo.ACQUIRENTE, true);

        // Crea prodotti
        prodotto1 = new Prodotto(1, "Pomodoro", "Pomodoro biologico",
                2.50, 100, venditore1);
        prodotto2 = new Prodotto(2, "Zucchine", "Zucchine fresche",
                1.80, 50, venditore2);

        // Crea observer mock
        observer1 = new MockVenditoreObserver(venditore1);
        observer2 = new MockVenditoreObserver(venditore2);
    }

    @Test
    void testAggiungiObserver() {
        // Test aggiunta observer
        ordineService.aggiungiObserver(observer1);
        ordineService.aggiungiObserver(observer2);

        // Verifica che gli observer siano stati aggiunti
        assertDoesNotThrow(() -> ordineService.aggiungiObserver(observer1));

        // Test con observer null
        assertThrows(IllegalArgumentException.class,
                () -> ordineService.aggiungiObserver(null));
    }

    @Test
    void testRimuoviObserver() {
        // Aggiungi e rimuovi observer
        ordineService.aggiungiObserver(observer1);
        ordineService.rimuoviObserver(observer1);

        // Test con observer null
        assertThrows(IllegalArgumentException.class,
                () -> ordineService.rimuoviObserver(null));
    }

    @Test
    void testNotificaObserversConOrdineVuoto() {
        ordineService.aggiungiObserver(observer1);

        // Crea ordine senza righe
        Ordine ordine = new Ordine(1, new Date(), acquirente);

        // Test con ordine null
        assertThrows(IllegalArgumentException.class,
                () -> ordineService.notificaObservers(null, null));

        // La notifica non dovrebbe causare errori anche con ordine vuoto
        assertDoesNotThrow(() -> ordineService.notificaObservers(ordine, null));

        // L'observer non dovrebbe essere stato notificato per ordine vuoto
        assertFalse(observer1.isNotificato());
    }

    @Test
    void testNotificaObserversConVenditoreSpecifico() {
        ordineService.aggiungiObserver(observer1);
        ordineService.aggiungiObserver(observer2);

        // Crea ordine con prodotto del venditore1
        Ordine ordine = new Ordine(1, new Date(), acquirente);
        RigaOrdine riga1 = new RigaOrdine(1, prodotto1, 5, prodotto1.getPrezzo());
        ordine.getRigheOrdine().add(riga1);

        // Notifica solo venditore1
        ordineService.notificaObservers(ordine, venditore1);

        // Solo observer1 dovrebbe essere stato notificato
        assertTrue(observer1.isNotificato());
        assertFalse(observer2.isNotificato());
        assertEquals(1, observer1.getRigheDiCompetenza().size());
        assertEquals(prodotto1, observer1.getRigheDiCompetenza().get(0).getAcquistabile());
    }

    @Test
    void testNotificaTuttiIVenditoriCoinvolti() {
        ordineService.aggiungiObserver(observer1);
        ordineService.aggiungiObserver(observer2);

        // Crea ordine con prodotti di entrambi i venditori
        Ordine ordine = new Ordine(1, new Date(), acquirente);
        RigaOrdine riga1 = new RigaOrdine(1, prodotto1, 3, prodotto1.getPrezzo());
        RigaOrdine riga2 = new RigaOrdine(2, prodotto2, 2, prodotto2.getPrezzo());
        ordine.getRigheOrdine().add(riga1);
        ordine.getRigheOrdine().add(riga2);

        // Notifica tutti i venditori coinvolti
        ordineService.notificaObservers(ordine, null);

        // Entrambi gli observer dovrebbero essere stati notificati
        assertTrue(observer1.isNotificato());
        assertTrue(observer2.isNotificato());

        // Ogni observer dovrebbe aver ricevuto solo le sue righe di competenza
        assertEquals(1, observer1.getRigheDiCompetenza().size());
        assertEquals(1, observer2.getRigheDiCompetenza().size());
        assertEquals(prodotto1, observer1.getRigheDiCompetenza().get(0).getAcquistabile());
        assertEquals(prodotto2, observer2.getRigheDiCompetenza().get(0).getAcquistabile());
    }

    /**
     * Mock implementation di VenditoreObserver per i test
     */
    private static class MockVenditoreObserver extends Produttore implements IVenditoreObserver {
        private boolean notificato = false;
        private Ordine ultimoOrdine;
        private List<RigaOrdine> righeDiCompetenza;

        public MockVenditoreObserver(Venditore venditore) {
            super(venditore.getId(), venditore.getNome(), venditore.getCognome(),
                    venditore.getEmail(), venditore.getPasswordHash(), venditore.getNumeroTelefono(),
                    venditore.getDatiAzienda(), venditore.getProdottiOfferti(),
                    venditore.getTipoRuolo(), venditore.isAttivo());
        }

        @Override
        public void update(Ordine ordine, List<RigaOrdine> righeDiCompetenza) {
            this.notificato = true;
            this.ultimoOrdine = ordine;
            this.righeDiCompetenza = new ArrayList<>(righeDiCompetenza);
        }

        public boolean isNotificato() {
            return notificato;
        }

        public Ordine getUltimoOrdine() {
            return ultimoOrdine;
        }

        public List<RigaOrdine> getRigheDiCompetenza() {
            return righeDiCompetenza;
        }

        public void reset() {
            this.notificato = false;
            this.ultimoOrdine = null;
            this.righeDiCompetenza = null;
        }
    }
}
