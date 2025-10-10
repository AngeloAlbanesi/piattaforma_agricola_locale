import { Injectable } from '@angular/core';
import { Observable, forkJoin, of, BehaviorSubject, combineLatest } from 'rxjs';
import { map, catchError, debounceTime, distinctUntilChanged, shareReplay } from 'rxjs/operators';

import { PublicProdottiService } from './public-prodotti.service';
import { PublicPacchettiService } from './public-pacchetti.service';
import { PublicAziendeService } from './public-aziende.service';

import {
    CatalogItem,
    CatalogFilters,
    CatalogSearchResult,
    CatalogFilterOptions,
    DEFAULT_CATALOG_FILTERS,
    CatalogSortBy
} from '../models/catalog.models';

import {
    PublicProdottoSummaryDTO,
    PublicPacchettoSummaryDTO,
    PublicAziendaSummaryDTO
} from '../models/public.models';

/**
 * Servizio per gestire il catalogo unificato di prodotti e pacchetti
 */
@Injectable({
    providedIn: 'root'
})
export class CatalogService {
    // Cache per le aziende
    private aziendeCache$?: Observable<PublicAziendaSummaryDTO[]>;

    // Subject per gestire i filtri correnti
    private filtersSubject = new BehaviorSubject<CatalogFilters>(DEFAULT_CATALOG_FILTERS);
    public filters$ = this.filtersSubject.asObservable();

    constructor(
        private prodottiService: PublicProdottiService,
        private pacchettiService: PublicPacchettiService,
        private aziendeService: PublicAziendeService
    ) { }

    // === METODI PRINCIPALI ===

    /**
     * Cerca nel catalogo applicando i filtri specificati
     */
    searchCatalog(filters: CatalogFilters): Observable<CatalogSearchResult> {
        // Aggiorna i filtri correnti
        this.filtersSubject.next(filters);

        // Determina quali API chiamare in base al filtro tipo
        const shouldFetchProdotti = !filters.tipo || filters.tipo === 'TUTTI' || filters.tipo === 'PRODOTTO';
        const shouldFetchPacchetti = !filters.tipo || filters.tipo === 'TUTTI' || filters.tipo === 'PACCHETTO';

        // Prepara le chiamate API
        const requests: Observable<CatalogItem[]>[] = [];

        if (shouldFetchProdotti) {
            requests.push(this.fetchProdotti(filters));
        }

        if (shouldFetchPacchetti) {
            requests.push(this.fetchPacchetti(filters));
        }

        // Se non ci sono richieste, ritorna risultato vuoto
        if (requests.length === 0) {
            return of(this.createEmptyResult());
        }

        // Esegue le richieste in parallelo e combina i risultati
        return forkJoin(requests).pipe(
            map(results => this.combineResults(results.flat(), filters)),
            catchError(error => {
                console.error('Errore durante la ricerca nel catalogo:', error);
                return of(this.createEmptyResult());
            })
        );
    }

    /**
     * Ottiene le opzioni disponibili per i filtri
     */
    getFilterOptions(): Observable<CatalogFilterOptions> {
        return combineLatest([
            this.getAllAziende(),
            this.getAllProdotti(),
            this.getAllPacchetti()
        ]).pipe(
            map(([aziende, prodotti, pacchetti]) => {
                const allItems = [
                    ...prodotti.map(p => this.convertProdottoToCatalogItem(p)),
                    ...pacchetti.map(p => this.convertPacchettoToCatalogItem(p))
                ];

                // Estrae categorie uniche
                const categorie = [...new Set(
                    allItems.map(item => item.categoria).filter(Boolean)
                )].sort() as string[];

                // Estrae certificazioni uniche
                const certificazioni = [...new Set(
                    allItems.flatMap(item => item.certificazioni || [])
                )].sort();

                // Calcola range prezzi
                const prezzi = allItems.map(item => item.prezzo);
                const prezzoMin = prezzi.length > 0 ? Math.floor(Math.min(...prezzi)) : 0;
                const prezzoMax = prezzi.length > 0 ? Math.ceil(Math.max(...prezzi)) : 100;

                return {
                    categorie,
                    certificazioni,
                    aziende: aziende.map(a => ({
                        id: a.id,
                        nome: a.nomeAzienda,
                        tipologia: a.tipologia
                    })),
                    prezzoMin,
                    prezzoMax
                };
            }),
            catchError(() => of({
                categorie: [],
                certificazioni: [],
                aziende: [],
                prezzoMin: 0,
                prezzoMax: 100
            }))
        );
    }

