import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { PacchettoTipicitaDTO, DistributoreProductDTO } from '../../../../../core/models/distributore.models';

export interface DeleteConfirmationDialogData {
    package?: PacchettoTipicitaDTO;
    product?: DistributoreProductDTO;
    type?: 'package' | 'product';
    title?: string;
    message?: string;
}

@Component({
    selector: 'app-delete-confirmation-dialog',
    standalone: true,
    imports: [
        CommonModule,
        MatDialogModule,
        MatButtonModule,
        MatIconModule,
        MatProgressSpinnerModule
    ],
    templateUrl: './delete-confirmation-dialog.component.html',
    styleUrls: ['./delete-confirmation-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class DeleteConfirmationDialogComponent {
    isLoading = false;

    constructor(
        private dialogRef: MatDialogRef<DeleteConfirmationDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: DeleteConfirmationDialogData
    ) {}

    onCancel(): void {
        this.dialogRef.close(false);
    }

    onConfirm(): void {
        this.isLoading = true;
        this.dialogRef.close(true);
    }

    // Getters per template
    get dialogTitle(): string {
        if (this.data.title) {
            return this.data.title;
        }
        
        switch (this.data.type) {
            case 'product':
                return 'Rimuovi Prodotto';
            case 'package':
            default:
                return 'Elimina Pacchetto';
        }
    }

    get dialogMessage(): string {
        if (this.data.message) {
            return this.data.message;
        }

        switch (this.data.type) {
            case 'product':
                return `Sei sicuro di voler eliminare definitivamente il prodotto "${this.data.product?.nome}"?`;
            case 'package':
            default:
                return `Sei sicuro di voler eliminare definitivamente il pacchetto "${this.data.package?.nome}"?`;
        }
    }

    get dialogIcon(): string {
        switch (this.data.type) {
            case 'product':
                return 'remove_circle';
            case 'package':
            default:
                return 'delete_forever';
        }
    }

    get confirmButtonText(): string {
        switch (this.data.type) {
            case 'product':
                return 'Rimuovi';
            case 'package':
            default:
                return 'Elimina';
        }
    }

    get warningMessage(): string {
        switch (this.data.type) {
            case 'product':
                return 'Questa azione non può essere annullata. Il prodotto verrà eliminato definitivamente.';
            case 'package':
            default:
                return 'Questa azione non può essere annullata. Il pacchetto verrà eliminato definitivamente.';
        }
    }

    get hasOrdersWarning(): boolean {
        // Se il pacchetto ha delle vendite, mostra un warning aggiuntivo
        return this.data.type === 'package' && this.data.package?.stato === 'ATTIVO';
    }

    get ordersWarningMessage(): string {
        return 'Attenzione: questo elemento potrebbe avere ordini associati. Verifica prima di procedere.';
    }

    // Getter per ottenere il nome dell'elemento
    get itemName(): string {
        return this.data.type === 'product' ? this.data.product?.nome || '' : this.data.package?.nome || '';
    }

    get itemPrice(): number {
        return this.data.type === 'product' ? this.data.product?.prezzo || 0 : this.data.package?.prezzo || 0;
    }

    get itemStato(): string {
        return this.data.type === 'product' ? this.data.product?.stato || '' : this.data.package?.stato || '';
    }

    // Utility methods per template
    formatCurrency(value: number): string {
        return new Intl.NumberFormat('it-IT', {
            style: 'currency',
            currency: 'EUR'
        }).format(value);
    }

    getStatusColor(stato: string): string {
        switch (stato) {
            case 'ATTIVO':
                return '#27ae60';
            case 'IN_PROGETTAZIONE':
                return '#f39c12';
            case 'INATTIVO':
                return '#e74c3c';
            default:
                return '#95a5a6';
        }
    }

    getStatusLabel(stato: string): string {
        switch (stato) {
            case 'ATTIVO':
                return 'Attivo';
            case 'IN_PROGETTAZIONE':
                return 'In Progettazione';
            case 'INATTIVO':
                return 'Inattivo';
            default:
                return stato;
        }
    }
}