/**
 * Modelli TypeScript per l'Animatore della Filiera basati sulle API del backend
 */

// === EVENTI ===
export interface EventoDTO {
  id: number;
  titolo: string;
  descrizione: string;
  dataInizio: string;
  dataFine: string;
  luogo: string;
  immagineUrl?: string;
  stato: 'DA_PUBBLICARE' | 'PUBBLICATO' | 'ANNULLATO';
  organizzatoreId: number;
  organizzatoreNome: string;
  partecipantiPrevisti: number;
  partecipantiConfermati: number;
  costo?: number;
  categoria: string;
  tags: string[];
  dataCreazione: string;
  dataUltimaModifica: string;
}

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
  titolo: string;
  descrizione: string;
  dataInizio: string;
  dataFine: string;
  luogo: string;
  immagineUrl?: string;
  costo?: number;
  categoria: string;
  tags: string[];
}

export interface AggiornaEventoRequestDTO {
  titolo?: string;
  descrizione?: string;
  dataInizio?: string;
  dataFine?: string;
  luogo?: string;
  immagineUrl?: string;
  stato?: 'DA_PUBBLICARE' | 'PUBBLICATO' | 'ANNULLATO';
  costo?: number;
  categoria?: string;
  tags?: string[];
}

// === FILTRI EVENTI ===
export interface EventoFilters {
  stato?: 'DA_PUBBLICARE' | 'PUBBLICATO' | 'ANNULLATO' | 'TUTTI';
  categoria?: string;
  search?: string;
  dataDa?: string;
  dataA?: string;
  organizzatoreId?: number;
  pagina?: number;
  elementiPerPagina?: number;
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
  DA_PUBBLICARE = 'DA_PUBBLICARE',
  PUBBLICATO = 'PUBBLICATO',
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