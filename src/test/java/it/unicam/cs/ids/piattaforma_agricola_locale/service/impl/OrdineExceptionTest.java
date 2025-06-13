package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unicam.cs.ids.piattaforma_agricola_locale.exception.OrdineException;

/**
 * Test per verificare il corretto funzionamento dell'eccezione OrdineException
 */
public class OrdineExceptionTest {

    private OrdineService ordineService;

    @BeforeEach
    void setUp() {
        ordineService = new OrdineService();
    }

    @Test
    void testOrdineExceptionWithMessage() {
        String message = "Test error message";
        OrdineException exception = new OrdineException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    void testOrdineExceptionWithMessageAndCause() {
        String message = "Test error message";
        Throwable cause = new RuntimeException("Root cause");
        OrdineException exception = new OrdineException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testOrdineExceptionWithCause() {
        Throwable cause = new RuntimeException("Root cause");
        OrdineException exception = new OrdineException(cause);

        assertEquals(cause, exception.getCause());
    }

    @Test
    void testCreaNuovoOrdineWithNullAcquirente() {
        // Verifica che OrdineException venga lanciata quando si passa un acquirente
        // null
        OrdineException exception = assertThrows(OrdineException.class, () -> {
            ordineService.creaNuovoOrdine(null);
        });

        assertTrue(exception.getMessage().contains("acquirente non può essere null"));
    }

    @Test
    void testCalcolaPrezzoOrdineWithNullOrdine() {
        // Verifica che OrdineException venga lanciata quando si passa un ordine null
        OrdineException exception = assertThrows(OrdineException.class, () -> {
            ordineService.calcolaPrezzoOrdine(null);
        });

        assertTrue(exception.getMessage().contains("ordine non può essere null"));
    }

    @Test
    void testSalvaOrdineWithNullOrdine() {
        // Verifica che OrdineException venga lanciata quando si passa un ordine null
        OrdineException exception = assertThrows(OrdineException.class, () -> {
            ordineService.salvaOrdine(null);
        });

        assertTrue(exception.getMessage().contains("ordine null"));
    }

    @Test
    void testAggiornaOrdineWithNullOrdine() {
        // Verifica che OrdineException venga lanciata quando si passa un ordine null
        OrdineException exception = assertThrows(OrdineException.class, () -> {
            ordineService.aggiornaOrdine(null);
        });

        assertTrue(exception.getMessage().contains("ordine null"));
    }

   /* @Test
    void testEliminaOrdineWithInvalidId() {
        // Verifica che OrdineException venga lanciata quando si cerca di eliminare un
        // ordine inesistente
        int invalidId = -999;
        OrdineException exception = assertThrows(OrdineException.class, () -> {
            ordineService.eliminaOrdine(invalidId);
        });

        assertTrue(exception.getMessage().contains("non trovato"));
    }*/

    @Test
    void testCreaOrdineDaCarrelloWithNullAcquirente() {
        // Verifica che OrdineException venga lanciata quando si passa un acquirente
        // null
        OrdineException exception = assertThrows(OrdineException.class, () -> {
            ordineService.creaOrdineDaCarrello(null);
        });

        assertTrue(exception.getMessage().contains("acquirente non può essere null"));
    }
}
