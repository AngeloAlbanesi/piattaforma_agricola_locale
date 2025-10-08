import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
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
import { DistributoreService } from '../../../../../core/services/distributore.service';
import { DistributoreProductDTO, CertificationDTO, CreateCertificazioneRequestDTO } from '../../../../../core/models/distributore.models';
import { AddCertificationDialogComponent } from './add-certification-dialog.component';
import { RemoveCertificationDialogComponent } from './remove-certification-dialog.component';

@Component({
    selector: 'app-certificazioni-distributore',
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
        DatePipe
    ],
    templateUrl: './certificazioni-distributore.component.html',
    styleUrl: './certificazioni-distributore.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CertificazioniDistributoreComponent implements OnInit {
    displayedColumns: string[] = ['idCertificazione', 'nomeCertificazione', 'enteRilascio', 'dataRilascio', 'dataScadenza', 'azioni'];
    dataSource = new MatTableDataSource<CertificationDTO>([]);

    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild(MatSort) sort!: MatSort;

    // Product selection
    products: DistributoreProductDTO[] = [];
    selectedProductId: number | null = null;
    selectedProduct: DistributoreProductDTO | null = null;
    isLoading = false;
    isLoadingProducts = true;

    // Store all certifications
    allCertifications: CertificationDTO[] = [];

    filters = {
        search: ''
    };

    constructor(
        private distributoreService: DistributoreService,
        private dialog: MatDialog,
        private snackBar: MatSnackBar,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit(): void {
        this.loadProducts();
    }

    ngAfterViewInit(): void {
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
    }

    loadProducts(): void {
        this.isLoadingProducts = true;
        this.cdr.markForCheck();

        this.distributoreService.getMyProducts().subscribe({
            next: (products) => {
                this.products = products;
                this.isLoadingProducts = false;
                this.cdr.markForCheck();
            },
            error: (error) => {
                console.error('Errore durante il caricamento dei prodotti:', error);
                this.snackBar.open('Errore durante il caricamento dei prodotti', 'Chiudi', { duration: 3000 });
                this.isLoadingProducts = false;
                this.cdr.markForCheck();
            }
        });
    }

    onProductSelected(productId: number): void {
        this.selectedProductId = productId;
        this.selectedProduct = this.products.find(p => p.id === productId) || null;

        if (this.selectedProductId) {
            this.loadProductDetails(this.selectedProductId);
        }
        this.cdr.markForCheck();
    }

    loadProductDetails(productId: number): void {
        this.isLoading = true;
        this.cdr.markForCheck();

        this.distributoreService.getProductById(productId).subscribe({
            next: (product) => {
                this.dataSource.data = product.certificazioni || [];
                this.isLoading = false;
                this.cdr.markForCheck();
            },
            error: (error) => {
                console.error('Errore durante il caricamento delle certificazioni:', error);
                this.snackBar.open('Errore durante il caricamento delle certificazioni', 'Chiudi', { duration: 3000 });
                this.isLoading = false;
                this.cdr.markForCheck();
            }
        });
    }

    aggiungiCertificazione(): void {
        if (!this.selectedProductId || !this.selectedProduct) {
            this.snackBar.open('Seleziona prima un prodotto', 'Chiudi', { duration: 3000 });
            return;
        }

        const dialogRef = this.dialog.open(AddCertificationDialogComponent, {
            width: '600px',
            data: {
                productId: this.selectedProductId,
                productName: this.selectedProduct.nome
            }
        });

        dialogRef.afterClosed().subscribe((result: CreateCertificazioneRequestDTO) => {
            if (result && this.selectedProductId) {
                this.isLoading = true;
                this.cdr.markForCheck();

                this.distributoreService.addCertificationToProduct(this.selectedProductId, result).subscribe({
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

    eliminaCertificazione(certificazione: CertificationDTO): void {
        if (!this.selectedProductId) {
            this.snackBar.open('Impossibile eliminare questa certificazione', 'Chiudi', { duration: 3000 });
            return;
        }

        const dialogRef = this.dialog.open(RemoveCertificationDialogComponent, {
            width: '400px',
            data: certificazione
        });

        dialogRef.afterClosed().subscribe((confirmed: boolean) => {
            if (confirmed && this.selectedProductId) {
                this.isLoading = true;
                this.cdr.markForCheck();

                this.distributoreService.removeCertificationFromProduct(
                    this.selectedProductId,
                    certificazione.idCertificazione
                ).subscribe({
                    next: () => {
                        this.snackBar.open('Certificazione rimossa con successo', 'Chiudi', { duration: 3000 });
                        // Reload product details to update certifications
                        this.loadProductDetails(this.selectedProductId!);
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

    applyFilter(): void {
        const filterValue = this.filters.search?.toLowerCase() || '';
        this.dataSource.filterPredicate = (data: CertificationDTO, filter: string) => {
            const searchMatch = data.nomeCertificazione.toLowerCase().includes(filter) ||
                data.enteRilascio.toLowerCase().includes(filter);
            return searchMatch;
        };
        this.dataSource.filter = filterValue;
    }
}
