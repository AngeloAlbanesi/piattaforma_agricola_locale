package it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.Evento;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.StatoEventoValori;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.AnimatoreDellaFiliera;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;

import java.util.Date;

public interface IEventoService {
    void creaEvento(String nomeEvento, String descrizione,
                    Date dataOraInizio, Date dataOraFine, String luogoEvento,
                    int capienzaMassima, AnimatoreDellaFiliera organizzatore);
    void aggiornaEvento(Long idEvento, String nuovoNomeEvento, String nuovaDescrizione,
                        Date nuovaDataOraInizio, Date nuovaDataOraFine, String nuovoLuogoEvento,
                        int nuovaCapienzaMassima, AnimatoreDellaFiliera organizzatore);
    void eliminaEvento(Long idEvento,AnimatoreDellaFiliera organizzatore);

    void aggiungiAziendaPartecipante(Long idEvento, Venditore venditore);

    void rimuoviAziendaPartecipante(Long idEvento, Venditore venditore);

    void prenotaPosti(Evento evento, int quantita);

    void eliminaPostiPrenotati(Evento evento, int quantita);

    void iniziaEvento(Long idEvento,AnimatoreDellaFiliera organizzatore);

    void terminaEvento(Long idEvento,AnimatoreDellaFiliera organizzatore);

    void annullaEvento(Long idEvento,AnimatoreDellaFiliera organizzatore);

}
