package it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Pacchetto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.Acquistabile;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DistributoreDiTipicita;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

/**
 * Implementazione concreta di {@link IPacchettoService} che gestisce la logica
 * per la creazione, gestione e modifica dei pacchetti di prodotti nella piattaforma agricola locale.
 *
 */
public class PacchettoService implements IPacchettoService {

    private List<Pacchetto> catalogoPacchetti = new ArrayList<>();

    @Override
    public void creaPacchetto(DistributoreDiTipicita distributore, String nome, String descrizione,
            double prezzoPacchetto) {
        int idPacchetto = UUID.randomUUID().hashCode();
        Pacchetto pacchetto = new Pacchetto(distributore, idPacchetto, nome, descrizione, prezzoPacchetto);
        distributore.getPacchettiOfferti().add(pacchetto);
        this.catalogoPacchetti.add(pacchetto);
        
    }

    @Override
    public Pacchetto getPacchettoById(int id) {
        for (Pacchetto pacchetto : catalogoPacchetti) {
            if (pacchetto.getId() == id) {
                return pacchetto;
            }
        }
        return null;
    }

    @Override
    public List<Pacchetto> getAllPacchetti() {
        return new ArrayList<>(catalogoPacchetti);
    }

    @Override
    public boolean aggiungiPacchettoCatalogo(Pacchetto pacchetto) {
        if (pacchetto != null && !catalogoPacchetti.contains(pacchetto)) {
            return catalogoPacchetti.add(pacchetto);
        }
        return false;
    }

    @Override
    public boolean rimuoviPacchettoCatalogo(Pacchetto pacchetto) {
        if (pacchetto != null) {
            boolean rimossoDalCatalogo = catalogoPacchetti.remove(pacchetto);
            if (rimossoDalCatalogo && pacchetto.getDistributore() != null) {
                pacchetto.getDistributore().getPacchettiOfferti().remove(pacchetto);
            }
            return rimossoDalCatalogo;
        }
        return false;
    }

    @Override
    public boolean aggiungiProdottoAlPacchetto(Pacchetto pacchetto, Acquistabile prodotto) {
        if (pacchetto != null && prodotto != null && pacchetto.getElementiInclusi() != null) {
            pacchetto.aggiungiElemento(prodotto);
            return true;
        }
        return false;
    }

    @Override
    public boolean rimuoviProdottoDalPacchetto(Pacchetto pacchetto, Acquistabile prodotto) {
        if (pacchetto != null && prodotto != null && pacchetto.getElementiInclusi() != null) {
            return pacchetto.getElementiInclusi().remove(prodotto);
        }
        return false;
    }

    @Override
    public void mostraPacchetti(DistributoreDiTipicita distributore) {
        for (Pacchetto pacchetto : distributore.getPacchettiOfferti()) {
            System.out.println("Pacchetto: " + pacchetto.getNome() + ", Prezzo: " + pacchetto.getPrezzo());
            stampaProdottiPacchetto(pacchetto);
        }
    }

    //Metodo per stampare a video i prodotti che sono dentro un pacchetto
    public void stampaProdottiPacchetto(Pacchetto pacchetto) {
        System.out.println("Prodotti nel pacchetto " + pacchetto.getNome() + ":");
        for (Acquistabile elemento : pacchetto.getElementiInclusi()) {
            if (elemento instanceof Prodotto) {
                Prodotto prodotto = (Prodotto) elemento;
                System.out.println("- " + prodotto.getNome() + " (ID: " + prodotto.getId() + ")");
            }
        }
    }

}
