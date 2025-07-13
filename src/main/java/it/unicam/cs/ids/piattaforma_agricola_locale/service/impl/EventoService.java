package it.unicam.cs.ids.piattaforma_agricola_locale.service.impl;

import it.unicam.cs.ids.piattaforma_agricola_locale.dto.social.PromoteRequestDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.dto.social.ShareResponseDTO;
import it.unicam.cs.ids.piattaforma_agricola_locale.exception.BusinessRuleViolationException;
import it.unicam.cs.ids.piattaforma_agricola_locale.exception.ResourceOwnershipException;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.EventoRegistrazione;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IEventoRegistrazioneRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Utente;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.Evento;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.eventi.StatoEventoValori;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IEventoRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.repository.IAnimatoreRepository;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.AnimatoreDellaFiliera;
import it.unicam.cs.ids.piattaforma_agricola_locale.model.utenti.Venditore;
import it.unicam.cs.ids.piattaforma_agricola_locale.service.interfaces.IEventoService;

@Service
public class EventoService implements IEventoService {

    private final IEventoRepository eventoRepository;
    private final IAnimatoreRepository animatoreRepository;
    private final IEventoRegistrazioneRepository eventoRegistrazioneRepository;

    @Autowired
    public EventoService(IEventoRepository eventoRepository, 
                         IAnimatoreRepository animatoreRepository,
                         IEventoRegistrazioneRepository eventoRegistrazioneRepository) {
        this.eventoRepository = eventoRepository;
        this.eventoRegistrazioneRepository = eventoRegistrazioneRepository;
        this.animatoreRepository = animatoreRepository;
    }

