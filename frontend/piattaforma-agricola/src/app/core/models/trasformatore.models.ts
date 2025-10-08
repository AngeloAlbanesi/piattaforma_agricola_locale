/**
 * Modelli TypeScript per il Trasformatore basati sulle API del backend
 */

// === PRODUTTORI ===
export interface ProduttoreSummaryDTO {
    id: number;
    nome: string;
    cognome: string;
    nomeAzienda: string;
}

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
    ordineEsecuzione: number;
    materiaPrimaUtilizzata: string;
    fonte: {
        tipo: 'ESTERNA' | 'INTERNA';
        nomeFornitore?: string; // Solo per tipo ESTERNA
        produttoreId?: number; // Solo per tipo INTERNA
    };
}

export interface CreateFaseLavorazioneRequestDTO {
    nome: string;
    descrizione: string;
    ordineEsecuzione: number;
    materiaPrimaUtilizzata: string;
    fonte: {
        tipo: 'ESTERNA' | 'INTERNA';
        nomeFornitore?: string; // Solo per tipo ESTERNA
        produttoreId?: number; // Solo per tipo INTERNA
    };
}

export interface UpdateFaseLavorazioneRequestDTO {
    nome?: string;
    descrizione?: string;
    ordineEsecuzione?: number;
    materiaPrimaUtilizzata?: string;
    fonte?: {
        tipo: 'ESTERNA' | 'INTERNA';
        nomeFornitore?: string; // Solo per tipo ESTERNA
        produttoreId?: number; // Solo per tipo INTERNA
    };
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
    metodoProduzione?: string;
    prodottoFinaleId?: number;
}

export interface UpdateProcessoRequestDTO {
    nome?: string;
    descrizione?: string;
    metodoProduzione?: string;
}

export interface UpdateStatoProcessoRequestDTO {
    stato: string;
    note?: string;
}

// === INTERFACCE PER DIALOG ===
export interface ProcessoDialogData {
    processo?: ProcessoTrasformazioneSummaryDTO | null;
    isEditMode: boolean;
}

export interface FaseDialogData {
    fase?: FaseLavorazioneDTO | null;
    processoId: number;
    isEditMode: boolean;
}

