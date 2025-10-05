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

// === GESTIONE PROFILO ===
export interface UserDetailDTO {
    id: number;
    username: string;
    email: string;
    nome: string;
    cognome: string;
    numeroTelefono?: string;
    indirizzo?: string;
    ruolo: string;
    dataRegistrazione: string;
    ultimoAccesso?: string;
    profiloCompleto: boolean;
}

export interface UserUpdateDTO {
    nome?: string;
    cognome?: string;
    numeroTelefono?: string;
    indirizzo?: string;
    email?: string;
}

// === CARRELLO ===
export interface CarrelloDTO {
    idCarrello: number;
    righeCarrello: RigaCarrelloDTO[];
    totale: number;
    numeroArticoli: number;
}

export interface RigaCarrelloDTO {
    idRiga: number;
    acquistabile: AcquistabileDTO;
    quantita: number;
    prezzoTotale: number;
}

export interface AcquistabileDTO {
    tipo: 'PRODOTTO' | 'PACCHETTO';
    id: number;
    nome: string;
    prezzo: number;
    immagineUrl?: string;
}

export interface AddToCartRequestDTO {
    quantita: number;
}

export interface UpdateCartItemRequestDTO {
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

export interface OrdineExtendedSummaryDTO extends OrdineSummaryDTO {
    venditore: {
        id: number;
        nome: string;
    };
    indirizzoSpedizione: string;
    metodoPagamento: string;
}

export interface OrdineDetailDTO extends OrdineSummaryDTO {
    venditore: {
        id: number;
        nome: string;
    };
    righeOrdine: RigaOrdineDTO[];
    indirizzoSpedizione: string;
    metodoPagamento: string;
    trackingNumber?: string;
    dataConsegnaPrevista?: string;
}

export interface RigaOrdineDTO {
    id: number;
    prodotto: ProductSummaryDTO;
    quantita: number;
    prezzoUnitario: number;
    sottoTotale: number;
}

export interface CreateOrdineRequestDTO {
    metodoPagamento: 'CARTA_CREDITO' | 'PAYPAL' | 'BONIFICO';
}

export interface OrderStatusDTO {
    statoCorrente: string;
    storicoStati: StatoOrdineDTO[];
    trackingInfo?: TrackingInfoDTO;
}

export interface StatoOrdineDTO {
    stato: string;
    dataOra: string;
}

export interface TrackingInfoDTO {
    trackingNumber: string;
    carrier: string;
    dataConsegnaPrevista: string;
}

export interface CancelOrderRequestDTO {
    motivoAnnullamento: string;
}

// === PAGAMENTO ===
export interface PagamentoRequestDTO {
    metodoPagamento: 'CARTA_CREDITO' | 'PAYPAL';
    numeroCarta?: string;
    intestatarioCarta?: string;
    dataScadenza?: string;
    cvv?: string;
    emailPayPal?: string;
    passwordPayPal?: string;
}

export interface DatiCartaCreditoDTO {
    numeroCarta: string;
    intestatarioCarta: string;
    dataScadenza: string;
    cvv: string;
}

export interface DatiPayPalDTO {
    emailPayPal: string;
    passwordPayPal: string;
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

// === CONDIVISIONE SOCIAL ===
export interface ShareRequestDTO {
    nickname: string;
    piattaforma: 'FACEBOOK' | 'TWITTER' | 'INSTAGRAM' | 'WHATSAPP';
    messaggio: string;
}

export interface ShareResponseDTO {
    success: boolean;
    shareUrl: string;
    message: string;
    timestamp: string;
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
    ATTESA_PAGAMENTO = 'ATTESA_PAGAMENTO',
    PRONTO_PER_LAVORAZIONE = 'PRONTO_PER_LAVORAZIONE',
    IN_LAVORAZIONE = 'IN_LAVORAZIONE',
    SPEDITO = 'SPEDITO',
    CONSEGNATO = 'CONSEGNATO',
    ANNULLATO = 'ANNULLATO',
    RIMBORSATO = 'RIMBORSATO'
}

// === METODI DI PAGAMENTO ===
export enum MetodoPagamento {
    CARTA_CREDITO = 'CARTA_CREDITO',
    PAYPAL = 'PAYPAL',
    BONIFICO = 'BONIFICO'
}

// === PIATTAFORME SOCIAL ===
export enum PiattaformaSocial {
    FACEBOOK = 'FACEBOOK',
    TWITTER = 'TWITTER',
    INSTAGRAM = 'INSTAGRAM',
    WHATSAPP = 'WHATSAPP'
}

// === STATI EVENTO ===
export enum StatoEvento {
    IN_PROGRAMMA = 'IN_PROGRAMMA',
    IN_CORSO = 'IN_CORSO',
    CONCLUSO = 'CONCLUSO',
    ANNULLATO = 'ANNULLATO'
}