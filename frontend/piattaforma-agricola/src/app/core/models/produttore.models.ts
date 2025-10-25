/**
 * Modelli TypeScript per il Produttore basati sulle API del backend
 */

// === PRODOTTI ===
export interface ProduttoreProductSummaryDTO {
    idProdotto: number;
    nome: string;
    descrizione: string;
    prezzo: number;
    quantitaDisponibile: number;
    unitaMisura: string;
    statoVerifica: string;
    tipoOrigine: string;
    certificazioni: string[];
    metodoDiColtivazione?: {
        id: number;
        nome: string;
        descrizione: string;
        tecniche: string[];
    };
    dataCreazione: string;
    dataUltimaModifica: string;
    immagineUrl?: string;
}

export interface ProduttoreProductDetailDTO extends ProduttoreProductSummaryDTO {
    venditore: {
        id: number;
        nomeAzienda: string;
        partitaIva: string;
        indirizzoAzienda: string;
    };
    certificazioniDettagli: CertificationDTO[];
    ordiniRicevuti: OrdineRiepilogoDTO[];
    visualizzazioni: number;
    // Campi aggiuntivi che potrebbero essere presenti
    ingredienti?: string[];
    allergeni?: string[];
    metodiConservazione?: string;
    numeroLotto?: string;
    dataScadenza?: string;
}

// === CERTIFICAZIONI ===
export interface CertificationDTO {
    idCertificazione: number;
    nomeCertificazione: string;
    enteRilascio: string;
    dataRilascio: string;
    dataScadenza: string;
    idProdottoAssociato: number;
    idAziendaAssociata: number;
}

export interface CreateCertificazioneRequestDTO {
    nomeCertificazione: string;
    enteRilascio: string;
    dataRilascio: string;
    dataScadenza: string;
}

// === METODI DI COLTIVAZIONE ===
export interface MetodoDiColtivazioneDTO {
    id: number;
    nome: string;
    descrizione: string;
    tecniche: string[];
    periodoColtivazione: string;
    superficie: number;
    ubicazione: string;
}

export interface CreateMetodoDiColtivazioneRequestDTO {
    nome: string;
    descrizione: string;
    tecniche: string[];
    periodoColtivazione: string;
    superficie: number;
    ubicazione: string;
}

// === ORDINI ===
export interface OrdineRiepilogoDTO {
    // Proprietà backend
    idOrdine: number;
    dataOrdine: string;
    importoTotale: number;
    statoCorrente: string;
    nomeAcquirente: string;
    idAcquirente: number;
    numeroArticoli: number;
    idVenditore: number;
    nomeVenditore: string;
    emailVenditore: string;
    nomeAziendaVenditore: string;
    articoli?: Array<{
        idRiga: number;
        nomeAcquistabile: string;
        quantitaOrdinata: number;
        prezzoUnitario: number;
    }>;
    
    // Proprietà alias per compatibilità frontend
    id: number;
    totale: number;
    stato: string;
    prodotti?: Array<{
        id: number;
        nome: string;
        quantita: number;
        prezzo: number;
    }>;
    acquirente?: {
        nome: string;
        cognome: string;
        email: string;
    };
}

// === STATISTICHE PRODUTTORE ===
export interface ProduttoreStatsDTO {
    prodottiTotali: number;
    prodottiApprovati: number;
    prodottiInAttesa: number;
    prodottiRespinti: number;
    ordiniTotali: number;
    ordiniCompletati: number;
    fatturatoTotale: number;
    prodottiVenduti: number;
    visualizzazioniTotali: number;
    certificazioniTotali: number;
    ordiniRecenti: OrdineRiepilogoDTO[];
    prodottiPopolari: ProduttoreProductSummaryDTO[];
    andamentoVendite: Array<{
        mese: string;
        vendite: number;
        fatturato: number;
    }>;
}

// === FILTRI E RICERCA ===
export interface ProduttoreProductFilters {
    search?: string;
    statoVerifica?: string;
    tipoOrigine?: string;
    certificazioni?: string[];
    periodo?: string;
    prezzoMin?: number;
    prezzoMax?: number;
    disponibilita?: boolean;
    ordinamento?: 'nome_asc' | 'nome_desc' | 'prezzo_asc' | 'prezzo_desc' | 'data_desc' | 'popolarita_desc';
}

// === STATI VERIFICA ===
export enum StatoVerifica {
    IN_ATTESA = 'IN_ATTESA_DI_VERIFICA',
    APPROVATO = 'APPROVATO',
    RESPINTO = 'RESPINTO'
}

// === TIPI ORIGINE PRODOTTO ===
export enum TipoOrigineProdotto {
    COLTIVATO = 'COLTIVATO',
    TRASFORMATO = 'TRASFORMATO',
    ARTIGIANALE = 'ARTIGIANALE'
}

// === TIPI COLTIVAZIONE ===
export enum TipoColtivazione {
    BIOLOGICO = 'BIOLOGICO',
    CONVENZIONALE = 'CONVENZIONALE',
    INTEGRALE = 'INTEGRALE',
    BIODINAMICO = 'BIODINAMICO'
}

// === PERIODO COLTIVAZIONE ===
export enum PeriodoColtivazione {
    ANNUALE = 'ANNUALE',
    STAGIONALE = 'STAGIONALE',
    PERENNE = 'PERENNE'
}

// === STATI ORDINE ===
export enum StatoOrdineProduttore {
    NUOVO = 'NUOVO_IN_ATTESA_DI_PAGAMENTO',
    PAGATO = 'PAGATO_PRONTO_PER_LAVORAZIONE',
    IN_LAVORAZIONE = 'IN_LAVORAZIONE',
    SPEDITO = 'SPEDITO',
    CONSEGNATO = 'CONSEGNATO',
    ANNULLATO = 'ANNULLATO'
}

// === DTO PER RICHIESTE PRODOTTI ===
export interface CreateProductRequestDTO {
    nome: string;
    descrizione: string;
    prezzo: number;
    quantitaDisponibile: number;
    tipoOrigine: string;
    idMetodoDiColtivazione?: number;
    idProcessoTrasformazioneOriginario?: number;
}

export interface UpdateProductRequestDTO {
    nome?: string;
    descrizione?: string;
    prezzo?: number;
    quantitaDisponibile?: number;
}

export interface ProductQuantityUpdateDTO {
    quantitaDisponibile: number;
}

// === AZIONI RAPIDE PRODUTTORE ===
export interface AzioneRapidaProduttore {
    id: string;
    label: string;
    icon: string;
    color: 'primary' | 'accent' | 'warn';
    description?: string;
    route?: string;
}