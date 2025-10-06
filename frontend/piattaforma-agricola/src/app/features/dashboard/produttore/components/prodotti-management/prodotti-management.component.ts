import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ProduttoreProductSummaryDTO, ProduttoreProductFilters, StatoVerifica, TipoOrigineProdotto, CreateProductRequestDTO, UpdateProductRequestDTO, ProductQuantityUpdateDTO } from '../../../../../core/models/produttore.models';
import { PaginatedResponse } from '../../../../../core/models/common.models';
import { ProduttoreService } from '../../../../../core/services/produttore.service';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { Router } from '@angular/router';
import { CreateProductDialogComponent } from './create-product-dialog.component';
import { EditProductDialogComponent } from './edit-product-dialog.component';
import { DeleteProductDialogComponent } from './delete-product-dialog.component';
import { UpdateQuantityDialogComponent } from './update-quantity-dialog.component';

@Component({
    selector: 'app-prodotti-management',
    standalone: true,
    imports: [
        CommonModule,
        FormsModule,
        MatCardModule,
        MatIconModule,
        MatButtonModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        MatOptionModule,
        MatPaginatorModule,
        MatSortModule,
        MatTableModule,
        MatSnackBarModule,
        MatProgressSpinnerModule,
        MatChipsModule,
        MatToolbarModule,
        MatTooltipModule,
        CurrencyPipe
    ],
    templateUrl: './prodotti-management.component.html',
    styleUrl: './prodotti-management.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProdottiManagementComponent implements OnInit {
    displayedColumns: string[] = ['id', 'nome', 'prezzo', 'quantitaDisponibile', 'stato', 'origine', 'actions'];
    dataSource = new MatTableDataSource<ProduttoreProductSummaryDTO>([]);
    isLoading = true;
    totalElements = 0;
    pageSize = 10;
    currentPage = 0;

    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild(MatSort) sort!: MatSort;

    filters: ProduttoreProductFilters & { pagina: number, elementiPerPagina: number } = {
        pagina: 0,
        elementiPerPagina: 10,
        statoVerifica: 'TUTTI',
        tipoOrigine: 'TUTTI'
    } as ProduttoreProductFilters & { pagina: number, elementiPerPagina: number };

    statiProdotto = [
        { value: 'TUTTI', viewValue: 'Tutti' },
        { value: StatoVerifica.APPROVATO, viewValue: 'Approvato' },
        { value: StatoVerifica.IN_ATTESA, viewValue: 'In Attesa di Verifica' },
        { value: StatoVerifica.RESPINTO, viewValue: 'Respinto' }
    ];

    tipiOrigine = [
        { value: 'TUTTI', viewValue: 'Tutti' },
        { value: TipoOrigineProdotto.COLTIVATO, viewValue: 'Coltivato' },
        { value: TipoOrigineProdotto.TRASFORMATO, viewValue: 'Trasformato' },
        { value: TipoOrigineProdotto.ARTIGIANALE, viewValue: 'Artigianale' }
    ];

    private searchTerms = new Subject<string>();

    constructor(
        private produttoreService: ProduttoreService,
        private dialog: MatDialog,
        private snackBar: MatSnackBar,
        private router: Router,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit(): void {
        this.loadProdotti();
        this.searchTerms.pipe(
            debounceTime(300),
            distinctUntilChanged(),
            switchMap((term: string) => {
                this.filters.search = term;
                this.filters.pagina = 0;
                return this.produttoreService.getMyProducts(this.filters);
            })
        ).subscribe((data: PaginatedResponse<ProduttoreProductSummaryDTO>) => {
            this.dataSource.data = data.content;
            this.totalElements = data.totalElements;
            this.isLoading = false;
        }, (error: any) => {
            this.snackBar.open('Errore durante il caricamento dei prodotti.', 'Chiudi', { duration: 3000 });
            this.isLoading = false;
        });
    }

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;

        if (this.paginator) {
            this.paginator.page.subscribe(() => {
                this.filters.pagina = this.paginator.pageIndex;
                this.filters.elementiPerPagina = this.paginator.pageSize;
                this.loadProdotti();
            });
        }

        if (this.sort) {
            this.sort.sortChange.subscribe(() => {
                this.filters.pagina = 0;
                // Implementare logica di ordinamento se l'API lo supporta
                this.loadProdotti();
            });
        }
    }

    loadProdotti(): void {
        this.isLoading = true;
        this.cdr.markForCheck();

        this.produttoreService.getMyProducts(this.filters).subscribe((data: PaginatedResponse<ProduttoreProductSummaryDTO>) => {
            this.dataSource.data = data.content;
            this.totalElements = data.totalElements;
            this.isLoading = false;
            this.cdr.markForCheck();
        }, (error: any) => {
            this.snackBar.open('Errore durante il caricamento dei prodotti.', 'Chiudi', { duration: 3000 });
            this.isLoading = false;
            this.cdr.markForCheck();
        });
    }

    applyFilter(event: Event): void {
        const filterValue = (event.target as HTMLInputElement).value;
        this.searchTerms.next(filterValue.trim().toLowerCase());
    }

    onStatusChange(status: StatoVerifica | 'TUTTI'): void {
        this.filters.statoVerifica = status;
        this.filters.pagina = 0;
        this.loadProdotti();
    }

    onOriginChange(origin: TipoOrigineProdotto | 'TUTTI'): void {
        this.filters.tipoOrigine = origin;
        this.filters.pagina = 0;
        this.loadProdotti();
    }

    viewProductDetails(prodotto: ProduttoreProductSummaryDTO): void {
        this.router.navigate(['/dashboard/produttore/prodotti', prodotto.idProdotto]);
    }

    editProduct(prodotto: ProduttoreProductSummaryDTO): void {
        const dialogRef = this.dialog.open(EditProductDialogComponent, {
            width: '600px',
            data: prodotto
        });

        dialogRef.afterClosed().subscribe((result: UpdateProductRequestDTO) => {
            if (result) {
                this.isLoading = true;
                this.cdr.markForCheck();

                this.produttoreService.updateProduct(prodotto.idProdotto, result).subscribe({
                    next: () => {
                        this.snackBar.open('Prodotto aggiornato con successo', 'Chiudi', { duration: 3000 });
                        this.loadProdotti();
                    },
                    error: (error) => {
                        console.error('Errore durante l\'aggiornamento del prodotto:', error);
                        this.snackBar.open('Errore durante l\'aggiornamento del prodotto', 'Chiudi', { duration: 3000 });
                        this.isLoading = false;
                        this.cdr.markForCheck();
                    }
                });
            }
        });
    }

    deleteProduct(prodotto: ProduttoreProductSummaryDTO): void {
        const dialogRef = this.dialog.open(DeleteProductDialogComponent, {
            width: '400px',
            data: prodotto
        });

        dialogRef.afterClosed().subscribe((confirmed: boolean) => {
            if (confirmed) {
                this.isLoading = true;
                this.cdr.markForCheck();

                this.produttoreService.deleteProduct(prodotto.idProdotto).subscribe({
                    next: () => {
                        this.snackBar.open('Prodotto eliminato con successo', 'Chiudi', { duration: 3000 });
                        this.loadProdotti();
                    },
                    error: (error) => {
                        console.error('Errore durante l\'eliminazione del prodotto:', error);
                        this.snackBar.open('Errore durante l\'eliminazione del prodotto', 'Chiudi', { duration: 3000 });
                        this.isLoading = false;
                        this.cdr.markForCheck();
                    }
                });
            }
        });
    }

    createNewProduct(): void {
        const dialogRef = this.dialog.open(CreateProductDialogComponent, {
            width: '600px'
        });

        dialogRef.afterClosed().subscribe((result: CreateProductRequestDTO) => {
            if (result) {
                this.isLoading = true;
                this.cdr.markForCheck();

                this.produttoreService.createProduct(result).subscribe({
                    next: () => {
                        this.snackBar.open('Prodotto creato con successo', 'Chiudi', { duration: 3000 });
                        this.loadProdotti();
                    },
                    error: (error) => {
                        console.error('Errore durante la creazione del prodotto:', error);
                        this.snackBar.open('Errore durante la creazione del prodotto', 'Chiudi', { duration: 3000 });
                        this.isLoading = false;
                        this.cdr.markForCheck();
                    }
                });
            }
        });
    }

    updateQuantity(prodotto: ProduttoreProductSummaryDTO): void {
        const dialogRef = this.dialog.open(UpdateQuantityDialogComponent, {
            width: '500px',
            data: prodotto
        });

        dialogRef.afterClosed().subscribe((result: ProductQuantityUpdateDTO) => {
            if (result) {
                this.isLoading = true;
                this.cdr.markForCheck();

                this.produttoreService.updateProductQuantity(prodotto.idProdotto, result).subscribe({
                    next: () => {
                        this.snackBar.open('Quantità aggiornata con successo', 'Chiudi', { duration: 3000 });
                        this.loadProdotti();
                    },
                    error: (error) => {
                        console.error('Errore durante l\'aggiornamento della quantità:', error);
                        this.snackBar.open('Errore durante l\'aggiornamento della quantità', 'Chiudi', { duration: 3000 });
                        this.isLoading = false;
                        this.cdr.markForCheck();
                    }
                });
            }
        });
    }

    getStatoClass(stato: string): string {
        switch (stato) {
            case StatoVerifica.APPROVATO:
                return 'status-approved';
            case StatoVerifica.IN_ATTESA:
                return 'status-pending';
            case StatoVerifica.RESPINTO:
                return 'status-rejected';
            default:
                return '';
        }
    }
}