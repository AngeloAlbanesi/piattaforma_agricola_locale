import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatRadioModule } from '@angular/material/radio';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { FaseDialogData, ProduttoreSummaryDTO } from '../../../../../core/models/trasformatore.models';
import { TrasformatoreService } from '../../../../../core/services/trasformatore.service';

@Component({
    selector: 'app-fase-dialog',
    standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        MatDialogModule,
        MatButtonModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        MatRadioModule,
        MatProgressSpinnerModule
    ],
    templateUrl: './fase-dialog.component.html',
    styleUrls: ['./fase-dialog.component.scss']
})
export class FaseDialogComponent implements OnInit {
    faseForm: FormGroup;
    isEditMode: boolean = false;
    processoId: number;
    produttori: ProduttoreSummaryDTO[] = [];
    isLoadingProduttori: boolean = false;

    constructor(
        public dialogRef: MatDialogRef<FaseDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: FaseDialogData,
        private fb: FormBuilder,
        private trasformatoreService: TrasformatoreService
    ) {
        this.isEditMode = data.isEditMode;
        this.processoId = data.processoId;
        this.faseForm = this.createForm();
    }

    ngOnInit(): void {
        // Carica lista produttori
        this.loadProduttori();

        if (this.isEditMode && this.data.fase) {
            this.faseForm.patchValue({
                nome: this.data.fase.nome,
                descrizione: this.data.fase.descrizione,
                ordineEsecuzione: this.data.fase.ordineEsecuzione,
                materiaPrimaUtilizzata: this.data.fase.materiaPrimaUtilizzata,
                fonte: {
                    tipo: this.data.fase.fonte.tipo,
                    nomeFornitore: this.data.fase.fonte.nomeFornitore || ''
                }
            });
        }

        // Setup validazione condizionale per nomeFornitore
        this.setupConditionalValidation();
    }

    createForm(): FormGroup {
        return this.fb.group({
            nome: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
            descrizione: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(500)]],
            ordineEsecuzione: [1, [Validators.required, Validators.min(1), Validators.max(100)]],
            materiaPrimaUtilizzata: ['', [Validators.required, Validators.maxLength(200)]],
            fonte: this.fb.group({
                tipo: ['ESTERNA', [Validators.required]],
                nomeFornitore: [''],
                produttoreId: [null]
            })
        });
    }

    setupConditionalValidation(): void {
        const fonteGroup = this.faseForm.get('fonte') as FormGroup;
        const tipoControl = fonteGroup.get('tipo');
        const nomeFornitoreControl = fonteGroup.get('nomeFornitore');
        const produttoreIdControl = fonteGroup.get('produttoreId');

        tipoControl?.valueChanges.subscribe((tipo) => {
            if (tipo === 'ESTERNA') {
                nomeFornitoreControl?.setValidators([Validators.required, Validators.maxLength(100)]);
                produttoreIdControl?.clearValidators();
                produttoreIdControl?.setValue(null);
            } else if (tipo === 'INTERNA') {
                produttoreIdControl?.setValidators([Validators.required]);
                nomeFornitoreControl?.clearValidators();
                nomeFornitoreControl?.setValue('');
            }
            nomeFornitoreControl?.updateValueAndValidity();
            produttoreIdControl?.updateValueAndValidity();
        });
    }

    getErrorMessage(fieldName: string): string {
        const field = this.faseForm.get(fieldName);
        if (!field || !field.errors) return '';

        if (field.errors['required']) {
            return 'Campo obbligatorio';
        }
        if (field.errors['minlength']) {
            const minLength = field.errors['minlength'].requiredLength;
            return `Minimo ${minLength} caratteri`;
        }
        if (field.errors['maxlength']) {
            const maxLength = field.errors['maxlength'].requiredLength;
            return `Massimo ${maxLength} caratteri`;
        }
        if (field.errors['min']) {
            const min = field.errors['min'].min;
            return `Valore minimo: ${min}`;
        }
        if (field.errors['max']) {
            const max = field.errors['max'].max;
            return `Valore massimo: ${max}`;
        }
        return '';
    }

    isFonteEsterna(): boolean {
        return this.faseForm.get('fonte.tipo')?.value === 'ESTERNA';
    }

    loadProduttori(): void {
        this.isLoadingProduttori = true;
        this.trasformatoreService.getAllProduttori().subscribe({
            next: (produttori) => {
                this.produttori = produttori;
                this.isLoadingProduttori = false;
            },
            error: (error) => {
                console.error('Errore nel caricamento dei produttori:', error);
                this.isLoadingProduttori = false;
            }
        });
    }

    getProduttoreLabel(produttore: ProduttoreSummaryDTO): string {
        return `${produttore.nome} ${produttore.cognome} - ${produttore.nomeAzienda || 'N/D'}`;
    }

    onSave(): void {
        if (this.faseForm.valid) {
            this.dialogRef.close(this.faseForm.value);
        } else {
            // Marca tutti i campi come touched per mostrare gli errori
            Object.keys(this.faseForm.controls).forEach(key => {
                this.faseForm.get(key)?.markAsTouched();
            });
            const fonteGroup = this.faseForm.get('fonte') as FormGroup;
            Object.keys(fonteGroup.controls).forEach(key => {
                fonteGroup.get(key)?.markAsTouched();
            });
        }
    }

    onCancel(): void {
        this.dialogRef.close(null);
    }
}
