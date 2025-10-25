/**
 * Modelli TypeScript per il Gestore Piattaforma basati sulle API del backend
 */

// === STATISTICHE GESTORE ===
export interface GestorePlatformaStatsDTO {
  utentiTotali: number;
  utentiAttivi: number;
  utentiNuoviMese: number;
  prodottiTotali: number;
  prodottiApprovati: number;
  aziendeTotali: number;
  aziendeApprovate: number;
  eventiTotali: number;
  eventiPubblicati: number;
  transazioniTotali: number;
  ricavoTotale: number;
  andamentoUtenti: Array<{
    mese: string;
    totali: number;
    attivi: number;
    nuovi: number;
  }>;
  andamentoProdotti: Array<{
    mese: string;
    creati: number;
    approvati: number;
  }>;
  andamentoEventi: Array<{
    mese: string;
    creati: number;
    pubblicati: number;
  }>;
}

// === UTENTI ===
export interface UtenteDTO {
  id: number;
  username: string;
  email: string;
  nome: string;
  cognome: string;
  ruolo: string;
  stato: 'ATTIVO' | 'INATTIVO' | 'SOSPESO';
  dataRegistrazione: string;
  ultimoAccesso?: string;
  profiloCompletato: boolean;
  telefono?: string;
  indirizzo?: string;
  avatarUrl?: string;
}

// === PRODOTTI ===
export interface ProdottoDTO {
  id: number;
  nome: string;
  descrizione: string;
  prezzo: number;
  quantitaDisponibile: number;
  venditoreId: number;
  venditoreNome: string;
  statoVerifica: string;
  categoria: string;
  immagineUrl?: string;
  dataCreazione: string;
  dataUltimaModifica: string;
}

// === AZIENDE ===
export interface AziendaDTO {
  id: number;
  nomeAzienda: string;
  descrizione: string;
  partitaIva: string;
  indirizzo: string;
  telefono: string;
  email: string;
  sitoWeb?: string;
  statoVerifica: string;
  dataCreazione: string;
  dataUltimaModifica: string;
  rappresentante: {
    id: number;
    nome: string;
    cognome: string;
    email: string;
    telefono: string;
  };
}

// === EVENTI ===
export interface EventoDTO {
  id: number;
  titolo: string;
  descrizione: string;
  dataInizio: string;
  dataFine: string;
  luogo: string;
  immagineUrl?: string;
  stato: string;
  organizzatoreId: number;
  organizzatoreNome: string;
  partecipantiPrevisti: number;
  partecipantiConfermati: number;
  costo?: number;
  categoria: string;
  dataCreazione: string;
  dataUltimaModifica: string;
}

// === FILTRI ===
export interface UtentiFilters {
  ruolo?: string;
  stato?: 'ATTIVO' | 'INATTIVO' | 'SOSPESO' | 'TUTTI';
  search?: string;
  dataDa?: string;
  dataA?: string;
  pagina?: number;
  elementiPerPagina?: number;
}

export interface ProdottiFilters {
  stato?: string;
  categoria?: string;
  search?: string;
  dataDa?: string;
  dataA?: string;
  venditoreId?: number;
  pagina?: number;
  elementiPerPagina?: number;
}

export interface AziendeFilters {
  stato?: string;
  search?: string;
  dataDa?: string;
  dataA?: string;
  pagina?: number;
  elementiPerPagina?: number;
}

export interface EventiFilters {
  stato?: string;
  categoria?: string;
  search?: string;
  dataDa?: string;
  dataA?: string;
  organizzatoreId?: number;
  pagina?: number;
  elementiPerPagina?: number;
}

// === AZIONI RAPIDE GESTORE ===
export interface AzioneRapidaGestore {
  id: string;
  label: string;
  icon: string;
  color: 'primary' | 'accent' | 'warn';
  description?: string;
  route?: string;
  count?: number;
}

// === STATI UTENTE ===
export enum StatoUtente {
  ATTIVO = 'ATTIVO',
  INATTIVO = 'INATTIVO',
  SOSPESO = 'SOSPESO'
}

// === RICHIESTE BLOCCO UTENTE ===
export interface BloccaUtenteRequestDTO {
  motivo: string;
  durataGiorni?: number;
}

// === RICHIESTE SOSPENSIONE UTENTE ===
export interface SospendiUtenteRequestDTO {
  motivo: string;
  durataGiorni?: number;
}