import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { ProduttoreProductSummaryDTO } from '@core/models/produttore.models';

@Component({
    selector: 'app-update-quantity-dialog',
    standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        MatDialogModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        MatIconModule
    ],
    template: `
    <h2 mat-dialog-title>
      <mat-icon>inventory</mat-icon>
      Aggiorna Quantità: {{ data.nome }}
    </h2>

    <mat-dialog-content>
      <form [formGroup]="quantityForm" class="quantity-form">
        <p class="current-quantity">
          Quantità attuale: <strong>{{ data.quantitaDisponibile }}</strong>
        </p>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Nuova Quantità Disponibile</mat-label>
          <input matInput type="number" formControlName="quantitaDisponibile" min="0" required>
          <mat-error *ngIf="quantityForm.get('quantitaDisponibile')?.hasError('required')">
            La quantità è obbligatoria
          </mat-error>
          <mat-error *ngIf="quantityForm.get('quantitaDisponibile')?.hasError('min')">
            La quantità deve essere almeno 0
          </mat-error>
        </mat-form-field>
      </form>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">Annulla</button>
      <button mat-raised-button color="primary" 
              [disabled]="!quantityForm.valid || !hasChanges()" 
              (click)="onUpdate()">
        <mat-icon>save</mat-icon>
        Aggiorna
      </button>
    </mat-dialog-actions>
  `,
    styles: [`
    .quantity-form {
      display: flex;
      flex-direction: column;
      gap: 16px;
      min-width: 400px;
      padding: 16px 0;
    }

    .current-quantity {
      font-size: 14px;
      color: #666;
      margin-bottom: 8px;
    }

    .full-width {
      width: 100%;
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
export class UpdateQuantityDialogComponent implements OnInit {
    quantityForm!: FormGroup;

    constructor(
        private fb: FormBuilder,
        private dialogRef: MatDialogRef<UpdateQuantityDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: ProduttoreProductSummaryDTO
    ) { }

    ngOnInit(): void {
        this.quantityForm = this.fb.group({
            quantitaDisponibile: [this.data.quantitaDisponibile, [Validators.required, Validators.min(0)]]
        });
    }

    hasChanges(): boolean {
        return this.quantityForm.value.quantitaDisponibile !== this.data.quantitaDisponibile;
    }

    onUpdate(): void {
        if (this.quantityForm.valid && this.hasChanges()) {
            this.dialogRef.close(this.quantityForm.value);
        }
    }

    onCancel(): void {
        this.dialogRef.close();
    }
}