    /**
     * Cerca per query testuale
     */
    searchByQuery(query: string, filters?: Partial<CatalogFilters>): Observable<CatalogSearchResult> {
        const searchFilters: CatalogFilters = {
            ...DEFAULT_CATALOG_FILTERS,
            ...filters,
            searchQuery: query
        };
        return this.searchCatalog(searchFilters);
    }

    /**
     * Filtra per azienda specifica
     */
    filterByAzienda(aziendaId: number, filters?: Partial<CatalogFilters>): Observable<CatalogSearchResult> {
        const aziendeFilters: CatalogFilters = {
            ...DEFAULT_CATALOG_FILTERS,
            ...filters,
            aziende: [aziendaId]
        };
        return this.searchCatalog(aziendeFilters);
    }

    // === METODI PRIVATI ===

    /**
     * Recupera tutti i prodotti applicando i filtri
     * NOTA: La ricerca viene fatta lato frontend perché l'API di ricerca del backend non funziona correttamente
     */
    private fetchProdotti(filters: CatalogFilters): Observable<CatalogItem[]> {
        // Se c'è un filtro per azienda, usa l'endpoint specifico
        if (filters.aziende && filters.aziende.length > 0) {
            return this.prodottiService.getProdottiByVenditore(filters.aziende[0], {
                categoria: filters.categorie?.[0],
                prezzoMin: filters.prezzoMin,
                prezzoMax: filters.prezzoMax,
                disponibilita: filters.disponibilitaSolo,
                page: filters.page,
                size: filters.size
            }).pipe(
                map(response => {
                    let prodotti = (response.content || []).map(p => this.convertProdottoToCatalogItem(p));
                    // Applica filtro ricerca lato frontend
                    if (filters.searchQuery) {
                        prodotti = this.filterBySearchQuery(prodotti, filters.searchQuery);
                    }
                    return prodotti;
                }),
                catchError(() => of([]))
            );
        }

        // Usa l'endpoint generale
        return this.prodottiService.getProdotti({
            categoria: filters.categorie?.[0],
            prezzoMin: filters.prezzoMin,
            prezzoMax: filters.prezzoMax,
            disponibilita: filters.disponibilitaSolo,
            page: filters.page,
            size: filters.size
        }).pipe(
            map(response => {
                let prodotti = (response.content || []).map(p => this.convertProdottoToCatalogItem(p));
                // Applica filtro ricerca lato frontend
                if (filters.searchQuery) {
                    prodotti = this.filterBySearchQuery(prodotti, filters.searchQuery);
                }
                return prodotti;
            }),
            catchError(() => of([]))
        );
    }

    /**
     * Recupera tutti i pacchetti applicando i filtri
     * NOTA: La ricerca viene fatta lato frontend perché l'API di ricerca del backend non funziona correttamente
     */
    private fetchPacchetti(filters: CatalogFilters): Observable<CatalogItem[]> {
        // Se c'è un filtro per azienda/distributore
        if (filters.aziende && filters.aziende.length > 0) {
            return this.pacchettiService.getPacchettiByDistributore(filters.aziende[0], {
                categoria: filters.categorie?.[0],
                prezzoMin: filters.prezzoMin,
                prezzoMax: filters.prezzoMax,
                disponibilita: filters.disponibilitaSolo,
                page: filters.page,
                size: filters.size
            }).pipe(
                map(response => {
                    let pacchetti = (response.content || []).map(p => this.convertPacchettoToCatalogItem(p));
                    // Applica filtro ricerca lato frontend
                    if (filters.searchQuery) {
                        pacchetti = this.filterBySearchQuery(pacchetti, filters.searchQuery);
                    }
                    return pacchetti;
                }),
                catchError(() => of([]))
            );
        }

        // Usa l'endpoint generale
        return this.pacchettiService.getPacchetti({
            categoria: filters.categorie?.[0],
            prezzoMin: filters.prezzoMin,
            prezzoMax: filters.prezzoMax,
            disponibilita: filters.disponibilitaSolo,
            page: filters.page,
            size: filters.size
        }).pipe(
            map(response => {
                let pacchetti = (response.content || []).map(p => this.convertPacchettoToCatalogItem(p));
                // Applica filtro ricerca lato frontend
                if (filters.searchQuery) {
                    pacchetti = this.filterBySearchQuery(pacchetti, filters.searchQuery);
                }
                return pacchetti;
            }),
            catchError(() => of([]))
        );
    }

