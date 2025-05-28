package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import it.unicam.cs.ids.piattaforma_agricola_locale.exception.OrdineException;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.Ordine;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.ordine.StatoCorrente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Acquirente;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.pagamento.IMetodoPagamentoStrategy;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.pagamento.PagamentoException;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.pagamento.impl.PagamentoSimulatoStrategy;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.pagamento.impl.PagamentoCartaCreditoStrategy;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.pagamento.impl.PagamentoPayPalStrategy;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.TipoRuolo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test completi per la nuova logica di pagamento di OrdineService
 * che utilizza il pattern Strategy.
 */
public class OrdineServicePagamentoTest {

    private OrdineService ordineService;
    private Acquirente acquirente;
    private Ordine ordine;

    @BeforeEach
    void setUp() {
        ordineService = new OrdineService();
        acquirente = new Acquirente(1, "Mario", "Rossi", "test@test.com", "password123",
                "3334567890", TipoRuolo.ACQUIRENTE, true);
        ordine = new Ordine(1, new Date(), acquirente);
        ordine.setImportoTotale(50.0);
    }

    @Test
    void testConfermaPagamentoConSuccesso() throws OrdineException, PagamentoException {
        // Arrange
        IMetodoPagamentoStrategy strategiaSuccesso = new PagamentoSimulatoStrategy(true);
        assertEquals(StatoCorrente.ATTESA_PAGAMENTO, ordine.getStatoOrdine());

        // Act
        ordineService.confermaPagamento(ordine, strategiaSuccesso);

        // Assert
        assertEquals(StatoCorrente.PRONTO_PER_LAVORAZIONE, ordine.getStatoOrdine());
    }

    @Test
    void testConfermaPagamentoConFallimento() {
        // Arrange
        IMetodoPagamentoStrategy strategiaFallimento = new PagamentoSimulatoStrategy(false);
        assertEquals(StatoCorrente.ATTESA_PAGAMENTO, ordine.getStatoOrdine());

        // Act & Assert
        PagamentoException exception = assertThrows(PagamentoException.class, () -> {
            ordineService.confermaPagamento(ordine, strategiaFallimento);
        });

        // Verifica che l'ordine rimanga nello stato originale
        assertEquals(StatoCorrente.ATTESA_PAGAMENTO, ordine.getStatoOrdine());
        assertTrue(exception.getMessage().contains("non è andato a buon fine"));
    }

    @Test
    void testConfermaPagamentoConOrdineNull() {
        // Arrange
        IMetodoPagamentoStrategy strategia = new PagamentoSimulatoStrategy();

        // Act & Assert
        OrdineException exception = assertThrows(OrdineException.class, () -> {
            ordineService.confermaPagamento(null, strategia);
        });

        assertTrue(exception.getMessage().contains("ordine non può essere null"));
    }

    @Test
    void testConfermaPagamentoConStrategiaNull() {
        // Act & Assert
        OrdineException exception = assertThrows(OrdineException.class, () -> {
            ordineService.confermaPagamento(ordine, null);
        });

        assertTrue(exception.getMessage().contains("strategia di pagamento non può essere null"));
    }

    @Test
    void testConfermaPagamentoConStatoNonValido() {
        // Arrange
        ordine.paga(); // Porta l'ordine a PRONTO_PER_LAVORAZIONE
        IMetodoPagamentoStrategy strategia = new PagamentoSimulatoStrategy();

        // Act & Assert
        OrdineException exception = assertThrows(OrdineException.class, () -> {
            ordineService.confermaPagamento(ordine, strategia);
        });

        assertTrue(exception.getMessage().toLowerCase().contains("non è in attesa di pagamento"));
    }

