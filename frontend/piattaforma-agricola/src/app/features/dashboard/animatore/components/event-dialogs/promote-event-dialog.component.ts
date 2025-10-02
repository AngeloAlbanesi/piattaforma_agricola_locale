import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { EventoDTO, PromoteRequestDTO } from '../../../../../core/models/animatore.models';

export interface PromoteEventDialogData {
    evento: EventoDTO;
}

@Component({
    selector: 'app-promote-event-dialog',
    standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        MatDialogModule,
        MatButtonModule,
        MatFormFieldModule,
        MatInputModule,
        MatCheckboxModule,
        MatIconModule
    ],
    template: `
    <h2 mat-dialog-title>
      <mat-icon>share</mat-icon>
      Promuovi Evento
    </h2>
    
    <mat-dialog-content>
      <form [formGroup]="promoteForm">
        <div class="event-info">
          <h3>{{ getEventoNome() }}</h3>
          <p class="event-date">{{ getEventoData() }}</p>
        </div>

        <div class="channels-section">
          <h4>Seleziona i canali di promozione:</h4>
          <mat-checkbox formControlName="facebook">
            <mat-icon>facebook</mat-icon>
            Facebook
          </mat-checkbox>
          <mat-checkbox formControlName="twitter">
            <mat-icon>tag</mat-icon>
            Twitter
          </mat-checkbox>
          <mat-checkbox formControlName="instagram">
            <mat-icon>photo_camera</mat-icon>
            Instagram
          </mat-checkbox>
          <mat-checkbox formControlName="email">
            <mat-icon>email</mat-icon>
            Email
          </mat-checkbox>
          <mat-checkbox formControlName="whatsapp">
            <mat-icon>phone</mat-icon>
            WhatsApp
          </mat-checkbox>
        </div>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Messaggio promozionale</mat-label>
          <textarea 
            matInput 
            formControlName="messaggio"
            rows="4"
            placeholder="Scrivi un messaggio accattivante per promuovere l'evento..."
          ></textarea>
          <mat-hint>Massimo 280 caratteri</mat-hint>
          <mat-error *ngIf="promoteForm.get('messaggio')?.hasError('required')">
            Il messaggio è obbligatorio
          </mat-error>
          <mat-error *ngIf="promoteForm.get('messaggio')?.hasError('maxlength')">
            Massimo 280 caratteri
          </mat-error>
        </mat-form-field>
      </form>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">Annulla</button>
      <button 
        mat-raised-button 
        color="primary" 
        (click)="onPromote()"
        [disabled]="!promoteForm.valid || !hasSelectedChannels()"
      >
        <mat-icon>send</mat-icon>
        Promuovi
      </button>
    </mat-dialog-actions>
  `,
    styles: [`
    mat-dialog-content {
      min-width: 500px;
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

      .event-date {
        margin: 0;
        color: #666;
        font-size: 14px;
      }
    }

    .channels-section {
      margin-bottom: 20px;

      h4 {
        margin-bottom: 12px;
        color: #333;
      }

      mat-checkbox {
        display: flex;
        align-items: center;
        margin-bottom: 10px;

        mat-icon {
          margin-right: 8px;
          font-size: 20px;
          width: 20px;
          height: 20px;
        }
      }
    }

    .full-width {
      width: 100%;
    }

    mat-dialog-actions {
      padding: 16px 24px;

      button mat-icon {
        margin-right: 8px;
      }
    }

    h2 mat-icon {
      vertical-align: middle;
      margin-right: 8px;
    }
  `]
})
export class PromoteEventDialogComponent {
    promoteForm: FormGroup;

    constructor(
        public dialogRef: MatDialogRef<PromoteEventDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: PromoteEventDialogData,
        private fb: FormBuilder
    ) {
        this.promoteForm = this.fb.group({
            facebook: [false],
            twitter: [false],
            instagram: [false],
            email: [false],
            whatsapp: [false],
            messaggio: ['', [Validators.required, Validators.maxLength(280)]]
        });
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

    hasSelectedChannels(): boolean {
        const formValue = this.promoteForm.value;
        return formValue.facebook || formValue.twitter || formValue.instagram ||
            formValue.email || formValue.whatsapp;
    }

    onCancel(): void {
        this.dialogRef.close();
    }

    onPromote(): void {
        if (this.promoteForm.valid && this.hasSelectedChannels()) {
            const formValue = this.promoteForm.value;
            const canali: ('FACEBOOK' | 'TWITTER' | 'INSTAGRAM' | 'EMAIL' | 'WHATSAPP')[] = [];

            if (formValue.facebook) canali.push('FACEBOOK');
            if (formValue.twitter) canali.push('TWITTER');
            if (formValue.instagram) canali.push('INSTAGRAM');
            if (formValue.email) canali.push('EMAIL');
            if (formValue.whatsapp) canali.push('WHATSAPP');

            const request: PromoteRequestDTO = {
                canali,
                messaggio: formValue.messaggio
            };

            this.dialogRef.close(request);
        }
    }
}
