import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { TipoOrigineProdotto } from '@core/models/produttore.models';

@Component({
    selector: 'app-create-product-dialog',
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
      <mat-icon>add_shopping_cart</mat-icon>
      Crea Nuovo Prodotto
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
            <mat-label>Quantità Disponibile</mat-label>
            <input matInput type="number" formControlName="quantitaDisponibile" min="0" required>
            <mat-error *ngIf="productForm.get('quantitaDisponibile')?.hasError('required')">
              La quantità è obbligatoria
            </mat-error>
            <mat-error *ngIf="productForm.get('quantitaDisponibile')?.hasError('min')">
              La quantità deve essere almeno 0
            </mat-error>
          </mat-form-field>
        </div>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Tipo Origine</mat-label>
          <mat-select formControlName="tipoOrigine" required>
            <mat-option [value]="TipoOrigineProdotto.COLTIVATO">Coltivato</mat-option>
            <mat-option [value]="TipoOrigineProdotto.TRASFORMATO">Trasformato</mat-option>
            <mat-option [value]="TipoOrigineProdotto.ARTIGIANALE">Artigianale</mat-option>
          </mat-select>
          <mat-error *ngIf="productForm.get('tipoOrigine')?.hasError('required')">
            Il tipo origine è obbligatorio
          </mat-error>
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>ID Metodo di Coltivazione (opzionale)</mat-label>
          <input matInput type="number" formControlName="idMetodoDiColtivazione" min="1">
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>ID Processo di Trasformazione (opzionale)</mat-label>
          <input matInput type="number" formControlName="idProcessoTrasformazioneOriginario" min="1">
        </mat-form-field>
      </form>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">Annulla</button>
      <button mat-raised-button color="primary" 
              [disabled]="!productForm.valid" 
              (click)="onCreate()">
        <mat-icon>save</mat-icon>
        Crea Prodotto
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
export class CreateProductDialogComponent implements OnInit {
    productForm!: FormGroup;
    TipoOrigineProdotto = TipoOrigineProdotto;

    constructor(
        private fb: FormBuilder,
        private dialogRef: MatDialogRef<CreateProductDialogComponent>
    ) { }

    ngOnInit(): void {
        this.productForm = this.fb.group({
            nome: ['', [Validators.required]],
            descrizione: ['', [Validators.required]],
            prezzo: [0, [Validators.required, Validators.min(0.01)]],
            quantitaDisponibile: [0, [Validators.required, Validators.min(0)]],
            tipoOrigine: [TipoOrigineProdotto.COLTIVATO, [Validators.required]],
            idMetodoDiColtivazione: [null],
            idProcessoTrasformazioneOriginario: [null]
        });
    }

    onCreate(): void {
        if (this.productForm.valid) {
            const formValue = this.productForm.value;
            // Remove null values for optional fields
            const productData = {
                ...formValue,
                idMetodoDiColtivazione: formValue.idMetodoDiColtivazione || undefined,
                idProcessoTrasformazioneOriginario: formValue.idProcessoTrasformazioneOriginario || undefined
            };
            this.dialogRef.close(productData);
        }
    }

    onCancel(): void {
        this.dialogRef.close();
    }
}

