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

import { ProdottiService } from '../../../../../core/services/prodotti.service';
import { AziendaService } from '../../../../../core/services/azienda.service';
import { TrasformatoreService } from '../../../../../core/services/trasformatore.service';
import { ProdottoDetailDTO } from '../../../../../core/models/trasformatore.models';
import { AziendaDetailDTO } from '../../../../../core/models/trasformatore.models';

export interface ProductDetailDialogData {
    productId: number;
    productSummary?: any;
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
    product: ProdottoDetailDTO | null = null;
    companyData: AziendaDetailDTO | null = null;
    isLoading = true;
    error: string | null = null;

    private destroy$ = new Subject<void>();

    constructor(
        public dialogRef: MatDialogRef<ProductDetailDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: ProductDetailDialogData,
        private prodottiService: ProdottiService,
        private aziendaService: AziendaService,
        private trasformatoreService: TrasformatoreService,
        private snackBar: MatSnackBar,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit(): void {
        console.log('ProductDetailDialogComponent inizializzato');
        this.loadProductDetails();
        this.loadCompanyData();
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

        this.prodottiService.getProductById(this.data.productId)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (product) => {
                    console.log('Dati prodotto ricevuti:', product);
                    // Mappa i campi del processo di trasformazione
                    const processoId = (product as any).idProcessoTrasformazioneOriginario || product.processoTrasformazioneId;
                    this.product = {
                        ...product,
                        processoTrasformazioneId: processoId,
                        processoTrasformazioneNome: (product as any).processoTrasformazioneNome
                    };

                    console.log('Processo ID:', processoId, 'Nome:', this.product.processoTrasformazioneNome);

                    // Se c'è un processo, carica il nome
                    if (processoId && !this.product.processoTrasformazioneNome) {
                        console.log('Caricamento nome processo necessario');
                        this.loadProcessoNome(processoId);
                    }

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

    private loadCompanyData(): void {
        this.aziendaService.getMyCompany()
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (company) => {
                    this.companyData = company;
                    console.log('Dati azienda ricevuti nel product detail:', company);
                    this.cdr.markForCheck();
                },
                error: (error) => {
                    console.error('Errore nel caricamento dei dati aziendali:', error);
                    this.companyData = null;
                    this.cdr.markForCheck();
                }
            });
    }

    private loadProcessoNome(processoId: number): void {
        console.log('Caricamento nome processo per ID:', processoId);
        this.trasformatoreService.getProcessById(processoId)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (processo: any) => {
                    console.log('Dati processo ricevuti:', processo);
                    if (this.product) {
                        // L'API restituisce "nomeProcesso" non "nome"
                        this.product.processoTrasformazioneNome = processo.nomeProcesso;
                        console.log('Nome processo impostato:', this.product.processoTrasformazioneNome);
                        this.cdr.markForCheck();
                    }
                },
                error: (error) => {
                    console.error('Errore nel caricamento del nome del processo:', error);
                }
            });
    }

    private createProductDetailFromSummary(summary: any): void {
        this.product = {
            ...summary,
            processoTrasformazioneId: summary.idProcessoTrasformazioneOriginario || summary.processoTrasformazioneId,
            processoTrasformazioneNome: summary.processoTrasformazioneNome,
            certificazioni: summary.certificazioni || [],
            tracciabilita: undefined,
            recensioni: [],
            mediaValutazione: 0,
            numeroRecensioni: 0
        };
    }

    private createMinimalProductDetail(): void {
        this.product = {
            id: this.data.productId,
            nome: 'Prodotto',
            descrizione: 'Dettagli non disponibili',
            prezzo: 0,
            quantitaDisponibile: 0,
            unitaMisura: 'KG',
            stato: 'BOZZA',
            dataCreazione: new Date().toISOString(),
            dataUltimaModifica: new Date().toISOString(),
            proprietarioId: 0,
            certificazioni: [],
            tracciabilita: undefined,
            recensioni: [],
            mediaValutazione: 0,
            numeroRecensioni: 0
        };
    }

    onClose(): void {
        this.dialogRef.close();
    }

    getStatoClass(stato: string): string {
        switch (stato) {
            case 'APPROVATO':
                return 'status-approved';
            case 'IN_APPROVAZIONE':
            case 'BOZZA':
                return 'status-pending';
            case 'RIFIUTATO':
                return 'status-rejected';
            default:
                return 'status-unknown';
        }
    }

    getStatoIcon(stato: string): string {
        switch (stato) {
            case 'APPROVATO':
                return 'check_circle';
            case 'IN_APPROVAZIONE':
            case 'BOZZA':
                return 'schedule';
            case 'RIFIUTATO':
                return 'cancel';
            default:
                return 'help';
        }
    }

    formatCurrency(value: number): string {
        return this.prodottiService.formatCurrency(value);
    }

    formatDate(date: string): string {
        return this.prodottiService.formatDate(date);
    }

    formatDateTime(date: string): string {
        return new Date(date).toLocaleDateString('it-IT', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    trackByCertId(index: number, cert: any): number {
        return cert.id;
    }

    /**
     * Formatta l'indirizzo completo dell'azienda
     */
    getIndirizzoAzienda(): string {
        if (!this.companyData?.indirizzo) return 'Non specificato';

        const address = this.companyData.indirizzo;
        const parts = [
            address.via,
            address.civico,
            address.cap,
            address.citta,
            address.provincia,
            address.paese
        ].filter(part => part && part.trim() !== '');

        return parts.join(', ') || 'Non specificato';
    }

    /**
     * Ottiene la descrizione dell'azienda con fallback
     */
    getDescrizioneAzienda(): string {
        if (!this.companyData?.descrizione || this.companyData.descrizione.trim() === '') {
            return 'Non specificata';
        }
        return this.companyData.descrizione;
    }

    /**
     * Ottiene il nome dell'azienda con fallback
     */
    getNomeAzienda(): string {
        if (!this.companyData?.nomeAzienda || this.companyData.nomeAzienda.trim() === '') {
            return 'Non specificato';
        }
        return this.companyData.nomeAzienda;
    }

    /**
     * Ottiene la partita IVA con fallback
     */
    getPartitaIva(): string {
        if (!this.companyData?.partitaIva || this.companyData.partitaIva.trim() === '') {
            return 'Non specificata';
        }
        return this.companyData.partitaIva;
    }

}

