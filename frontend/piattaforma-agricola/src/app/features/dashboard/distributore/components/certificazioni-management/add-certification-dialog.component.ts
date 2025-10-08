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

@Component({
    selector: 'app-distributore-add-certification-dialog',
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
      <mat-icon>verified</mat-icon>
      Aggiungi Certificazione al Prodotto
    </h2>

    <mat-dialog-content>
      <form [formGroup]="certificationForm" class="certification-form">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Nome Certificazione</mat-label>
          <input matInput formControlName="nomeCertificazione" placeholder="Es. Biologico UE" required>
          <mat-error *ngIf="certificationForm.get('nomeCertificazione')?.hasError('required')">
            Il nome della certificazione è obbligatorio
          </mat-error>
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Ente Rilascio</mat-label>
          <input matInput formControlName="enteRilascio" placeholder="Es. ICEA" required>
          <mat-error *ngIf="certificationForm.get('enteRilascio')?.hasError('required')">
            L'ente di rilascio è obbligatorio
          </mat-error>
        </mat-form-field>

        <div class="form-row">
          <mat-form-field appearance="outline" class="half-width">
            <mat-label>Data Rilascio</mat-label>
            <input matInput [matDatepicker]="pickerRilascio" formControlName="dataRilascio" required>
            <mat-datepicker-toggle matSuffix [for]="pickerRilascio"></mat-datepicker-toggle>
            <mat-datepicker #pickerRilascio></mat-datepicker>
            <mat-error *ngIf="certificationForm.get('dataRilascio')?.hasError('required')">
              La data di rilascio è obbligatoria
            </mat-error>
          </mat-form-field>

          <mat-form-field appearance="outline" class="half-width">
            <mat-label>Data Scadenza</mat-label>
            <input matInput [matDatepicker]="pickerScadenza" formControlName="dataScadenza" required>
            <mat-datepicker-toggle matSuffix [for]="pickerScadenza"></mat-datepicker-toggle>
            <mat-datepicker #pickerScadenza></mat-datepicker>
            <mat-error *ngIf="certificationForm.get('dataScadenza')?.hasError('required')">
              La data di scadenza è obbligatoria
            </mat-error>
          </mat-form-field>
        </div>
      </form>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">Annulla</button>
      <button mat-raised-button color="primary" 
              [disabled]="!certificationForm.valid" 
              (click)="onAdd()">
        <mat-icon>save</mat-icon>
        Aggiungi Certificazione
      </button>
    </mat-dialog-actions>
  `,
    styles: [`
    .certification-form {
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
export class AddCertificationDialogComponent implements OnInit {
    certificationForm!: FormGroup;

    constructor(
        private fb: FormBuilder,
        private dialogRef: MatDialogRef<AddCertificationDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: { productId: number; productName: string }
    ) { }

    ngOnInit(): void {
        this.certificationForm = this.fb.group({
            nomeCertificazione: ['', [Validators.required]],
            enteRilascio: ['', [Validators.required]],
            dataRilascio: ['', [Validators.required]],
            dataScadenza: ['', [Validators.required]]
        });
    }

    onAdd(): void {
        if (this.certificationForm.valid) {
            const formValue = this.certificationForm.value;
            // Format dates to YYYY-MM-DD
            const certificationData = {
                nomeCertificazione: formValue.nomeCertificazione,
                enteRilascio: formValue.enteRilascio,
                dataRilascio: this.formatDate(formValue.dataRilascio),
                dataScadenza: this.formatDate(formValue.dataScadenza)
            };
            this.dialogRef.close(certificationData);
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
