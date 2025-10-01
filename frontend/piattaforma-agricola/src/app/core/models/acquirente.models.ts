/**
 * Modelli TypeScript per l'Acquirente basati sulle API del backend
 */

// === PRODOTTI ===
export interface ProductSummaryDTO {
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
}

export interface ProductDetailDTO extends ProductSummaryDTO {
  metodoDiColtivazione?: {
    id: number;
    nome: string;
    descrizione: string;
    tecniche: string[];
  };
  certificazioniDettagli: CertificationDTO[];
  dataCreazione: string;
  dataUltimaModifica: string;
}

export interface CertificationDTO {
  idCertificazione: number;
  nomeCertificazione: string;
  enteRilascio: string;
  dataRilascio: string;
  dataScadenza: string;
  idProdottoAssociato: number;
  idAziendaAssociata: number;
}

// === CARRELLO ===
export interface CarrelloDTO {
  acquirente: {
    id: number;
    nome: string;
    cognome: string;
    email: string;
  };
  elementiCarrello: ElementoCarrelloDTO[];
  ultimaModifica: string;
}

export interface ElementoCarrelloDTO {
  idElemento: number;
  tipoAcquistabile: 'PRODOTTO' | 'PACCHETTO' | 'EVENTO';
  acquistabile: {
    id: number;
    nome: string;
    descrizione: string;
    prezzo: number;
    immagineUrl?: string;
  };
  quantita: number;
  prezzoTotale: number;
  dataAggiunta: string;
}

export interface AddToCartRequestDTO {
  tipoAcquistabile: 'PRODOTTO' | 'PACCHETTO' | 'EVENTO';
  idAcquistabile: number;
  quantita: number;
}

// === ORDINI ===
export interface OrdineSummaryDTO {
  id: number;
  dataOrdine: string;
  stato: string;
  totale: number;
  numeroElementi: number;
  venditoreNome: string;
}

export interface OrdineDetailDTO extends OrdineSummaryDTO {
  righeOrdine: RigaOrdineDTO[];
  indirizzoSpedizione: string;
  dataConsegnaPrevista?: string;
  metodoPagamento: string;
  trackingNumber?: string;
}

export interface RigaOrdineDTO {
  id: number;
  prodotto: ProductSummaryDTO;
  quantita: number;
  prezzoUnitario: number;
  prezzoTotale: number;
}

export interface CreateOrdineRequestDTO {
  indirizzoSpedizione: string;
  metodoPagamento: string;
  note?: string;
}

// === EVENTI ===
export interface EventoSummaryDTO {
  idEvento: number;
  nomeEvento: string;
  descrizione: string;
  dataOraInizio: string;
  dataOraFine: string;
  luogoEvento: string;
  capienzaMassima: number;
  postiDisponibili: number;
  statoEvento: string;
  prezzo?: number;
}

export interface EventoDetailDTO extends EventoSummaryDTO {
  organizzatore: {
    id: number;
    nome: string;
    email: string;
  };
  aziendePartecipanti: Array<{
    id: number;
    nomeAzienda: string;
    descrizioneAzienda: string;
  }>;
  numeroPartecipanti: number;
}

export interface EventoRegistrazioneRequestDTO {
  numeroPosti: number;
  note?: string;
}

// === PACCHETTI ===
export interface PacchettoSummaryDTO {
  id: number;
  nome: string;
  descrizione: string;
  prezzoTotale: number;
  quantitaDisponibile: number;
  distributoreNome: string;
  numeroElementi: number;
  immagineUrl?: string;
}

export interface PacchettoDetailDTO extends PacchettoSummaryDTO {
  elementi: ElementoPacchettoDTO[];
  composizione: PackageCompositionDTO;
  distributore: {
    id: number;
    nomeAzienda: string;
    descrizioneAzienda: string;
  };
}

export interface ElementoPacchettoDTO {
  tipoElemento: string;
  idElemento: number;
  nomeElemento: string;
  descrizioneElemento: string;
  prezzoElemento: number;
  quantita: number;
}

export interface PackageCompositionDTO {
  id: number;
  nome: string;
  descrizione: string;
  prezzoTotale: number;
  quantitaDisponibile: number;
  elementi: ElementoPacchettoDTO[];
  prezzoCalcolato: number;
  disponibilitaCompleta: boolean;
}

// === STATISTICHE ACQUIRENTE ===
export interface AcquirenteStatsDTO {
  totaleOrdini: number;
  spesaTotale: number;
  prodottiAcquistati: number;
  eventiPartecipati: number;
  ordiniRecenti: OrdineSummaryDTO[];
  prodottiPreferiti: ProductSummaryDTO[];
  eventiProssimi: EventoSummaryDTO[];
}

// === FILTRI E RICERCA ===
export interface ProductFilters {
  search?: string;
  categoria?: string;
  prezzoMin?: number;
  prezzoMax?: number;
  venditoreId?: number;
  soloDisponibili?: boolean;
  certificazioni?: string[];
  ordinamento?: 'prezzo_asc' | 'prezzo_desc' | 'nome_asc' | 'nome_desc' | 'data_desc';
}

export interface PaginationParams {
  page: number;
  size: number;
  sortBy?: string;
  sortDirection?: 'asc' | 'desc';
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

// === TIPI DI ORIGINE PRODOTTO ===
export enum TipoOrigineProdotto {
  COLTIVATO = 'COLTIVATO',
  TRASFORMATO = 'TRASFORMATO',
  ARTIGIANALE = 'ARTIGIANALE'
}

// === STATI ORDINE ===
export enum StatoOrdine {
  NUOVO = 'NUOVO_IN_ATTESA_DI_PAGAMENTO',
  PAGATO = 'PAGATO_PRONTO_PER_LAVORAZIONE',
  IN_LAVORAZIONE = 'IN_LAVORAZIONE',
  SPEDITO = 'SPEDITO',
  CONSEGNATO = 'CONSEGNATO',
  ANNULLATO = 'ANNULLATO'
}

// === STATI EVENTO ===
export enum StatoEvento {
  IN_PROGRAMMA = 'IN_PROGRAMMA',
  IN_CORSO = 'IN_CORSO',
  CONCLUSO = 'CONCLUSO',
  ANNULLATO = 'ANNULLATO'
}