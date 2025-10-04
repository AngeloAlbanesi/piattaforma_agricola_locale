import { ChangeDetectionStrategy, Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatChipsModule } from '@angular/material/chips';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Subject, takeUntil, debounceTime, distinctUntilChanged, startWith, combineLatest } from 'rxjs';

import { ProdottiService } from '../../../../../core/services/prodotti.service';
import { DistributoreService } from '../../../../../core/services/distributore.service';
import { ProdottoSummaryDTO, PaginatedResponse } from '../../../../../core/models/common.models';
import { PacchettoTipicitaDTO } from '../../../../../core/models/distributore.models';
import { ProductDetailDialogComponent, ProductDetailDialogData } from '../product-detail-dialog/product-detail-dialog.component';
import { AddToPackageDialogComponent, AddToPackageDialogData } from '../add-to-package-dialog/add-to-package-dialog.component';

export interface ProductFilters {
    search?: string;
    categoria?: string;
    produttore?: string;
    prezzoMin?: number;
    prezzoMax?: number;
    luogoOrigine?: string;
    conCertificazioni?: boolean;
}

@Component({
    selector: 'app-available-products',
    standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        MatCardModule,
        MatButtonModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        MatChipsModule,
        MatTableModule,
        MatSortModule,
        MatPaginatorModule,
        MatProgressSpinnerModule,
        MatTooltipModule,
        MatDialogModule,
        MatSnackBarModule
    ],
    templateUrl: './available-products.component.html',
    styleUrls: ['./available-products.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class AvailableProductsComponent implements OnInit, OnDestroy {
    private destroy$ = new Subject<void>();

    // Data
    products: ProdottoSummaryDTO[] = [];
    totalProducts = 0;
    isLoading = false;
    errorMessage: string | null = null;

    // Pagination
    currentPage = 0;
    pageSize = 12;
    pageSizeOptions = [6, 12, 24, 48];

    // Filters
    searchControl = new FormControl('');
    categoriaControl = new FormControl('');
    produttoreControl = new FormControl('');
    luogoOrigineControl = new FormControl('');
    prezzoMinControl = new FormControl<number | null>(null);
    prezzoMaxControl = new FormControl<number | null>(null);
    conCertificazioniControl = new FormControl(false);

    // Filter options
    categories: string[] = [];
    producers: string[] = [];
    locations: string[] = [];

    // View mode
    viewMode: 'grid' | 'list' = 'grid';

    // Table columns for list view
    displayedColumns: string[] = ['immagine', 'nome', 'produttore', 'categoria', 'prezzo', 'disponibilita', 'luogo', 'actions'];

    constructor(
        private prodottiService: ProdottiService,
        private distributoreService: DistributoreService,
        private dialog: MatDialog,
        private snackBar: MatSnackBar,
        private cdr: ChangeDetectorRef
    ) {}

    ngOnInit(): void {
        this.initializeFilters();
        this.loadProducts();
        this.loadFilterOptions();
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    private initializeFilters(): void {
        // Combina tutti i controlli filtro con debounce per ottimizzare le chiamate API
        combineLatest([
            this.searchControl.valueChanges.pipe(startWith(''), debounceTime(300), distinctUntilChanged()),
            this.categoriaControl.valueChanges.pipe(startWith('')),
            this.produttoreControl.valueChanges.pipe(startWith('')),
            this.luogoOrigineControl.valueChanges.pipe(startWith('')),
            this.prezzoMinControl.valueChanges.pipe(startWith(null)),
            this.prezzoMaxControl.valueChanges.pipe(startWith(null)),
            this.conCertificazioniControl.valueChanges.pipe(startWith(false))
        ])
        .pipe(takeUntil(this.destroy$))
        .subscribe(() => {
            this.currentPage = 0; // Reset alla prima pagina quando cambiano i filtri
            this.loadProducts();
        });
    }

    private loadProducts(): void {
        this.isLoading = true;
        this.errorMessage = null;

        const filters = this.buildFilters();

        this.prodottiService.getProducts(filters)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (response: PaginatedResponse<ProdottoSummaryDTO>) => {
                    this.products = response.content || [];
                    this.totalProducts = response.totalElements || 0;
                    this.isLoading = false;
                    this.cdr.markForCheck();
                },
                error: (error) => {
                    console.error('Errore nel caricamento prodotti:', error);
                    this.errorMessage = 'Impossibile caricare i prodotti. Riprova più tardi.';
                    this.isLoading = false;
                    this.cdr.markForCheck();
                }
            });
    }

    private loadFilterOptions(): void {
        // Carica tutte le opzioni di filtro disponibili
        this.prodottiService.getProducts({ elementiPerPagina: 1000, stato: 'APPROVATO' })
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (response) => {
                    const products = response.content || [];
                    
                    // Estrai categorie uniche
                    this.categories = [...new Set(
                        products
                            .map(p => p.categoria)
                            .filter((c): c is string => c != null && c.trim() !== '')
                    )].sort();

                    // Estrai produttori unici
                    this.producers = [...new Set(
                        products
                            .map(p => p.produttore?.nomeAzienda)
                            .filter((p): p is string => p != null && p.trim() !== '')
                    )].sort();

                    // Estrai luoghi unici
                    this.locations = [...new Set(
                        products
                            .map(p => p.luogoOrigine)
                            .filter((l): l is string => l != null && l.trim() !== '')
                    )].sort();

                    this.cdr.markForCheck();
                },
                error: (error) => {
                    console.error('Errore nel caricamento opzioni filtro:', error);
                }
            });
    }

    private buildFilters(): any {
        const filters: any = {
            pagina: this.currentPage,
            elementiPerPagina: this.pageSize,
            stato: 'APPROVATO' // Solo prodotti approvati
        };

        const search = this.searchControl.value?.trim();
        if (search) {
            filters.search = search;
        }

        const categoria = this.categoriaControl.value;
        if (categoria) {
            filters.categoria = categoria;
        }

        const produttore = this.produttoreControl.value;
        if (produttore) {
            filters.produttore = produttore;
        }

        return filters;
    }

    // Event handlers
    onPageChange(event: PageEvent): void {
        this.currentPage = event.pageIndex;
        this.pageSize = event.pageSize;
        this.loadProducts();
    }

    toggleViewMode(): void {
        this.viewMode = this.viewMode === 'grid' ? 'list' : 'grid';
        this.cdr.markForCheck();
    }

    clearFilters(): void {
        this.searchControl.setValue('');
        this.categoriaControl.setValue('');
        this.produttoreControl.setValue('');
        this.luogoOrigineControl.setValue('');
        this.prezzoMinControl.setValue(null);
        this.prezzoMaxControl.setValue(null);
        this.conCertificazioniControl.setValue(false);
    }

    refreshProducts(): void {
        this.loadProducts();
    }

    viewProductDetails(product: ProdottoSummaryDTO): void {
        const dialogData: ProductDetailDialogData = {
            productId: product.id
        };

        const dialogRef = this.dialog.open(ProductDetailDialogComponent, {
            width: '800px',
            maxWidth: '95vw',
            maxHeight: '90vh',
            data: dialogData
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result?.action === 'addToPackage') {
                this.addToPackage(product);
            }
        });
    }

    addToPackage(product: ProdottoSummaryDTO): void {
        const dialogData: AddToPackageDialogData = {
            product: product
        };

        const dialogRef = this.dialog.open(AddToPackageDialogComponent, {
            width: '600px',
            maxWidth: '95vw',
            data: dialogData
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.snackBar.open('Prodotto aggiunto al pacchetto con successo!', 'Chiudi', {
                    duration: 3000,
                    panelClass: 'success-snackbar'
                });
            }
        });
    }

    // Utility methods
    formatCurrency(value: number): string {
        return this.distributoreService.formatCurrency(value);
    }

    getAvailabilityStatus(quantity: number): { label: string; color: string; icon: string } {
        if (quantity === 0) {
            return { label: 'Esaurito', color: '#f44336', icon: 'remove_circle' };
        } else if (quantity < 10) {
            return { label: 'Scorte limitate', color: '#ff9800', icon: 'warning' };
        } else {
            return { label: 'Disponibile', color: '#4caf50', icon: 'check_circle' };
        }
    }

    getCertificationsBadgeText(certificazioni: string[] | undefined): string {
        if (!certificazioni || certificazioni.length === 0) {
            return '';
        }
        if (certificazioni.length === 1) {
            return certificazioni[0];
        }
        return `${certificazioni[0]} +${certificazioni.length - 1}`;
    }

    // Getters for template
    get hasFilters(): boolean {
        return !!(
            this.searchControl.value ||
            this.categoriaControl.value ||
            this.produttoreControl.value ||
            this.luogoOrigineControl.value ||
            this.prezzoMinControl.value ||
            this.prezzoMaxControl.value ||
            this.conCertificazioniControl.value
        );
    }

    get filteredProductsText(): string {
        if (this.totalProducts === 0) {
            return 'Nessun prodotto trovato';
        }
        
        const start = (this.currentPage * this.pageSize) + 1;
        const end = Math.min((this.currentPage + 1) * this.pageSize, this.totalProducts);
        
        return `${start}-${end} di ${this.totalProducts} prodotti`;
    }

    get isGridView(): boolean {
        return this.viewMode === 'grid';
    }

    get isListView(): boolean {
        return this.viewMode === 'list';
    }

    trackByProductId(index: number, product: ProdottoSummaryDTO): number {
        return product.id;
    }
}