package it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.social.PromoteRequestDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.social.ShareResponseDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.Evento;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.EventoRegistrazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.StatoEventoValori;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.AnimatoreDellaFiliera;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Utente;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    // Public catalog methods
    Page<Evento> getAllEventi(Pageable pageable);
    Optional<Evento> getEventoById(Long id);
    List<Evento> searchEventiByNome(String nome);
    List<Evento> getEventiByOrganizzatore(Long organizzatoreId);
    
    // Event registration methods
    EventoRegistrazione registraUtenteEvento(Long idEvento, Utente utente, int numeroPosti, String note);
    void cancellaRegistrazioneUtente(Long idEvento, Utente utente);
    boolean isUtenteRegistrato(Long idEvento, Utente utente);
    List<EventoRegistrazione> getRegistrazioniEvento(Long idEvento);
    List<EventoRegistrazione> getRegistrazioniUtente(Utente utente);

    Optional<ShareResponseDTO> promuoviEvento(Long id, PromoteRequestDTO request);
}
