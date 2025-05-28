package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Pacchetto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.StatoVerificaValori;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Produttore;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DistributoreDiTipicita;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.TipoRuolo;
import it.unicam.cs.ids.piattaforma_agricola_locale.exception.QuantitaNonDisponibileException;
import java.util.ArrayList;

/**
 * Test class per verificare il corretto funzionamento dei metodi
 * decrementaQuantita in ProdottoService e PacchettoService
 */
public class DecrementaQuantitaTest {

    private ProdottoService prodottoService;
    private PacchettoService pacchettoService;
    private Produttore produttore;
    private DistributoreDiTipicita distributore;
    private Prodotto prodotto;
    private Pacchetto pacchetto;

    @BeforeEach
    void setUp() {
        prodottoService = new ProdottoService();
        pacchettoService = new PacchettoService();

        // Crea dati azienda di test
        DatiAzienda datiAzienda = new DatiAzienda("Test Farm", "123456789", "Via Test 123", "Azienda di test",
                "logo.jpg", "www.test.com");

        // Crea produttore e distributore di test
        produttore = new Produttore(1, "Mario", "Rossi", "test@produttore.com", "password", "1234567890", datiAzienda,
                new ArrayList<>(), TipoRuolo.PRODUTTORE, true);
        distributore = new DistributoreDiTipicita(2, "Luca", "Bianchi", "test@distributore.com", "password",
                "0987654321", datiAzienda, new ArrayList<>(), TipoRuolo.DISTRIBUTORE_DI_TIPICITA, true);

        // Crea un prodotto di test
        prodottoService.creaProdotto("Pomodori", "Pomodori freschi", 5.0, 100, produttore);
        // Prendi il prodotto creato (dovrebbe essere l'ultimo nell'elenco)
        if (!produttore.getProdottiOfferti().isEmpty()) {
            prodotto = produttore.getProdottiOfferti().get(produttore.getProdottiOfferti().size() - 1);
            prodotto.setStatoVerifica(StatoVerificaValori.APPROVATO);
        }

        // Crea un pacchetto di test
        pacchettoService.creaPacchetto(distributore, "Pacchetto Test", "Pacchetto di prodotti", 50, 25.0);
        // Prendi il pacchetto creato
        if (!distributore.getPacchettiOfferti().isEmpty()) {
            pacchetto = distributore.getPacchettiOfferti().get(distributore.getPacchettiOfferti().size() - 1);
        }
    }

    @Test
    void testDecrementaQuantitaProdotto_Success() {
        // Test caso normale: decrementa 10 unità da 100 disponibili
        int quantitaIniziale = prodotto.getQuantitaDisponibile();
        int quantitaDaDecrementare = 10;

        prodottoService.decrementaQuantita(prodotto.getId(), quantitaDaDecrementare);

        assertEquals(quantitaIniziale - quantitaDaDecrementare, prodotto.getQuantitaDisponibile());
    }

    @Test
    void testDecrementaQuantitaProdotto_QuantitaInsufficiente() {
        // Test caso errore: decrementa 150 unità da 100 disponibili
        int quantitaDaDecrementare = 150;

        assertThrows(QuantitaNonDisponibileException.class, () -> {
            prodottoService.decrementaQuantita(prodotto.getId(), quantitaDaDecrementare);
        });
    }

    @Test
    void testDecrementaQuantitaProdotto_QuantitaNegativa() {
        // Test caso errore: quantità negativa
        assertThrows(IllegalArgumentException.class, () -> {
            prodottoService.decrementaQuantita(prodotto.getId(), -5);
        });
    }

    @Test
    void testDecrementaQuantitaProdotto_ProdottoInesistente() {
        // Test caso errore: prodotto inesistente
        assertThrows(IllegalArgumentException.class, () -> {
            prodottoService.decrementaQuantita(99999, 10);
        });
    }

    @Test
    void testDecrementaQuantitaPacchetto_Success() {
        // Test caso normale: decrementa 5 unità da 50 disponibili
        int quantitaIniziale = pacchetto.getQuantitaDisponibile();
        int quantitaDaDecrementare = 5;

        pacchettoService.decrementaQuantita(pacchetto.getId(), quantitaDaDecrementare);

        assertEquals(quantitaIniziale - quantitaDaDecrementare, pacchetto.getQuantitaDisponibile());
    }

    @Test
    void testDecrementaQuantitaPacchetto_QuantitaInsufficiente() {
        // Test caso errore: decrementa 75 unità da 50 disponibili
        int quantitaDaDecrementare = 75;

        assertThrows(QuantitaNonDisponibileException.class, () -> {
            pacchettoService.decrementaQuantita(pacchetto.getId(), quantitaDaDecrementare);
        });
    }

    @Test
    void testDecrementaQuantitaPacchetto_QuantitaNegativa() {
        // Test caso errore: quantità negativa
        assertThrows(IllegalArgumentException.class, () -> {
            pacchettoService.decrementaQuantita(pacchetto.getId(), -3);
        });
    }

    @Test
    void testDecrementaQuantitaPacchetto_PacchettoInesistente() {
        // Test caso errore: pacchetto inesistente
        assertThrows(IllegalArgumentException.class, () -> {
            pacchettoService.decrementaQuantita(99999, 5);
        });
    }
}
