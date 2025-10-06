import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MetodoDiColtivazioneDTO, ProduttoreProductSummaryDTO, CreateMetodoDiColtivazioneRequestDTO } from '../../../../../core/models/produttore.models';
import { ProduttoreService } from '../../../../../core/services/produttore.service';
import { CreateMethodDialogComponent } from './create-method-dialog.component';
import { EditMethodDialogComponent } from './edit-method-dialog.component';

@Component({
    selector: 'app-metodi-coltivazione',
    standalone: true,
    imports: [
        CommonModule,
        FormsModule,
        MatCardModule,
        MatButtonModule,
        MatIconModule,
        MatFormFieldModule,
        MatSelectModule,
        MatTooltipModule,
        MatProgressSpinnerModule,
        MatSnackBarModule,
        MatDialogModule,
        MatDividerModule
    ],
    templateUrl: './metodi-coltivazione.component.html',
    styleUrl: './metodi-coltivazione.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class MetodiColtivazioneComponent implements OnInit {
    // Product selection
    products: ProduttoreProductSummaryDTO[] = [];
    selectedProductId: number | null = null;
    selectedProduct: ProduttoreProductSummaryDTO | null = null;
    cultivationMethod: MetodoDiColtivazioneDTO | null = null;

    isLoading = false;
    isLoadingProducts = true;
    hasMethod = false;

    get isProductApproved(): boolean {
        return this.selectedProduct?.statoVerifica === 'APPROVATO';
    }

    get selectedProductName(): string {
        return this.selectedProduct?.nome || '';
    }

    get canManageCultivationMethod(): boolean {
        return this.selectedProductId !== null && this.isProductApproved;
    }

    constructor(
        private produttoreService: ProduttoreService,
        private dialog: MatDialog,
        private snackBar: MatSnackBar,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit(): void {
        this.loadProducts();
    }

    loadProducts(): void {
        this.isLoadingProducts = true;
        this.produttoreService.getMyProducts().subscribe({
            next: (response) => {
                this.products = response.content;
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
        this.selectedProduct = this.products.find(p => p.idProdotto === productId) || null;
        if (this.selectedProductId) {
            this.loadCultivationMethod(this.selectedProductId);
        }
    }

    loadCultivationMethod(productId: number): void {
        this.isLoading = true;
        this.cdr.markForCheck();

        this.produttoreService.getCultivationMethod(productId).subscribe({
            next: (method) => {
                this.cultivationMethod = method;
                this.hasMethod = true;
                this.isLoading = false;
                this.cdr.markForCheck();
            },
            error: (error) => {
                // 404 means no cultivation method exists for this product
                this.cultivationMethod = null;
                this.hasMethod = false;
                this.isLoading = false;
                this.cdr.markForCheck();
            }
        });
    }

    aggiungiMetodo(): void {
        if (!this.selectedProductId || !this.selectedProduct) {
            this.snackBar.open('Seleziona prima un prodotto', 'Chiudi', { duration: 3000 });
            return;
        }

        // Controllo stato di approvazione del prodotto
        if (this.selectedProduct.statoVerifica !== 'APPROVATO') {
            this.snackBar.open(
                'Il metodo di coltivazione può essere creato solo per prodotti approvati. Stato attuale: ' + this.selectedProduct.statoVerifica, 
                'Chiudi', 
                { duration: 5000 }
            );
            return;
        }

        if (this.hasMethod) {
            this.snackBar.open('Questo prodotto ha già un metodo di coltivazione', 'Chiudi', { duration: 3000 });
            return;
        }

        const dialogRef = this.dialog.open(CreateMethodDialogComponent, {
            width: '600px',
            data: {
                productId: this.selectedProductId,
                productName: this.selectedProduct.nome
            }
        });

        dialogRef.afterClosed().subscribe((result: CreateMetodoDiColtivazioneRequestDTO) => {
            if (result && this.selectedProductId) {
                this.isLoading = true;
                this.cdr.markForCheck();

                this.produttoreService.createCultivationMethod(this.selectedProductId, result).subscribe({
                    next: () => {
                        this.snackBar.open('Metodo di coltivazione creato con successo', 'Chiudi', { duration: 3000 });
                        this.loadCultivationMethod(this.selectedProductId!);
                    },
                    error: (error) => {
                        console.error('Errore durante la creazione del metodo:', error);
                        this.snackBar.open('Errore durante la creazione del metodo', 'Chiudi', { duration: 3000 });
                        this.isLoading = false;
                        this.cdr.markForCheck();
                    }
                });
            }
        });
    }

    modificaMetodo(): void {
        if (!this.selectedProductId || !this.cultivationMethod || !this.selectedProduct) {
            return;
        }

        // Controllo stato di approvazione del prodotto
        if (this.selectedProduct.statoVerifica !== 'APPROVATO') {
            this.snackBar.open(
                'Il metodo di coltivazione può essere modificato solo per prodotti approvati. Stato attuale: ' + this.selectedProduct.statoVerifica, 
                'Chiudi', 
                { duration: 5000 }
            );
            return;
        }

        const dialogRef = this.dialog.open(EditMethodDialogComponent, {
            width: '600px',
            data: this.cultivationMethod
        });

        dialogRef.afterClosed().subscribe((result: CreateMetodoDiColtivazioneRequestDTO) => {
            if (result && this.selectedProductId) {
                this.isLoading = true;
                this.cdr.markForCheck();

                this.produttoreService.updateCultivationMethod(this.selectedProductId, result).subscribe({
                    next: () => {
                        this.snackBar.open('Metodo di coltivazione aggiornato con successo', 'Chiudi', { duration: 3000 });
                        this.loadCultivationMethod(this.selectedProductId!);
                    },
                    error: (error) => {
                        console.error('Errore durante l\'aggiornamento del metodo:', error);
                        this.snackBar.open('Errore durante l\'aggiornamento del metodo', 'Chiudi', { duration: 3000 });
                        this.isLoading = false;
                        this.cdr.markForCheck();
                    }
                });
            }
        });
    }

    eliminaMetodo(): void {
        if (!this.selectedProductId || !this.selectedProduct) {
            return;
        }

        // Controllo stato di approvazione del prodotto
        if (this.selectedProduct.statoVerifica !== 'APPROVATO') {
            this.snackBar.open(
                'Il metodo di coltivazione può essere eliminato solo per prodotti approvati. Stato attuale: ' + this.selectedProduct.statoVerifica, 
                'Chiudi', 
                { duration: 5000 }
            );
            return;
        }

        if (confirm('Sei sicuro di voler eliminare questo metodo di coltivazione?')) {
            this.isLoading = true;
            this.cdr.markForCheck();

            this.produttoreService.deleteCultivationMethod(this.selectedProductId).subscribe({
                next: () => {
                    this.snackBar.open('Metodo di coltivazione eliminato con successo', 'Chiudi', { duration: 3000 });
                    this.cultivationMethod = null;
                    this.hasMethod = false;
                    this.isLoading = false;
                    this.cdr.markForCheck();
                },
                error: (error) => {
                    console.error('Errore durante l\'eliminazione del metodo:', error);
                    this.snackBar.open('Errore durante l\'eliminazione del metodo', 'Chiudi', { duration: 3000 });
                    this.isLoading = false;
                    this.cdr.markForCheck();
                }
            });
        }
    }
}