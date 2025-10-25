import { ChangeDetectionStrategy, Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { PacchettoTipicitaDTO } from '../../../../../core/models/distributore.models';

@Component({
    selector: 'app-pacchetto-card',
    standalone: true,
    imports: [
        CommonModule,
        MatCardModule,
        MatIconModule,
        MatButtonModule
    ],
    templateUrl: './pacchetto-card.component.html',
    styleUrls: ['./pacchetto-card.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PacchettoCardComponent {
    @Input() pacchetto: PacchettoTipicitaDTO | null = null;
    @Output() edit = new EventEmitter<number>();
    @Output() delete = new EventEmitter<number>();
    @Output() viewDetails = new EventEmitter<number>();

    onEdit(): void {
        if (this.pacchetto) {
            this.edit.emit(this.pacchetto.id);
        }
    }

    onDelete(): void {
        if (this.pacchetto) {
            this.delete.emit(this.pacchetto.id);
        }
    }

    onViewDetails(): void {
        if (this.pacchetto) {
            this.viewDetails.emit(this.pacchetto.id);
        }
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
            month: 'short',
            day: 'numeric'
        });
    }

    getStatoColor(stato: string): string {
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

    getStatoLabel(stato: string): string {
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

    getProdottiCount(): number {
        if (this.pacchetto?.prodotti?.length) {
            return this.pacchetto.prodotti.length;
        }
        return (this.pacchetto as any)?.numeroElementi || 0;
    }
}