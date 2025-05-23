package it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces;

import java.util.ArrayList;
import java.util.List;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Ingrediente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.catalogo.Prodotto;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.common.StatoVerificaValori;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.UtenteRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.DatiAzienda;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

public class CuratoreService implements ICuratoreService {

    private UtenteRepository utenteRepository;

    @Override
    public List<DatiAzienda> getDatiAziendaInAttesaRevisione() {
        List<DatiAzienda> datiAziendaInAttesa = new ArrayList<>();
        for (Venditore venditore : utenteRepository.findAllVenditori()) {
            if (venditore.getDatiAzienda().getStatoVerifica() == StatoVerificaValori.IN_REVISIONE) {
                datiAziendaInAttesa.add(venditore.getDatiAzienda());
            }
        }
        return datiAziendaInAttesa;
    }

    @Override
    public void approvaDatiAzienda(Venditore venditore, String feedbackVerifica) {
        if (venditore.getDatiAzienda().getStatoVerifica() == StatoVerificaValori.IN_REVISIONE) {
            venditore.getDatiAzienda().setStatoVerifica(StatoVerificaValori.APPROVATO);
            venditore.getDatiAzienda().setFeedbackVerifica(feedbackVerifica);
        }
    }

    @Override
    public void respingiDatiAzienda(Venditore venditore, String feedbackVerifica) {
        if (venditore.getDatiAzienda().getStatoVerifica() == StatoVerificaValori.IN_REVISIONE) {
            venditore.getDatiAzienda().setStatoVerifica(StatoVerificaValori.RESPINTO);
            venditore.getDatiAzienda().setFeedbackVerifica(feedbackVerifica);
        }
    }

    @Override
    public void approvaProdotto(Prodotto prodotto, String feedbackVerifica) {
        if (prodotto.getStatoVerifica() == StatoVerificaValori.IN_REVISIONE) {
            prodotto.setStatoVerifica(StatoVerificaValori.APPROVATO);
            prodotto.setFeedbackVerifica(feedbackVerifica);
        }
    }

    @Override
    public void respingiProdotto(Prodotto prodotto, String feedbackVerifica) {
        if (prodotto.getStatoVerifica() == StatoVerificaValori.IN_REVISIONE) {
            prodotto.setStatoVerifica(StatoVerificaValori.RESPINTO);
            prodotto.setFeedbackVerifica(feedbackVerifica);
        }
    }

    @Override
    public void verificaIngredientiLocaliTrasformatore(Ingrediente ingrediente) {
        // TODO: Implementa la logica per verificare gli ingredienti locali
    }

    @Override
    public List<Prodotto> getProdottiInAttesaRevisione() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProdottiInAttesaRevisione'");
    }

    @Override
    public List<Prodotto> getProdottiInAttesaRevisioneByVenditore(Venditore venditore) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProdottiInAttesaRevisioneByVenditore'");
    }
}
