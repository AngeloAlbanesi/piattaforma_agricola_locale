/*
 *   Copyright (c) 2025 Angelo Albanesi
 *   All rights reserved.
 */
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { CompanyModerationDTO } from '../../../../../core/models/admin.models';

@Component({
    selector: 'app-company-details-dialog',
    standalone: true,
    imports: [
        CommonModule,
        MatDialogModule,
        MatButtonModule,
        MatIconModule,
        MatCardModule,
        MatChipsModule,
        MatDividerModule
    ],
    templateUrl: './company-details-dialog.component.html',
    styleUrls: ['./company-details-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CompanyDetailsDialogComponent {
    constructor(
        @Inject(MAT_DIALOG_DATA) public data: CompanyModerationDTO,
        private dialogRef: MatDialogRef<CompanyDetailsDialogComponent>
    ) { }

    close(): void {
        this.dialogRef.close();
    }

    getStatoColor(): string {
        switch (this.data.statoVerifica) {
            case 'APPROVATO':
                return 'success';
            case 'IN_ATTESA_REVISIONE':
                return 'warn';
            case 'RIFIUTATO':
                return 'error';
            case 'SOSPESO':
                return 'default';
            default:
                return 'default';
        }
    }

    getStatoLabel(): string {
        switch (this.data.statoVerifica) {
            case 'APPROVATO':
                return 'Approvato';
            case 'IN_ATTESA_REVISIONE':
                return 'In Attesa di Revisione';
            case 'RIFIUTATO':
                return 'Rifiutato';
            case 'SOSPESO':
                return 'Sospeso';
            default:
                return this.data.statoVerifica;
        }
    }
}
