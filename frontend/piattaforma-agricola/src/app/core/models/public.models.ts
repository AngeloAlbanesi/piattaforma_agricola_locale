/**
 * Modelli DTO per le API pubbliche della piattaforma agricola
 * Basati sulle specifiche definite in Api pubbliche/api pubbliche.json
 */

import { PaginatedResponse } from './common.models';

// === MODELLI PER PRODOTTI ===

// DTO base per prodotto pubblico
export interface PublicProdottoSummaryDTO {
    idProdotto?: number;  // Campo dal backend
    id?: number;          // Campo alternativo
    nome: string;
    descrizione?: string;
    prezzo: number;
    categoria?: string;
    quantitaDisponibile: number;
    unitaMisura?: string;
    luogoOrigine?: string;
    // Campi dal backend
    nomeVenditore?: string;
    idVenditore?: number;
    nomeAzienda?: string;     // Nome dell'azienda del venditore
    idAzienda?: number;       // ID dell'azienda del venditore
    // Campi alternativi
    produttore?: {
        id: number;
        nomeAzienda: string;
    };
    certificazioni?: string[];
    immagineUrl?: string;
}

// DTO dettagliato per prodotto pubblico
export interface PublicProdottoDetailDTO extends PublicProdottoSummaryDTO {
    tipoOrigine?: string; // 'COLTIVATO', 'COLTIVATO_ALLEVATO', 'TRASFORMATO'
    idProcessoTrasformazioneOriginario?: number;
    idMetodoDiColtivazione?: number;
    metodoColtivazione?: {
        id: number;
        nome: string;
        descrizione: string;
        tipo?: string;
        principi?: string[];
        restrizioni?: string[];
        dataInizio?: string;
        dataFine?: string;
    };
    metodoDiColtivazione?: {  // Alias per backend compatibility
        id: number;
        nome: string;
        descrizione: string;
        tipo?: string;
        principi?: string[];
        restrizioni?: string[];
        dataInizio?: string;
        dataFine?: string;
    };
    certificazioniDettagli?: CertificazioneProdottoDTO[];  // Frontend format
    certificazioni?: any[];  // Backend format
    tracciabilita?: TracciabilitaProdottoDTO;
    dataRaccolta?: string;
    dataDisponibilita?: string;
    valutazioneMedia?: number;
    numeroRecensioni?: number;
    prodottoVenditoreId?: number;
    venditore?: {
        idUtente: number;
        nome: string;
        cognome: string;
        tipoRuolo: string;
        statoAccreditamento?: string;
        isAttivo: boolean;
    };
}

// DTO per certificazioni prodotto
export interface CertificazioneProdottoDTO {
    id: number;
    tipoCertificazione: string;
    numeroRiferimento: string;
    enteRilascio: string;
    dataRilascio: string;
    dataScadenza: string;
    documentoUrl?: string;
}

// DTO per metodo di coltivazione
export interface MetodoColtivazioneDTO {
    id: number;
    nome: string;
    descrizione: string;
    tipo: string;
    principi: string[];
    restrizioni: string[];
}

// DTO per tracciabilità prodotto
export interface TracciabilitaProdottoDTO {
    prodottoFinale: {
        id: number;
        nome: string;
    };
    processiTrasformazione: Array<{
        id: number;
        nome: string;
        data: string;
    }>;
    prodottiOrigine: Array<{
        id: number;
        nome: string;
        produttore: string;
        quantita: number;
        unitaMisura: string;
    }>;
    certificazioni: string[];
    catenaCompleta: boolean;
}

// === MODELLI PER PROCESSI DI TRASFORMAZIONE ===

// DTO per processo di trasformazione pubblico
export interface ProcessoTrasformazionePublicDTO {
    idProcesso: number;
    nomeProcesso: string;
    descrizioneProcesso: string;
    metodoProduzione: string;
    dataCreazione: string;
    fasiLavorazione?: FaseLavorazioneDTO[];
}

// DTO per fase di lavorazione
export interface FaseLavorazioneDTO {
    id: number;
    nome: string;
    numeroFase: number;
    descrizione: string;
    fontiMateriePrime?: FonteMateriaPrimaDTO[];
}

