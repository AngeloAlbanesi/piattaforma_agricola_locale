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
    void aggiornaEvento(int idEvento, String nuovoNomeEvento, String nuovaDescrizione,
                        Date nuovaDataOraInizio, Date nuovaDataOraFine, String nuovoLuogoEvento,
                        int nuovaCapienzaMassima);
    void eliminaEvento(int idEvento);

    void aggiungiAziendaPartecipante(int idEvento, Venditore venditore);

    void rimuoviAziendaPartecipante(int idEvento, Venditore venditore);

    void prenotaPosti(Evento evento, int quantita);

    void eliminaPostiPrenotati(Evento evento, int quantita);

    void iniziaEvento(int idEvento);

    void terminaEvento(int idEvento);

    void annullaEvento(int idEvento);

}
