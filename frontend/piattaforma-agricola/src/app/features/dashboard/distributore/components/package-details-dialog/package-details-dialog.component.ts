import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Inject, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatTabsModule } from '@angular/material/tabs';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Subject, takeUntil } from 'rxjs';

import { DistributoreService } from '../../../../../core/services/distributore.service';
import { DettaglioPacchettoDTO } from '../../../../../core/models/distributore.models';

export interface PackageDetailsDialogData {
    packageId: number;
}

@Component({
    selector: 'app-package-details-dialog',
    standalone: true,
    imports: [
        CommonModule,
        MatDialogModule,
        MatButtonModule,
        MatIconModule,
        MatCardModule,
        MatChipsModule,
        MatTabsModule,
        MatProgressSpinnerModule,
        MatSnackBarModule
    ],
    templateUrl: './package-details-dialog.component.html',
    styleUrls: ['./package-details-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PackageDetailsDialogComponent implements OnInit, OnDestroy {
    private destroy$ = new Subject<void>();

    packageDetails: DettaglioPacchettoDTO | null = null;
    isLoading = true;

    constructor(
        private distributoreService: DistributoreService,
        private snackBar: MatSnackBar,
        private dialogRef: MatDialogRef<PackageDetailsDialogComponent>,
        private cdr: ChangeDetectorRef,
        @Inject(MAT_DIALOG_DATA) public data: PackageDetailsDialogData
    ) { }

    ngOnInit(): void {
        this.loadPackageDetails();
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    private loadPackageDetails(): void {
        this.isLoading = true;

        this.distributoreService.getPackageById(this.data.packageId)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (details) => {
                    this.packageDetails = details;
                    this.isLoading = false;
                    this.cdr.markForCheck();
                },
                error: (error) => {
                    console.error('Errore nel caricamento dettagli pacchetto:', error);
                    this.snackBar.open('Errore nel caricamento dei dettagli', 'Chiudi', {
                        duration: 3000,
                        panelClass: 'error-snackbar'
                    });
                    this.isLoading = false;
                    this.cdr.markForCheck();
                    this.dialogRef.close();
                }
            });
    }

    onClose(): void {
        this.dialogRef.close();
    }

    onEdit(): void {
        this.dialogRef.close({ action: 'edit', package: this.packageDetails });
    }

    onDelete(): void {
        this.dialogRef.close({ action: 'delete', package: this.packageDetails });
    }

    // Utility methods
    formatCurrency(value: number): string {
        return this.distributoreService.formatCurrency(value);
    }

    formatDate(date: string): string {
        return this.distributoreService.formatDate(date);
    }

    formatDateTime(date: string): string {
        return new Date(date).toLocaleString('it-IT', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    getStatoColor(stato: string): string {
        switch (stato) {
            case 'ATTIVO':
                return '#27ae60';
            case 'IN_PROGETTAZIONE':
                return '#f39c12';
            case 'INATTIVO':
                return '#e74c3c';
            default:
                return '#95a5a6';
        }
    }

    getStatoLabel(stato: string): string {
        switch (stato) {
            case 'ATTIVO':
                return 'Attivo';
            case 'IN_PROGETTAZIONE':
                return 'In Progettazione';
            case 'INATTIVO':
                return 'Inattivo';
            default:
                return stato;
        }
    }

    getStatoIcon(stato: string): string {
        switch (stato) {
            case 'ATTIVO':
                return 'check_circle';
            case 'IN_PROGETTAZIONE':
                return 'construction';
            case 'INATTIVO':
                return 'pause_circle';
            default:
                return 'help';
        }
    }

    // Getters per template
    get hasPackage(): boolean {
        return !!this.packageDetails;
    }

    get totalProducts(): number {
        return this.packageDetails?.prodotti?.length || 0;
    }

    get totalQuantity(): number {
        return this.packageDetails?.prodotti?.reduce((sum, p) => sum + p.quantita, 0) || 0;
    }

    get totalProductsCost(): number {
        return this.packageDetails?.prodotti?.reduce((sum, p) => sum + (p.prezzo * p.quantita), 0) || 0;
    }

    get profitMargin(): number {
        if (!this.packageDetails || this.totalProductsCost === 0) return 0;
        return ((this.packageDetails.prezzo - this.totalProductsCost) / this.totalProductsCost) * 100;
    }

    get hasReviews(): boolean {
        return (this.packageDetails?.recensioni?.length || 0) > 0;
    }

    get hasCertifications(): boolean {
        return (this.packageDetails?.certificazioni?.length || 0) > 0;
    }

    get hasStats(): boolean {
        return !!this.packageDetails?.statisticheVendite;
    }

    get averageRating(): number {
        return this.packageDetails?.statisticheVendite?.mediaValutazione || 0;
    }

    get totalSales(): number {
        return this.packageDetails?.statisticheVendite?.venditeTotali || 0;
    }

    get reviewsCount(): number {
        return this.packageDetails?.statisticheVendite?.numeroRecensioni || 0;
    }

    // Rating stars helper
    getRatingStars(rating: number): string[] {
        const stars = [];
        const fullStars = Math.floor(rating);
        const hasHalfStar = rating % 1 >= 0.5;

        for (let i = 0; i < fullStars; i++) {
            stars.push('star');
        }

        if (hasHalfStar) {
            stars.push('star_half');
        }

        const emptyStars = 5 - stars.length;
        for (let i = 0; i < emptyStars; i++) {
            stars.push('star_border');
        }

        return stars;
    }

    trackByProductId(index: number, product: any): number {
        return product.id;
    }

    trackByReviewId(index: number, review: any): number {
        return review.id;
    }

    trackByCertificationId(index: number, certification: any): number {
        return certification.idCertificazione;
    }
}