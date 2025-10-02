import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { EventoDTO } from '../../../../../core/models/animatore.models';

export interface ConfirmActionDialogData {
    title: string;
    message: string;
    evento: EventoDTO;
    actionType: 'start' | 'end' | 'cancel';
    confirmButtonText: string;
    confirmButtonColor?: 'primary' | 'accent' | 'warn';
}

@Component({
    selector: 'app-confirm-action-dialog',
    standalone: true,
    imports: [
        CommonModule,
        MatDialogModule,
        MatButtonModule,
        MatIconModule
    ],
    template: `
    <h2 mat-dialog-title>
      <mat-icon [class]="'icon-' + data.actionType">{{ getIcon() }}</mat-icon>
      {{ data.title }}
    </h2>
    
    <mat-dialog-content>
      <div class="event-info">
        <h3>{{ getEventoNome() }}</h3>
        <p class="event-date">{{ getEventoData() }}</p>
        <p class="event-location">{{ getEventoLocation() }}</p>
      </div>

      <div class="message">
        <p>{{ data.message }}</p>
      </div>

      <div class="warning" *ngIf="data.actionType === 'cancel'">
        <mat-icon>warning</mat-icon>
        <span>Questa azione non può essere annullata.</span>
      </div>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">Annulla</button>
      <button 
        mat-raised-button 
        [color]="data.confirmButtonColor || 'primary'"
        (click)="onConfirm()"
      >
        {{ data.confirmButtonText }}
      </button>
    </mat-dialog-actions>
  `,
    styles: [`
    mat-dialog-content {
      min-width: 450px;
      padding: 20px;
    }

    .event-info {
      background: #f5f5f5;
      padding: 15px;
      border-radius: 8px;
      margin-bottom: 20px;

      h3 {
        margin: 0 0 8px 0;
        color: #333;
      }

      .event-date, .event-location {
        margin: 4px 0;
        color: #666;
        font-size: 14px;
      }
    }

    .message {
      margin-bottom: 20px;

      p {
        margin: 0;
        color: #555;
        line-height: 1.5;
      }
    }

    .warning {
      display: flex;
      align-items: center;
      background: #fff3cd;
      border: 1px solid #ffc107;
      border-radius: 4px;
      padding: 12px;
      color: #856404;

      mat-icon {
        margin-right: 8px;
        color: #ffc107;
      }
    }

    h2 mat-icon {
      vertical-align: middle;
      margin-right: 8px;

      &.icon-start {
        color: #4caf50;
      }

      &.icon-end {
        color: #2196f3;
      }

      &.icon-cancel {
        color: #f44336;
      }
    }

    mat-dialog-actions {
      padding: 16px 24px;
    }
  `]
})
export class ConfirmActionDialogComponent {
    constructor(
        public dialogRef: MatDialogRef<ConfirmActionDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: ConfirmActionDialogData
    ) { }

    getIcon(): string {
        switch (this.data.actionType) {
            case 'start':
                return 'play_arrow';
            case 'end':
                return 'stop';
            case 'cancel':
                return 'cancel';
            default:
                return 'help';
        }
    }

    getEventoNome(): string {
        return this.data.evento.nomeEvento || this.data.evento.titolo || 'Evento';
    }

    getEventoData(): string {
        const dataInizio = this.data.evento.dataOraInizio || this.data.evento.dataInizio;
        if (dataInizio) {
            return new Date(dataInizio).toLocaleString('it-IT', {
                day: 'numeric',
                month: 'long',
                year: 'numeric',
                hour: '2-digit',
                minute: '2-digit'
            });
        }
        return '';
    }

    getEventoLocation(): string {
        return this.data.evento.luogoEvento || this.data.evento.luogo || '';
    }

    onCancel(): void {
        this.dialogRef.close(false);
    }

    onConfirm(): void {
        this.dialogRef.close(true);
    }
}
