// Modelli per la gestione degli ordini

export interface OrdineDTO {
    id: number;
    numeroOrdine: string;
    dataOrdine: string;
    dataConsegnaRichiesta?: string;
    stato: StatoOrdine;
    totale: number;
    note?: string;
    indirizzoConsegna: IndirizzoConsegnaDTO;
    acquirente: AcquirenteDTO;
    righeOrdine: RigaOrdineDTO[];
    createdAt: string;
    updatedAt: string;
}

export interface DettaglioOrdineDTO extends OrdineDTO {
    storico: StoricoStatoOrdineDTO[];
    documenti?: DocumentoOrdineDTO[];
    comunicazioni?: ComunicazioneOrdineDTO[];
    fatturazioneInfo?: FatturazioneInfoDTO;
}

export interface RigaOrdineDTO {
    id: number;
    pacchetto: PacchettoOrdineDTO;
    quantita: number;
    prezzoUnitario: number;
    totaleRiga: number;
    note?: string;
}

export interface PacchettoOrdineDTO {
    id: number;
    nome: string;
    descrizione?: string;
    immagineUrl?: string;
    prodotti: ProdottoOrdineDTO[];
}

export interface ProdottoOrdineDTO {
    id: number;
    nome: string;
    quantita: number;
    unitaMisura?: string;
    produttore: {
        id: number;
        nomeAzienda: string;
    };
}

export interface AcquirenteDTO {
    id: number;
    nome: string;
    cognome: string;
    email: string;
    telefono?: string;
    codiceFiscale?: string;
    partitaIva?: string;
    tipo: 'PRIVATO' | 'AZIENDA';
}

export interface IndirizzoConsegnaDTO {
    via: string;
    civico: string;
    citta: string;
    provincia: string;
    cap: string;
    paese: string;
    note?: string;
}

export interface StoricoStatoOrdineDTO {
    id: number;
    statoVecchio?: StatoOrdine;
    statoNuovo: StatoOrdine;
    dataModifica: string;
    note?: string;
    modificatoDa: string;
}

export interface DocumentoOrdineDTO {
    id: number;
    tipo: TipoDocumento;
    nome: string;
    url: string;
    dataCaricamento: string;
    dimensione: number;
}

export interface ComunicazioneOrdineDTO {
    id: number;
    mittente: string;
    destinatario: string;
    messaggio: string;
    dataInvio: string;
    letto: boolean;
    tipo: 'NOTA' | 'RICHIESTA' | 'PROBLEMA' | 'INFO';
}

export interface FatturazioneInfoDTO {
    ragioneSociale?: string;
    codiceFiscale: string;
    partitaIva?: string;
    indirizzo: IndirizzoConsegnaDTO;
    codiceDestinatario?: string;
    pec?: string;
}

// Enums
export enum StatoOrdine {
    RICEVUTO = 'RICEVUTO',
    CONFERMATO = 'CONFERMATO',
    IN_PREPARAZIONE = 'IN_PREPARAZIONE',
    PRONTO_PER_CONSEGNA = 'PRONTO_PER_CONSEGNA',
    IN_CONSEGNA = 'IN_CONSEGNA',
    CONSEGNATO = 'CONSEGNATO',
    ANNULLATO = 'ANNULLATO',
    RIMBORSATO = 'RIMBORSATO'
}

export enum TipoDocumento {
    FATTURA = 'FATTURA',
    RICEVUTA = 'RICEVUTA',
    DOCUMENTO_TRASPORTO = 'DOCUMENTO_TRASPORTO',
    CERTIFICATO_QUALITA = 'CERTIFICATO_QUALITA',
    ALTRO = 'ALTRO'
}

// Filtri e richieste
export interface OrdiniFiltri {
    stato?: StatoOrdine[];
    dataInizio?: string;
    dataFine?: string;
    numeroOrdine?: string;
    acquirente?: string;
    importoMin?: number;
    importoMax?: number;
    pagina?: number;
    elementiPerPagina?: number;
    ordinamento?: 'dataOrdine' | 'totale' | 'numeroOrdine';
    direzione?: 'ASC' | 'DESC';
}

