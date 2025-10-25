import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { AziendaPartecipanteDTO } from '../../../../../core/models/animatore.models';
import { PublicAziendeService } from '../../../../../core/services/public-aziende.service';
import { PublicAziendaSummaryDTO } from '../../../../../core/models/public.models';

export interface ManageAziendeDialogData {
    eventoId: number;
    eventoNome: string;
    aziendePartecipanti: AziendaPartecipanteDTO[];
    aziendeDisponibili: AziendaPartecipanteDTO[]; // Tutte le aziende che possono essere aggiunte
}

@Component({
    selector: 'app-manage-aziende-dialog',
    standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        MatDialogModule,
        MatButtonModule,
        MatFormFieldModule,
        MatSelectModule,
        MatIconModule,
        MatProgressSpinnerModule,
        MatChipsModule
    ],
    template: `
    <h2 mat-dialog-title>
      <mat-icon>business</mat-icon>
      Gestisci Aziende Partecipanti
    </h2>
    
    <mat-dialog-content>
      <div class="event-info">
        <h3>{{ data.eventoNome }}</h3>
        <p>{{ data.aziendePartecipanti.length }} aziende partecipanti</p>
      </div>

      <!-- Aggiungi nuova azienda -->
      <div *ngIf="isLoadingAziende" class="loading-state">
        <mat-spinner diameter="40"></mat-spinner>
        <p>Caricamento aziende...</p>
      </div>

      <div *ngIf="!isLoadingAziende" class="add-section">
        <h4>Aggiungi azienda</h4>
        <form [formGroup]="addForm" class="add-form">
          <mat-form-field appearance="outline" class="full-width">
            <mat-label>Seleziona azienda da aggiungere</mat-label>
            <mat-select formControlName="aziendaSelect">
              <mat-option *ngFor="let azienda of aziendeDisponibiliFiltered" [value]="azienda">
                <div class="azienda-option">
                  <span class="azienda-name">{{ azienda.nomeAzienda }}</span>
                  <span class="azienda-piva">P.IVA: {{ azienda.partitaIva || 'N/D' }}</span>
                </div>
              </mat-option>
            </mat-select>
            <mat-icon matSuffix>business</mat-icon>
          </mat-form-field>
          <button 
            mat-raised-button 
            color="primary" 
            (click)="addSelectedAzienda()"
            [disabled]="!addForm.get('aziendaSelect')?.value"
            type="button"
          >
            <mat-icon>add</mat-icon> Aggiungi Azienda
          </button>
        </form>
      </div>

      <!-- Lista aziende partecipanti -->
      <div class="aziende-list">
        <h4>Aziende partecipanti ({{ data.aziendePartecipanti.length }})</h4>
        <div *ngIf="data.aziendePartecipanti.length === 0" class="empty-state">
          <mat-icon>business_center</mat-icon>
          <p>Nessuna azienda partecipante</p>
        </div>
        <div 
          *ngFor="let azienda of data.aziendePartecipanti" 
          class="azienda-card"
        >
          <div class="azienda-info">
            <div class="azienda-header">
              <h5>{{ azienda.nomeAzienda }}</h5>
              <button 
                mat-icon-button 
                color="warn"
                (click)="removeAzienda(azienda)"
                matTooltip="Rimuovi azienda"
              >
                <mat-icon>delete</mat-icon>
              </button>
            </div>
            <p class="azienda-detail">
              <mat-icon>badge</mat-icon>
              P.IVA: {{ azienda.partitaIva }}
            </p>
            <p class="azienda-detail" *ngIf="azienda.indirizzoAzienda">
              <mat-icon>location_on</mat-icon>
              {{ azienda.indirizzoAzienda }}
            </p>
            <p class="azienda-detail" *ngIf="azienda.sitoWebUrl">
              <mat-icon>language</mat-icon>
              <a [href]="azienda.sitoWebUrl" target="_blank">{{ azienda.sitoWebUrl }}</a>
            </p>
            <p class="azienda-description" *ngIf="azienda.descrizioneAzienda">
              {{ azienda.descrizioneAzienda }}
            </p>
            <div class="certifications" *ngIf="azienda.certificazioniAzienda && azienda.certificazioniAzienda.length > 0">
              <mat-chip-set>
                <mat-chip *ngFor="let cert of azienda.certificazioniAzienda">
                  {{ cert }}
                </mat-chip>
              </mat-chip-set>
            </div>
          </div>
        </div>
      </div>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button (click)="onClose()">Chiudi</button>
    </mat-dialog-actions>
  `,
    styles: [`
    mat-dialog-content {
      min-width: 600px;
      max-width: 800px;
      min-height: 400px;
      max-height: 70vh;
      overflow-y: auto;
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

      p {
        margin: 0;
        color: #666;
        font-size: 14px;
      }
    }

    .loading-state {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 40px 20px;
      margin-bottom: 20px;

      p {
        margin-top: 15px;
        color: #666;
        font-size: 14px;
      }
    }

    .add-section {
      margin-bottom: 30px;
      padding-bottom: 20px;
      border-bottom: 1px solid #e0e0e0;

      h4 {
        margin-bottom: 15px;
        color: #333;
      }

      .add-form {
        display: flex;
        gap: 12px;
        align-items: flex-start;
      }

      .full-width {
        flex: 1;
      }

      button {
        margin-top: 4px;
      }
    }

    .azienda-option {
      display: flex;
      flex-direction: column;
      padding: 8px 0;

      .azienda-name {
        font-weight: 500;
        color: #333;
      }

      .azienda-piva {
        font-size: 12px;
        color: #666;
      }
    }

    .aziende-list {
      h4 {
        margin-bottom: 15px;
        color: #333;
      }
    }

    .empty-state {
      text-align: center;
      padding: 40px;
      color: #999;

      mat-icon {
        font-size: 48px;
        width: 48px;
        height: 48px;
        margin-bottom: 10px;
      }

      p {
        margin: 0;
      }
    }

    .azienda-card {
      background: #fff;
      border: 1px solid #e0e0e0;
      border-radius: 8px;
      padding: 16px;
      margin-bottom: 12px;
      transition: box-shadow 0.3s;

      &:hover {
        box-shadow: 0 2px 8px rgba(0,0,0,0.1);
      }

      .azienda-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;

        h5 {
          margin: 0;
          color: #333;
          font-size: 16px;
        }
      }

      .azienda-detail {
        display: flex;
        align-items: center;
        margin: 8px 0;
        font-size: 14px;
        color: #666;

        mat-icon {
          font-size: 18px;
          width: 18px;
          height: 18px;
          margin-right: 8px;
          color: #999;
        }

        a {
          color: #1976d2;
          text-decoration: none;

          &:hover {
            text-decoration: underline;
          }
        }
      }

      .azienda-description {
        margin: 12px 0;
        padding: 12px;
        background: #f9f9f9;
        border-radius: 4px;
        font-size: 14px;
        color: #555;
        line-height: 1.5;
      }

      .certifications {
        margin-top: 12px;
      }
    }

    h2 mat-icon {
      vertical-align: middle;
      margin-right: 8px;
    }

    mat-dialog-actions {
      padding: 16px 24px;
    }
  `]
})
export class ManageAziendeDialogComponent implements OnInit {
    addForm: FormGroup;
    aziendeDisponibiliFiltered: AziendaPartecipanteDTO[] = [];
    isLoadingAziende = true;

