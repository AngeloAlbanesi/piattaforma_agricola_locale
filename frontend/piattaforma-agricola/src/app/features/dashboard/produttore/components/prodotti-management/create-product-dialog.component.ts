import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
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
        MatSelectModule,
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
        <!-- Nome -->
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Nome Prodotto</mat-label>
          <input matInput formControlName="nome" placeholder="Es. Pomodori San Marzano" required>
          <mat-icon matPrefix>label</mat-icon>
          @if (productForm.get('nome')?.hasError('required') && productForm.get('nome')?.touched) {
          <mat-error>{{ getErrorMessage('nome') }}</mat-error>
          }
          @if (productForm.get('nome')?.hasError('minlength')) {
          <mat-error>{{ getErrorMessage('nome') }}</mat-error>
          }
          @if (productForm.get('nome')?.hasError('maxlength')) {
          <mat-error>{{ getErrorMessage('nome') }}</mat-error>
          }
          <mat-hint>{{ productForm.get('nome')?.value?.length || 0 }}/100</mat-hint>
        </mat-form-field>

        <!-- Descrizione -->
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Descrizione</mat-label>
          <textarea matInput formControlName="descrizione"
                    placeholder="Descrizione dettagliata del prodotto, caratteristiche, origine..." rows="4" required></textarea>
          <mat-icon matPrefix>description</mat-icon>
          @if (productForm.get('descrizione')?.hasError('required') && productForm.get('descrizione')?.touched) {
          <mat-error>{{ getErrorMessage('descrizione') }}</mat-error>
          }
          @if (productForm.get('descrizione')?.hasError('minlength')) {
          <mat-error>{{ getErrorMessage('descrizione') }}</mat-error>
          }
          @if (productForm.get('descrizione')?.hasError('maxlength')) {
          <mat-error>{{ getErrorMessage('descrizione') }}</mat-error>
          }
          <mat-hint>{{ productForm.get('descrizione')?.value?.length || 0 }}/500</mat-hint>
        </mat-form-field>

        <!-- Prezzo -->
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Prezzo (€)</mat-label>
          <input matInput type="number" formControlName="prezzo" placeholder="0.00" step="0.01" min="0" required>
          <mat-icon matPrefix>euro</mat-icon>
          @if (productForm.get('prezzo')?.hasError('required') && productForm.get('prezzo')?.touched) {
          <mat-error>{{ getErrorMessage('prezzo') }}</mat-error>
          }
          @if (productForm.get('prezzo')?.hasError('min')) {
          <mat-error>{{ getErrorMessage('prezzo') }}</mat-error>
          }
          <mat-hint>Prezzo al pubblico</mat-hint>
        </mat-form-field>

        <!-- Quantità e Unità di Misura -->
        <div class="form-row">
          <mat-form-field appearance="outline" class="half-width">
            <mat-label>Quantità Disponibile</mat-label>
            <input matInput type="number" formControlName="quantitaDisponibile" placeholder="0" min="0" required>
            <mat-icon matPrefix>inventory</mat-icon>
            @if (productForm.get('quantitaDisponibile')?.hasError('required') &&
            productForm.get('quantitaDisponibile')?.touched) {
            <mat-error>{{ getErrorMessage('quantitaDisponibile') }}</mat-error>
            }
            @if (productForm.get('quantitaDisponibile')?.hasError('min')) {
            <mat-error>{{ getErrorMessage('quantitaDisponibile') }}</mat-error>
            }
            <mat-hint>Unità disponibili</mat-hint>
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
            @if (productForm.get('unitaMisura')?.hasError('required') && productForm.get('unitaMisura')?.touched) {
            <mat-error>{{ getErrorMessage('unitaMisura') }}</mat-error>
            }
          </mat-form-field>
        </div>

        <!-- Tipo Origine -->
        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Tipo Origine</mat-label>
          <mat-select formControlName="tipoOrigine" required>
            <mat-option [value]="TipoOrigineProdotto.COLTIVATO">Coltivato</mat-option>
            <mat-option [value]="TipoOrigineProdotto.TRASFORMATO">Trasformato</mat-option>
            <mat-option [value]="TipoOrigineProdotto.ARTIGIANALE">Artigianale</mat-option>
          </mat-select>
          @if (productForm.get('tipoOrigine')?.hasError('required') && productForm.get('tipoOrigine')?.touched) {
          <mat-error>Il tipo origine è obbligatorio</mat-error>
          }
          <mat-hint>Scegli la categoria del prodotto</mat-hint>
        </mat-form-field>

        <!-- Metodi e Processi (sezione opzionale) -->
        <div class="section-divider">
          <mat-icon>settings</mat-icon>
          <span>Informazioni Aggiuntive (Opzionale)</span>
        </div>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Metodo di Coltivazione (opzionale)</mat-label>
          <mat-select formControlName="idMetodoDiColtivazione">
            <mat-option value="">Nessun metodo specificato</mat-option>
            <!-- Le opzioni verrebbero caricate dinamicamente -->
            <mat-option value="1">Biologico</mat-option>
            <mat-option value="2">Convenzionale</mat-option>
            <mat-option value="3">Integrato</mat-option>
          </mat-select>
          <mat-icon matPrefix>eco</mat-icon>
          <mat-hint>Collega questo prodotto a un metodo di coltivazione</mat-hint>
        </mat-form-field>

        <mat-form-field appearance="outline" class="full-width">
          <mat-label>Processo di Trasformazione (opzionale)</mat-label>
          <mat-select formControlName="idProcessoTrasformazioneOriginario">
            <mat-option value="">Nessun processo specificato</mat-option>
            <!-- Le opzioni verrebbero caricate dinamicamente -->
            <mat-option value="1">Confezionamento Manuale</mat-option>
            <mat-option value="2">Processo Artigianale</mat-option>
            <mat-option value="3">Conservazione Naturale</mat-option>
          </mat-select>
          <mat-icon matPrefix>precision_manufacturing</mat-icon>
          <mat-hint>Collega questo prodotto a un processo di trasformazione</mat-hint>
        </mat-form-field>
      </form>
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">
        <mat-icon>close</mat-icon>
        Annulla
      </button>
      <button mat-raised-button color="primary" 
              [disabled]="!productForm.valid" 
              (click)="onCreate()">
        <mat-icon>add_shopping_cart</mat-icon>
        Crea Prodotto
      </button>
    </mat-dialog-actions>
  `,
    styles: [`
    .product-form {
      display: flex;
      flex-direction: column;
      gap: 16px;
      padding: 0;
      margin: 0;
    }

    .full-width {
      width: 100%;
    }

    .form-row {
      display: flex;
      gap: 16px;
      align-items: flex-start;
    }

    .half-width {
      flex: 1;
    }

    .section-divider {
      display: flex;
      align-items: center;
      gap: 8px;
      margin: 16px 0 12px 0;
      padding: 8px 0;
      border-top: 1px solid #e0e0e0;
      color: #666;
      font-size: 0.8rem;
      font-weight: 500;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }

    h2 {
      display: flex;
      align-items: center;
      gap: 12px;
      color: #4CAF50;
      margin: 0;
      font-size: 1.25rem;
      font-weight: 500;
    }

    ::ng-deep .mat-dialog-container {
      border-radius: 12px;
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
      overflow: hidden;
      max-width: 90vw;
      max-height: 90vh;
      width: auto;
    }

    ::ng-deep .mat-dialog-content {
      padding: 20px 24px;
      margin: 0;
      overflow-y: auto;
      max-height: 60vh;
    }

    ::ng-deep .mat-dialog-actions {
      padding: 16px 24px;
      display: flex;
      justify-content: flex-end;
      gap: 12px;
      border-top: 1px solid #e0e0e0;
      margin: 0;
    }

    ::ng-deep .mat-dialog-title {
      padding: 20px 24px 16px 24px;
      margin: 0;
      border-bottom: 1px solid #e0e0e0;
    }

    ::ng-deep mat-dialog-actions button {
      display: flex;
      align-items: center;
      gap: 6px;
      min-width: 100px;
      padding: 6px 12px;
      border-radius: 6px;
      font-weight: 500;
      transition: all 0.3s ease;
      font-size: 0.875rem;
    }

    ::ng-deep mat-dialog-actions button[mat-raised-button] {
      background: linear-gradient(135deg, #4CAF50, #45a049);
      color: white;
      box-shadow: 0 2px 4px rgba(76, 175, 80, 0.3);
    }

    ::ng-deep mat-dialog-actions button[mat-raised-button]:hover:not(:disabled) {
      background: linear-gradient(135deg, #45a049, #3d8b40);
      box-shadow: 0 4px 8px rgba(76, 175, 80, 0.4);
      transform: translateY(-1px);
    }

    ::ng-deep mat-dialog-actions button[mat-button] {
      background: #f5f5f5;
      color: #666;
    }

    ::ng-deep mat-dialog-actions button[mat-button]:hover {
      background: #eeeeee;
      color: #333;
    }

    mat-form-field {
      margin-bottom: 8px;
    }

    ::ng-deep mat-form-field.mat-focused .mat-form-field-outline {
      border-color: #4CAF50;
    }

    ::ng-deep mat-form-field.mat-focused .mat-form-field-label {
      color: #4CAF50;
    }

    ::ng-deep mat-icon[matPrefix] {
      color: #4CAF50;
      font-size: 18px;
      width: 18px;
      height: 18px;
    }

    .section-divider mat-icon {
      color: #666;
      font-size: 14px;
      width: 14px;
      height: 14px;
    }

    ::ng-deep .mat-mdc-form-field-flex {
      margin-top: 4px;
    }

    ::ng-deep .mat-mdc-text-field-wrapper {
      margin-bottom: 4px;
    }

    /* Ridurre spazi eccessivi */
    ::ng-deep .mat-mdc-form-field-infix {
      padding-top: 8px !important;
      padding-bottom: 8px !important;
    }

    ::ng-deep .mat-mdc-form-field-subscript-wrapper {
      margin-top: 0 !important;
    }

    ::ng-deep .mat-mdc-form-field-hint-wrapper {
      padding-top: 2px !important;
    }

    /* Stili per campi opzionali */
    mat-form-field:has(mat-select) ::ng-deep .mat-mdc-form-field-hint {
      color: #666;
      font-style: italic;
      font-size: 0.75rem;
    }

    /* Stili per placeholder */
    ::ng-deep input::placeholder, 
    ::ng-deep textarea::placeholder {
      color: #999;
      font-style: italic;
      opacity: 0.8;
    }

    /* Media queries per schermi più piccoli */
    @media (max-width: 768px) {
      ::ng-deep .mat-dialog-container {
        max-width: 95vw;
        max-height: 95vh;
      }

      ::ng-deep .mat-dialog-content {
        padding: 16px;
        max-height: 65vh;
      }

      ::ng-deep .mat-dialog-title {
        padding: 16px;
      }

      ::ng-deep .mat-dialog-actions {
        padding: 12px 16px;
        flex-direction: column-reverse;
        align-items: stretch;
      }

      ::ng-deep .mat-dialog-actions button {
        width: 100%;
      }

      .form-row {
        flex-direction: column;
        gap: 12px;
      }

      .half-width {
        flex: none;
        width: 100%;
      }
    }

    @media (max-width: 480px) {
      ::ng-deep .mat-dialog-container {
        max-width: 98vw;
        max-height: 98vh;
      }

      .product-form {
        gap: 12px;
      }
    }

    /* Animazioni */
    mat-form-field {
      animation: fadeInUp 0.3s ease-out;
    }

    @keyframes fadeInUp {
      from {
        opacity: 0;
        transform: translateY(10px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }

    /* Scrollbar styling */
    ::ng-deep .mat-dialog-content::-webkit-scrollbar {
      width: 6px;
    }

    ::ng-deep .mat-dialog-content::-webkit-scrollbar-track {
      background: #f1f1f1;
      border-radius: 3px;
    }

    ::ng-deep .mat-dialog-content::-webkit-scrollbar-thumb {
      background: #c1c1c1;
      border-radius: 3px;
    }

    ::ng-deep .mat-dialog-content::-webkit-scrollbar-thumb:hover {
      background: #a8a8a8;
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
            unitaMisura: ['KG', [Validators.required]],
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

    getErrorMessage(fieldName: string): string {
        const control = this.productForm.get(fieldName);
        if (!control) return '';

        if (control.hasError('required')) {
            return 'Questo campo è obbligatorio';
        }
        if (control.hasError('minlength')) {
            const minLength = control.getError('minlength').requiredLength;
            return `Minimo ${minLength} caratteri`;
        }
        if (control.hasError('maxlength')) {
            const maxLength = control.getError('maxlength').requiredLength;
            return `Massimo ${maxLength} caratteri`;
        }
        if (control.hasError('min')) {
            const min = control.getError('min').min;
            return `Valore minimo: ${min}`;
        }
        return '';
    }
}





