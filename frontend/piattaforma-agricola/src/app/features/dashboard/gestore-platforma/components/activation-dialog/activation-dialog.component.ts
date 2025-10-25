import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';

type UtenteTipo = 'ACQUIRENTE' | 'VENDITORE' | 'CURATORE' | 'ANIMATORE';

@Component({
    selector: 'app-activation-dialog',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatSelectModule, MatOptionModule, MatButtonModule],
    templateUrl: './activation-dialog.component.html',
    styleUrls: ['./activation-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ActivationDialogComponent {
    tipoCtrl = new FormControl<UtenteTipo>('VENDITORE', { nonNullable: true, validators: [Validators.required] });

    constructor(
        @Inject(MAT_DIALOG_DATA) public data: { displayName: string; attivo: boolean; skipTipoSelection?: boolean },
        private dialogRef: MatDialogRef<ActivationDialogComponent>
    ) { }

    confirm(): void {
        // Se skipTipoSelection è true, restituisci solo true
        if (this.data.skipTipoSelection) {
            this.dialogRef.close(true);
            return;
        }

        // Altrimenti, restituisci l'oggetto con tipo (backward compatibility)
        if (this.tipoCtrl.invalid) return;
        this.dialogRef.close({ tipo: this.tipoCtrl.value as UtenteTipo });
    }

    cancel(): void {
        this.dialogRef.close();
    }
}