    @Test
    void testConfermaPagamentoConCartaCredito() throws OrdineException, PagamentoException {
        // Arrange
        IMetodoPagamentoStrategy strategiaCarta = new PagamentoCartaCreditoStrategy();
        assertEquals(StatoCorrente.ATTESA_PAGAMENTO, ordine.getStatoOrdine());

        // Act
        ordineService.confermaPagamento(ordine, strategiaCarta);

        // Assert
        assertEquals(StatoCorrente.PRONTO_PER_LAVORAZIONE, ordine.getStatoOrdine());
    }

    @Test
    void testConfermaPagamentoConPayPal() throws OrdineException, PagamentoException {
        // Arrange
        IMetodoPagamentoStrategy strategiaPayPal = new PagamentoPayPalStrategy();
        assertEquals(StatoCorrente.ATTESA_PAGAMENTO, ordine.getStatoOrdine());

        // Act
        ordineService.confermaPagamento(ordine, strategiaPayPal);

        // Assert
        assertEquals(StatoCorrente.PRONTO_PER_LAVORAZIONE, ordine.getStatoOrdine());
    }

    @Test
    void testConfermaPagamentoConStrategiaCheLanciaPagamentoException() {
        // Arrange
        IMetodoPagamentoStrategy strategiaConEccezione = new IMetodoPagamentoStrategy() {
            @Override
            public boolean elaboraPagamento(Ordine ordine) throws PagamentoException {
                throw new PagamentoException("Errore di connessione al gateway di pagamento");
            }
        };

        // Act & Assert
        PagamentoException exception = assertThrows(PagamentoException.class, () -> {
            ordineService.confermaPagamento(ordine, strategiaConEccezione);
        });

        // Verifica che l'ordine rimanga nello stato originale
        assertEquals(StatoCorrente.ATTESA_PAGAMENTO, ordine.getStatoOrdine());
        assertEquals("Errore di connessione al gateway di pagamento", exception.getMessage());
    }

    @Test
    void testConfermaPagamentoConStrategiaCheLanciaRuntimeException() {
        // Arrange
        IMetodoPagamentoStrategy strategiaConEccezione = new IMetodoPagamentoStrategy() {
            @Override
            public boolean elaboraPagamento(Ordine ordine) throws PagamentoException {
                throw new RuntimeException("Errore imprevisto");
            }
        };

        // Act & Assert
        OrdineException exception = assertThrows(OrdineException.class, () -> {
            ordineService.confermaPagamento(ordine, strategiaConEccezione);
        });

        // Verifica che l'ordine rimanga nello stato originale
        assertEquals(StatoCorrente.ATTESA_PAGAMENTO, ordine.getStatoOrdine());
        assertTrue(exception.getMessage().contains("Errore imprevisto"));
    }

    @Test
    void testFlussibilityDelPatternStrategy() throws OrdineException, PagamentoException {
        // Test che dimostra la flessibilità del pattern Strategy
        // utilizzando diverse strategie per lo stesso ordine (in scenari diversi)
        
        // Scenario 1: Pagamento con carta di credito
        Ordine ordine1 = new Ordine(2, new Date(), acquirente);
        ordine1.setImportoTotale(25.0);
        ordineService.confermaPagamento(ordine1, new PagamentoCartaCreditoStrategy());
        assertEquals(StatoCorrente.PRONTO_PER_LAVORAZIONE, ordine1.getStatoOrdine());

        // Scenario 2: Pagamento con PayPal
        Ordine ordine2 = new Ordine(3, new Date(), acquirente);
        ordine2.setImportoTotale(75.0);
        ordineService.confermaPagamento(ordine2, new PagamentoPayPalStrategy());
        assertEquals(StatoCorrente.PRONTO_PER_LAVORAZIONE, ordine2.getStatoOrdine());

        // Scenario 3: Pagamento simulato
        Ordine ordine3 = new Ordine(4, new Date(), acquirente);
        ordine3.setImportoTotale(100.0);
        ordineService.confermaPagamento(ordine3, new PagamentoSimulatoStrategy());
        assertEquals(StatoCorrente.PRONTO_PER_LAVORAZIONE, ordine3.getStatoOrdine());
    }
}