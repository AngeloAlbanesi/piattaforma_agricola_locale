import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { EventoDTO, StatoEvento } from '../../../../../core/models/animatore.models';
import { AnimatoreService } from '../../../../../core/services/animatore.service';

export interface ViewEventDetailsDialogData {
    evento: EventoDTO;
}

@Component({
    selector: 'app-view-event-details-dialog',
    standalone: true,
    imports: [
        CommonModule,
        MatDialogModule,
        MatButtonModule,
        MatIconModule,
        MatChipsModule,
        MatDividerModule,
        MatProgressSpinnerModule
    ],
    template: `
    <h2 mat-dialog-title>
      <mat-icon>event</mat-icon>
      {{ getEventoNome() }}
    </h2>
    
    <mat-dialog-content>
      <div class="loading-container" *ngIf="isLoading">
        <mat-spinner diameter="40"></mat-spinner>
        <p>Caricamento dettagli...</p>
      </div>

      <div class="event-details" *ngIf="!isLoading && eventoDettagli">
        <!-- Stato -->
        <div class="detail-row">
          <span class="detail-label">Stato:</span>
          <mat-chip-listbox>
            <mat-chip [color]="getStatoClass()" selected>
              {{ getStato() }}
            </mat-chip>
          </mat-chip-listbox>
        </div>

        <mat-divider></mat-divider>

        <!-- Nome Evento -->
        <div class="detail-row">
          <span class="detail-label">Nome Evento:</span>
          <span class="detail-value">{{ getEventoNome() }}</span>
        </div>

        <mat-divider></mat-divider>

        <!-- Descrizione -->
        <div class="detail-row">
          <span class="detail-label">Descrizione:</span>
          <span class="detail-value description">{{ getDescrizione() }}</span>
        </div>

        <mat-divider></mat-divider>

        <!-- Data Inizio -->
        <div class="detail-row">
          <span class="detail-label">Data Inizio:</span>
          <span class="detail-value">{{ getDataInizio() }}</span>
        </div>

        <mat-divider></mat-divider>

        <!-- Data Fine -->
        <div class="detail-row">
          <span class="detail-label">Data Fine:</span>
          <span class="detail-value">{{ getDataFine() }}</span>
        </div>

        <mat-divider></mat-divider>

        <!-- Luogo -->
        <div class="detail-row">
          <span class="detail-label">Luogo:</span>
          <span class="detail-value">{{ getLuogo() }}</span>
        </div>

        <mat-divider></mat-divider>

        <!-- Capienza Massima -->
        <div class="detail-row">
          <span class="detail-label">Capienza Massima:</span>
          <span class="detail-value">{{ getCapienzaMassima() }}</span>
        </div>
      </div>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-raised-button color="primary" (click)="onClose()">
        <mat-icon>close</mat-icon>
        Chiudi
      </button>
    </mat-dialog-actions>
  `,
    styles: [`
    mat-dialog-content {
      min-width: 500px;
      max-width: 700px;
      padding: 20px;
    }

    h2 mat-icon {
      vertical-align: middle;
      margin-right: 8px;
      color: #1976d2;
    }

    .event-details {
      display: flex;
      flex-direction: column;
      gap: 16px;
    }

    .detail-row {
      display: flex;
      flex-direction: column;
      gap: 8px;
    }

    .detail-label {
      font-weight: 600;
      color: #666;
      font-size: 14px;
      text-transform: uppercase;
    }

    .detail-value {
      color: #333;
      font-size: 16px;
      line-height: 1.5;

      &.description {
        white-space: pre-wrap;
        word-wrap: break-word;
      }
    }

    mat-divider {
      margin: 8px 0;
    }

    mat-chip-listbox {
      margin: 0;
    }

    mat-chip {
      font-weight: 500;
    }

    mat-dialog-actions {
      padding: 16px 24px;
    }

    .loading-container {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 40px;
      gap: 16px;

      p {
        margin: 0;
        color: #666;
      }
    }
  `]
})
export class ViewEventDetailsDialogComponent implements OnInit {
    eventoDettagli: any = null;
    isLoading = true;

    constructor(
        public dialogRef: MatDialogRef<ViewEventDetailsDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: ViewEventDetailsDialogData,
        private animatoreService: AnimatoreService
    ) { }

