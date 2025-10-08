import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ProcessoDialogData } from '../../../../../core/models/trasformatore.models';

@Component({
    selector: 'app-processo-dialog',
    standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        MatDialogModule,
        MatButtonModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        MatProgressSpinnerModule
    ],
    templateUrl: './processo-dialog.component.html',
    styleUrls: ['./processo-dialog.component.scss']
})
export class ProcessoDialogComponent implements OnInit {
    processoForm: FormGroup;
    isEditMode: boolean = false;

    constructor(
        public dialogRef: MatDialogRef<ProcessoDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: ProcessoDialogData,
        private fb: FormBuilder
    ) {
        this.isEditMode = data.isEditMode;
        this.processoForm = this.createForm();
    }

    ngOnInit(): void {
        if (this.isEditMode && this.data.processo) {
            this.processoForm.patchValue({
                nome: this.data.processo.nome,
                descrizione: this.data.processo.descrizione
            });
        }
    }

    createForm(): FormGroup {
        return this.fb.group({
            nome: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
            descrizione: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(500)]],
            metodoProduzione: ['', [Validators.maxLength(200)]]
        });
    }

    getErrorMessage(fieldName: string): string {
        const field = this.processoForm.get(fieldName);
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
        return '';
    }

    onSave(): void {
        if (this.processoForm.valid) {
            this.dialogRef.close(this.processoForm.value);
        } else {
            // Marca tutti i campi come touched per mostrare gli errori
            Object.keys(this.processoForm.controls).forEach(key => {
                this.processoForm.get(key)?.markAsTouched();
            });
        }
    }

    onCancel(): void {
        this.dialogRef.close(null);
    }
}
