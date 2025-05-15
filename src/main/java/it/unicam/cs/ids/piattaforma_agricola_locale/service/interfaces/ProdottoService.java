package it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Pacchetto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.Acquistabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

/**
 * Implementazione concreta di {@link IProdottoService} che gestisce la logica
 * per la gestione dei prodotti offerti dai venditori nella piattaforma agricola
 * locale.
 *
 */
public class ProdottoService implements IProdottoService {

    private List<Prodotto> catalogoProdotti = new ArrayList<>();

    @Override
    public void creaProdotto(String nome, String descrizione, double prezzo, int quantitaDisponibile,
            Venditore venditore) {
        if (nome == null || descrizione == null || prezzo <= 0 || quantitaDisponibile <= 0 || venditore == null) {
            return;
        }
        int idProdotto = UUID.randomUUID().hashCode();
        Prodotto prodotto = new Prodotto(idProdotto, nome, descrizione, prezzo, quantitaDisponibile, venditore);
        this.catalogoProdotti.add(prodotto);
        venditore.getProdottiOfferti().add(prodotto);
    }

    @Override
    public List<Prodotto> getProdottiOfferti(Venditore venditore) {
        return venditore.getProdottiOfferti();
    }

    @Override
    public boolean aggiungiProdottoCatalogo(Prodotto prodotto) {
        if (prodotto == null || prodotto.getNome() == null || prodotto.getDescrizione() == null
                || prodotto.getPrezzo() <= 0 || prodotto.getQuantitaDisponibile() <= 0) {
            return false;
        }
        prodotto.getVenditore().getProdottiOfferti().add(prodotto);
        return true;
    }

    @Override
    public boolean rimuoviProdottoCatalogo(Prodotto prodotto) {
        return prodotto.getVenditore().getProdottiOfferti().remove(prodotto);
    }

    @Override
    public boolean aggiornaProdottoCatalogo(Prodotto prodotto, int nuovaQuantita) {
        if (prodotto == null || nuovaQuantita <= 0) {
            return false;
        }
        prodotto.aggiornaQuantitaDisponibile(nuovaQuantita);
        return true;
    }

}