import { ChangeDetectionStrategy, Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { DistributoreService } from '../../../../../core/services/distributore.service';
import { PacchettoTipicitaDTO } from '../../../../../core/models/distributore.models';
import { PacchettoCardComponent } from '../pacchetto-card/pacchetto-card.component';
import { PackageFormDialogComponent, PackageFormDialogData } from '../package-form-dialog/package-form-dialog.component';
import { PackageDetailsDialogComponent, PackageDetailsDialogData } from '../package-details-dialog/package-details-dialog.component';
import { DeleteConfirmationDialogComponent, DeleteConfirmationDialogData } from '../delete-confirmation-dialog/delete-confirmation-dialog.component';

@Component({
    selector: 'app-pacchetti-management',
    standalone: true,
    imports: [
        CommonModule,
        MatProgressSpinnerModule,
        MatCardModule,
        MatButtonModule,
        MatIconModule,
        MatSnackBarModule,
        PacchettoCardComponent
    ],
    templateUrl: './pacchetti-management.component.html',
    styleUrls: ['./pacchetti-management.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PacchettiManagementComponent implements OnInit {
    pacchetti: PacchettoTipicitaDTO[] = [];
    isLoading = false;
    errorMessage: string | null = null;

    constructor(
        private distributoreService: DistributoreService,
        private dialog: MatDialog,
        private snackBar: MatSnackBar,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit(): void {
        this.loadPacchetti();
    }

    private loadPacchetti(): void {
        this.isLoading = true;
        this.errorMessage = null;

        this.distributoreService.getMyPackages().subscribe({
            next: (pacchetti) => {
                this.pacchetti = pacchetti;
                this.isLoading = false;
                this.cdr.markForCheck();
            },
            error: (error) => {
                console.error('Errore nel caricamento pacchetti:', error);
                this.errorMessage = 'Impossibile caricare i pacchetti. Riprova più tardi.';
                this.isLoading = false;
                this.cdr.markForCheck();
            }
        });
    }

    refreshPacchetti(): void {
        this.loadPacchetti();
    }

    createNewPackage(): void {
        const dialogData: PackageFormDialogData = {
            mode: 'create'
        };

        const dialogRef = this.dialog.open(PackageFormDialogComponent, {
            width: '800px',
            maxWidth: '90vw',
            data: dialogData,
            disableClose: true
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.snackBar.open('Pacchetto creato con successo!', 'Chiudi', {
                    duration: 3000,
                    panelClass: 'success-snackbar'
                });
                this.loadPacchetti(); // Ricarica la lista
            }
        });
    }

    editPackage(packageId: number): void {
        const packageToEdit = this.pacchetti.find(p => p.id === packageId);
        if (!packageToEdit) {
            this.snackBar.open('Pacchetto non trovato', 'Chiudi', {
                duration: 3000,
                panelClass: 'error-snackbar'
            });
            return;
        }

        const dialogData: PackageFormDialogData = {
            mode: 'edit',
            package: packageToEdit
        };

        const dialogRef = this.dialog.open(PackageFormDialogComponent, {
            width: '800px',
            maxWidth: '90vw',
            data: dialogData,
            disableClose: true
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result) {
                this.snackBar.open('Pacchetto aggiornato con successo!', 'Chiudi', {
                    duration: 3000,
                    panelClass: 'success-snackbar'
                });
                this.loadPacchetti(); // Ricarica la lista
            }
        });
    }

    deletePackage(packageId: number): void {
        const packageToDelete = this.pacchetti.find(p => p.id === packageId);
        if (!packageToDelete) {
            this.snackBar.open('Pacchetto non trovato', 'Chiudi', {
                duration: 3000,
                panelClass: 'error-snackbar'
            });
            return;
        }

        const dialogData: DeleteConfirmationDialogData = {
            package: packageToDelete,
            type: 'package'
        };

        const dialogRef = this.dialog.open(DeleteConfirmationDialogComponent, {
            width: '500px',
            maxWidth: '90vw',
            data: dialogData
        });

        dialogRef.afterClosed().subscribe(confirmed => {
            if (confirmed) {
                this.performDeletePackage(packageId);
            }
        });
    }

    private performDeletePackage(packageId: number): void {
        this.distributoreService.deletePackage(packageId).subscribe({
            next: () => {
                this.snackBar.open('Pacchetto eliminato con successo', 'Chiudi', {
                    duration: 3000,
                    panelClass: 'success-snackbar'
                });
                this.loadPacchetti(); // Ricarica la lista
            },
            error: (error) => {
                console.error('Errore nell\'eliminazione del pacchetto:', error);
                this.snackBar.open('Errore nell\'eliminazione del pacchetto', 'Chiudi', {
                    duration: 3000,
                    panelClass: 'error-snackbar'
                });
            }
        });
    }

    viewPackageDetails(packageId: number): void {
        const dialogData: PackageDetailsDialogData = {
            packageId: packageId
        };

        const dialogRef = this.dialog.open(PackageDetailsDialogComponent, {
            width: '900px',
            maxWidth: '95vw',
            maxHeight: '90vh',
            data: dialogData
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result?.action === 'edit') {
                this.editPackage(packageId);
            } else if (result?.action === 'delete') {
                this.deletePackage(packageId);
            }
        });
    }

    formatCurrency(value: number): string {
        return this.distributoreService.formatCurrency(value);
    }

    formatDate(date: string): string {
        return this.distributoreService.formatDate(date);
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
}