    @Override
    public Evento creaEvento(String nomeEvento, String descrizione,

                           Date dataOraInizio, Date dataOraFine, String luogoEvento,
                           int capienzaMassima, AnimatoreDellaFiliera organizzatore) {
        if(nomeEvento == null || nomeEvento.isEmpty()) {

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

        return eventoRepository.save(evento);
    }

    @Override
    public void aggiornaEvento(Long idEvento, String nuovoNomeEvento, String nuovaDescrizione,

                              Date nuovaDataOraInizio, Date nuovaDataOraFine, String nuovoLuogoEvento,
                              int nuovaCapienzaMassima,AnimatoreDellaFiliera organizzatore) {


        Evento evento = eventoRepository.findById(idEvento)
                .orElseThrow(() -> new IllegalArgumentException("Evento con ID " + idEvento + " non trovato."));
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

    public void eliminaEvento(Long idEvento,AnimatoreDellaFiliera organizzatore  ) {
        Evento evento = eventoRepository.findById(idEvento)
                .orElseThrow(() -> new IllegalArgumentException("Evento con ID " + idEvento + " non trovato."));
        if (!evento.getOrganizzatore().equals(organizzatore)) {
            throw new IllegalArgumentException("Questo animatore non ha i permessi per modificare l'evento");
        }
        eventoRepository.deleteById(idEvento); // L'implementazione di delete deve gestire la rimozione
    }

    @Override
    public void aggiungiAziendaPartecipante(Long idEvento, Venditore venditore) {
        Evento evento = eventoRepository.findById(idEvento)
                .orElseThrow(() -> new IllegalArgumentException("Evento con ID " + idEvento + " non trovato."));
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
        Evento evento = eventoRepository.findById(idEvento)
                .orElseThrow(() -> new IllegalArgumentException("Evento con ID " + idEvento + " non trovato."));
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
        Evento evento = eventoRepository.findById(idEvento)
                .orElseThrow(() -> new IllegalArgumentException("Evento con ID " + idEvento + " non trovato."));
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
        Evento evento = eventoRepository.findById(idEvento)
                .orElseThrow(() -> new IllegalArgumentException("Evento con ID " + idEvento + " non trovato."));
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

    public void annullaEvento(Long idEvento,AnimatoreDellaFiliera organizzatore) {

        Evento evento = eventoRepository.findById(idEvento)
                .orElseThrow(() -> new IllegalArgumentException("Evento con ID " + idEvento + " non trovato."));
        if (!evento.getOrganizzatore().equals(organizzatore)) {
            throw new IllegalArgumentException("Questo animatore non ha i permessi per modificare l'evento");
        }
        if (evento.getStatoEvento() == StatoEventoValori.CONCLUSO) {
            throw new IllegalArgumentException("L'evento non può essere annullato se è già concluso.");
        }
        evento.setStatoEvento(StatoEventoValori.ANNULLATO);
        eventoRepository.save(evento); // L'implementazione di save deve gestire l'aggiornamento
    }

    // ===== PUBLIC CATALOG METHODS =====
    
    @Override
    public Page<Evento> getAllEventi(Pageable pageable) {
        return eventoRepository.findAll(pageable);
    }

    @Override
    public Optional<Evento> getEventoById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID evento non può essere null");
        }
        return eventoRepository.findById(id);
    }

    @Override
    public List<Evento> searchEventiByNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome di ricerca non può essere vuoto");
        }
        return eventoRepository.findByNomeEventoContainingIgnoreCase(nome.trim());
    }

    @Override
    public List<Evento> getEventiByOrganizzatore(Long organizzatoreId) {
        if (organizzatoreId == null) {
            throw new IllegalArgumentException("ID organizzatore non può essere null");
        }
        AnimatoreDellaFiliera organizzatore = animatoreRepository.findById(organizzatoreId)
                .orElseThrow(() -> new IllegalArgumentException("Organizzatore con ID " + organizzatoreId + " non trovato"));
        return eventoRepository.findByOrganizzatore(organizzatore);
    }
    
    // ===== EVENT REGISTRATION METHODS =====
    
    @Override
    public EventoRegistrazione registraUtenteEvento(Long idEvento, Utente utente, int numeroPosti, String note) {
        if (utente == null) {
            throw new IllegalArgumentException("L'utente non può essere nullo");
        }
        
        Evento evento = eventoRepository.findById(idEvento)
                .orElseThrow(() -> new IllegalArgumentException("Evento non trovato con ID: " + idEvento));
        
        // Verifica se l'evento è in uno stato valido per la registrazione
        if (evento.getStatoEvento() != StatoEventoValori.IN_PROGRAMMA) {
            throw new BusinessRuleViolationException("Non è possibile registrarsi a un evento che non è in programma");
        }
        
        // Verifica se l'utente è già registrato
        if (eventoRegistrazioneRepository.existsByEventoAndUtente(evento, utente)) {
            throw new BusinessRuleViolationException("L'utente è già registrato a questo evento");
        }
        
        // Verifica disponibilità posti
        if (evento.getPostiDisponibili() < numeroPosti) {
            throw new BusinessRuleViolationException(
                    "Non ci sono abbastanza posti disponibili. Richiesti: " + numeroPosti + 
                    ", Disponibili: " + evento.getPostiDisponibili());
        }
        
        // Crea la registrazione
        EventoRegistrazione registrazione = new EventoRegistrazione(evento, utente, numeroPosti);
        registrazione.setNote(note);
        
        // Se l'utente è un Venditore (PRODUTTORE, DISTRIBUTORE, TRASFORMATORE), aggiungi l'azienda ai partecipanti
        if (utente instanceof Venditore) {
            Venditore venditore = (Venditore) utente;
            // Verifica che l'azienda non sia già nella lista
            if (!evento.getAziendePartecipanti().contains(venditore)) {
                evento.addAziendaPartecipante(venditore);
                eventoRepository.save(evento); // Salva l'evento aggiornato
            }
        }
        
        // Aggiorna i posti prenotati nell'evento
        prenotaPosti(evento, numeroPosti);
        
        // Salva la registrazione
        return eventoRegistrazioneRepository.save(registrazione);
    }
    
    @Override
    public void cancellaRegistrazioneUtente(Long idEvento, Utente utente) {
        if (utente == null) {
            throw new IllegalArgumentException("L'utente non può essere nullo");
        }
        
        Evento evento = eventoRepository.findById(idEvento)
                .orElseThrow(() -> new IllegalArgumentException("Evento non trovato con ID: " + idEvento));
        
        // Verifica se l'evento è in uno stato valido per la cancellazione
        if (evento.getStatoEvento() != StatoEventoValori.IN_PROGRAMMA) {
            throw new BusinessRuleViolationException(
                    "Non è possibile cancellare la registrazione per un evento che non è in programma");
        }
        
        // Trova la registrazione
        EventoRegistrazione registrazione = eventoRegistrazioneRepository.findByEventoAndUtente(evento, utente)
                .orElseThrow(() -> new BusinessRuleViolationException("Registrazione non trovata per questo utente"));
        
        // Aggiorna i posti prenotati nell'evento
        eliminaPostiPrenotati(evento, registrazione.getNumeroPosti());
        
        // Se l'utente è un Venditore, rimuovi l'azienda dai partecipanti
        if (utente instanceof Venditore) {
            Venditore venditore = (Venditore) utente;
            // Rimuovi l'azienda dai partecipanti (la logica può essere migliorata per verificare se ha altre registrazioni)
            evento.removeAziendaPartecipante(venditore);
            eventoRepository.save(evento); // Salva l'evento aggiornato
        }
        
        // Elimina la registrazione
        eventoRegistrazioneRepository.delete(registrazione);
    }
    
    @Override
    public boolean isUtenteRegistrato(Long idEvento, Utente utente) {
        if (utente == null) {
            return false;
        }
        
        Optional<Evento> eventoOpt = eventoRepository.findById(idEvento);
        if (eventoOpt.isEmpty()) {
            return false;
        }
        
        return eventoRegistrazioneRepository.existsByEventoAndUtente(eventoOpt.get(), utente);
    }
    
    @Override
    public List<EventoRegistrazione> getRegistrazioniEvento(Long idEvento) {
        Evento evento = eventoRepository.findById(idEvento)
                .orElseThrow(() -> new IllegalArgumentException("Evento non trovato con ID: " + idEvento));
        
        return eventoRegistrazioneRepository.findByEvento(evento);
    }
    
    @Override
    public List<EventoRegistrazione> getRegistrazioniUtente(Utente utente) {
        if (utente == null) {
            throw new IllegalArgumentException("L'utente non può essere nullo");
        }
        
        return eventoRegistrazioneRepository.findByUtente(utente);
    }

    public Optional<ShareResponseDTO> promuoviEvento(Long idEvento, PromoteRequestDTO request) {
        // 1. Trova l'evento nel database
        Optional<Evento> eventoOpt = eventoRepository.findById(idEvento);
        if (eventoOpt.isEmpty()) {
            return Optional.empty(); // Evento non trovato
        }
        Evento evento = eventoOpt.get();

        // 2. Estrai le informazioni necessarie dall'evento
        String nomeEvento = evento.getNome();
        Date dataInizio = evento.getDataOraInizio();
        String luogoEvento = evento.getLuogoEvento();

        // 3. Formatta la data per renderla più leggibile
        // Puoi personalizzare il formato come preferisci
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy 'alle ore' HH:mm");
        String dataFormattata = formatter.format(dataInizio);

        // 4. Prepara il nickname
        String nickname = request.getNickname();
        String nicknameFormattato = "@" + nickname.replaceAll("\\s", "");

        // 5. Costruisci la stringa finale come da richiesta
        String messaggioFinale = String.format(
                "%s ha postato: Ricordo a tutti di non perdere l'evento \"%s\" che si terrà il %s a %s",
                nicknameFormattato,
                nomeEvento,
                dataFormattata,
                luogoEvento
        );

        // 6. Crea e restituisci il DTO di risposta (riutilizzando quello esistente)
        ShareResponseDTO responseDTO = new ShareResponseDTO(messaggioFinale);
        return Optional.of(responseDTO);
    }
}
