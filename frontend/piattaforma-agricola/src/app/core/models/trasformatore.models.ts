/**
 * Modelli TypeScript per il Trasformatore basati sulle API del backend
 */

// === PROCESSI DI TRASFORMAZIONE ===
export interface ProcessoTrasformazioneSummaryDTO {
  id: number;
  nome: string;
  descrizione: string;
  stato: string;
  dataCreazione: string;
  dataUltimaModifica: string;
  numeroFasi: number;
  prodottiInput: Array<{
    id: number;
    nome: string;
    quantita: number;
  }>;
  prodottiOutput: Array<{
    id: number;
    nome: string;
    quantita: number;
  }>;
}

export interface ProcessoTrasformazioneDetailDTO extends ProcessoTrasformazioneSummaryDTO {
  trasformatore: {
    id: number;
    nomeAzienda: string;
    partitaIva: string;
  };
  fasi: FaseLavorazioneDTO[];
  tracciabilita: TracciabilitaDTO[];
  certificazioni: CertificationDTO[];
  costi: CostoProcessoDTO[];
  resa: ResaProcessoDTO[];
}

// === FASI DI LAVORAZIONE ===
export interface FaseLavorazioneDTO {
  id: number;
  nome: string;
  descrizione: string;
  ordine: number;
  durataPrevista: number;
  durataEffettiva?: number;
  stato: string;
  dataInizio?: string;
  dataFine?: string;
  tecniche: string[];
  attrezzature: string[];
  materiali: string[];
  note?: string;
}

export interface CreateFaseLavorazioneRequestDTO {
  nome: string;
  descrizione: string;
  ordine: number;
  durataPrevista: number;
  tecniche: string[];
  attrezzature: string[];
  materiali: string[];
  note?: string;
}

export interface UpdateFaseLavorazioneRequestDTO {
  nome?: string;
  descrizione?: string;
  durataPrevista?: number;
  tecniche?: string[];
  attrezzature?: string[];
  materiali?: string[];
  note?: string;
}

// === TRACCIABILITÀ ===
export interface TracciabilitaDTO {
  id: number;
  codiceUnivoco: string;
  dataCreazione: string;
  processo: {
    id: number;
    nome: string;
  };
  prodottiInput: Array<{
    id: number;
    nome: string;
    quantita: number;
    lotto: string;
    dataRicezione: string;
  }>;
  fasiEseguite: Array<{
    id: number;
    nome: string;
    dataInizio: string;
    dataFine: string;
    durata: number;
    note?: string;
  }>;
  prodottiOutput: Array<{
    id: number;
    nome: string;
    quantita: number;
    lotto: string;
    dataProduzione: string;
  }>;
  certificazioni: CertificationDTO[];
}

export interface CreateTracciabilitaRequestDTO {
  codiceUnivoco: string;
  note?: string;
}

// === CERTIFICAZIONI ===
export interface CertificationDTO {
  idCertificazione: number;
  nomeCertificazione: string;
  enteRilascio: string;
  dataRilascio: string;
  dataScadenza: string;
  idProcessoAssociato: number;
  idAziendaAssociata: number;
}

export interface CreateCertificazioneRequestDTO {
  nomeCertificazione: string;
  enteRilascio: string;
  dataRilascio: string;
  dataScadenza: string;
}

// === COSTI E RESA ===
export interface CostoProcessoDTO {
  id: number;
  descrizione: string;
  importo: number;
  data: string;
  categoria: string;
}

export interface ResaProcessoDTO {
  id: number;
  descrizione: string;
  importo: number;
  data: string;
  categoria: string;
}

// === STATISTICHE TRASFORMATORE ===
export interface TrasformatoreStatsDTO {
  processiTotali: number;
  processiAttivi: number;
  processiCompletati: number;
  prodottiTrasformati: number;
  valoreProduzione: number;
  costiTotali: number;
  ricavoTotale: number;
  margineProfitto: number;
  certificazioniTotali: number;
  tracciabilitaAttive: number;
  processiRecenti: ProcessoTrasformazioneSummaryDTO[];
  prodottiPopolari: Array<{
    id: number;
    nome: string;
    quantita: number;
    valore: number;
  }>;
  andamentoProduzione: Array<{
    mese: string;
    processi: number;
    valore: number;
  }>;
}

// === FILTRI E RICERCA ===
export interface ProcessoFilters {
  search?: string;
  stato?: string;
  dataInizio?: string;
  dataFine?: string;
  prodottiInput?: string[];
  prodottiOutput?: string[];
  certificazioni?: string[];
  ordinamento?: 'nome_asc' | 'nome_desc' | 'data_desc' | 'stato_asc' | 'stato_desc';
}

// === STATI PROCESSO ===
export enum StatoProcesso {
  IN_PROGETTAZIONE = 'IN_PROGETTAZIONE',
  IN_CORSO = 'IN_CORSO',
  COMPLETATO = 'COMPLETATO',
  SOSPESO = 'SOSPESO',
  ANNULLATO = 'ANNULLATO'
}

// === STATI FASE ===
export enum StatoFase {
  DA_INIZIARE = 'DA_INIZIARE',
  IN_CORSO = 'IN_CORSO',
  COMPLETATA = 'COMPLETATA',
  SOSPESA = 'SOSPESA',
  ANNULLATA = 'ANNULLATA'
}

// === CATEGORIE COSTI ===
export enum CategoriaCosto {
  MANODOPERA = 'MANODOPERA',
  MATERIE_PRIME = 'MATERIE_PRIME',
  ENERGIA = 'ENERGIA',
  ATTREZZATURE = 'ATTREZZATURE',
  CERTIFICAZIONI = 'CERTIFICAZIONI',
  TRASPORTO = 'TRASPORTO',
  ALTRO = 'ALTRO'
}

// === DTO PER RICHIESTE PROCESSI ===
export interface CreateProcessoRequestDTO {
  nome: string;
  descrizione: string;
  prodottiInput: Array<{
    id: number;
    quantita: number;
  }>;
  prodottiOutput: Array<{
    id: number;
    quantita: number;
  }>;
}

export interface UpdateProcessoRequestDTO {
  nome?: string;
  descrizione?: string;
  prodottiInput?: Array<{
    id: number;
    quantita: number;
  }>;
  prodottiOutput?: Array<{
    id: number;
    quantita: number;
  }>;
}

export interface UpdateStatoProcessoRequestDTO {
  stato: string;
  note?: string;
}

// === AZIONI RAPIDE TRASFORMATORE ===
export interface AzioneRapidaTrasformatore {
  id: string;
  label: string;
  icon: string;
  color: 'primary' | 'accent' | 'warn';
  description?: string;
  route?: string;
}