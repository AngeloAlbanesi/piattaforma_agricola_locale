import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { DistributoreService } from '@core/services/distributore.service';
import { DistributoreProductDTO } from '@core/models/distributore.models';
import { DistributoreProductFormDialogComponent } from '../distributore-product-form-dialog/distributore-product-form-dialog.component';
import { DeleteConfirmationDialogComponent, DeleteConfirmationDialogData } from '../delete-confirmation-dialog/delete-confirmation-dialog.component';
import { ProductDetailDialogComponent } from '../product-detail-dialog/product-detail-dialog.component';

@Component({
    selector: 'app-distributore-prodotti-management',
    standalone: true,
    imports: [
        CommonModule,
        MatButtonModule,
        MatIconModule,
        MatTableModule,
        MatCardModule,
        MatDialogModule,
        MatProgressSpinnerModule,
        MatSnackBarModule,
        MatTooltipModule,
        ProductDetailDialogComponent
    ],
    templateUrl: './distributore-prodotti-management.component.html',
    styleUrls: ['./distributore-prodotti-management.component.scss']
})
export class DistributoreProdottiManagementComponent implements OnInit {
    products: DistributoreProductDTO[] = [];
    isLoading = false;

    displayedColumns: string[] = ['nome', 'prezzo', 'quantita', 'unitaMisura', 'stato', 'azioni'];

    constructor(
        private distributoreService: DistributoreService,
        private dialog: MatDialog,
        private snackBar: MatSnackBar
    ) { }

    ngOnInit(): void {
        this.loadProducts();
    }

    loadProducts(): void {
        this.isLoading = true;
        this.distributoreService.getMyProducts().subscribe({
            next: (products) => {
                // Ensure products is always an array
                this.products = Array.isArray(products) ? products : [];
                this.isLoading = false;
            },
            error: (error) => {
                console.error('Errore nel caricamento prodotti:', error);
                this.snackBar.open('Errore nel caricamento dei prodotti', 'Chiudi', { duration: 3000 });
                this.products = [];
                this.isLoading = false;
            }
        });
    }

    openCreateProductDialog(): void {
        const dialogRef = this.dialog.open(DistributoreProductFormDialogComponent, {
            width: '600px',
            data: {
                mode: 'create'
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.loadProducts();
            }
        });
    }

    openEditProductDialog(product: DistributoreProductDTO): void {
        const dialogRef = this.dialog.open(DistributoreProductFormDialogComponent, {
            width: '600px',
            data: {
                mode: 'edit',
                product: product
            }
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.loadProducts();
            }
        });
    }

    deleteProduct(product: DistributoreProductDTO): void {
        const dialogData: DeleteConfirmationDialogData = {
            product: product,
            type: 'product'
        };

        const dialogRef = this.dialog.open(DeleteConfirmationDialogComponent, {
            width: '500px',
            maxWidth: '90vw',
            data: dialogData
        });

        dialogRef.afterClosed().subscribe(confirmed => {
            if (confirmed) {
                this.distributoreService.deleteProduct(product.id).subscribe({
                    next: () => {
                        this.snackBar.open('Prodotto eliminato con successo', 'Chiudi', { duration: 3000 });
                        this.loadProducts();
                    },
                    error: (error) => {
                        console.error('Errore nell\'eliminazione del prodotto:', error);
                        this.snackBar.open('Errore nell\'eliminazione del prodotto', 'Chiudi', { duration: 3000 });
                    }
                });
            }
        });
    }

    formatCurrency(value: number): string {
        return new Intl.NumberFormat('it-IT', {
            style: 'currency',
            currency: 'EUR'
        }).format(value);
    }

    viewProductDetails(product: DistributoreProductDTO): void {
        const dialogRef = this.dialog.open(ProductDetailDialogComponent, {
            width: '900px',
            maxWidth: '95vw',
            maxHeight: '90vh',
            data: {
                productId: product.id
            },
            panelClass: 'product-detail-dialog-container'
        });

        dialogRef.afterClosed().subscribe(() => {
            console.log('Product detail dialog closed');
        });
    }

    getStatusClass(stato: string): string {
        return (stato || '').toLowerCase();
    }

    getStatusDisplay(stato: string): string {
        return stato || 'Sconosciuto';
    }
}
