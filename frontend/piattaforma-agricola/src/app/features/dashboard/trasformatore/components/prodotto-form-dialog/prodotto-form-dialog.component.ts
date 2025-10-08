import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormArray } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ProdottiService } from '@core/services/prodotti.service';
import { TrasformatoreService } from '@core/services/trasformatore.service';
import { ProdottoDTO, CreateProdottoRequestDTO, UpdateProdottoRequestDTO, ProcessoTrasformazioneSummaryDTO } from '@core/models/trasformatore.models';

export interface ProdottoFormDialogData {
    mode: 'create' | 'edit';
    product?: ProdottoDTO;
}

@Component({
    selector: 'app-prodotto-form-dialog',
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
    templateUrl: './prodotto-form-dialog.component.html',
    styleUrls: ['./prodotto-form-dialog.component.scss']
})
export class ProdottoFormDialogComponent implements OnInit {
    productForm: FormGroup;
    isLoading = false;
    isEditMode = false;
    processi: ProcessoTrasformazioneSummaryDTO[] = [];

    constructor(
        private fb: FormBuilder,
        private prodottiService: ProdottiService,
        private trasformatoreService: TrasformatoreService,
        private snackBar: MatSnackBar,
        public dialogRef: MatDialogRef<ProdottoFormDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: ProdottoFormDialogData
    ) {
        this.isEditMode = data.mode === 'edit';
        this.productForm = this.createForm();
    }

    ngOnInit(): void {
        this.loadProcessi();
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
            unitaMisura: ['', Validators.required],
            tipoOrigine: ['TRASFORMATO'],
            processoTrasformazioneId: [null]
        });
    }

    populateForm(product: ProdottoDTO): void {
        this.productForm.patchValue({
            nome: product.nome,
            descrizione: product.descrizione,
            prezzo: product.prezzo,
            quantitaDisponibile: product.quantitaDisponibile,
            unitaMisura: product.unitaMisura,
            processoTrasformazioneId: (product as any).processoTrasformazioneId || null
        });
    }

    loadProcessi(): void {
        this.trasformatoreService.getMyProcesses().subscribe({
            next: (response) => {
                this.processi = response.content || [];
            },
            error: (error) => {
                console.error('Errore nel caricamento dei processi:', error);
                this.snackBar.open('Errore nel caricamento dei processi', 'Chiudi', { duration: 3000 });
            }
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
        const request: CreateProdottoRequestDTO = {
            nome: formValue.nome,
            descrizione: formValue.descrizione,
            prezzo: formValue.prezzo,
            quantitaDisponibile: formValue.quantitaDisponibile,
            unitaMisura: formValue.unitaMisura,
            tipoOrigine: formValue.tipoOrigine || 'TRASFORMATO',
            idProcessoTrasformazioneOriginario: formValue.processoTrasformazioneId || undefined
        };

        this.prodottiService.createProduct(request).subscribe({
            next: () => {
                this.isLoading = false;
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
        const request: UpdateProdottoRequestDTO = {
            nome: formValue.nome,
            descrizione: formValue.descrizione,
            prezzo: formValue.prezzo,
            quantitaDisponibile: formValue.quantitaDisponibile,
            unitaMisura: formValue.unitaMisura
        };

        this.prodottiService.updateProduct(this.data.product.id, request).subscribe({
            next: () => {
                this.isLoading = false;
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
        if (control.hasError('max')) {
            const max = control.getError('max').max;
            return `Valore massimo: ${max}`;
        }
        return '';
    }
}