// DTO per fonte materia prima
export interface FonteMateriaPrimaDTO {
    id: number;
    tipoFonte: string; // 'ESTERNA' o 'INTERNA'
    descrizioneFonte?: string;
    prodottoId?: number;
    prodottoNome?: string;
    quantita: number;
    unitaMisura: string;
}

// === MODELLI PER PACCHETTI ===

// DTO base per pacchetto pubblico
export interface PublicPacchettoSummaryDTO {
    idPacchetto?: number;  // Campo dal backend
    id?: number;           // Campo alternativo
    nome: string;
    descrizione?: string;
    prezzoPacchetto?: number;  // Campo dal backend
    prezzo?: number;           // Campo alternativo
    sconto?: number;
    prezzoScontato?: number;
    quantitaDisponibile: number;
    // Campi dal backend
    nomeDistributore?: string;
    idDistributore?: number;
    numeroElementi?: number;  // Campo dal backend
    nomeAzienda?: string;      // Nome dell'azienda del distributore
    idAzienda?: number;        // ID dell'azienda del distributore
    // Campi alternativi
    distributore?: {
        id: number;
        nomeAzienda: string;
    };
    numeroProdotti?: number;
    immagineUrl?: string;
    categoria?: string;
    // Lista opzionale dei prodotti contenuti nel pacchetto (quando fornita dal backend)
    prodotti?: ElementoPacchettoDTO[];
}

// DTO dettagliato per pacchetto pubblico
export interface PublicPacchettoDetailDTO extends PublicPacchettoSummaryDTO {
    prodotti: ElementoPacchettoDTO[];
    composizione: ComposizionePacchettoDTO[];
    certificazioni?: CertificazioneProdottoDTO[];
    dataCreazione: string;
    dataScadenza?: string;
    valutazioneMedia?: number;
    numeroRecensioni?: number;
}

// DTO per elemento pacchetto
export interface ElementoPacchettoDTO {
    id: number;
    prodotto: PublicProdottoSummaryDTO;
    quantita: number;
    unitaMisura: string;
}

// DTO per composizione pacchetto
export interface ComposizionePacchettoDTO {
    id: number;
    nome: string;
    descrizione?: string;
    prodotti: ElementoPacchettoDTO[];
    percentuale: number;
}

// === MODELLI PER EVENTI ===

// DTO base per evento pubblico
export interface PublicEventoSummaryDTO {
    idEvento: number;  // Allineato con risposta API
    id?: number;  // Alias per compatibilità
    nomeEvento: string;  // Allineato con risposta API
    nome?: string;  // Alias per compatibilità
    descrizione?: string;
    dataOraInizio: string;
    dataOraFine: string;
    luogoEvento: string;  // Allineato con risposta API
    luogo?: string;  // Alias per compatibilità
    indirizzo?: string;
    organizzatore?: {
        id: number;
        nomeAzienda: string;
    };
    nomeOrganizzatore?: string;  // Dalla risposta API
    idOrganizzatore?: number;  // Dalla risposta API
    numeroPartecipanti?: number;
    numeroMassimoPartecipanti?: number;
    capienzaMassima?: number;  // Dalla risposta API
    postiDisponibili?: number;  // Dalla risposta API
    immagineUrl?: string;
    statoEvento: string;  // Allineato con risposta API
    stato?: string;  // Alias per compatibilità
}

// DTO dettagliato per evento pubblico
export interface PublicEventoDetailDTO extends PublicEventoSummaryDTO {
    programma?: ProgrammaEventoDTO[];
    aziendePartecipanti: AziendaPartecipanteDTO[];
    costo?: number;
    modalitaIscrizione: string;
    requisitiPartecipazione?: string[];
    contatti: ContattoEventoDTO[];
    materialiForniti?: string[];
    note?: string;
}

// DTO per programma evento
export interface ProgrammaEventoDTO {
    id: number;
    oraInizio: string;
    oraFine: string;
    titolo: string;
    descrizione: string;
    relatore?: string;
    luogo?: string;
}

// DTO per azienda partecipante
export interface AziendaPartecipanteDTO {
    id: number;
    nomeAzienda: string;
    descrizione?: string;
    tipologiaProdotti: string[];
    stand?: string;
}

// DTO per contatti evento
export interface ContattoEventoDTO {
    tipo: 'email' | 'telefono' | 'sito';
    valore: string;
    etichetta?: string;
}

