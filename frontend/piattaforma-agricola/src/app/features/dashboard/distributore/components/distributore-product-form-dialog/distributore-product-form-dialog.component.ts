import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { DistributoreService } from '@core/services/distributore.service';
import { CreateDistributoreProductRequestDTO } from '@core/models/distributore.models';

export interface DistributoreProductFormDialogData {
  mode: 'create' | 'edit';
  product?: any;
}

@Component({
  selector: 'app-distributore-product-form-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  templateUrl: './distributore-product-form-dialog.component.html',
  styleUrls: ['./distributore-product-form-dialog.component.scss']
})
export class DistributoreProductFormDialogComponent implements OnInit {
  productForm: FormGroup;
  isLoading = false;
  isEditMode = false;

  constructor(
    private fb: FormBuilder,
    private distributoreService: DistributoreService,
    private snackBar: MatSnackBar,
    public dialogRef: MatDialogRef<DistributoreProductFormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DistributoreProductFormDialogData
  ) {
    this.isEditMode = data.mode === 'edit';
    this.productForm = this.createForm();
  }

  ngOnInit(): void {
    if (this.isEditMode && this.data.product) {
      this.populateForm(this.data.product);
    }
  }

  createForm(): FormGroup {
    return this.fb.group({
      nome: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      descrizione: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(500)]],
      prezzo: ['', [Validators.required, Validators.min(0.01)]],
      quantitaDisponibile: ['', [Validators.required, Validators.min(0)]],
      unitaMisura: ['KG', Validators.required],
      tipoOrigine: ['COLTIVATO', Validators.required],
      immagineUrl: ['']
    });
  }

  populateForm(product: any): void {
    this.productForm.patchValue({
      nome: product.nome,
      descrizione: product.descrizione,
      prezzo: product.prezzo,
      quantitaDisponibile: product.quantitaDisponibile,
      unitaMisura: product.unitaMisura,
      tipoOrigine: product.tipoOrigine || 'COLTIVATO',
      immagineUrl: product.immagineUrl || ''
    });
  }

  onSubmit(): void {
    if (this.productForm.invalid) {
      this.productForm.markAllAsTouched();
      this.snackBar.open('Compila tutti i campi obbligatori', 'Chiudi', { duration: 3000 });
      return;
    }

    this.isLoading = true;

    if (this.isEditMode && this.data.product) {
      this.updateProduct();
    } else {
      this.createProduct();
    }
  }

  createProduct(): void {
    const formValue = this.productForm.value;
    const request: CreateDistributoreProductRequestDTO = {
      nome: formValue.nome,
      descrizione: formValue.descrizione,
      prezzo: formValue.prezzo,
      quantitaDisponibile: formValue.quantitaDisponibile,
      unitaMisura: formValue.unitaMisura,
      tipoOrigine: formValue.tipoOrigine,
      immagineUrl: formValue.immagineUrl || undefined
    };

    this.distributoreService.createProduct(request).subscribe({
      next: () => {
        this.isLoading = false;
        this.snackBar.open('Prodotto creato con successo', 'Chiudi', { duration: 3000 });
        this.dialogRef.close(true);
      },
      error: (error) => {
        console.error('Errore nella creazione del prodotto:', error);
        this.snackBar.open('Errore nella creazione del prodotto', 'Chiudi', { duration: 3000 });
        this.isLoading = false;
      }
    });
  }

  updateProduct(): void {
    if (!this.data.product) return;

    const formValue = this.productForm.value;
    const request = {
      nome: formValue.nome,
      descrizione: formValue.descrizione,
      prezzo: formValue.prezzo,
      quantitaDisponibile: formValue.quantitaDisponibile,
      unitaMisura: formValue.unitaMisura,
      tipoOrigine: formValue.tipoOrigine,
      immagineUrl: formValue.immagineUrl || undefined
    };

    this.distributoreService.updateProduct(this.data.product.id, request).subscribe({
      next: () => {
        this.isLoading = false;
        this.snackBar.open('Prodotto aggiornato con successo', 'Chiudi', { duration: 3000 });
        this.dialogRef.close(true);
      },
      error: (error) => {
        console.error('Errore nell\'aggiornamento del prodotto:', error);
        this.snackBar.open('Errore nell\'aggiornamento del prodotto', 'Chiudi', { duration: 3000 });
        this.isLoading = false;
      }
    });
  }

  onCancel(): void {
    this.dialogRef.close(false);
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