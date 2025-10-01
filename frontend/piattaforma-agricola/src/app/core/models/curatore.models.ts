/**
 * Modelli TypeScript per il Curatore basati sulle API del backend
 */

// === APPROVAZIONI IN CORSO ===
export interface ApprovazionePendingDTO {
  id: number;
  tipo: 'PRODOTTO' | 'AZIENDA' | 'CONTENUTO';
  elementoId: number;
  elementoNome: string;
  descrizione: string;
  stato: 'IN_ATTESA' | 'APPROVATO' | 'RIFIUTATO';
  dataRichiesta: string;
  richiedente: {
    id: number;
    nome: string;
    email: string;
  };
  motivoReiezione?: string;
  dataApprovazione?: string;
  approvatore?: {
    id: number;
    nome: string;
    email: string;
  };
}

// === STATISTICHE CURATORE ===
export interface CuratoreStatsDTO {
  prodottiDaApprovare: number;
  aziendeDaApprovare: number;
  contenutiDaModerare: number;
  prodottiApprovati: number;
  prodottiRifiutati: number;
  aziendeApprovate: number;
  aziendeRifiutate: number;
  contenutiModerati: number;
  andamentoApprovazioni: Array<{
    mese: string;
    prodotti: number;
    aziende: number;
    contenuti: number;
  }>;
}

// === DTO PER RICHIESTE ===
export interface ApprovazioneRequestDTO {
  approvato: boolean;
  motivoReiezione?: string;
  note?: string;
}

// === FILTRI APPROVAZIONI ===
export interface ApprovazioneFilters {
  tipo?: 'PRODOTTO' | 'AZIENDA' | 'CONTENUTO' | 'TUTTI';
  stato?: 'IN_ATTESA' | 'APPROVATO' | 'RIFIUTATO' | 'TUTTI';
  search?: string;
  dataDa?: string;
  dataA?: string;
  richiedenteId?: number;
  pagina?: number;
  elementiPerPagina?: number;
}

// === ALTRE INTERFACCE ===
export interface ProdottoApprovazioneDTO {
  id: number;
  nome: string;
  descrizione: string;
  prezzo: number;
  quantitaDisponibile: number;
  venditoreId: number;
  venditoreNome: string;
  statoVerifica: string;
  tipoOrigine?: string;
  certificazioni?: string[];
  immagineUrl?: string;
  dataCreazione: string;
  dataUltimaModifica: string;
}

export interface AziendaApprovazioneDTO {
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

export interface ContenutoApprovazioneDTO {
  id: number;
  tipo: 'ARTICOLO' | 'RICETTA' | 'EVENTO_DESCRIZIONE';
  titolo: string;
  contenuto: string;
  autoreId: number;
  autoreNome: string;
  statoModerazione: string;
  dataCreazione: string;
  dataUltimaModifica: string;
  segnalazioni?: number;
}

// === STATI APPROVAZIONE ===
export enum StatoApprovazione {
  IN_ATTESA = 'IN_ATTESA',
  APPROVATO = 'APPROVATO',
  RIFIUTATO = 'RIFIUTATO'
}

// === TIPI ELEMENTO DA APPROVARE ===
export enum TipoElementoApprovazione {
  PRODOTTO = 'PRODOTTO',
  AZIENDA = 'AZIENDA',
  CONTENUTO = 'CONTENUTO'
}

// === AZIONI RAPIDE CURATORE ===
export interface AzioneRapidaCuratore {
  id: string;
  label: string;
  icon: string;
  color: 'primary' | 'accent' | 'warn';
  description?: string;
  route?: string;
  count?: number;
}