    /**
     * Recupera tutte le aziende (con cache)
     */
    private getAllAziende(): Observable<PublicAziendaSummaryDTO[]> {
        if (!this.aziendeCache$) {
            this.aziendeCache$ = this.aziendeService.getAziende({ size: 1000 }).pipe(
                map(response => response.content || []),
                shareReplay(1),
                catchError(() => of([]))
            );
        }
        return this.aziendeCache$;
    }

    /**
     * Recupera tutti i prodotti (per opzioni filtri)
     */
    private getAllProdotti(): Observable<PublicProdottoSummaryDTO[]> {
        return this.prodottiService.getProdotti({ size: 1000 }).pipe(
            map(response => response.content || []),
            catchError(() => of([]))
        );
    }

    /**
     * Recupera tutti i pacchetti (per opzioni filtri)
     */
    private getAllPacchetti(): Observable<PublicPacchettoSummaryDTO[]> {
        return this.pacchettiService.getPacchetti({ size: 1000 }).pipe(
            map(response => response.content || []),
            catchError(() => of([]))
        );
    }

    /**
     * Converte un prodotto in CatalogItem
     */
    private convertProdottoToCatalogItem(prodotto: PublicProdottoSummaryDTO): CatalogItem {
        // Estrae ID prodotto dai possibili campi
        const id = prodotto.idProdotto || prodotto.id || 0;

        // Estrae info azienda dai possibili campi
        const aziendaId = prodotto.idVenditore || prodotto.produttore?.id || 0;
        const aziendaNome = prodotto.nomeVenditore || prodotto.produttore?.nomeAzienda || 'Azienda Sconosciuta';

        return {
            id,
            tipo: 'PRODOTTO',
            nome: prodotto.nome,
            descrizione: prodotto.descrizione,
            prezzo: prodotto.prezzo,
            quantitaDisponibile: prodotto.quantitaDisponibile,
            immagineUrl: prodotto.immagineUrl,
            categoria: prodotto.categoria,
            certificazioni: prodotto.certificazioni,
            azienda: {
                id: aziendaId,
                nome: aziendaNome
            },
            unitaMisura: prodotto.unitaMisura,
            luogoOrigine: prodotto.luogoOrigine,
            originalData: prodotto
        };
    }

    /**
     * Converte un pacchetto in CatalogItem
     */
    private convertPacchettoToCatalogItem(pacchetto: PublicPacchettoSummaryDTO): CatalogItem {
        // Estrae ID pacchetto dai possibili campi
        const id = pacchetto.idPacchetto || pacchetto.id || 0;

        // Estrae info azienda/distributore dai possibili campi
        const aziendaId = pacchetto.idDistributore || pacchetto.distributore?.id || 0;
        const aziendaNome = pacchetto.nomeDistributore || pacchetto.distributore?.nomeAzienda || 'Azienda Sconosciuta';

        // Estrae prezzo dai possibili campi
        const prezzoBase = pacchetto.prezzoPacchetto || pacchetto.prezzoScontato || pacchetto.prezzo || 0;
        const sconto = pacchetto.sconto || 0;

        const prezzoScontato = sconto > 0 && prezzoBase
            ? prezzoBase * (1 - sconto / 100)
            : undefined;

        // Estrae numero prodotti dai possibili campi
        const numeroProdotti = pacchetto.numeroElementi || pacchetto.numeroProdotti || 0;

        return {
            id,
            tipo: 'PACCHETTO',
            nome: pacchetto.nome,
            descrizione: pacchetto.descrizione,
            prezzo: prezzoScontato || prezzoBase,
            prezzoOriginale: prezzoScontato ? prezzoBase : undefined,
            prezzoScontato,
            sconto: pacchetto.sconto,
            quantitaDisponibile: pacchetto.quantitaDisponibile,
            immagineUrl: pacchetto.immagineUrl,
            categoria: pacchetto.categoria,
            certificazioni: [],
            azienda: {
                id: aziendaId,
                nome: aziendaNome
            },
            numeroProdotti,
            originalData: pacchetto
        };
    }

