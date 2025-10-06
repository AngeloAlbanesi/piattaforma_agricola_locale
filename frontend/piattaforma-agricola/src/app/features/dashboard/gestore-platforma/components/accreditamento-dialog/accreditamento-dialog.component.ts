/*
 *   Copyright (c) 2025 Angelo Albanesi
 *   All rights reserved.
 */
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatRadioModule } from '@angular/material/radio';

export interface AccreditamentoDialogData {
    displayName: string;
    ruolo: string;
}

export interface AccreditamentoDialogResult {
    azione: 'ACCREDITATO' | 'RIFIUTATO';
    nota?: string;
}

@Component({
    selector: 'app-accreditamento-dialog',
    standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        MatDialogModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        MatIconModule,
        MatRadioModule
    ],
    templateUrl: './accreditamento-dialog.component.html',
    styleUrls: ['./accreditamento-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class AccreditamentoDialogComponent {
    azione = new FormControl<'ACCREDITATO' | 'RIFIUTATO'>('ACCREDITATO', {
        nonNullable: true,
        validators: [Validators.required]
    });

    nota = new FormControl('', { nonNullable: true });

    constructor(
        @Inject(MAT_DIALOG_DATA) public data: AccreditamentoDialogData,
        private dialogRef: MatDialogRef<AccreditamentoDialogComponent>
    ) { }

    confirm(): void {
        if (this.azione.invalid) return;

        const result: AccreditamentoDialogResult = {
            azione: this.azione.value,
            nota: this.nota.value.trim() || undefined
        };

        this.dialogRef.close(result);
    }

    cancel(): void {
        this.dialogRef.close();
    }

    get isRifiuto(): boolean {
        return this.azione.value === 'RIFIUTATO';
    }
}

