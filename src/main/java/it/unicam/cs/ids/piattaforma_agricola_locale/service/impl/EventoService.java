package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.Evento;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.StatoEventoValori;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.EventoRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IEventoRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.AnimatoreDellaFiliera;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IEventoService;

import java.util.Date;

public class EventoService implements IEventoService {

    private final IEventoRepository eventoRepository;

    public EventoService(IEventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    public EventoService() {
        this.eventoRepository = new EventoRepository();
    }

    @Override
    public void creaEvento(String nomeEvento, String descrizione,
            Date dataOraInizio, Date dataOraFine, String luogoEvento,
            int capienzaMassima, AnimatoreDellaFiliera organizzatore) {
        if (nomeEvento == null || nomeEvento.isEmpty()) {
            throw new IllegalArgumentException("Il nome dell'evento non può essere vuoto.");
        }
        if (dataOraInizio == null || dataOraFine == null) {
            throw new IllegalArgumentException("Le date di inizio e fine dell'evento non possono essere nulle.");
        }
        if (dataOraInizio.after(dataOraFine)) {
            throw new IllegalArgumentException("La data di inizio non può essere successiva alla data di fine.");
        }
        if (capienzaMassima <= 0) {
            throw new IllegalArgumentException("La capienza massima deve essere un numero positivo.");
        }
        if (organizzatore == null) {
            throw new IllegalArgumentException("L'organizzatore dell'evento non può essere nullo.");
        }

        Evento evento = new Evento(nomeEvento, descrizione,
                dataOraInizio, dataOraFine, luogoEvento,
                capienzaMassima, organizzatore);
        eventoRepository.save(evento);
    }

    @Override
    public void aggiornaEvento(Long idEvento, String nuovoNomeEvento, String nuovaDescrizione,
            Date nuovaDataOraInizio, Date nuovaDataOraFine, String nuovoLuogoEvento,
            int nuovaCapienzaMassima, AnimatoreDellaFiliera organizzatore) {

        Evento evento = eventoRepository.findById(idEvento);
        if (evento == null) {
            throw new IllegalArgumentException("Evento con ID " + idEvento + " non trovato.");
        }
        if (!evento.getOrganizzatore().equals(organizzatore)) {
            throw new IllegalArgumentException("Questo animatore non ha i permessi per modificare l'evento");
        }
        if (nuovoNomeEvento != null)
            evento.setNome(nuovoNomeEvento);
        if (nuovaDescrizione != null)
            evento.setDescrizione(nuovaDescrizione);
        if (nuovaDataOraInizio != null) {
            if (nuovaDataOraInizio.after(nuovaDataOraFine)) {
                throw new IllegalArgumentException(
                        "La nuova data di inizio non può essere successiva alla data di fine.");
            }
            evento.setDataOraInizio(nuovaDataOraInizio);
        }
        if (nuovaDataOraFine != null) {
            if (nuovaDataOraFine.before(nuovaDataOraInizio)) {
                throw new IllegalArgumentException(
                        "La nuova data di fine non può essere precedente alla data di inizio.");
            }
            evento.setDataOraFine(nuovaDataOraFine);
        }
        if (nuovoLuogoEvento != null)
            evento.setLuogoEvento(nuovoLuogoEvento);
        if (nuovaCapienzaMassima > 0)
            evento.setCapienzaMassima(nuovaCapienzaMassima);
        eventoRepository.save(evento); // L'implementazione di save deve gestire l'aggiornamento
    }

    @Override
    public void eliminaEvento(Long idEvento, AnimatoreDellaFiliera organizzatore) {
        Evento evento = eventoRepository.findById(idEvento);
        if (evento == null) {
            throw new IllegalArgumentException("Evento con ID " + idEvento + " non trovato.");
        }
        if (!evento.getOrganizzatore().equals(organizzatore)) {
            throw new IllegalArgumentException("Questo animatore non ha i permessi per modificare l'evento");
        }
        eventoRepository.deleteById(idEvento); // L'implementazione di delete deve gestire la rimozione
    }

    @Override
    public void aggiungiAziendaPartecipante(Long idEvento, Venditore venditore) {
        Evento evento = eventoRepository.findById(idEvento);
        if (evento == null) {
            throw new IllegalArgumentException("Evento con ID " + idEvento + " non trovato.");
        }
        if (venditore == null) {
            throw new IllegalArgumentException("Il venditore non può essere nullo.");
        }
        if (evento.getAziendePartecipanti().contains(venditore)) {
            throw new IllegalArgumentException("Il venditore è già un partecipante all'evento.");
        }
        evento.addAziendaPartecipante(venditore);
        eventoRepository.save(evento); // L'implementazione di save deve gestire l'aggiornamento
    }

    @Override
    public void rimuoviAziendaPartecipante(Long idEvento, Venditore venditore) {
        // Implementazione della rimozione di un'azienda partecipante da un evento
        Evento evento = eventoRepository.findById(idEvento);
        if (evento == null) {
            throw new IllegalArgumentException("Evento con ID " + idEvento + " non trovato.");
        }
        if (venditore == null) {
            throw new IllegalArgumentException("Il venditore non può essere nullo.");
        }
        if (!evento.getAziendePartecipanti().contains(venditore)) {
            throw new IllegalArgumentException("Il venditore non è un partecipante all'evento.");
        }
        evento.removeAziendaPartecipante(venditore);
        eventoRepository.save(evento); // L'implementazione di save deve gestire l'aggiornamento

    }

    @Override
    public void prenotaPosti(Evento evento, int quantita) {
        // Implementazione della prenotazione di posti per un evento
        if (evento == null) {
            throw new IllegalArgumentException("L'evento non può essere nullo.");
        }
        if (quantita <= 0) {
            throw new IllegalArgumentException("La quantità di posti da prenotare deve essere un numero positivo.");
        }
        if (evento.getPostiDisponibili() < quantita) {
            throw new IllegalArgumentException(
                    "Non ci sono abbastanza posti disponibili per prenotare " + quantita + " posti.");
        }
        evento.setPostiAttualmentePrenotati(evento.getPostiAttualmentePrenotati() + quantita);
        eventoRepository.save(evento); // L'implementazione di save deve gestire l'aggiornamento
    }

    @Override
    public void eliminaPostiPrenotati(Evento evento, int quantita) {
        // Implementazione della cancellazione di posti prenotati per un evento
        if (evento == null) {
            throw new IllegalArgumentException("L'evento non può essere nullo.");
        }
        if (quantita <= 0) {
            throw new IllegalArgumentException("La quantità di posti da cancellare deve essere un numero positivo.");
        }
        if (evento.getPostiAttualmentePrenotati() < quantita) {
            throw new IllegalArgumentException(
                    "Non ci sono abbastanza posti prenotati per cancellare " + quantita + " posti.");
        }
        evento.setPostiAttualmentePrenotati(evento.getPostiAttualmentePrenotati() - quantita);
        eventoRepository.save(evento); // L'implementazione di save deve gestire l'aggiornamento
    }

    @Override
    public void iniziaEvento(Long idEvento, AnimatoreDellaFiliera organizzatore) {
        Evento evento = eventoRepository.findById(idEvento);
        if (evento == null) {
            throw new IllegalArgumentException("Evento con ID " + idEvento + " non trovato.");
        }
        if (!evento.getOrganizzatore().equals(organizzatore)) {
            throw new IllegalArgumentException("Questo animatore non ha i permessi per modificare l'evento");
        }
        if (evento.getStatoEvento() != StatoEventoValori.IN_PROGRAMMA) {
            throw new IllegalArgumentException("L'evento non può essere iniziato se non è in programma.");
        }
        evento.setStatoEvento(StatoEventoValori.IN_CORSO);
        eventoRepository.save(evento); // L'implementazione di save deve gestire l'aggiornamento
    }

    @Override
    public void terminaEvento(Long idEvento, AnimatoreDellaFiliera organizzatore) {
        Evento evento = eventoRepository.findById(idEvento);
        if (evento == null) {
            throw new IllegalArgumentException("Evento con ID " + idEvento + " non trovato.");
        }
        if (!evento.getOrganizzatore().equals(organizzatore)) {
            throw new IllegalArgumentException("Questo animatore non ha i permessi per modificare l'evento");
        }
        if (evento.getStatoEvento() != StatoEventoValori.IN_CORSO) {
            throw new IllegalArgumentException("L'evento non può essere terminato se non è in corso.");
        }
        evento.setStatoEvento(StatoEventoValori.CONCLUSO);
        eventoRepository.save(evento); // L'implementazione di save deve gestire l'aggiornamento
    }

    @Override
    public void annullaEvento(Long idEvento, AnimatoreDellaFiliera organizzatore) {
        Evento evento = eventoRepository.findById(idEvento);
        if (evento == null) {
            throw new IllegalArgumentException("Evento con ID " + idEvento + " non trovato.");
        }
        if (!evento.getOrganizzatore().equals(organizzatore)) {
            throw new IllegalArgumentException("Questo animatore non ha i permessi per modificare l'evento");
        }
        if (evento.getStatoEvento() == StatoEventoValori.CONCLUSO) {
            throw new IllegalArgumentException("L'evento non può essere annullato se è già concluso.");
        }
        evento.setStatoEvento(StatoEventoValori.ANNULLATO);
        eventoRepository.save(evento); // L'implementazione di save deve gestire l'aggiornamento
    }
}
