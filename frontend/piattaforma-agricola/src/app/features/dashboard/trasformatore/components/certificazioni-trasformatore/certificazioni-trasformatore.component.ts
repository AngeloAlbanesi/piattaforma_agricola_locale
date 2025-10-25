import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { MatChipsModule } from '@angular/material/chips';
import { ProdottoDTO, CertificazioneProdottoDTO, AddCertificazioneRequestDTO } from '../../../../../core/models/trasformatore.models';
import { ProdottiService } from '../../../../../core/services/prodotti.service';
import { AddProductCertificationDialogComponent } from './add-product-certification-dialog.component';
import { RemoveCertificationConfirmationDialogComponent } from './remove-certification-confirmation-dialog.component';

@Component({
    selector: 'app-certificazioni-trasformatore',
    standalone: true,
    imports: [
        CommonModule,
        FormsModule,
        MatCardModule,
        MatButtonModule,
        MatIconModule,
        MatTableModule,
        MatPaginatorModule,
        MatSortModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        MatOptionModule,
        MatTooltipModule,
        MatProgressSpinnerModule,
        MatSnackBarModule,
        MatChipsModule,
        DatePipe
    ],
    templateUrl: './certificazioni-trasformatore.component.html',
    styleUrl: './certificazioni-trasformatore.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CertificazioniTrasformatoreComponent implements OnInit {
    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild(MatSort) sort!: MatSort;

    // Products and certifications data
    products: ProdottoDTO[] = [];
    selectedProductId: number | null = null;
    selectedProduct: ProdottoDTO | null = null;

    // Table configuration
    displayedColumns: string[] = ['id', 'nomeCertificazione', 'enteRilascio', 'dataRilascio', 'dataScadenza', 'azioni'];
    dataSource = new MatTableDataSource<CertificazioneProdottoDTO>([]);

    // Loading states
    isLoading: boolean = false;
    isLoadingProducts: boolean = true;

    // Filters
    filters = {
        search: ''
    };

    constructor(
        private prodottiService: ProdottiService,
        private dialog: MatDialog,
        private snackBar: MatSnackBar,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit(): void {
        this.loadProducts();
    }

    /**
     * Load all products for the current user
     */
    loadProducts(): void {
        this.isLoadingProducts = true;
        this.cdr.markForCheck();

        this.prodottiService.getMyProducts().subscribe({
            next: (response) => {
                this.products = response.content || [];
                this.isLoadingProducts = false;
                this.cdr.markForCheck();
            },
            error: (error) => {
                console.error('Errore nel caricamento dei prodotti:', error);
                this.snackBar.open('Errore nel caricamento dei prodotti', 'Chiudi', { duration: 3000 });
                this.isLoadingProducts = false;
                this.cdr.markForCheck();
            }
        });
    }

    /**
     * Handle product selection
     */
    onProductSelected(productId: number): void {
        this.selectedProductId = productId;
        this.selectedProduct = this.products.find(p => p.idProdotto === productId) || null;

        if (this.selectedProduct) {
            this.loadProductDetails(productId);
        }
    }

    /**
     * Load product details including certifications
     */
    loadProductDetails(productId: number): void {
        this.isLoading = true;
        this.cdr.markForCheck();

        this.prodottiService.getProductById(productId).subscribe({
            next: (product) => {
                // Update dataSource with certifications
                this.dataSource.data = product.certificazioni || [];
                this.dataSource.paginator = this.paginator;
                this.dataSource.sort = this.sort;
                this.isLoading = false;
                this.cdr.markForCheck();
            },
            error: (error) => {
                console.error('Errore nel caricamento del prodotto:', error);
                this.snackBar.open('Errore nel caricamento delle certificazioni', 'Chiudi', { duration: 3000 });
                this.isLoading = false;
                this.cdr.markForCheck();
            }
        });
    }

    /**
     * Open dialog to add a new certification
     */
    aggiungiCertificazione(): void {
        if (!this.selectedProductId || !this.selectedProduct) {
            this.snackBar.open('Seleziona prima un prodotto', 'Chiudi', { duration: 3000 });
            return;
        }

        const dialogRef = this.dialog.open(AddProductCertificationDialogComponent, {
            width: '600px',
            data: {
                productId: this.selectedProductId,
                productName: this.selectedProduct.nome
            }
        });

        dialogRef.afterClosed().subscribe((result: AddCertificazioneRequestDTO) => {
            if (result && this.selectedProductId) {
                this.isLoading = true;
                this.cdr.markForCheck();

                this.prodottiService.addCertification(this.selectedProductId, result).subscribe({
                    next: () => {
                        this.snackBar.open('Certificazione aggiunta con successo', 'Chiudi', { duration: 3000 });
                        // Reload product details to update certifications
                        this.loadProductDetails(this.selectedProductId!);
                    },
                    error: (error) => {
                        console.error('Errore durante l\'aggiunta della certificazione:', error);
                        this.snackBar.open('Errore durante l\'aggiunta della certificazione', 'Chiudi', { duration: 3000 });
                        this.isLoading = false;
                        this.cdr.markForCheck();
                    }
                });
            }
        });
    }

    /**
     * Open dialog to confirm and delete a certification
     */
    eliminaCertificazione(certificazione: CertificazioneProdottoDTO): void {
        const dialogRef = this.dialog.open(RemoveCertificationConfirmationDialogComponent, {
            width: '500px',
            data: certificazione
        });

        dialogRef.afterClosed().subscribe((confirmed: boolean) => {
            if (confirmed && certificazione.prodottoId) {
                this.isLoading = true;
                this.cdr.markForCheck();

                this.prodottiService.removeCertification(certificazione.prodottoId, certificazione.id).subscribe({
                    next: () => {
                        this.snackBar.open('Certificazione rimossa con successo', 'Chiudi', { duration: 3000 });
                        // Reload product details to update certifications
                        this.loadProductDetails(certificazione.prodottoId);
                    },
                    error: (error) => {
                        console.error('Errore durante la rimozione della certificazione:', error);
                        this.snackBar.open('Errore durante la rimozione della certificazione', 'Chiudi', { duration: 3000 });
                        this.isLoading = false;
                        this.cdr.markForCheck();
                    }
                });
            }
        });
    }

    /**
     * Apply filter to the table
     */
    applyFilter(): void {
        const filterValue = this.filters.search?.toLowerCase() || '';
        this.dataSource.filterPredicate = (data: CertificazioneProdottoDTO, filter: string) => {
            const searchMatch =
                data.nomeCertificazione.toLowerCase().includes(filter) ||
                data.enteRilascio.toLowerCase().includes(filter);
            return searchMatch;
        };
        this.dataSource.filter = filterValue;
    }
}