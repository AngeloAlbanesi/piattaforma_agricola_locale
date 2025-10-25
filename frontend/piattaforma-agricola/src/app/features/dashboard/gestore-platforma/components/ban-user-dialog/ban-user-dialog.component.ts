import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

@Component({
    selector: 'app-ban-user-dialog',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatButtonModule],
    templateUrl: './ban-user-dialog.component.html',
    styleUrls: ['./ban-user-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class BanUserDialogComponent {
    motivo = new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.minLength(5)] });

    constructor(
        @Inject(MAT_DIALOG_DATA) public data: { displayName: string },
        private dialogRef: MatDialogRef<BanUserDialogComponent>
    ) { }

    confirm(): void {
        if (this.motivo.invalid) return;
        this.dialogRef.close(this.motivo.value);
    }

    cancel(): void {
        this.dialogRef.close();
    }
}