// === MODELLI PER PROCESSI DI TRASFORMAZIONE ===

// DTO base per processo di trasformazione pubblico
export interface PublicProcessoSummaryDTO {
    id: number;
    nome: string;
    descrizione?: string;
    trasformatore: {
        id: number;
        nomeAzienda: string;
    };
    numeroFasi: number;
    prodottiInput: string[];
    prodottiOutput: string[];
    dataCreazione: string;
    stato: string;
}

// DTO dettagliato per processo di trasformazione pubblico
export interface PublicProcessoDetailDTO extends PublicProcessoSummaryDTO {
    fasi: FaseLavorazionePubblicaDTO[];
    tracciabilita: TracciabilitaProcessoDTO[];
    certificazioni: CertificazioneProdottoDTO[];
    durataTotale?: number;
    resa?: number;
}

// DTO per fase di lavorazione pubblica
export interface FaseLavorazionePubblicaDTO {
    id: number;
    nome: string;
    descrizione?: string;
    ordine: number;
    durataPrevista: number;
    durataEffettiva?: number;
    stato: string;
    dataInizio?: string;
    dataFine?: string;
    tecniche: string[];
    attrezzature: string[];
    materiali?: string[];
    note?: string;
}

// DTO per tracciabilità processo
export interface TracciabilitaProcessoDTO {
    id: number;
    codiceUnivoco: string;
    dataCreazione: string;
    prodottiInput: Array<{
        nome: string;
        quantita: number;
        provenienza: string;
    }>;
    fasiEseguite: Array<{
        nome: string;
        dataInizio: string;
        dataFine: string;
    }>;
    prodottiOutput: Array<{
        nome: string;
        quantita: number;
        lotto: string;
    }>;
}

// === MODELLI PER AZIENDE ===

// DTO base per azienda pubblica
export interface PublicAziendaSummaryDTO {
    id: number;
    nomeAzienda: string;
    descrizione?: string;
    tipologia: string;
    indirizzo: {
        via: string;
        citta: string;
        provincia: string;
        cap: string;
    };
    logo?: string;
    numeroProdotti?: number;
    rating?: number;
}

// DTO dettagliato per azienda pubblica
export interface PublicAziendaDetailDTO extends PublicAziendaSummaryDTO {
    telefono?: string;
    email?: string;
    sito?: string;
    certificazioni: CertificazioneAziendaDTO[];
    prodotti: PaginatedResponse<PublicProdottoSummaryDTO>;
    coordinate?: CoordinateDTO;
    distanza?: number;
    dataFondazione?: string;
    storia?: string;
    valori?: string[];
    social?: SocialAziendaDTO[];
}

// DTO per certificazioni azienda
export interface CertificazioneAziendaDTO {
    id: number;
    tipoCertificazione: string;
    numeroRiferimento: string;
    enteRilascio: string;
    dataRilascio: string;
    dataScadenza: string;
    documentoUrl?: string;
}

// DTO per coordinate geografiche
export interface CoordinateDTO {
    latitudine: number;
    longitudine: number;
    indirizzo: string;
}

// DTO per social azienda
export interface SocialAziendaDTO {
    tipo: 'facebook' | 'instagram' | 'twitter' | 'linkedin' | 'youtube';
    url: string;
}

// DTO per calcolo distanza
export interface DistanzaDTO {
    distanzaKm: number;
    durataMinuti?: number;
    indirizzoPartenza: string;
    indirizzoDestinazione: string;
}

// === MODELLI PER FILTRI E RICERCA ===

// Filtri per prodotti
export interface PublicProdottoFilters {
    query?: string;
    categoria?: string;
    prezzoMin?: number;
    prezzoMax?: number;
    produttoreId?: number;
    certificazione?: string;
    luogoOrigine?: string;
    disponibilita?: boolean;
    page?: number;
    size?: number;
    sortBy?: 'prezzo_asc' | 'prezzo_desc' | 'nome_asc' | 'nome_desc' | 'disponibilita';
}

// Filtri per pacchetti
export interface PublicPacchettoFilters {
    query?: string;
    distributoreId?: number;
    prezzoMin?: number;
    prezzoMax?: number;
    categoria?: string;
    disponibilita?: boolean;
    page?: number;
    size?: number;
    sortBy?: 'prezzo_asc' | 'prezzo_desc' | 'nome_asc' | 'nome_desc' | 'sconto_desc';
}

