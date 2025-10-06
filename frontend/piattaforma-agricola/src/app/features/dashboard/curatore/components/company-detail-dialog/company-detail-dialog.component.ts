import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { CuratoreService } from '../../../../../core/services/curatore.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CompanyModerationDTO } from '../../../../../core/models/curatore.models';

export interface CompanyDetailData {
    companyId: number;
    companyName: string;
}

@Component({
    selector: 'app-company-detail-dialog',
    standalone: true,
    imports: [
        CommonModule,
        MatDialogModule,
        MatButtonModule,
        MatIconModule,
        MatProgressSpinnerModule,
        MatChipsModule,
        MatCardModule,
        MatDividerModule,
        MatFormFieldModule,
        MatInputModule,
        FormsModule
    ],
    templateUrl: './company-detail-dialog.component.html',
    styleUrls: ['./company-detail-dialog.component.scss']
})
export class CompanyDetailDialogComponent implements OnInit {
    company: CompanyModerationDTO | null = null;
    isLoading = true;
    error: string | null = null;

    // Reject dialog state
    showRejectDialog = false;
    rejectReason = '';

    constructor(
        @Inject(MAT_DIALOG_DATA) public data: CompanyDetailData,
        private dialogRef: MatDialogRef<CompanyDetailDialogComponent>,
        private curatoreService: CuratoreService,
        private snackBar: MatSnackBar
    ) { }

    ngOnInit(): void {
        this.loadCompanyDetails();
    }

    private loadCompanyDetails(): void {
        this.isLoading = true;
        this.error = null;

        this.curatoreService.getCompanyDetailsForApproval(this.data.companyId).subscribe({
            next: (company) => {
                this.company = company;
                this.isLoading = false;
            },
            error: (err) => {
                console.error('Errore caricamento dettagli azienda:', err);
                this.error = 'Impossibile caricare i dettagli dell\'azienda';
                this.isLoading = false;
            }
        });
    }

    onApprove(): void {
        if (!this.company) return;

        const motivazione = 'Documentazione completa e conforme ai requisiti.';
        this.isLoading = true;

        this.curatoreService.approveElement(this.company.id, 'AZIENDA', { motivazione }).subscribe({
            next: (response) => {
                console.log('Risposta approvazione:', response);
                this.snackBar.open('Azienda approvata con successo', 'Chiudi', {
                    duration: 3000,
                    panelClass: 'success-snackbar'
                });
                this.dialogRef.close({ action: 'approved', companyId: this.company!.id });
            },
            error: (err) => {
                console.error('Errore approvazione azienda:', err);
                this.snackBar.open('Errore durante l\'approvazione', 'Chiudi', {
                    duration: 3000,
                    panelClass: 'error-snackbar'
                });
                this.isLoading = false;
            }
        });
    }

    onReject(): void {
        this.showRejectDialog = true;
    }

    confirmReject(): void {
        if (!this.company || !this.rejectReason.trim()) return;

        this.isLoading = true;
        this.showRejectDialog = false;

        this.curatoreService.rejectElement(
            this.company.id,
            'AZIENDA',
            { motivazione: this.rejectReason.trim() }
        ).subscribe({
            next: (response) => {
                console.log('Risposta rifiuto:', response);
                this.snackBar.open('Azienda rifiutata', 'Chiudi', {
                    duration: 3000,
                    panelClass: 'success-snackbar'
                });
                this.dialogRef.close({ action: 'rejected', companyId: this.company!.id });
            },
            error: (err) => {
                console.error('Errore rifiuto azienda:', err);
                this.snackBar.open('Errore durante il rifiuto', 'Chiudi', {
                    duration: 3000,
                    panelClass: 'error-snackbar'
                });
                this.isLoading = false;
            }
        });
    }

    cancelReject(): void {
        this.showRejectDialog = false;
        this.rejectReason = '';
    }

    onClose(): void {
        this.dialogRef.close();
    }

    getStatoColor(stato: string): string {
        switch (stato) {
            case 'IN_ATTESA_REVISIONE':
            case 'IN_ATTESA':
                return '#ff9800';
            case 'APPROVATO':
                return '#4caf50';
            case 'RIFIUTATO':
                return '#f44336';
            default:
                return '#9e9e9e';
        }
    }

    getStatoLabel(stato: string): string {
        switch (stato) {
            case 'IN_ATTESA_REVISIONE':
                return 'In Attesa di Revisione';
            case 'IN_ATTESA':
                return 'In Attesa';
            case 'APPROVATO':
                return 'Approvato';
            case 'RIFIUTATO':
                return 'Rifiutato';
            default:
                return stato;
        }
    }
}

