/**
 * Modelli TypeScript per l'Animatore della Filiera basati sulle API del backend
 */

// === EVENTI ===
export interface EventoDTO {
    id: number;
    nomeEvento?: string; // Alias per titolo (da API backend)
    titolo?: string; // Alias per nomeEvento
    descrizione: string;
    dataInizio?: string; // Alias per dataOraInizio
    dataFine?: string; // Alias per dataOraFine
    dataOraInizio?: string; // Da API backend
    dataOraFine?: string; // Da API backend
    luogo?: string; // Alias per luogoEvento
    luogoEvento?: string; // Da API backend
    immagineUrl?: string;
    stato: 'IN_PROGRAMMA' | 'IN_CORSO' | 'CONCLUSO' | 'ANNULLATO';
    organizzatoreId: number;
    organizzatoreNome?: string;
    partecipantiPrevisti?: number;
    partecipantiConfermati?: number;
    capienzaMassima?: number; // Da API backend
    costo?: number;
    categoria?: string;
    tags?: string[];
    dataCreazione?: string;
    dataUltimaModifica?: string;
}

// Alias per compatibilità con documentazione API_ANIMATORE.md
export interface EventoDetailDTO extends EventoDTO { }

// === STATISTICHE ANIMATORE ===
export interface AnimatoreStatsDTO {
    eventiCreati: number;
    eventiPubblicati: number;
    eventiInCorso: number;
    eventiPassati: number;
    partecipantiTotali: number;
    mediaPartecipantiPerEvento: number;
    prossimiEventi: EventoDTO[];
    eventiPopolari: EventoDTO[];
    andamentoPartecipazioni: Array<{
        mese: string;
        partecipanti: number;
        eventi: number;
    }>;
}

// === DTO PER RICHIESTE ===
export interface CreaEventoRequestDTO {
    nomeEvento: string;
    descrizione: string;
    dataOraInizio: string;
    dataOraFine: string;
    luogoEvento: string;
    capienzaMassima?: number;
}

export interface CreateEventoRequestDTO extends CreaEventoRequestDTO { } // Alias per API

export interface AggiornaEventoRequestDTO {
    nomeEvento?: string;
    descrizione?: string;
    dataOraInizio?: string;
    dataOraFine?: string;
    luogoEvento?: string;
    capienzaMassima?: number;
}

// === FILTRI EVENTI ===
export interface EventoFilters {
    stato?: 'IN_PROGRAMMA' | 'IN_CORSO' | 'CONCLUSO' | 'ANNULLATO' | 'TUTTI';
    categoria?: string;
    search?: string;
    dataDa?: string;
    dataA?: string;
    organizzatoreId?: number;
    pagina?: number;
    elementiPerPagina?: number;
}

// === PARTECIPANTI UTENTI ===
export interface EventoPartecipanteDTO {
    idUtente: number;
    nomeCompleto: string;
    email: string;
    numeroPosti: number;
    dataRegistrazione: string;
    note?: string;
}

// === AZIENDE PARTECIPANTI ===
export interface AziendaPartecipanteDTO {
    id: number;
    nomeAzienda: string;
    partitaIva: string;
    indirizzoAzienda: string;
    descrizioneAzienda?: string;
    sitoWebUrl?: string;
    certificazioniAzienda?: string[];
}

// === PROMOZIONE EVENTI ===
export interface PromoteRequestDTO {
    canali: ('FACEBOOK' | 'TWITTER' | 'INSTAGRAM' | 'EMAIL' | 'WHATSAPP')[];
    messaggio: string;
}

export interface ShareResponseDTO {
    successo: boolean;
    messaggio: string;
    canaliPromossi: string[];
    errori?: string[];
}

// === ALTRE INTERFACCE ===
export interface PartecipanteEventoDTO {
    id: number;
    nome: string;
    email: string;
    telefono?: string;
    dataIscrizione: string;
    stato: 'CONFERMATO' | 'IN_ATTESA';
    eventoId: number;
    eventoTitolo: string;
}

export interface FeedbackEventoDTO {
    id: number;
    eventoId: number;
    eventoTitolo: string;
    partecipanteId: number;
    partecipanteNome: string;
    rating: number;
    commento: string;
    dataFeedback: string;
    visibile: boolean;
}

// === STATI EVENTO ===
export enum StatoEvento {
    IN_PROGRAMMA = 'IN_PROGRAMMA',
    IN_CORSO = 'IN_CORSO',
    CONCLUSO = 'CONCLUSO',
    ANNULLATO = 'ANNULLATO'
}

// === AZIONI RAPIDE ANIMATORE ===
export interface AzioneRapidaAnimatore {
    id: string;
    label: string;
    icon: string;
    color: 'primary' | 'accent' | 'warn';
    description?: string;
    route?: string;
    count?: number;
}