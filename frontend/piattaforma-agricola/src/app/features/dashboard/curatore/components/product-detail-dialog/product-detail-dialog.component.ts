import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { CuratoreService } from '../../../../../core/services/curatore.service';
import { MatSnackBar } from '@angular/material/snack-bar';

export interface ProductDetailData {
    productId: number;
    productName: string;
}

interface ProductDetail {
    idProdotto: number;
    nomeProdotto: string;
    descrizione: string;
    prezzo: number;
    categoria: string;
    quantitaDisponibile?: number;
    venditoreId: number;
    nomeVenditore: string;
    immagini: string[];
    statoVerifica?: string;
    certificazioni?: any[];
    tipoOrigine?: string;
    dataCreazione?: string;
}

@Component({
    selector: 'app-product-detail-dialog',
    standalone: true,
    imports: [
        CommonModule,
        MatDialogModule,
        MatButtonModule,
        MatIconModule,
        MatProgressSpinnerModule,
        MatChipsModule,
        MatCardModule,
        MatDividerModule,
        MatFormFieldModule,
        MatInputModule,
        FormsModule
    ],
    templateUrl: './product-detail-dialog.component.html',
    styleUrls: ['./product-detail-dialog.component.scss']
})
export class ProductDetailDialogComponent implements OnInit {
    product: ProductDetail | null = null;
    isLoading = true;
    error: string | null = null;

    // Reject dialog state
    showRejectDialog = false;
    rejectReason = '';

    constructor(
        @Inject(MAT_DIALOG_DATA) public data: ProductDetailData,
        private dialogRef: MatDialogRef<ProductDetailDialogComponent>,
        private curatoreService: CuratoreService,
        private snackBar: MatSnackBar
    ) { }

    ngOnInit(): void {
        this.loadProductDetails();
    }

    private loadProductDetails(): void {
        this.isLoading = true;
        this.error = null;

        // Chiamo l'endpoint pubblico per ottenere i dettagli del prodotto
        this.curatoreService.getProductDetailsForApproval(this.data.productId).subscribe({
            next: (product) => {
                this.product = product;
                this.isLoading = false;
            },
            error: (err) => {
                console.error('Errore caricamento dettagli prodotto:', err);
                this.error = 'Impossibile caricare i dettagli del prodotto';
                this.isLoading = false;
            }
        });
    }

    onApprove(): void {
        if (!this.product) return;

        const motivazione = 'Prodotto conforme ai requisiti. Approvato.';
        this.isLoading = true;

        this.curatoreService.approveElement(this.product.idProdotto, 'PRODOTTO', { motivazione }).subscribe({
            next: (response) => {
                console.log('Risposta approvazione:', response);
                this.snackBar.open('Prodotto approvato con successo', 'Chiudi', {
                    duration: 3000,
                    panelClass: 'success-snackbar'
                });
                this.dialogRef.close({ action: 'approved', productId: this.product!.idProdotto });
            },
            error: (err) => {
                console.error('Errore approvazione prodotto:', err);
                this.snackBar.open('Errore durante l\'approvazione', 'Chiudi', {
                    duration: 3000,
                    panelClass: 'error-snackbar'
                });
                this.isLoading = false;
            }
        });
    }

    onReject(): void {
        this.showRejectDialog = true;
    }

    confirmReject(): void {
        if (!this.product || !this.rejectReason.trim()) return;

        this.isLoading = true;
        this.showRejectDialog = false;

        this.curatoreService.rejectElement(
            this.product.idProdotto,
            'PRODOTTO',
            { motivazione: this.rejectReason.trim() }
        ).subscribe({
            next: (response) => {
                console.log('Risposta rifiuto:', response);
                this.snackBar.open('Prodotto rifiutato', 'Chiudi', {
                    duration: 3000,
                    panelClass: 'success-snackbar'
                });
                this.dialogRef.close({ action: 'rejected', productId: this.product!.idProdotto });
            },
            error: (err) => {
                console.error('Errore rifiuto prodotto:', err);
                this.snackBar.open('Errore durante il rifiuto', 'Chiudi', {
                    duration: 3000,
                    panelClass: 'error-snackbar'
                });
                this.isLoading = false;
            }
        });
    }

    cancelReject(): void {
        this.showRejectDialog = false;
        this.rejectReason = '';
    }

    onClose(): void {
        this.dialogRef.close();
    }

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
}