    ngOnInit(): void {
        const eventoId = (this.data.evento as any).idEvento || this.data.evento.id;
        this.animatoreService.getEventById(eventoId).subscribe({
            next: (dettagli) => {
                this.eventoDettagli = dettagli;
                this.isLoading = false;
            },
            error: (err) => {
                console.error('Errore caricamento dettagli evento:', err);
                // In caso di errore, usa i dati parziali
                this.eventoDettagli = this.data.evento;
                this.isLoading = false;
            }
        });
    }

    getEventoNome(): string {
        if (this.eventoDettagli) {
            return this.eventoDettagli.nomeEvento || this.eventoDettagli.titolo || 'N/A';
        }
        return this.data.evento.nomeEvento || this.data.evento.titolo || 'N/A';
    }

    getDescrizione(): string {
        if (!this.eventoDettagli) return 'Caricamento...';
        return this.eventoDettagli.descrizione || 'Nessuna descrizione disponibile';
    }

    getDataInizio(): string {
        if (!this.eventoDettagli) return 'Caricamento...';
        const dataInizio = this.eventoDettagli.dataOraInizio || this.eventoDettagli.dataInizio;
        if (dataInizio) {
            return this.formatDateTime(dataInizio);
        }
        return 'N/A';
    }

    getDataFine(): string {
        if (!this.eventoDettagli) return 'Caricamento...';
        const dataFine = this.eventoDettagli.dataOraFine || this.eventoDettagli.dataFine;
        if (dataFine) {
            return this.formatDateTime(dataFine);
        }
        return 'N/A';
    }

    getLuogo(): string {
        if (!this.eventoDettagli) return 'Caricamento...';
        return this.eventoDettagli.luogoEvento || this.eventoDettagli.luogo || 'N/A';
    }

    getCapienzaMassima(): string {
        if (!this.eventoDettagli) return 'Caricamento...';
        const capienza = this.eventoDettagli.capienzaMassima || this.eventoDettagli.partecipantiPrevisti;
        return capienza !== undefined && capienza !== null ? capienza.toString() : 'Illimitata';
    }

    getStato(): string {
        if (!this.eventoDettagli) return 'Caricamento...';
        // Usa statoEvento che è il campo corretto dal backend
        return this.eventoDettagli.statoEvento || this.eventoDettagli.stato || 'N/A';
    }

    getStatoClass(): 'primary' | 'accent' | 'warn' | '' {
        if (!this.eventoDettagli) return '';
        // Usa statoEvento che è il campo corretto dal backend
        const stato = this.eventoDettagli.statoEvento || this.eventoDettagli.stato;
        switch (stato) {
            case StatoEvento.IN_PROGRAMMA:
                return 'accent';
            case StatoEvento.IN_CORSO:
                return 'primary';
            case StatoEvento.CONCLUSO:
                return '';
            case StatoEvento.ANNULLATO:
                return 'warn';
            default:
                return '';
        }
    }

    /**
     * Formatta una data/ora dal backend nel formato dd/MM/yyyy HH:mm
     * Il backend restituisce: "dd-MM-yyyy/HH-mm" (es: "01-01-2026/07-00")
     */
    private formatDateTime(dateTimeStr: string): string {
        if (!dateTimeStr) return 'N/A';

        // Se il formato è "dd-MM-yyyy/HH-mm"
        if (dateTimeStr.includes('/')) {
            const [datePart, timePart] = dateTimeStr.split('/');
            const [day, month, year] = datePart.split('-');
            const [hour, minute] = timePart.split('-');
            return `${day}/${month}/${year} ${hour}:${minute}`;
        }

        // Fallback: prova a parsare come data ISO
        try {
            const date = new Date(dateTimeStr);
            const day = date.getDate().toString().padStart(2, '0');
            const month = (date.getMonth() + 1).toString().padStart(2, '0');
            const year = date.getFullYear();
            const hour = date.getHours().toString().padStart(2, '0');
            const minute = date.getMinutes().toString().padStart(2, '0');
            return `${day}/${month}/${year} ${hour}:${minute}`;
        } catch {
            return dateTimeStr;
        }
    }

    onClose(): void {
        this.dialogRef.close();
    }
}