// Filtri per eventi
export interface PublicEventoFilters {
    query?: string;
    organizzatoreId?: number;
    dataInizio?: string;
    dataFine?: string;
    luogo?: string;
    gratuito?: boolean;
    disponibilita?: boolean;
    page?: number;
    size?: number;
    sortBy?: 'data_asc' | 'data_desc' | 'nome_asc' | 'nome_desc';
}

// Filtri per aziende
export interface PublicAziendaFilters {
    query?: string;
    tipologia?: string;
    citta?: string;
    provincia?: string;
    certificazione?: string;
    page?: number;
    size?: number;
    sortBy?: 'nome_asc' | 'nome_desc' | 'rating_desc' | 'prodotti_desc';
}

// Filtri per processi
export interface PublicProcessoFilters {
    query?: string;
    trasformatoreId?: number;
    page?: number;
    size?: number;
    sortBy?: 'nome_asc' | 'nome_desc' | 'data_desc';
    sortDirection?: 'asc' | 'desc';
}

// === TIPO DI DATI ENUM ===

// Stati evento
export enum StatoEvento {
    PROGRAMMATO = 'PROGRAMMATO',
    IN_CORSO = 'IN_CORSO',
    COMPLETATO = 'COMPLETATO',
    ANNULLATO = 'ANNULLATO'
}

// Tipologie certificazioni
export enum TipoCertificazione {
    BIOLOGICO = 'BIOLOGICO',
    ARTIGIANALE = 'ARTIGIANALE',
    DOP = 'DOP',
    IGP = 'IGP',
    STG = 'STG',
    HACCP = 'HACCP'
}

// Tipologie azienda
export enum TipologiaAzienda {
    PRODUZIONE = 'PRODUZIONE',
    TRASFORMAZIONE = 'TRASFORMAZIONE',
    DISTRIBUZIONE = 'DISTRIBUZIONE'
}

// Modalità iscrizione eventi
export enum ModalitaIscrizione {
    GRATUITA = 'GRATUITA',
    A_PAGAMENTO = 'A_PAGAMENTO',
    SU_INVITO = 'SU_INVITO'
}

// === INTERFACCE PER RISPOSTE PAGINATE ===

export interface PublicProdottiResponse extends PaginatedResponse<PublicProdottoSummaryDTO> { }
export interface PublicPacchettiResponse extends PaginatedResponse<PublicPacchettoSummaryDTO> { }
export interface PublicEventiResponse extends PaginatedResponse<PublicEventoSummaryDTO> { }
export interface PublicAziendeResponse extends PaginatedResponse<PublicAziendaSummaryDTO> { }
export interface PublicProcessiResponse extends PaginatedResponse<PublicProcessoSummaryDTO> { }

// === INTERFACCE PER RICHIESTE ===

// Richiesta per calcolo distanza
export interface CalcolaDistanzaRequest {
    indirizzoPartenza: string;
    mezzo?: 'auto' | 'pedonale' | 'bici';
}

// Richiesta per ricerca avanzata
export interface RicercaAvanzataRequest {
    query?: string;
    filtri?: {
        categorie?: string[];
        certificazioni?: string[];
        prezzoMin?: number;
        prezzoMax?: number;
        localita?: string[];
    };
    ordinamento?: {
        campo: string;
        direzione: 'asc' | 'desc';
    };
    paginazione?: {
        pagina: number;
        elementiPerPagina: number;
    };
}

// === HELPER FUNCTIONS ===

/**
 * Estrae l'ID da un prodotto gestendo i vari formati del backend
 */
export function getProdottoId(prodotto: PublicProdottoSummaryDTO): number {
    return prodotto.idProdotto || prodotto.id || 0;
}

/**
 * Estrae l'ID da un pacchetto gestendo i vari formati del backend
 */
export function getPacchettoId(pacchetto: PublicPacchettoSummaryDTO): number {
    return pacchetto.idPacchetto || pacchetto.id || 0;
}

/**
 * Estrae il prezzo da un pacchetto gestendo i vari formati del backend
 */
export function getPacchettoPrezzo(pacchetto: PublicPacchettoSummaryDTO): number {
    return pacchetto.prezzoPacchetto || pacchetto.prezzo || 0;
}