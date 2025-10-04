import { ChangeDetectionStrategy, Component, Inject, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatTabsModule } from '@angular/material/tabs';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Subject, takeUntil } from 'rxjs';

import { OrdiniService } from '../../../../../core/services/ordini.service';
import { 
    DettaglioOrdineDTO, 
    StatoOrdine, 
    STATO_ORDINE_LABELS, 
    STATO_ORDINE_COLORS, 
    STATO_ORDINE_ICONS 
} from '../../../../../core/models/ordini.models';

export interface OrderDetailsDialogData {
    ordineId: number;
}

@Component({
    selector: 'app-order-details-dialog',
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
        MatDividerModule,
        MatExpansionModule,
        MatSnackBarModule
    ],
    templateUrl: './order-details-dialog.component.html',
    styleUrls: ['./order-details-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class OrderDetailsDialogComponent implements OnInit, OnDestroy {
    private destroy$ = new Subject<void>();

    orderDetails: DettaglioOrdineDTO | null = null;
    isLoading = true;

    // Constants
    readonly statoLabels = STATO_ORDINE_LABELS;
    readonly statoColors = STATO_ORDINE_COLORS;
    readonly statoIcons = STATO_ORDINE_ICONS;

    constructor(
        private ordiniService: OrdiniService,
        private snackBar: MatSnackBar,
        private cdr: ChangeDetectorRef,
        private dialogRef: MatDialogRef<OrderDetailsDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: OrderDetailsDialogData
    ) {}

    ngOnInit(): void {
        this.loadOrderDetails();
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    private loadOrderDetails(): void {
        this.isLoading = true;

        this.ordiniService.getDettaglioOrdine(this.data.ordineId)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (details) => {
                    this.orderDetails = details;
                    this.isLoading = false;
                    this.cdr.markForCheck();
                },
                error: (error) => {
                    console.error('Errore nel caricamento dettagli ordine:', error);
                    this.snackBar.open('Errore nel caricamento dei dettagli', 'Chiudi', {
                        duration: 3000,
                        panelClass: 'error-snackbar'
                    });
                    this.isLoading = false;
                    this.dialogRef.close();
                }
            });
    }

    onClose(): void {
        this.dialogRef.close();
    }

    onUpdateStatus(): void {
        this.dialogRef.close({ action: 'updateStatus', order: this.orderDetails });
    }

    onAddCommunication(): void {
        this.dialogRef.close({ action: 'addCommunication', order: this.orderDetails });
    }

    onUploadDocument(): void {
        this.dialogRef.close({ action: 'uploadDocument', order: this.orderDetails });
    }

    markCommunicationAsRead(comunicazioneId: number): void {
        if (!this.orderDetails) return;

        this.ordiniService.marcaComunicazioneLetta(this.orderDetails.id, comunicazioneId)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: () => {
                    // Aggiorna lo stato locale
                    const comunicazione = this.orderDetails?.comunicazioni?.find(c => c.id === comunicazioneId);
                    if (comunicazione) {
                        comunicazione.letto = true;
                        this.cdr.markForCheck();
                    }
                },
                error: (error) => {
                    console.error('Errore nel segnare come letta:', error);
                }
            });
    }

    // Utility methods
    formatCurrency(value: number): string {
        return this.ordiniService.formatCurrency(value);
    }

    formatDate(date: string): string {
        return this.ordiniService.formatDate(date);
    }

    formatDateTime(date: string): string {
        return this.ordiniService.formatDateTime(date);
    }

    getStatoLabel(stato: StatoOrdine): string {
        return this.statoLabels[stato];
    }

    getStatoColor(stato: StatoOrdine): string {
        return this.statoColors[stato];
    }

    getStatoIcon(stato: StatoOrdine): string {
        return this.statoIcons[stato];
    }

    getCommunicationTypeIcon(tipo: string): string {
        switch (tipo) {
            case 'NOTA': return 'note';
            case 'RICHIESTA': return 'help';
            case 'PROBLEMA': return 'warning';
            case 'INFO': return 'info';
            default: return 'message';
        }
    }

    getCommunicationTypeColor(tipo: string): string {
        switch (tipo) {
            case 'NOTA': return '#2196f3';
            case 'RICHIESTA': return '#ff9800';
            case 'PROBLEMA': return '#f44336';
            case 'INFO': return '#4caf50';
            default: return '#666';
        }
    }

    getDocumentTypeIcon(tipo: string): string {
        switch (tipo) {
            case 'FATTURA': return 'receipt';
            case 'RICEVUTA': return 'receipt_long';
            case 'DOCUMENTO_TRASPORTO': return 'local_shipping';
            case 'CERTIFICATO_QUALITA': return 'verified';
            default: return 'description';
        }
    }

    // Getters per template
    get hasOrder(): boolean {
        return !!this.orderDetails;
    }

    get totalItems(): number {
        return this.orderDetails?.righeOrdine?.reduce((sum, riga) => sum + riga.quantita, 0) || 0;
    }

    get totalProducts(): number {
        return this.orderDetails?.righeOrdine?.reduce((sum, riga) => 
            sum + (riga.pacchetto?.prodotti?.length || 0), 0) || 0;
    }

    get hasDocuments(): boolean {
        return !!(this.orderDetails?.documenti?.length);
    }

    get hasCommunications(): boolean {
        return !!(this.orderDetails?.comunicazioni?.length);
    }

    get hasHistory(): boolean {
        return !!(this.orderDetails?.storico?.length);
    }

    get unreadCommunications(): number {
        return this.orderDetails?.comunicazioni?.filter(c => !c.letto).length || 0;
    }

    get canUpdateStatus(): boolean {
        if (!this.orderDetails) return false;
        const prossimiStati = this.ordiniService.getProssimiStatiPossibili(this.orderDetails.stato);
        return prossimiStati.length > 0;
    }

    get isOrderCompleted(): boolean {
        return this.orderDetails?.stato === StatoOrdine.CONSEGNATO;
    }

    get isOrderCancelled(): boolean {
        return this.orderDetails?.stato === StatoOrdine.ANNULLATO || 
               this.orderDetails?.stato === StatoOrdine.RIMBORSATO;
    }

    get customerFullName(): string {
        if (!this.orderDetails) return '';
        const { nome, cognome } = this.orderDetails.acquirente;
        return `${nome} ${cognome}`;
    }

    get deliveryAddress(): string {
        if (!this.orderDetails) return '';
        const addr = this.orderDetails.indirizzoConsegna;
        return `${addr.via} ${addr.civico}, ${addr.cap} ${addr.citta} (${addr.provincia})`;
    }

    trackByHistoryId(index: number, item: any): number {
        return item.id;
    }

    trackByCommunicationId(index: number, item: any): number {
        return item.id;
    }

    trackByDocumentId(index: number, item: any): number {
        return item.id;
    }

    trackByRigaId(index: number, item: any): number {
        return item.id;
    }

    trackByProductId(index: number, item: any): number {
        return item.id;
    }
}