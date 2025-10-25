import { Injectable } from '@angular/core';
import { Observable, forkJoin, of, BehaviorSubject, combineLatest } from 'rxjs';
import { map, catchError, debounceTime, distinctUntilChanged, shareReplay, switchMap } from 'rxjs/operators';

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

    // Mappa prodottoId -> aziendaId (popolata dalle certificazioni)
    private prodottoToAziendaMap = new Map<number, number>();

    // Mappa aziendaId -> nome azienda (popolata dalle aziende caricate)
    private aziendaNameMap = new Map<number, string>();

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
     * Prima carica le aziende per popolare le mappe, poi i prodotti/pacchetti
     */
    searchCatalog(filters: CatalogFilters): Observable<CatalogSearchResult> {
        // Aggiorna i filtri correnti
        this.filtersSubject.next(filters);

        // Prima carica le aziende per popolare le mappe
        return this.getAllAziende().pipe(
            switchMap(() => {
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
                    map(results => this.combineResults(results.flat(), filters))
                );
            }),
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
     * NOTA: La ricerca e il filtro aziende vengono fatti lato frontend perché l'API di ricerca del backend non funziona correttamente
     */
    private fetchProdotti(filters: CatalogFilters): Observable<CatalogItem[]> {
        // Usa sempre l'endpoint generale e filtra lato frontend
        return this.prodottiService.getProdotti({
            categoria: filters.categorie?.[0],
            prezzoMin: filters.prezzoMin,
            prezzoMax: filters.prezzoMax,
            disponibilita: filters.disponibilitaSolo,
            page: 0,
            size: 1000  // Prende tutti i prodotti per filtrarli client-side
        }).pipe(
            map(response => {
                let prodotti = (response.content || []).map(p => this.convertProdottoToCatalogItem(p));

                // Applica filtro ricerca lato frontend
                if (filters.searchQuery) {
                    prodotti = this.filterBySearchQuery(prodotti, filters.searchQuery);
                }

                // Applica filtro aziende lato frontend
                if (filters.aziende && filters.aziende.length > 0) {
                    prodotti = prodotti.filter(p =>
                        filters.aziende!.includes(p.azienda.id)
                    );
                }

                // Applica filtro disponibilità lato frontend
                if (filters.disponibilitaSolo) {
                    prodotti = prodotti.filter(p => p.quantitaDisponibile > 0);
                }

                // Applica filtro prezzo lato frontend
                if (filters.prezzoMin !== undefined) {
                    prodotti = prodotti.filter(p => p.prezzo >= filters.prezzoMin!);
                }
                if (filters.prezzoMax !== undefined) {
                    prodotti = prodotti.filter(p => p.prezzo <= filters.prezzoMax!);
                }

                return prodotti;
            }),
            catchError(() => of([]))
        );
    }

    /**
     * Recupera tutti i pacchetti applicando i filtri
     * NOTA: La ricerca e il filtro aziende vengono fatti lato frontend perché l'API di ricerca del backend non funziona correttamente
     */
    private fetchPacchetti(filters: CatalogFilters): Observable<CatalogItem[]> {
        // Usa sempre l'endpoint generale e filtra lato frontend
        return this.pacchettiService.getPacchetti({
            categoria: filters.categorie?.[0],
            prezzoMin: filters.prezzoMin,
            prezzoMax: filters.prezzoMax,
            disponibilita: filters.disponibilitaSolo,
            page: 0,
            size: 1000  // Prende tutti i pacchetti per filtrarli client-side
        }).pipe(
            map(response => {
                let pacchetti = (response.content || []).map(p => this.convertPacchettoToCatalogItem(p));

                // Applica filtro ricerca lato frontend
                if (filters.searchQuery) {
                    pacchetti = this.filterBySearchQuery(pacchetti, filters.searchQuery);
                }

                // Applica filtro aziende lato frontend
                if (filters.aziende && filters.aziende.length > 0) {
                    pacchetti = pacchetti.filter(p =>
                        filters.aziende!.includes(p.azienda.id)
                    );
                }

                // Applica filtro disponibilità lato frontend
                if (filters.disponibilitaSolo) {
                    pacchetti = pacchetti.filter(p => p.quantitaDisponibile > 0);
                }

                // Applica filtro prezzo lato frontend
                if (filters.prezzoMin !== undefined) {
                    pacchetti = pacchetti.filter(p => p.prezzo >= filters.prezzoMin!);
                }
                if (filters.prezzoMax !== undefined) {
                    pacchetti = pacchetti.filter(p => p.prezzo <= filters.prezzoMax!);
                }

                return pacchetti;
            }),
            catchError(() => of([]))
        );
    }

    /**
     * Recupera tutte le aziende (con cache) e popola le mappe
     */
    private getAllAziende(): Observable<PublicAziendaSummaryDTO[]> {
        if (!this.aziendeCache$) {
            this.aziendeCache$ = this.aziendeService.getAziende({ size: 1000 }).pipe(
                map(response => {
                    const aziende = response.content || [];
                    // Popola le mappe
                    aziende.forEach(azienda => {
                        const nome = azienda.nomeAzienda || 'Azienda Sconosciuta';
                        // Mappa aziendaId -> nome
                        this.aziendaNameMap.set(azienda.id, nome);

                        // Mappa prodottoId -> aziendaId usando le certificazioni
                        // NOTA: L'API restituisce certificazioniAzienda ma il DTO non lo riflette
                        const azienda_any = azienda as any;
                        if (azienda_any.certificazioniAzienda) {
                            azienda_any.certificazioniAzienda.forEach((cert: any) => {
                                if (cert.idProdottoAssociato) {
                                    this.prodottoToAziendaMap.set(cert.idProdottoAssociato, azienda.id);
                                }
                            });
                        }
                    });
                    return aziende;
                }),
                shareReplay(1),
                catchError(() => of([]))
            );
        }
        return this.aziendeCache$;
    }

    /**
     * Ottiene il nome dell'azienda dato un ID prodotto
     */
    private getAziendaNameByProdottoId(prodottoId: number): string {
        const aziendaId = this.prodottoToAziendaMap.get(prodottoId);
        if (aziendaId) {
            return this.aziendaNameMap.get(aziendaId) || 'Azienda Sconosciuta';
        }
        return 'Azienda Sconosciuta';
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

        // Usa direttamente i campi nomeAzienda e idAzienda dal backend
        const aziendaNome = prodotto.nomeAzienda || prodotto.produttore?.nomeAzienda || 'Azienda Sconosciuta';
        const aziendaId = prodotto.idAzienda || prodotto.produttore?.id || 0;

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

        // Usa direttamente i campi nomeAzienda e idAzienda dal backend
        const aziendaNome = pacchetto.nomeAzienda || pacchetto.distributore?.nomeAzienda || 'Azienda Sconosciuta';
        const aziendaId = pacchetto.idAzienda || pacchetto.distributore?.id || 0;

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

