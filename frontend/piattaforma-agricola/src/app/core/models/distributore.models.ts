/**
 * Modelli TypeScript per il Distributore di Tipicità basati sulle API del backend
 */

// === PACCHETTI DI TIPICITÀ ===
export interface PacchettoTipicitaDTO {
    id: number;
    nome: string;
    descrizione: string;
    prezzo: number;
    immagineUrl?: string;
    stato: string;
    dataCreazione: string;
    dataUltimaModifica: string;
    prodotti: ProdottoPacchettoDTO[];
    distributore: {
        id: number;
        nomeAzienda: string;
        partitaIva: string;
    };
}

export interface DettaglioPacchettoDTO extends PacchettoTipicitaDTO {
    prodotti: ProdottoPacchettoDTO[];
    certificazioni: CertificationDTO[];
    recensioni: RecensioneDTO[];
    statisticheVendite: {
        venditeTotali: number;
        mediaValutazione: number;
        numeroRecensioni: number;
    };
}

export interface ProdottoPacchettoDTO {
    id: number;
    nome: string;
    descrizione: string;
    prezzo: number;
    quantita: number;
    produttore: {
        id: number;
        nomeAzienda: string;
    };
    certificazioni: string[];
}

// === STATISTICHE DISTRIBUTORE ===
export interface DistributoreStatsDTO {
    pacchettiTotali: number;
    pacchettiAttivi: number;
    pacchettiVenduti: number;
    prodottiTotali: number;
    ricavoTotale: number;
    mediaPrezzoPacchetto: number;
    pacchettiPopolari: PacchettoTipicitaDTO[];
    andamentoVendite: Array<{
        mese: string;
        pacchetti: number;
        ricavo: number;
    }>;
}

// === DTO PER RICHIESTE ===
export interface CreatePacchettoRequestDTO {
    nome: string;
    descrizione: string;
    prezzo: number;
    prodotti: Array<{
        id: number;
        quantita: number;
    }>;
}

export interface UpdatePacchettoRequestDTO {
    nome?: string;
    descrizione?: string;
    prezzo?: number;
    prodotti?: Array<{
        id: number;
        quantita: number;
    }>;
}

// === ALTRE INTERFACCE ===
export interface CertificationDTO {
    idCertificazione: number;
    nomeCertificazione: string;
    enteRilascio: string;
    dataRilascio: string;
    dataScadenza: string;
    idProdottoAssociato?: number;
}

export interface CreateCertificazioneRequestDTO {
    nomeCertificazione: string;
    enteRilascio: string;
    dataRilascio: string;
    dataScadenza: string;
}

export interface RecensioneDTO {
    id: number;
    valutazione: number;
    commento: string;
    dataRecensione: string;
    acquirente: {
        id: number;
        nome: string;
        cognome: string;
    };
}

// === STATI PACCHETTO ===
export enum StatoPacchetto {
    IN_PROGETTAZIONE = 'IN_PROGETTAZIONE',
    ATTIVO = 'ATTIVO',
    INATTIVO = 'INATTIVO',
    ELIMINATO = 'ELIMINATO'
}

// === AZIONI RAPIDE DISTRIBUTORE ===
export interface AzioneRapidaDistributore {
    id: string;
    label: string;
    icon: string;
    color: 'primary' | 'accent' | 'warn';
    description?: string;
    route?: string;
}

// === GESTIONE PRODOTTI DISTRIBUTORE ===

export interface DistributoreProductDTO {
    id: number;
    nome: string;
    descrizione: string;
    prezzo: number;
    quantitaDisponibile: number;
    unitaMisura: string;
    tipoOrigine: 'COLTIVATO' | 'COLTIVATO_ALLEVATO' | 'TRASFORMATO';
    stato: string;
    dataCreazione: string;
    dataUltimaModifica: string;
    immagineUrl?: string;
    distributore?: {
        id: number;
        nomeAzienda: string;
        partitaIva: string;
    };
    venditore?: {
        idUtente: number;
        nome: string;
        cognome: string;
        tipoRuolo: string;
        statoAccreditamento?: string;
        isAttivo: boolean;
    };
    certificazioni?: CertificationDTO[];
}

export interface CreateDistributoreProductRequestDTO {
    nome: string;
    descrizione: string;
    prezzo: number;
    quantitaDisponibile: number;
    unitaMisura: string;
    tipoOrigine: 'COLTIVATO' | 'COLTIVATO_ALLEVATO' | 'TRASFORMATO';
    immagineUrl?: string;
}

export interface UpdateDistributoreProductRequestDTO {
    nome?: string;
    descrizione?: string;
    prezzo?: number;
    quantitaDisponibile?: number;
    unitaMisura?: string;
    tipoOrigine?: 'COLTIVATO' | 'COLTIVATO_ALLEVATO' | 'TRASFORMATO';
    immagineUrl?: string;
}