export interface UpdateStatoOrdineRequest {
    nuovoStato: StatoOrdine;
    note?: string;
}

export interface AggiungiComunicazioneRequest {
    messaggio: string;
    tipo: 'NOTA' | 'RICHIESTA' | 'PROBLEMA' | 'INFO';
}

// Statistiche
export interface StatisticheOrdiniDTO {
    ordiniTotali: number;
    ordiniRicevuti: number;
    ordiniInPreparazione: number;
    ordiniConsegnati: number;
    fatturatoDiOggi: number;
    fatturatoDiQuesto: number;
    ordiniUltimaSettimana: number;
    tempoMedioCompletamento: number; // in ore
    valutazioneMediaServizio: number;
}

// Utilità per mappature
export const STATO_ORDINE_LABELS: Record<StatoOrdine, string> = {
    [StatoOrdine.RICEVUTO]: 'Ricevuto',
    [StatoOrdine.CONFERMATO]: 'Confermato',
    [StatoOrdine.IN_PREPARAZIONE]: 'In Preparazione',
    [StatoOrdine.PRONTO_PER_CONSEGNA]: 'Pronto per Consegna',
    [StatoOrdine.IN_CONSEGNA]: 'In Consegna',
    [StatoOrdine.CONSEGNATO]: 'Consegnato',
    [StatoOrdine.ANNULLATO]: 'Annullato',
    [StatoOrdine.RIMBORSATO]: 'Rimborsato'
};

export const STATO_ORDINE_COLORS: Record<StatoOrdine, string> = {
    [StatoOrdine.RICEVUTO]: '#2196f3',
    [StatoOrdine.CONFERMATO]: '#4caf50',
    [StatoOrdine.IN_PREPARAZIONE]: '#ff9800',
    [StatoOrdine.PRONTO_PER_CONSEGNA]: '#9c27b0',
    [StatoOrdine.IN_CONSEGNA]: '#3f51b5',
    [StatoOrdine.CONSEGNATO]: '#4caf50',
    [StatoOrdine.ANNULLATO]: '#f44336',
    [StatoOrdine.RIMBORSATO]: '#795548'
};

export const STATO_ORDINE_ICONS: Record<StatoOrdine, string> = {
    [StatoOrdine.RICEVUTO]: 'inbox',
    [StatoOrdine.CONFERMATO]: 'check_circle',
    [StatoOrdine.IN_PREPARAZIONE]: 'build',
    [StatoOrdine.PRONTO_PER_CONSEGNA]: 'ready_for_pickup',
    [StatoOrdine.IN_CONSEGNA]: 'local_shipping',
    [StatoOrdine.CONSEGNATO]: 'done_all',
    [StatoOrdine.ANNULLATO]: 'cancel',
    [StatoOrdine.RIMBORSATO]: 'money_off'
};

// Workflow degli stati (transizioni permesse)
export const TRANSIZIONI_PERMESSE: Record<StatoOrdine, StatoOrdine[]> = {
    [StatoOrdine.RICEVUTO]: [StatoOrdine.CONFERMATO, StatoOrdine.ANNULLATO],
    [StatoOrdine.CONFERMATO]: [StatoOrdine.IN_PREPARAZIONE, StatoOrdine.ANNULLATO],
    [StatoOrdine.IN_PREPARAZIONE]: [StatoOrdine.PRONTO_PER_CONSEGNA, StatoOrdine.ANNULLATO],
    [StatoOrdine.PRONTO_PER_CONSEGNA]: [StatoOrdine.IN_CONSEGNA],
    [StatoOrdine.IN_CONSEGNA]: [StatoOrdine.CONSEGNATO],
    [StatoOrdine.CONSEGNATO]: [StatoOrdine.RIMBORSATO],
    [StatoOrdine.ANNULLATO]: [StatoOrdine.RIMBORSATO],
    [StatoOrdine.RIMBORSATO]: []
};