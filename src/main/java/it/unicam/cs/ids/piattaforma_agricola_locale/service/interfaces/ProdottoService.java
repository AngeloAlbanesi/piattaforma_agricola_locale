package it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Certificazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.ProdottoRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

/**
 * Implementazione concreta di {@link IProdottoService} che gestisce la logica
 * per la gestione dei prodotti offerti dai venditori nella piattaforma agricola
 * locale.
 *
 */
public class ProdottoService implements IProdottoService {

    // private List<Prodotto> catalogoProdotti = new ArrayList<>();
    private ProdottoRepository prodottoRepository = new ProdottoRepository();

    @Override
    public void creaProdotto(String nome, String descrizione, double prezzo, int quantitaDisponibile,
            Venditore venditore) {
        if (nome == null || descrizione == null || prezzo <= 0 || quantitaDisponibile <= 0 || venditore == null) {
            return;
        }
        int idProdotto = UUID.randomUUID().hashCode();
        Prodotto prodotto = new Prodotto(idProdotto, nome, descrizione, prezzo, quantitaDisponibile, venditore);
        // this.catalogoProdotti.add(prodotto);
        venditore.getProdottiOfferti().add(prodotto);
        prodottoRepository.save(prodotto);
    }

    @Override
    public List<Prodotto> getProdottiOfferti(Venditore venditore) {
        return venditore.getProdottiOfferti();
    }

    // TODO contrallare se lasciare boolean o void
    @Override
    public boolean rimuoviProdottoCatalogo(Venditore venditore, Prodotto prodotto) {
        if (venditore != prodotto.getVenditore())
            return false; // TODO cambiare con eccezione??
        return prodotto.getVenditore().getProdottiOfferti().remove(prodotto);
    }

    // TODO contrallare se lasciare boolean o void
    @Override
    public boolean aggiornaQuantitaProdotto(Venditore venditore, Prodotto prodotto, int nuovaQuantita) {
        if (venditore != prodotto.getVenditore())
            return false; // TODO cambiare con eccezione??
        if (prodotto == null || nuovaQuantita <= 0) {
            return false;
        }
        prodotto.setQuantitaDisponibile(nuovaQuantita);

        return true;
    }

    // TODO contralle se lasciare boolean o void
    @Override
    public boolean aggiungiQuantitaProdotto(Venditore venditore, Prodotto prodotto, int quantitaAggiunta) {
        if (venditore != prodotto.getVenditore())
            return false; // TODO cambiare con eccezione??
        if (prodotto == null || quantitaAggiunta <= 0) {
            return false;
        }
        prodotto.setQuantitaDisponibile(prodotto.getQuantitaDisponibile() + quantitaAggiunta);

        return true;
    }

    // TODO contralle se lasciare boolean o void
    @Override
    public boolean rimuoviQuantitaProdotto(Venditore venditore, Prodotto prodotto, int quantitaRimossa) {
        if (venditore != prodotto.getVenditore())
            return false; // TODO cambiare con eccezione??
        if (prodotto == null || prodotto.getQuantitaDisponibile() - quantitaRimossa < 0) {
            return false;
        }
        prodotto.setQuantitaDisponibile(prodotto.getQuantitaDisponibile() - quantitaRimossa);

        return true;
    }

    public void mostraProdotti(Venditore venditore) {
        for (Prodotto p : venditore.getProdottiOfferti()) {
            System.out.println(p.getNome() + " - " + p.getPrezzo() + "€ - disponibili:" + p.getQuantitaDisponibile());
            stampaCertificazioni(p);
        }
    }

    public void aggiungiCertificazione(String nomeCertificazione, String enteRilascio, Date dataRilascio,
            Date dataScadenza, Prodotto prodotto) {
        int idCertificazione = UUID.randomUUID().hashCode();
        Certificazione certificazione = new Certificazione(idCertificazione, nomeCertificazione, enteRilascio,
                dataRilascio, dataScadenza);
        prodotto.getCertificazioni().add(certificazione);
    }

    public void stampaCertificazioni(Prodotto prodotto) {
        List<Certificazione> certificazioni = prodotto.getCertificazioni();
        for (Certificazione c : certificazioni) {
            c.stampaCertificazione();
        }
    }

    public ProdottoRepository getProdottoRepository() {
        return prodottoRepository;
    }
}