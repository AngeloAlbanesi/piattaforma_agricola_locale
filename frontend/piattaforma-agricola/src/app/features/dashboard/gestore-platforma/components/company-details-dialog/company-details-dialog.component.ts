import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { AdminService } from '../../../../../core/services/admin.service';
import { CompanyModerationDTO } from '../../../../../core/models/admin.models';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
    selector: 'app-company-details-dialog',
    standalone: true,
    imports: [CommonModule, MatDialogModule, MatCardModule, MatButtonModule, MatProgressSpinnerModule],
    templateUrl: './company-details-dialog.component.html',
    styleUrls: ['./company-details-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CompanyDetailsDialogComponent implements OnInit {
    dataAzienda: CompanyModerationDTO | null = null;
    isLoading = false;

    constructor(
        @Inject(MAT_DIALOG_DATA) public data: { userId: number },
        private dialogRef: MatDialogRef<CompanyDetailsDialogComponent>,
        private adminService: AdminService
    ) { }

    ngOnInit(): void {
        this.isLoading = true;
        this.adminService.getCompanyByVenditore(this.data.userId).subscribe({
            next: (res) => { this.dataAzienda = res; this.isLoading = false; },
            error: () => { this.isLoading = false; }
        });
    }

    close(): void { this.dialogRef.close(); }
}
