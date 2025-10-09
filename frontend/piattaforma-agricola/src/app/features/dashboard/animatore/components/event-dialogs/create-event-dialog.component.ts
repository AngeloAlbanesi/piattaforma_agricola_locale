import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, AbstractControl, ValidationErrors } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule, DateAdapter, MAT_DATE_LOCALE, MAT_DATE_FORMATS } from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon';
import { CreateEventoRequestDTO } from '../../../../../core/models/animatore.models';

@Component({
    selector: 'app-create-event-dialog',
    standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        MatDialogModule,
        MatButtonModule,
        MatFormFieldModule,
        MatInputModule,
        MatDatepickerModule,
        MatNativeDateModule,
        MatIconModule
    ],
    providers: [
        { provide: MAT_DATE_LOCALE, useValue: 'it-IT' }
    ],
    template: `
    <h2 mat-dialog-title>
      <mat-icon>event</mat-icon>
      Crea Nuovo Evento
    </h2>
    
    <mat-dialog-content class="dialog-content">
      <form [formGroup]="eventForm" class="event-form">
        <!-- Nome Evento -->
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Nome Evento</mat-label>
          <input 
            matInput 
            formControlName="nomeEvento"
            placeholder="Es. Mercato dei Produttori Locali"
          >
          <mat-icon matSuffix>title</mat-icon>
          <mat-hint>Massimo 200 caratteri</mat-hint>
          <mat-error *ngIf="eventForm.get('nomeEvento')?.hasError('required')">
            Il nome dell'evento è obbligatorio
          </mat-error>
          <mat-error *ngIf="eventForm.get('nomeEvento')?.hasError('maxlength')">
            Massimo 200 caratteri
          </mat-error>
        </mat-form-field>

        <!-- Descrizione -->
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Descrizione</mat-label>
          <textarea 
            matInput 
            formControlName="descrizione"
            rows="4"
            placeholder="Descrivi l'evento..."
          ></textarea>
          <mat-icon matSuffix>description</mat-icon>
          <mat-hint>Massimo 1000 caratteri</mat-hint>
          <mat-error *ngIf="eventForm.get('descrizione')?.hasError('maxlength')">
            Massimo 1000 caratteri
          </mat-error>
        </mat-form-field>

        <!-- Data e Ora Inizio -->
        <div class="datetime-row">
          <mat-form-field appearance="outline" class="datetime-field">
            <mat-label>Data Inizio</mat-label>
            <input 
              matInput 
              [matDatepicker]="pickerInizio"
              formControlName="dataInizio"
              placeholder="Seleziona data"
            >
            <mat-datepicker-toggle matSuffix [for]="pickerInizio"></mat-datepicker-toggle>
            <mat-datepicker #pickerInizio></mat-datepicker>
            <mat-error *ngIf="eventForm.get('dataInizio')?.hasError('required')">
              La data di inizio è obbligatoria
            </mat-error>
            <mat-error *ngIf="eventForm.get('dataInizio')?.hasError('pastDate')">
              La data deve essere nel futuro
            </mat-error>
          </mat-form-field>

          <mat-form-field appearance="outline" class="time-field">
            <mat-label>Ora Inizio</mat-label>
            <input 
              matInput 
              type="time"
              formControlName="oraInizio"
              placeholder="HH:MM"
            >
            <mat-icon matSuffix>schedule</mat-icon>
            <mat-error *ngIf="eventForm.get('oraInizio')?.hasError('required')">
              L'ora è obbligatoria
            </mat-error>
          </mat-form-field>
        </div>

        <!-- Data e Ora Fine -->
        <div class="datetime-row">
          <mat-form-field appearance="outline" class="datetime-field">
            <mat-label>Data Fine</mat-label>
            <input 
              matInput 
              [matDatepicker]="pickerFine"
              formControlName="dataFine"
              placeholder="Seleziona data"
            >
            <mat-datepicker-toggle matSuffix [for]="pickerFine"></mat-datepicker-toggle>
            <mat-datepicker #pickerFine></mat-datepicker>
            <mat-error *ngIf="eventForm.get('dataFine')?.hasError('required')">
              La data di fine è obbligatoria
            </mat-error>
            <mat-error *ngIf="eventForm.get('dataFine')?.hasError('pastDate')">
              La data deve essere nel futuro
            </mat-error>
          </mat-form-field>

          <mat-form-field appearance="outline" class="time-field">
            <mat-label>Ora Fine</mat-label>
            <input 
              matInput 
              type="time"
              formControlName="oraFine"
              placeholder="HH:MM"
            >
            <mat-icon matSuffix>schedule</mat-icon>
            <mat-error *ngIf="eventForm.get('oraFine')?.hasError('required')">
              L'ora è obbligatoria
            </mat-error>
          </mat-form-field>
        </div>

        <!-- Errore di validazione date -->
        <div class="error-message" *ngIf="eventForm.hasError('dateRange')">
          <mat-icon>error</mat-icon>
          <span>La data/ora di fine deve essere successiva alla data/ora di inizio</span>
        </div>

        <!-- Luogo Evento -->
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Luogo Evento</mat-label>
          <input 
            matInput 
            formControlName="luogoEvento"
            placeholder="Es. Piazza del Popolo, Camerino (MC)"
          >
          <mat-icon matSuffix>location_on</mat-icon>
          <mat-hint>Massimo 255 caratteri</mat-hint>
          <mat-error *ngIf="eventForm.get('luogoEvento')?.hasError('required')">
            Il luogo è obbligatorio
          </mat-error>
          <mat-error *ngIf="eventForm.get('luogoEvento')?.hasError('maxlength')">
            Massimo 255 caratteri
          </mat-error>
        </mat-form-field>

        <!-- Capienza Massima -->
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Capienza Massima</mat-label>
          <input 
            matInput 
            type="number"
            formControlName="capienzaMassima"
            placeholder="Es. 100"
          >
          <mat-icon matSuffix>people</mat-icon>
          <mat-hint>Numero massimo di partecipanti (1-1000)</mat-hint>
          <mat-error *ngIf="eventForm.get('capienzaMassima')?.hasError('required')">
            La capienza massima è obbligatoria
          </mat-error>
          <mat-error *ngIf="eventForm.get('capienzaMassima')?.hasError('min')">
            Minimo 1 partecipante
          </mat-error>
          <mat-error *ngIf="eventForm.get('capienzaMassima')?.hasError('max')">
            Massimo 1000 partecipanti
          </mat-error>
        </mat-form-field>
      </form>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">Annulla</button>
      <button 
        mat-raised-button 
        color="primary" 
        (click)="onCreate()"
        [disabled]="!eventForm.valid"
      >
        <mat-icon>add</mat-icon>
        Crea Evento
      </button>
    </mat-dialog-actions>
  `,
    styles: [`
    .dialog-content {
      min-width: 500px;
      max-width: 600px;
      max-height: 70vh;
      overflow-y: auto;
      padding: 20px;
    }

    .event-form {
      display: flex;
      flex-direction: column;
      gap: 12px;
    }

    .full-width {
      width: 100%;
    }

    .datetime-row {
      display: flex;
      gap: 16px;
      align-items: flex-start;

      .datetime-field {
        flex: 2;
      }

      .time-field {
        flex: 1;
      }
    }

    .error-message {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 12px;
      background: #ffebee;
      border-radius: 4px;
      color: #c62828;
      font-size: 14px;

      mat-icon {
        font-size: 20px;
        width: 20px;
        height: 20px;
      }
    }

    h2 mat-icon {
      vertical-align: middle;
      margin-right: 8px;
    }

    mat-dialog-actions {
      padding: 16px 24px;

      button mat-icon {
        margin-right: 8px;
      }
    }
  `]
})
export class CreateEventDialogComponent {
    eventForm: FormGroup;

