import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { ProduttoreProductSummaryDTO } from '@core/models/produttore.models';

@Component({
    selector: 'app-edit-product-dialog',
    standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        MatDialogModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        MatSelectModule,
        MatIconModule
    ],
    template: `
    <h2 mat-dialog-title>
      <mat-icon>edit</mat-icon>
      Modifica Prodotto: {{ data.nome }}
    </h2>

    <mat-dialog-content>
      <form [formGroup]="productForm" class="product-form">
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Nome Prodotto</mat-label>
          <input matInput formControlName="nome" placeholder="Es. Pomodori San Marzano" required>
          <mat-error *ngIf="productForm.get('nome')?.hasError('required')">
            Il nome è obbligatorio
          </mat-error>
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Descrizione</mat-label>
          <textarea matInput formControlName="descrizione" rows="3" 
                    placeholder="Descrizione dettagliata del prodotto" required></textarea>
          <mat-error *ngIf="productForm.get('descrizione')?.hasError('required')">
            La descrizione è obbligatoria
          </mat-error>
        </mat-form-field>

        <div class="form-row">
          <mat-form-field appearance="outline" class="half-width">
            <mat-label>Prezzo (€)</mat-label>
            <input matInput type="number" formControlName="prezzo" step="0.01" min="0" required>
            <mat-error *ngIf="productForm.get('prezzo')?.hasError('required')">
              Il prezzo è obbligatorio
            </mat-error>
            <mat-error *ngIf="productForm.get('prezzo')?.hasError('min')">
              Il prezzo deve essere maggiore di 0
            </mat-error>
          </mat-form-field>

          <mat-form-field appearance="outline" class="half-width">
            <!-- Spazio vuoto per bilanciare il layout -->
          </mat-form-field>
        </div>

        <div class="form-row">
          <mat-form-field appearance="outline" class="half-width">
            <mat-label>Quantità Disponibile</mat-label>
            <input matInput type="number" formControlName="quantitaDisponibile" min="0" required>
            <mat-error *ngIf="productForm.get('quantitaDisponibile')?.hasError('required')">
              La quantità è obbligatoria
            </mat-error>
            <mat-error *ngIf="productForm.get('quantitaDisponibile')?.hasError('min')">
              La quantità deve essere almeno 0
            </mat-error>
          </mat-form-field>

          <mat-form-field appearance="outline" class="half-width">
            <mat-label>Unità di Misura</mat-label>
            <mat-select formControlName="unitaMisura" required>
              <mat-option value="KG">Chilogrammi (kg)</mat-option>
              <mat-option value="G">Grammi (g)</mat-option>
              <mat-option value="L">Litri (l)</mat-option>
              <mat-option value="ML">Millilitri (ml)</mat-option>
              <mat-option value="BOTTIGLIE">Bottiglie</mat-option>
              <mat-option value="CONFEZIONI">Confezioni</mat-option>
              <mat-option value="PEZZI">Pezzi</mat-option>
              <mat-option value="PACCHI">Pacchi</mat-option>
            </mat-select>
            <mat-error *ngIf="productForm.get('unitaMisura')?.hasError('required')">
              L&apos;unità di misura è obbligatoria
            </mat-error>
          </mat-form-field>
        </div>
      </form>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">Annulla</button>
      <button mat-raised-button color="primary" 
              [disabled]="!productForm.valid || !hasChanges()" 
              (click)="onUpdate()">
        <mat-icon>save</mat-icon>
        Salva Modifiche
      </button>
    </mat-dialog-actions>
  `,
    styles: [`
    .product-form {
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
export class EditProductDialogComponent implements OnInit {
    productForm!: FormGroup;

    constructor(
        private fb: FormBuilder,
        private dialogRef: MatDialogRef<EditProductDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: ProduttoreProductSummaryDTO
    ) { }

    ngOnInit(): void {
        this.productForm = this.fb.group({
            nome: [this.data.nome, [Validators.required]],
            descrizione: [this.data.descrizione, [Validators.required]],
            prezzo: [this.data.prezzo, [Validators.required, Validators.min(0.01)]],
            quantitaDisponibile: [this.data.quantitaDisponibile, [Validators.required, Validators.min(0)]],
            unitaMisura: [this.data.unitaMisura || 'KG', [Validators.required]]
        });
    }

    hasChanges(): boolean {
        const formValue = this.productForm.value;
        return formValue.nome !== this.data.nome ||
            formValue.descrizione !== this.data.descrizione ||
            formValue.prezzo !== this.data.prezzo ||
            formValue.quantitaDisponibile !== this.data.quantitaDisponibile ||
            formValue.unitaMisura !== (this.data.unitaMisura || 'KG');
    }

    onUpdate(): void {
        if (this.productForm.valid && this.hasChanges()) {
            this.dialogRef.close(this.productForm.value);
        }
    }

    onCancel(): void {
        this.dialogRef.close();
    }
}





