import { Component, OnInit, OnDestroy, ChangeDetectorRef, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Subject, combineLatest } from 'rxjs';
import { takeUntil, finalize } from 'rxjs/operators';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatSidenavModule } from '@angular/material/sidenav';

import { CatalogService } from '../../../../core/services/catalog.service';
import { AuthService } from '../../../../core/services/auth.service';
import {
    CatalogItem,
    CatalogFilters,
    CatalogSearchResult,
    CatalogFilterOptions,
    DEFAULT_CATALOG_FILTERS,
    CatalogSortBy,
    CatalogViewMode
} from '../../../../core/models/catalog.models';

import { CatalogItemCardComponent } from '../../components/catalog-item-card/catalog-item-card.component';
import { CatalogFiltersComponent } from '../../components/catalog-filters/catalog-filters.component';
import { CatalogSearchBarComponent } from '../../components/catalog-search-bar/catalog-search-bar.component';
import { CatalogSortHeaderComponent } from '../../components/catalog-sort-header/catalog-sort-header.component';

/**
 * Componente principale per la vista del catalogo
 */
@Component({
    selector: 'app-catalog-view',
    standalone: true,
    imports: [
        CommonModule,
        RouterModule,
        MatProgressSpinnerModule,
        MatPaginatorModule,
        MatButtonModule,
        MatIconModule,
        MatSnackBarModule,
        MatSidenavModule,
        CatalogItemCardComponent,
        CatalogFiltersComponent,
        CatalogSearchBarComponent,
        CatalogSortHeaderComponent
    ],
    templateUrl: './catalog-view.component.html',
    styleUrls: ['./catalog-view.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CatalogViewComponent implements OnInit, OnDestroy {
    private destroy$ = new Subject<void>();

    // Stato
    isLoading = false;
    isAuthenticated = false;
    filters: CatalogFilters = { ...DEFAULT_CATALOG_FILTERS };
    filterOptions: CatalogFilterOptions = {
        categorie: [],
        certificazioni: [],
        aziende: [],
        prezzoMin: 0,
        prezzoMax: 100
    };
    results?: CatalogSearchResult;
    viewMode: CatalogViewMode = 'grid';
    filtersOpened = false;

    constructor(
        private catalogService: CatalogService,
        private authService: AuthService,
        private route: ActivatedRoute,
        private router: Router,
        private snackBar: MatSnackBar,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit(): void {
        this.checkAuthentication();
        this.loadFilterOptions();
        this.loadQueryParams();
        this.performSearch();
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    // === INIZIALIZZAZIONE ===

    /**
     * Verifica lo stato di autenticazione
     */
    private checkAuthentication(): void {
        this.isAuthenticated = this.authService.isAuthenticated();
    }

    /**
     * Carica le opzioni per i filtri
     */
    private loadFilterOptions(): void {
        this.catalogService.getFilterOptions()
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: options => {
                    this.filterOptions = options;
                    this.cdr.markForCheck();
                },
                error: error => {
                    console.error('Errore caricamento opzioni filtri:', error);
                    this.showError('Errore nel caricamento delle opzioni di filtro');
                }
            });
    }

    /**
     * Carica i parametri dalla query string
     */
    private loadQueryParams(): void {
        this.route.queryParams
            .pipe(takeUntil(this.destroy$))
            .subscribe(params => {
                this.filters = {
                    ...DEFAULT_CATALOG_FILTERS,
                    searchQuery: params['q'] || undefined,
                    tipo: params['tipo'] || 'TUTTI',
                    aziende: params['aziende'] ? params['aziende'].split(',').map(Number) : undefined,
                    categorie: params['categorie'] ? params['categorie'].split(',') : undefined,
                    prezzoMin: params['prezzoMin'] ? Number(params['prezzoMin']) : undefined,
                    prezzoMax: params['prezzoMax'] ? Number(params['prezzoMax']) : undefined,
                    certificazioni: params['certificazioni'] ? params['certificazioni'].split(',') : undefined,
                    disponibilitaSolo: params['disponibili'] === 'true',
                    sortBy: (params['sort'] as CatalogSortBy) || 'nome_asc',
                    page: params['page'] ? Number(params['page']) : 0,
                    size: params['size'] ? Number(params['size']) : 20
                };
                this.viewMode = (params['view'] as CatalogViewMode) || 'grid';
            });
    }

    /**
     * Aggiorna i query params nell'URL
     */
    private updateQueryParams(): void {
        const queryParams: any = {};

        if (this.filters.searchQuery) queryParams.q = this.filters.searchQuery;
        if (this.filters.tipo && this.filters.tipo !== 'TUTTI') queryParams.tipo = this.filters.tipo;
        if (this.filters.aziende?.length) queryParams.aziende = this.filters.aziende.join(',');
        if (this.filters.categorie?.length) queryParams.categorie = this.filters.categorie.join(',');
        if (this.filters.prezzoMin) queryParams.prezzoMin = this.filters.prezzoMin;
        if (this.filters.prezzoMax) queryParams.prezzoMax = this.filters.prezzoMax;
        if (this.filters.certificazioni?.length) queryParams.certificazioni = this.filters.certificazioni.join(',');
        if (this.filters.disponibilitaSolo) queryParams.disponibili = 'true';
        if (this.filters.sortBy !== 'nome_asc') queryParams.sort = this.filters.sortBy;
        if (this.filters.page > 0) queryParams.page = this.filters.page;
        if (this.filters.size !== 20) queryParams.size = this.filters.size;
        if (this.viewMode !== 'grid') queryParams.view = this.viewMode;

        this.router.navigate([], {
            relativeTo: this.route,
            queryParams,
            queryParamsHandling: 'merge',
            replaceUrl: true
        });
    }

    // === RICERCA E FILTRI ===

    /**
     * Esegue la ricerca nel catalogo
     */
    private performSearch(): void {
        this.isLoading = true;
        this.cdr.markForCheck();

        this.catalogService.searchCatalog(this.filters)
            .pipe(
                takeUntil(this.destroy$),
                finalize(() => {
                    this.isLoading = false;
                    this.cdr.markForCheck();
                })
            )
            .subscribe({
                next: results => {
                    this.results = results;
                    this.updateQueryParams();
                },
                error: error => {
                    console.error('Errore durante la ricerca:', error);
                    this.showError('Errore durante la ricerca nel catalogo');
                }
            });
    }

    /**
     * Gestisce il cambio di ricerca
     */
    onSearch(query: string): void {
        this.filters = {
            ...this.filters,
            searchQuery: query || undefined,
            page: 0
        };
        this.performSearch();
    }

    /**
     * Gestisce la pulizia della ricerca
     */
    onClearSearch(): void {
        this.filters = {
            ...this.filters,
            searchQuery: undefined,
            page: 0
        };
        this.performSearch();
    }

    /**
     * Gestisce il cambio dei filtri
     */
    onFiltersChange(newFilters: Partial<CatalogFilters>): void {
        this.filters = {
            ...this.filters,
            ...newFilters,
            page: 0 // Reset pagina quando cambiano i filtri
        };
        this.performSearch();
    }

    /**
     * Gestisce il reset dei filtri
     */
    onResetFilters(): void {
        this.filters = { ...DEFAULT_CATALOG_FILTERS };
        this.performSearch();
    }

    /**
     * Gestisce il cambio di ordinamento
     */
    onSortChange(sortBy: CatalogSortBy): void {
        this.filters = {
            ...this.filters,
            sortBy
        };
        this.performSearch();
    }

    /**
     * Gestisce il cambio di modalità vista
     */
    onViewModeChange(viewMode: CatalogViewMode): void {
        this.viewMode = viewMode;
        this.updateQueryParams();
        this.cdr.markForCheck();
    }

    /**
     * Gestisce il cambio di pagina
     */
    onPageChange(event: PageEvent): void {
        this.filters = {
            ...this.filters,
            page: event.pageIndex,
            size: event.pageSize
        };
        this.performSearch();
        // Scroll to top
        window.scrollTo({ top: 0, behavior: 'smooth' });
    }

    // === AZIONI ITEMS ===

    /**
     * Gestisce l'aggiunta al carrello
     */
    onAddToCart(item: CatalogItem): void {
        // TODO: Implementare logica carrello
        this.snackBar.open(
            `${item.nome} aggiunto al carrello!`,
            'Chiudi',
            {
                duration: 3000,
                horizontalPosition: 'end',
                verticalPosition: 'bottom',
                panelClass: ['success-snackbar']
            }
        );
    }

    /**
     * Gestisce la visualizzazione dettagli
     */
    onViewDetails(item: CatalogItem): void {
        const route = item.tipo === 'PRODOTTO' ? '/prodotti' : '/pacchetti';
        this.router.navigate([route, item.id]);
    }

    /**
     * Gestisce il click sull'azienda
     */
    onClickAzienda(aziendaId: number): void {
        this.filters = {
            ...this.filters,
            aziende: [aziendaId],
            page: 0
        };
        this.performSearch();
        this.filtersOpened = true;
    }

    // === UTILITIES ===

    /**
     * Mostra un messaggio di errore
     */
    private showError(message: string): void {
        this.snackBar.open(message, 'Chiudi', {
            duration: 5000,
            horizontalPosition: 'end',
            verticalPosition: 'bottom',
            panelClass: ['error-snackbar']
        });
    }

    /**
     * Toggle della sidebar filtri (mobile)
     */
    toggleFilters(): void {
        this.filtersOpened = !this.filtersOpened;
    }

    // === GETTERS ===

    get hasResults(): boolean {
        return !!(this.results && this.results.items.length > 0);
    }

    get isEmpty(): boolean {
        return !!(this.results && this.results.items.length === 0);
    }

    get totalResults(): number {
        return this.results?.totalElements || 0;
    }

    get gridClass(): string {
        return this.viewMode === 'grid' ? 'items-grid' : 'items-list';
    }
}