    constructor(
        public dialogRef: MatDialogRef<CreateEventDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: any,
        private fb: FormBuilder
    ) {
        this.eventForm = this.fb.group({
            nomeEvento: ['', [Validators.required, Validators.maxLength(200)]],
            descrizione: ['', [Validators.maxLength(1000)]],
            dataInizio: ['', [Validators.required, this.futureDateValidator]],
            oraInizio: ['', Validators.required],
            dataFine: ['', [Validators.required, this.futureDateValidator]],
            oraFine: ['', Validators.required],
            luogoEvento: ['', [Validators.required, Validators.maxLength(255)]],
            capienzaMassima: ['', [Validators.required, Validators.min(1), Validators.max(1000)]]
        }, { validators: this.dateRangeValidator });
    }

    /**
     * Validator to ensure date is in the future
     */
    futureDateValidator(control: AbstractControl): ValidationErrors | null {
        if (!control.value) {
            return null;
        }

        const selectedDate = new Date(control.value);
        const today = new Date();
        today.setHours(0, 0, 0, 0);

        if (selectedDate < today) {
            return { pastDate: true };
        }

        return null;
    }

    /**
     * Validator to ensure end date/time is after start date/time
     */
    dateRangeValidator(group: AbstractControl): ValidationErrors | null {
        const dataInizio = group.get('dataInizio')?.value;
        const oraInizio = group.get('oraInizio')?.value;
        const dataFine = group.get('dataFine')?.value;
        const oraFine = group.get('oraFine')?.value;

        if (!dataInizio || !oraInizio || !dataFine || !oraFine) {
            return null;
        }

        // Combine date and time inline
        const combineDateTime = (date: Date, time: string): Date => {
            const [hours, minutes] = time.split(':').map(Number);
            const combined = new Date(date);
            combined.setHours(hours, minutes, 0, 0);
            return combined;
        };

        const dateTimeInizio = combineDateTime(dataInizio, oraInizio);
        const dateTimeFine = combineDateTime(dataFine, oraFine);

        if (dateTimeFine <= dateTimeInizio) {
            return { dateRange: true };
        }

        return null;
    }

