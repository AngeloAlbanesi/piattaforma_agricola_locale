import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Inject, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatTabsModule } from '@angular/material/tabs';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDividerModule } from '@angular/material/divider';
import { MatListModule } from '@angular/material/list';
import { Subject, takeUntil } from 'rxjs';

import { ProduttoreService } from '../../../../../core/services/produttore.service';
import { ProduttoreProductDetailDTO, StatoVerifica, TipoOrigineProdotto } from '../../../../../core/models/produttore.models';

export interface ProductDetailDialogData {
    productId: number;
    productSummary?: any; // Dati di summary già disponibili
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
        MatSnackBarModule,
        MatDividerModule,
        MatListModule
    ],
    templateUrl: './product-detail-dialog.component.html',
    styleUrls: ['./product-detail-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductDetailDialogComponent implements OnInit, OnDestroy {
    product: ProduttoreProductDetailDTO | null = null;
    isLoading = true;
    error: string | null = null;

    private destroy$ = new Subject<void>();

    constructor(
        public dialogRef: MatDialogRef<ProductDetailDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: ProductDetailDialogData,
        private produttoreService: ProduttoreService,
        private snackBar: MatSnackBar,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit(): void {
        this.loadProductDetails();
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    loadProductDetails(): void {
        this.isLoading = true;
        this.error = null;

        console.log('Caricamento dettagli prodotto con ID:', this.data.productId);
        console.log('Dati summary disponibili:', this.data.productSummary);

        // Carichiamo sempre i dati completi dall'API per avere le certificazioni
        this.produttoreService.getProductById(this.data.productId)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (product) => {
                    console.log('Dati prodotto ricevuti:', product);
                    // Map certificazioni from backend to certificazioniDettagli
                    this.product = {
                        ...product,
                        certificazioniDettagli: (product as any).certificazioni || []
                    };
                    this.isLoading = false;
                    this.cdr.markForCheck();
                },
                error: (error) => {
                    console.error('Errore nel caricamento dei dettagli del prodotto:', error);
                    console.error('Status:', error.status);
                    console.error('Message:', error.message);
                    console.error('Error details:', error.error);

                    // Come fallback, usiamo i dati di summary se disponibili
                    if (this.data.productSummary) {
                        console.log('Fallback: uso dati di summary disponibili');
                        this.createProductDetailFromSummary(this.data.productSummary);
                    } else {
                        console.log('Tentativo di creare dati minimi per il prodotto');
                        this.createMinimalProductDetail();
                    }

                    this.error = `Attenzione: Alcuni dettagli potrebbero non essere disponibili (${error.status || 'Errore API'})`;
                    this.isLoading = false;
                    this.cdr.markForCheck();
                    this.snackBar.open(
                        this.error,
                        'Chiudi',
                        { duration: 3000 }
                    );
                }
            });
    }

    private createProductDetailFromSummary(summary: any): void {
        this.product = {
            ...summary,
            certificazioniDettagli: summary.certificazioni?.map((cert: string) => ({
                idCertificazione: Math.random(),
                nomeCertificazione: cert,
                enteRilascio: 'Non specificato',
                dataRilascio: new Date().toISOString(),
                dataScadenza: new Date(Date.now() + 365 * 24 * 60 * 60 * 1000).toISOString(),
                idProdottoAssociato: summary.idProdotto,
                idAziendaAssociata: 0
            })) || [],
            ordiniRicevuti: [],
            visualizzazioni: 0,
            venditore: {
                id: 0,
                nomeAzienda: 'La tua azienda',
                partitaIva: 'Non disponibile',
                indirizzoAzienda: 'Non disponibile'
            }
        };
    }

    private createMinimalProductDetail(): void {
        this.product = {
            idProdotto: this.data.productId,
            nome: 'Prodotto',
            descrizione: 'Dettagli non disponibili',
            prezzo: 0,
            quantitaDisponibile: 0,
            statoVerifica: 'SCONOSCIUTO',
            tipoOrigine: 'NON_SPECIFICATO',
            certificazioni: [],
            dataCreazione: new Date().toISOString(),
            dataUltimaModifica: new Date().toISOString(),
            certificazioniDettagli: [],
            ordiniRicevuti: [],
            visualizzazioni: 0,
            venditore: {
                id: 0,
                nomeAzienda: 'Non disponibile',
                partitaIva: 'Non disponibile',
                indirizzoAzienda: 'Non disponibile'
            }
        };
    }

    onClose(): void {
        this.dialogRef.close();
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
                return 'status-unknown';
        }
    }

    getStatoIcon(stato: string): string {
        switch (stato) {
            case StatoVerifica.APPROVATO:
                return 'check_circle';
            case StatoVerifica.IN_ATTESA:
                return 'schedule';
            case StatoVerifica.RESPINTO:
                return 'cancel';
            default:
                return 'help';
        }
    }

    getOriginIcon(tipo: string): string {
        switch (tipo) {
            case TipoOrigineProdotto.COLTIVATO:
                return 'eco';
            case TipoOrigineProdotto.TRASFORMATO:
                return 'factory';
            case TipoOrigineProdotto.ARTIGIANALE:
                return 'handyman';
            default:
                return 'inventory_2';
        }
    }

    formatCurrency(value: number): string {
        return this.produttoreService.formatCurrency(value);
    }

    formatDate(date: string): string {
        return this.produttoreService.formatDate(date);
    }

    formatDateTime(date: string): string {
        return this.produttoreService.formatDateTime(date);
    }

    getOrderStatusColor(stato: string): string {
        switch (stato.toUpperCase()) {
            case 'NUOVO_IN_ATTESA_DI_PAGAMENTO':
                return 'warn';
            case 'PAGATO_PRONTO_PER_LAVORAZIONE':
                return 'accent';
            case 'IN_LAVORAZIONE':
                return 'primary';
            case 'SPEDITO':
                return 'primary';
            case 'CONSEGNATO':
                return 'primary';
            case 'ANNULLATO':
                return 'warn';
            default:
                return '';
        }
    }

    getTotalOrderValue(): number {
        if (!this.product?.ordiniRicevuti) return 0;
        return this.product.ordiniRicevuti.reduce((sum, ordine) => sum + ordine.totale, 0);
    }

    getTotalProductsSold(): number {
        if (!this.product?.ordiniRicevuti) return 0;
        return this.product.ordiniRicevuti.reduce((sum, ordine) => {
            const prodottoInOrdine = ordine.prodotti.find(p => p.id === this.product!.idProdotto);
            return sum + (prodottoInOrdine?.quantita || 0);
        }, 0);
    }

    trackByOrderId(index: number, ordine: any): number {
        return ordine.id;
    }

}