    constructor(
        public dialogRef: MatDialogRef<ManageAziendeDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: ManageAziendeDialogData,
        private fb: FormBuilder,
        private publicAziendeService: PublicAziendeService
    ) {
        this.addForm = this.fb.group({
            aziendaSelect: [null, Validators.required]
        });
    }

    ngOnInit(): void {
        // Fetch all available companies from public API
        this.loadAziendeDisponibili();
    }

    /**
     * Load all available companies from the public API
     */
    private loadAziendeDisponibili(): void {
        this.isLoadingAziende = true;

        this.publicAziendeService.getAziende({ page: 0, size: 100 }).subscribe({
            next: (response) => {
                // Convert to AziendaPartecipanteDTO
                const tutteLeAziende = response.content.map(a => this.mapToAziendaPartecipante(a));

                // Filter out companies already participating
                const idsPartecipanti = new Set(
                    this.data.aziendePartecipanti.map(a => a.id)
                );

                this.aziendeDisponibiliFiltered = tutteLeAziende.filter(
                    a => !idsPartecipanti.has(a.id)
                );

                this.isLoadingAziende = false;
            },
            error: (err) => {
                console.error('Error loading companies:', err);
                this.isLoadingAziende = false;
            }
        });
    }

    /**
     * Map PublicAziendaSummaryDTO to AziendaPartecipanteDTO
     */
    private mapToAziendaPartecipante(azienda: PublicAziendaSummaryDTO): AziendaPartecipanteDTO {
        return {
            id: azienda.id,
            nomeAzienda: azienda.nomeAzienda,
            partitaIva: '', // Not available in public API
            indirizzoAzienda: this.publicAziendeService.formatIndirizzoCompleto(azienda),
            descrizioneAzienda: azienda.descrizione || '',
            sitoWebUrl: '', // Not available in summary DTO
            certificazioniAzienda: [] // Not available in summary DTO
        };
    }

    /**
     * Add selected company from dropdown
     */
    addSelectedAzienda(): void {
        const aziendaSelezionata = this.addForm.get('aziendaSelect')?.value;

        if (aziendaSelezionata) {
            this.selectAzienda(aziendaSelezionata);
            this.addForm.reset();
        }
    }

    selectAzienda(azienda: AziendaPartecipanteDTO): void {
        // Emetti evento per aggiungere l'azienda
        this.dialogRef.close({ action: 'add', azienda });
    }

    removeAzienda(azienda: AziendaPartecipanteDTO): void {
        // Emetti evento per rimuovere l'azienda
        this.dialogRef.close({ action: 'remove', azienda });
    }

    onClose(): void {
        this.dialogRef.close();
    }
}
