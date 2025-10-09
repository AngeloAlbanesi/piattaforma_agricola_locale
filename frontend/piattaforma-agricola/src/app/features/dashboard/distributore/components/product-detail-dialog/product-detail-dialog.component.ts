import { Component, Inject, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatTabsModule } from '@angular/material/tabs';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Subject } from 'rxjs';

import { DistributoreService } from '../../../../../core/services/distributore.service';
import { DistributoreProductDTO } from '../../../../../core/models/distributore.models';

export interface ProductDetailDialogData {
    productId: number;
}

@Component({
    selector: 'app-product-detail-dialog',
    standalone: true,
    imports: [
        CommonModule,
        MatDialogModule,
        MatButtonModule,
        MatIconModule,
        MatCardModule,
        MatChipsModule,
        MatTabsModule,
        MatProgressSpinnerModule,
        MatSnackBarModule
    ],
    templateUrl: './product-detail-dialog.component.html',
    styleUrls: ['./product-detail-dialog.component.scss'],

})
export class ProductDetailDialogComponent implements OnInit, OnDestroy {
    private destroy$ = new Subject<void>();

    productDetails: DistributoreProductDTO | null = null;
    isLoading = true;

    constructor(
        private distributoreService: DistributoreService,
        private snackBar: MatSnackBar,
        private dialogRef: MatDialogRef<ProductDetailDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: ProductDetailDialogData
    ) {}

    ngOnInit(): void {
        this.loadProductDetails();
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    private loadProductDetails(): void {
        this.isLoading = true;

        console.log('🔍 [ProductDetailDialog] Caricamento dettagli prodotto ID:', this.data.productId);

        // Carica i dettagli reali del prodotto dall'API
        this.distributoreService.getProductById(this.data.productId).subscribe({
            next: (product) => {
                console.log('✅ [ProductDetailDialog] Dati prodotto ricevuti:', product);
                this.productDetails = product;
                this.isLoading = false;
            },
            error: (error) => {
                console.error('❌ [ProductDetailDialog] Errore nel caricamento dei dettagli del prodotto:', error);
                this.snackBar.open('Errore nel caricamento dei dettagli del prodotto', 'Chiudi', { 
                    duration: 3000 
                });
                this.isLoading = false;
                this.dialogRef.close();
            }
        });
    }

    onClose(): void {
        this.dialogRef.close();
    }

    onAddToPackage(): void {
        this.dialogRef.close({ action: 'addToPackage', product: this.productDetails });
    }

    // Utility methods
    formatCurrency(value: number): string {
        return new Intl.NumberFormat('it-IT', {
            style: 'currency',
            currency: 'EUR'
        }).format(value);
    }

    formatDate(date: string): string {
        return new Date(date).toLocaleDateString('it-IT', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
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

    getStatusClass(stato: string): string {
        return (stato || '').toLowerCase();
    }

    getStatusDisplay(stato: string): string {
        return stato || 'Sconosciuto';
    }

    // Getters per template
    get hasProduct(): boolean {
        return !!this.productDetails;
    }

    get hasCertifications(): boolean {
        return !!(this.productDetails?.certificazioni?.length);
    }

    // Helper per formattare il tipo di origine
    getTipoOrigineDisplay(tipoOrigine?: string): string {
        if (!tipoOrigine) return 'Non specificato';
        
        switch (tipoOrigine) {
            case 'COLTIVATO':
                return 'Coltivato';
            case 'COLTIVATO_ALLEVATO':
                return 'Coltivato/Allevato';
            case 'TRASFORMATO':
                return 'Trasformato';
            default:
                return tipoOrigine;
        }
    }

    // Helper per tracciare le certificazioni nel template
    trackByCertificationId(index: number, certification: any): number {
        return certification.idCertificazione;
    }
}