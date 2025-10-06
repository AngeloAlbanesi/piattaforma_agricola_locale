import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MetodoDiColtivazioneDTO } from '@core/models/produttore.models';

@Component({
    selector: 'app-edit-method-dialog',
    standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        MatDialogModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        MatIconModule,
        MatDatepickerModule,
        MatNativeDateModule
    ],
    template: `
    <h2 mat-dialog-title>
      <mat-icon>edit</mat-icon>
      Modifica Metodo di Coltivazione
    </h2>

    <mat-dialog-content>
      <form [formGroup]="methodForm" class="method-form">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Nome Metodo</mat-label>
          <input matInput formControlName="nome" placeholder="Es. Coltivazione Biologica" required>
          <mat-error *ngIf="methodForm.get('nome')?.hasError('required')">
            Il nome del metodo è obbligatorio
          </mat-error>
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Descrizione</mat-label>
          <textarea matInput formControlName="descrizione" rows="3" 
                    placeholder="Descrizione del metodo di coltivazione" required></textarea>
          <mat-error *ngIf="methodForm.get('descrizione')?.hasError('required')">
            La descrizione è obbligatoria
          </mat-error>
        </mat-form-field>

        <div class="form-row">
          <mat-form-field appearance="outline" class="half-width">
            <mat-label>Data Inizio</mat-label>
            <input matInput [matDatepicker]="pickerInizio" formControlName="dataInizio" required>
            <mat-datepicker-toggle matSuffix [for]="pickerInizio"></mat-datepicker-toggle>
            <mat-datepicker #pickerInizio></mat-datepicker>
            <mat-error *ngIf="methodForm.get('dataInizio')?.hasError('required')">
              La data di inizio è obbligatoria
            </mat-error>
          </mat-form-field>

          <mat-form-field appearance="outline" class="half-width">
            <mat-label>Data Fine</mat-label>
            <input matInput [matDatepicker]="pickerFine" formControlName="dataFine" required>
            <mat-datepicker-toggle matSuffix [for]="pickerFine"></mat-datepicker-toggle>
            <mat-datepicker #pickerFine></mat-datepicker>
            <mat-error *ngIf="methodForm.get('dataFine')?.hasError('required')">
              La data di fine è obbligatoria
            </mat-error>
          </mat-form-field>
        </div>
      </form>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">Annulla</button>
      <button mat-raised-button color="primary" 
              [disabled]="!methodForm.valid" 
              (click)="onUpdate()">
        <mat-icon>save</mat-icon>
        Salva Modifiche
      </button>
    </mat-dialog-actions>
  `,
    styles: [`
    .method-form {
      display: flex;
      flex-direction: column;
      gap: 16px;
      min-width: 500px;
      padding: 16px 0;
    }

    .full-width {
      width: 100%;
    }

    .form-row {
      display: flex;
      gap: 16px;
    }

    .half-width {
      flex: 1;
    }

    h2 {
      display: flex;
      align-items: center;
      gap: 8px;
    }

    mat-dialog-actions button {
      display: flex;
      align-items: center;
      gap: 4px;
    }
  `]
})
export class EditMethodDialogComponent implements OnInit {
    methodForm!: FormGroup;

    constructor(
        private fb: FormBuilder,
        private dialogRef: MatDialogRef<EditMethodDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: MetodoDiColtivazioneDTO
    ) { }

    ngOnInit(): void {
        // Parse periodo if available (assuming it comes as a string like "2024-03-01 - 2024-09-30")
        const periodo = this.data.periodoColtivazione?.split(' - ') || [];
        const dataInizio = periodo[0] ? new Date(periodo[0]) : new Date();
        const dataFine = periodo[1] ? new Date(periodo[1]) : new Date();

        this.methodForm = this.fb.group({
            nome: [this.data.nome, [Validators.required]],
            descrizione: [this.data.descrizione, [Validators.required]],
            dataInizio: [dataInizio, [Validators.required]],
            dataFine: [dataFine, [Validators.required]]
        });
    }

    onUpdate(): void {
        if (this.methodForm.valid) {
            const formValue = this.methodForm.value;
            const methodData = {
                nome: formValue.nome,
                descrizione: formValue.descrizione,
                dataInizio: this.formatDate(formValue.dataInizio),
                dataFine: this.formatDate(formValue.dataFine)
            };
            this.dialogRef.close(methodData);
        }
    }

    onCancel(): void {
        this.dialogRef.close();
    }

    private formatDate(date: Date): string {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    }
}