export interface DeleteConfirmationDialogData {
    title: string;
    message: string;
    confirmText?: string;
    cancelText?: string;
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

// === GESTIONE PRODOTTI TRASFORMATI ===
export interface ProdottoDTO {
    id?: number; // Deprecated, use idProdotto
    idProdotto: number;
    nome: string;
    descrizione: string;
    prezzo: number;
    quantitaDisponibile: number;
    unitaMisura: string;
    immagini?: string[];
    stato: 'BOZZA' | 'IN_APPROVAZIONE' | 'APPROVATO' | 'RIFIUTATO';
    dataCreazione: string;
    dataUltimaModifica: string;
    proprietarioId: number;
    proprietarioNome?: string;
}

export interface ProdottoDetailDTO extends ProdottoDTO {
    processoTrasformazioneId?: number;
    processoTrasformazioneNome?: string;
    certificazioni: CertificazioneProdottoDTO[];
    tracciabilita?: TracciabilitaProdottoDTO;
    recensioni?: RecensioneDTO[];
    mediaValutazione?: number;
    numeroRecensioni?: number;
}

export interface CreateProdottoRequestDTO {
    nome: string;
    descrizione: string;
    prezzo: number;
    quantitaDisponibile: number;
    unitaMisura: string;
    tipoOrigine: string;
    immagini?: string[];
    idProcessoTrasformazioneOriginario?: number;
    idMetodoDiColtivazione?: number;
}

export interface UpdateProdottoRequestDTO {
    nome?: string;
    descrizione?: string;
    prezzo?: number;
    quantitaDisponibile?: number;
    unitaMisura?: string;
    immagini?: string[];
}

export interface IngredienteDTO {
    id?: number;
    prodottoOriginarioId?: number;
    prodottoOriginarioNome?: string;
    nome?: string;
    percentuale: number;
}

export interface CertificazioneProdottoDTO {
    id: number;
    nomeCertificazione: string;
    enteRilascio: string;
    dataRilascio: string;
    dataScadenza: string;
    prodottoId: number;
}

export interface AddCertificazioneRequestDTO {
    nomeCertificazione: string;
    enteRilascio: string;
    dataRilascio: string;
    dataScadenza: string;
}

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

export interface RecensioneDTO {
    id: number;
    valutazione: number;
    commento?: string;
    dataCreazione: string;
    utenteNome: string;
}

// === GESTIONE ORDINI VENDITORE ===
export interface OrdineVenditoreDTO {
    id: number;
    numeroOrdine: string;
    dataOrdine: string;
    stato: 'PENDING' | 'IN_LAVORAZIONE' | 'PRONTO_SPEDIZIONE' | 'SPEDITO' | 'CONSEGNATO' | 'ANNULLATO';
    totale: number;
    clienteId: number;
    clienteNome: string;
    clienteEmail: string;
    numeroArticoli: number;
    dataPrevistaConsegna?: string;
}

export interface OrdineVenditoreDetailDTO extends OrdineVenditoreDTO {
    articoli: ArticoloOrdineDTO[];
    indirizzoSpedizione: IndirizzoDTO;
    indirizzoFatturazione: IndirizzoDTO;
    spedizione?: SpedizioneDTO;
    pagamento: PagamentoDTO;
    note?: string;
    storicoStati: StoricoStatoOrdineDTO[];
}

export interface ArticoloOrdineDTO {
    id: number;
    prodottoId: number;
    prodottoNome: string;
    prodottoImmagine?: string;
    quantita: number;
    prezzoUnitario: number;
    subtotale: number;
    unitaMisura: string;
}

export interface IndirizzoDTO {
    via: string;
    civico: string;
    citta: string;
    cap: string;
    provincia: string;
    paese: string;
    telefono?: string;
    note?: string;
}

export interface SpedizioneDTO {
    corriere: string;
    trackingNumber: string;
    dataSpedizione: string;
    dataConsegnaPrevista: string;
    dataConsegnaEffettiva?: string;
    note?: string;
}

export interface SpedizioneRequestDTO {
    corriere: string;
    trackingNumber: string;
    dataSpedizione: string;
    dataConsegnaPrevista: string;
}

export interface PagamentoDTO {
    id: number;
    metodo: string;
    importo: number;
    stato: string;
    dataPagamento: string;
    transazioneId?: string;
}

export interface StoricoStatoOrdineDTO {
    stato: string;
    data: string;
    nota?: string;
}

export interface AnnullaOrdineRequestDTO {
    motivo: string;
}

// === GESTIONE AZIENDA ===
export interface AziendaDetailDTO {
    id: number;
    nomeAzienda: string;
    partitaIva: string;
    codiceFiscale?: string;
    descrizione?: string;
    indirizzo: IndirizzoDTO;
    telefono: string;
    email: string;
    sito?: string;
    logo?: string;
    certificazioni: CertificazioneAziendaDTO[];
    dataRegistrazione: string;
    statoAccreditamento: 'PENDING' | 'ACCREDITATO' | 'RIFIUTATO' | 'SOSPESO';
    tipologiaAzienda: 'TRASFORMAZIONE' | 'PRODUZIONE' | 'DISTRIBUZIONE';
}

export interface UpdateAziendaRequestDTO {
    nomeAzienda?: string;
    descrizione?: string;
    indirizzo?: IndirizzoDTO;
    telefono?: string;
    email?: string;
    sito?: string;
    logo?: string;
}

export interface CertificazioneAziendaDTO {
    id: number;
    tipoCertificazione: string;
    numeroRiferimento: string;
    enteRilascio: string;
    dataRilascio: string;
    dataScadenza: string;
    documentoUrl?: string;
}

// === FILTRI PRODOTTI ===
export interface ProdottoFilters {
    search?: string;
    categoria?: number;
    stato?: string;
    prezzoMin?: number;
    prezzoMax?: number;
    page?: number;
    size?: number;
}

// === FILTRI ORDINI ===
export interface OrdineFilters {
    stato?: string;
    dataInizio?: string;
    dataFine?: string;
    clienteId?: number;
    page?: number;
    size?: number;
}