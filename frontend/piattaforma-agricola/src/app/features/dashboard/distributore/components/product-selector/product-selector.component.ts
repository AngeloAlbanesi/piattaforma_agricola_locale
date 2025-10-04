import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, OnDestroy, Output, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Subject, takeUntil, debounceTime, distinctUntilChanged, startWith } from 'rxjs';

import { ProdottiService } from '../../../../../core/services/prodotti.service';
import { ProdottoSummaryDTO } from '../../../../../core/models/common.models';

export interface SelectedProduct {
    id: number;
    nome: string;
    prezzo: number;
    quantita: number;
    maxQuantita?: number;
}

export interface ProductFilters {
    search?: string;
    categoria?: string;
    produttore?: string;
    prezzoMin?: number;
    prezzoMax?: number;
}

@Component({
    selector: 'app-product-selector',
    standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        MatButtonModule,
        MatIconModule,
        MatChipsModule,
        MatProgressSpinnerModule,
        MatTableModule,
        MatCheckboxModule,
        MatTooltipModule,
        MatPaginatorModule,
        MatSnackBarModule
    ],
    templateUrl: './product-selector.component.html',
    styleUrls: ['./product-selector.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductSelectorComponent implements OnInit, OnDestroy {
    private destroy$ = new Subject<void>();

    @Input() selectedProducts: SelectedProduct[] = [];
    @Output() productsSelected = new EventEmitter<SelectedProduct[]>();
    @Output() productQuantityChanged = new EventEmitter<{productId: number, quantity: number}>();
    @Output() productRemoved = new EventEmitter<number>();

    // Form controls
    searchControl = new FormControl('');
    categoriaControl = new FormControl('');
    produttoreControl = new FormControl('');

    // Data
    availableProducts: ProdottoSummaryDTO[] = [];
    filteredProducts: ProdottoSummaryDTO[] = [];
    categories: string[] = [];
    producers: string[] = [];
    
    // State
    isLoading = false;
    showProductList = false;
    currentPage = 0;
    pageSize = 10;
    totalProducts = 0;

    // Table
    displayedColumns: string[] = ['select', 'nome', 'produttore', 'prezzo', 'disponibilita', 'actions'];

    constructor(
        private prodottiService: ProdottiService,
        private snackBar: MatSnackBar,
        private cdr: ChangeDetectorRef
    ) {}

    ngOnInit(): void {
        this.initializeSearchFilters();
        this.loadAvailableProducts();
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    private initializeSearchFilters(): void {
        // Search con debounce
        this.searchControl.valueChanges
            .pipe(
                startWith(''),
                debounceTime(300),
                distinctUntilChanged(),
                takeUntil(this.destroy$)
            )
            .subscribe(() => {
                this.applyFilters();
            });

        // Filtri categoria e produttore
        this.categoriaControl.valueChanges
            .pipe(takeUntil(this.destroy$))
            .subscribe(() => {
                this.applyFilters();
            });

        this.produttoreControl.valueChanges
            .pipe(takeUntil(this.destroy$))
            .subscribe(() => {
                this.applyFilters();
            });
    }

    private loadAvailableProducts(): void {
        this.isLoading = true;
        
        this.prodottiService.getProducts({
            pagina: this.currentPage,
            elementiPerPagina: 100, // Carichiamo più prodotti per il filtro locale
            stato: 'APPROVATO' // Solo prodotti approvati
        })
        .pipe(takeUntil(this.destroy$))
        .subscribe({
            next: (response) => {
                this.availableProducts = response.content || [];
                this.totalProducts = response.totalElements || 0;
                
                // Estraiamo categorie e produttori univoci
                this.extractUniqueValues();
                this.applyFilters();
                
                this.isLoading = false;
                this.cdr.markForCheck();
            },
            error: (error) => {
                console.error('Errore nel caricamento prodotti:', error);
                this.snackBar.open('Errore nel caricamento dei prodotti', 'Chiudi', {
                    duration: 3000,
                    panelClass: 'error-snackbar'
                });
                this.isLoading = false;
                this.cdr.markForCheck();
            }
        });
    }

    private extractUniqueValues(): void {
        // Estraiamo categorie univoche
        this.categories = [...new Set(
            this.availableProducts
                .map(p => p.categoria)
                .filter((c): c is string => c != null && c.trim() !== '')
        )].sort();

        // Estraiamo produttori univoci
        this.producers = [...new Set(
            this.availableProducts
                .map(p => p.produttore?.nomeAzienda)
                .filter((p): p is string => p != null && p.trim() !== '')
        )].sort();
    }

    private applyFilters(): void {
        let filtered = [...this.availableProducts];

        // Filtro ricerca
        const searchTerm = this.searchControl.value?.toLowerCase().trim();
        if (searchTerm) {
            filtered = filtered.filter(product => 
                product.nome.toLowerCase().includes(searchTerm) ||
                product.descrizione?.toLowerCase().includes(searchTerm) ||
                product.produttore?.nomeAzienda.toLowerCase().includes(searchTerm)
            );
        }

        // Filtro categoria
        const categoria = this.categoriaControl.value;
        if (categoria) {
            filtered = filtered.filter(product => product.categoria === categoria);
        }

        // Filtro produttore
        const produttore = this.produttoreControl.value;
        if (produttore) {
            filtered = filtered.filter(product => product.produttore?.nomeAzienda === produttore);
        }

        // Escludiamo prodotti già selezionati
        const selectedIds = this.selectedProducts.map(p => p.id);
        filtered = filtered.filter(product => !selectedIds.includes(product.id));

        this.filteredProducts = filtered;
        this.cdr.markForCheck();
    }

    toggleProductList(): void {
        this.showProductList = !this.showProductList;
        if (this.showProductList && this.availableProducts.length === 0) {
            this.loadAvailableProducts();
        }
    }

    isProductSelected(productId: number): boolean {
        return this.selectedProducts.some(p => p.id === productId);
    }

    onProductToggle(product: ProdottoSummaryDTO, selected: boolean): void {
        if (selected) {
            this.addProduct(product);
        } else {
            this.removeProduct(product.id);
        }
    }

    addProduct(product: ProdottoSummaryDTO): void {
        const selectedProduct: SelectedProduct = {
            id: product.id,
            nome: product.nome,
            prezzo: product.prezzo,
            quantita: 1,
            maxQuantita: product.quantitaDisponibile
        };

        const updatedProducts = [...this.selectedProducts, selectedProduct];
        this.selectedProducts = updatedProducts;
        this.productsSelected.emit(updatedProducts);
        
        // Riapplica i filtri per nascondere il prodotto appena aggiunto
        this.applyFilters();
    }

    removeProduct(productId: number): void {
        const updatedProducts = this.selectedProducts.filter(p => p.id !== productId);
        this.selectedProducts = updatedProducts;
        this.productsSelected.emit(updatedProducts);
        this.productRemoved.emit(productId);
        
        // Riapplica i filtri per mostrare di nuovo il prodotto
        this.applyFilters();
    }

    updateProductQuantity(productId: number, quantity: number): void {
        const product = this.selectedProducts.find(p => p.id === productId);
        if (product && quantity > 0) {
            // Verifica limite massimo se disponibile
            if (product.maxQuantita && quantity > product.maxQuantita) {
                this.snackBar.open(
                    `Quantità massima disponibile: ${product.maxQuantita}`, 
                    'Chiudi', 
                    { duration: 3000, panelClass: 'warning-snackbar' }
                );
                return;
            }

            product.quantita = quantity;
            this.productQuantityChanged.emit({ productId, quantity });
            this.productsSelected.emit([...this.selectedProducts]);
            this.cdr.markForCheck();
        }
    }

    clearFilters(): void {
        this.searchControl.setValue('');
        this.categoriaControl.setValue('');
        this.produttoreControl.setValue('');
    }

    formatCurrency(value: number): string {
        return new Intl.NumberFormat('it-IT', {
            style: 'currency',
            currency: 'EUR'
        }).format(value);
    }

    // Getters per template
    get hasFilters(): boolean {
        return !!(
            this.searchControl.value ||
            this.categoriaControl.value ||
            this.produttoreControl.value
        );
    }

    get filteredProductsCount(): number {
        return this.filteredProducts.length;
    }

    get selectedProductsCount(): number {
        return this.selectedProducts.length;
    }

    get canShowMore(): boolean {
        return this.filteredProducts.length > this.pageSize;
    }

    get displayedProducts(): ProdottoSummaryDTO[] {
        const start = this.currentPage * this.pageSize;
        const end = start + this.pageSize;
        return this.filteredProducts.slice(start, end);
    }

    // Paginazione
    onPageChange(page: number): void {
        this.currentPage = page;
        this.cdr.markForCheck();
    }

    get totalPages(): number {
        return Math.ceil(this.filteredProducts.length / this.pageSize);
    }

    get canGoToPreviousPage(): boolean {
        return this.currentPage > 0;
    }

    get canGoToNextPage(): boolean {
        return this.currentPage < this.totalPages - 1;
    }

    previousPage(): void {
        if (this.canGoToPreviousPage) {
            this.currentPage--;
            this.cdr.markForCheck();
        }
    }

    nextPage(): void {
        if (this.canGoToNextPage) {
            this.currentPage++;
            this.cdr.markForCheck();
        }
    }

    trackByProductId(index: number, product: SelectedProduct): number {
        return product.id;
    }
}