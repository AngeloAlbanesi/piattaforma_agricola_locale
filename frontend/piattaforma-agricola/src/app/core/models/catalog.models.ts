/**
 * Modelli per il catalogo unificato di prodotti e pacchetti
 */

import { PublicProdottoSummaryDTO, PublicPacchettoSummaryDTO } from './public.models';

/**
 * Tipo di item nel catalogo
 */
export type CatalogItemType = 'PRODOTTO' | 'PACCHETTO';

/**
 * Vista del catalogo
 */
export type CatalogViewMode = 'grid' | 'list';

/**
 * Criteri di ordinamento
 */
export type CatalogSortBy =
    | 'prezzo_asc'
    | 'prezzo_desc'
    | 'nome_asc'
    | 'nome_desc'
    | 'disponibilita_desc'
    | 'sconto_desc';

/**
 * Item unificato per il catalogo che può rappresentare sia un prodotto che un pacchetto
 */
export interface CatalogItem {
    // Campi comuni
    id: number;
    tipo: CatalogItemType;
    nome: string;
    descrizione?: string;
    prezzo: number;
    prezzoOriginale?: number; // Per visualizzare il prezzo barrato se scontato
    prezzoScontato?: number;
    sconto?: number;
    quantitaDisponibile: number;
    immagineUrl?: string;
    categoria?: string;
    certificazioni?: string[];

    // Informazioni azienda
    azienda: {
        id: number;
        nome: string;
    };

    // Campi specifici per prodotto
    unitaMisura?: string;
    luogoOrigine?: string;

    // Campi specifici per pacchetto
    numeroProdotti?: number;

    // Dati originali per dettagli
    originalData: PublicProdottoSummaryDTO | PublicPacchettoSummaryDTO;
}

/**
 * Filtri applicabili al catalogo
 */
export interface CatalogFilters {
    // Ricerca testuale
    searchQuery?: string;

    // Filtro per tipo
    tipo?: CatalogItemType | 'TUTTI';

    // Filtro per aziende (array di ID)
    aziende?: number[];

    // Filtro per categorie
    categorie?: string[];

    // Filtro per range di prezzo
    prezzoMin?: number;
    prezzoMax?: number;

    // Filtro per certificazioni
    certificazioni?: string[];

    // Solo prodotti/pacchetti disponibili
    disponibilitaSolo?: boolean;

    // Ordinamento
    sortBy?: CatalogSortBy;

    // Paginazione
    page: number;
    size: number;
}

/**
 * Opzioni per i filtri (valori disponibili)
 */
export interface CatalogFilterOptions {
    categorie: string[];
    certificazioni: string[];
    aziende: Array<{ id: number; nome: string; tipologia: string }>;
    prezzoMin: number;
    prezzoMax: number;
}

/**
 * Risultato della ricerca nel catalogo
 */
export interface CatalogSearchResult {
    items: CatalogItem[];
    totalElements: number;
    totalPages: number;
    currentPage: number;
    pageSize: number;
    hasNext: boolean;
    hasPrevious: boolean;
}

/**
 * Stato del catalogo
 */
export interface CatalogState {
    filters: CatalogFilters;
    viewMode: CatalogViewMode;
    isLoading: boolean;
    error?: string;
    results?: CatalogSearchResult;
}

/**
 * Parametri per la query del catalogo (usati anche per URL params)
 */
export interface CatalogQueryParams {
    q?: string; // search query
    tipo?: string;
    aziende?: string; // comma-separated IDs
    categorie?: string; // comma-separated
    prezzoMin?: string;
    prezzoMax?: string;
    certificazioni?: string; // comma-separated
    disponibili?: string; // 'true' | 'false'
    sort?: string;
    page?: string;
    size?: string;
    view?: string; // 'grid' | 'list'
}

/**
 * Evento di aggiunta al carrello
 */
export interface AddToCartEvent {
    item: CatalogItem;
    quantity: number;
}

/**
 * Configurazione del catalogo
 */
export interface CatalogConfig {
    defaultPageSize: number;
    defaultViewMode: CatalogViewMode;
    defaultSortBy: CatalogSortBy;
    enableCache: boolean;
    cacheDuration: number; // in milliseconds
    showOutOfStock: boolean;
}

/**
 * Valori di default per i filtri
 */
export const DEFAULT_CATALOG_FILTERS: CatalogFilters = {
    tipo: 'TUTTI',
    page: 0,
    size: 20,
    sortBy: 'nome_asc',
    disponibilitaSolo: false
};

/**
 * Configurazione di default del catalogo
 */
export const DEFAULT_CATALOG_CONFIG: CatalogConfig = {
    defaultPageSize: 20,
    defaultViewMode: 'grid',
    defaultSortBy: 'nome_asc',
    enableCache: true,
    cacheDuration: 5 * 60 * 1000, // 5 minuti
    showOutOfStock: true
};

/**
 * Etichette per le categorie
 */
export const CATEGORY_LABELS: Record<string, string> = {
    'FRUTTA': 'Frutta',
    'VERDURA': 'Verdura',
    'LATTE_DERIVATI': 'Latte e Derivati',
    'CARNE': 'Carne',
    'PESCE': 'Pesce',
    'CEREALI': 'Cereali',
    'LEGUMI': 'Legumi',
    'CONSERVE': 'Conserve',
    'OLIO': 'Olio',
    'VINO': 'Vino',
    'FORMAGGI': 'Formaggi',
    'PANE': 'Pane e Panificati',
    'DOLCI': 'Dolci e Pasticceria',
    'MISTO': 'Misto',
    'BIO': 'Biologico',
    'KM0': 'Km0',
    'STAGIONALE': 'Stagionale',
    'REGALO': 'Regalo',
    'ALTRO': 'Altro'
};

/**
 * Etichette per le certificazioni
 */
export const CERTIFICATION_LABELS: Record<string, string> = {
    'BIOLOGICO': 'Biologico',
    'ARTIGIANALE': 'Artigianale',
    'DOP': 'DOP',
    'IGP': 'IGP',
    'STG': 'STG',
    'HACCP': 'HACCP',
    'KM0': 'Km 0',
    'LOCALE': 'Locale'
};

/**
 * Icone Material per le certificazioni
 */
export const CERTIFICATION_ICONS: Record<string, string> = {
    'BIOLOGICO': 'eco',
    'ARTIGIANALE': 'handyman',
    'DOP': 'verified',
    'IGP': 'verified_user',
    'STG': 'workspace_premium',
    'HACCP': 'health_and_safety',
    'KM0': 'near_me',
    'LOCALE': 'place'
};

/**
 * Colori per le certificazioni
 */
export const CERTIFICATION_COLORS: Record<string, string> = {
    'BIOLOGICO': '#4caf50',
    'ARTIGIANALE': '#ff9800',
    'DOP': '#2196f3',
    'IGP': '#9c27b0',
    'STG': '#f44336',
    'HACCP': '#00bcd4',
    'KM0': '#8bc34a',
    'LOCALE': '#3f51b5'
};