    /**
     * Combine date and time into a single Date object
     */
    private combineDateTime(date: Date, time: string): Date {
        const [hours, minutes] = time.split(':').map(Number);
        const combined = new Date(date);
        combined.setHours(hours, minutes, 0, 0);
        return combined;
    }

    onCancel(): void {
        this.dialogRef.close();
    }

    onCreate(): void {
        if (this.eventForm.valid) {
            const formValue = this.eventForm.value;

            // Combine date and time for both start and end
            const dataOraInizio = this.combineDateTime(formValue.dataInizio, formValue.oraInizio);
            const dataOraFine = this.combineDateTime(formValue.dataFine, formValue.oraFine);

            // Format dates as "dd-MM-yyyy/HH-mm" for backend
            const formatDateForBackend = (date: Date): string => {
                const day = date.getDate().toString().padStart(2, '0');
                const month = (date.getMonth() + 1).toString().padStart(2, '0');
                const year = date.getFullYear();
                const hours = date.getHours().toString().padStart(2, '0');
                const minutes = date.getMinutes().toString().padStart(2, '0');
                return `${day}-${month}-${year}/${hours}-${minutes}`;
            };

            const request: CreateEventoRequestDTO = {
                nomeEvento: formValue.nomeEvento,
                descrizione: formValue.descrizione || '',
                dataOraInizio: formatDateForBackend(dataOraInizio),
                dataOraFine: formatDateForBackend(dataOraFine),
                luogoEvento: formValue.luogoEvento,
                capienzaMassima: Number(formValue.capienzaMassima)
            };

            this.dialogRef.close(request);
        }
    }
}