    /**
     * Combina i risultati e applica ordinamento e filtri aggiuntivi
     */
    private combineResults(items: CatalogItem[], filters: CatalogFilters): CatalogSearchResult {
        let filteredItems = [...items];

        // Applica filtro certificazioni se presente
        if (filters.certificazioni && filters.certificazioni.length > 0) {
            filteredItems = filteredItems.filter(item =>
                item.certificazioni?.some(cert => filters.certificazioni?.includes(cert))
            );
        }

        // Applica filtro categorie se presente (filtro aggiuntivo client-side)
        if (filters.categorie && filters.categorie.length > 0) {
            filteredItems = filteredItems.filter(item =>
                filters.categorie?.includes(item.categoria || '')
            );
        }

        // Applica ordinamento
        filteredItems = this.sortItems(filteredItems, filters.sortBy || 'nome_asc');

        // Calcola paginazione
        const totalElements = filteredItems.length;
        const totalPages = Math.ceil(totalElements / filters.size);
        const startIndex = filters.page * filters.size;
        const endIndex = startIndex + filters.size;
        const paginatedItems = filteredItems.slice(startIndex, endIndex);

        return {
            items: paginatedItems,
            totalElements,
            totalPages,
            currentPage: filters.page,
            pageSize: filters.size,
            hasNext: filters.page < totalPages - 1,
            hasPrevious: filters.page > 0
        };
    }

    /**
     * Ordina gli items secondo il criterio specificato
     */
    private sortItems(items: CatalogItem[], sortBy: CatalogSortBy): CatalogItem[] {
        const sortedItems = [...items];

        switch (sortBy) {
            case 'prezzo_asc':
                return sortedItems.sort((a, b) => a.prezzo - b.prezzo);
            case 'prezzo_desc':
                return sortedItems.sort((a, b) => b.prezzo - a.prezzo);
            case 'nome_asc':
                return sortedItems.sort((a, b) => a.nome.localeCompare(b.nome));
            case 'nome_desc':
                return sortedItems.sort((a, b) => b.nome.localeCompare(a.nome));
            case 'disponibilita_desc':
                return sortedItems.sort((a, b) => b.quantitaDisponibile - a.quantitaDisponibile);
            case 'sconto_desc':
                return sortedItems.sort((a, b) => (b.sconto || 0) - (a.sconto || 0));
            default:
                return sortedItems;
        }
    }

    /**
     * Crea un risultato vuoto
     */
    private createEmptyResult(): CatalogSearchResult {
        return {
            items: [],
            totalElements: 0,
            totalPages: 0,
            currentPage: 0,
            pageSize: DEFAULT_CATALOG_FILTERS.size,
            hasNext: false,
            hasPrevious: false
        };
    }

    /**
     * Filtra gli item per query di ricerca (lato frontend)
     * Cerca in modo case-insensitive su: nome, descrizione, azienda, categoria
     */
    private filterBySearchQuery(items: CatalogItem[], query: string): CatalogItem[] {
        if (!query || query.trim().length === 0) {
            return items;
        }

        const searchTerm = query.toLowerCase().trim();

        return items.filter(item => {
            // Cerca nel nome
            if (item.nome?.toLowerCase().includes(searchTerm)) {
                return true;
            }

            // Cerca nella descrizione
            if (item.descrizione?.toLowerCase().includes(searchTerm)) {
                return true;
            }

            // Cerca nel nome azienda
            if (item.azienda?.nome?.toLowerCase().includes(searchTerm)) {
                return true;
            }

            // Cerca nella categoria
            if (item.categoria?.toLowerCase().includes(searchTerm)) {
                return true;
            }

            // Cerca nelle certificazioni
            if (item.certificazioni?.some(cert => cert.toLowerCase().includes(searchTerm))) {
                return true;
            }

            return false;
        });
    }

    // === METODI UTILITY PUBBLICI ===

    /**
     * Resetta i filtri ai valori di default
     */
    resetFilters(): void {
        this.filtersSubject.next(DEFAULT_CATALOG_FILTERS);
    }

    /**
     * Aggiorna i filtri correnti
     */
    updateFilters(filters: Partial<CatalogFilters>): void {
        const currentFilters = this.filtersSubject.value;
        this.filtersSubject.next({ ...currentFilters, ...filters });
    }

    /**
     * Ottiene i filtri correnti
     */
    getCurrentFilters(): CatalogFilters {
        return this.filtersSubject.value;
    }

    /**
     * Invalida la cache delle aziende
     */
    invalidateCache(): void {
        this.aziendeCache$ = undefined;
    }
}

