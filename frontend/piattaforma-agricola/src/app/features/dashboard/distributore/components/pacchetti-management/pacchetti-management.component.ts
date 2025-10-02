import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { DistributoreService } from '../../../../../core/services/distributore.service';
import { PacchettoTipicitaDTO } from '../../../../../core/models/distributore.models';
import { PacchettoCardComponent } from '../pacchetto-card/pacchetto-card.component';

@Component({
    selector: 'app-pacchetti-management',
    standalone: true,
    imports: [
        CommonModule,
        MatProgressSpinnerModule,
        MatCardModule,
        MatButtonModule,
        MatIconModule,
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

    constructor(private distributoreService: DistributoreService) { }

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
            },
            error: (error) => {
                console.error('Errore nel caricamento pacchetti:', error);
                this.errorMessage = 'Impossibile caricare i pacchetti. Riprova più tardi.';
                this.isLoading = false;
            }
        });
    }

    refreshPacchetti(): void {
        this.loadPacchetti();
    }

    createNewPackage(): void {
        // TODO: Implementare navigazione alla creazione pacchetto
        console.log('Creazione nuovo pacchetto');
    }

    editPackage(packageId: number): void {
        // TODO: Implementare navigazione alla modifica pacchetto
        console.log('Modifica pacchetto:', packageId);
    }

    deletePackage(packageId: number): void {
        // TODO: Implementare eliminazione pacchetto
        console.log('Eliminazione pacchetto:', packageId);
    }

    viewPackageDetails(packageId: number): void {
        // TODO: Implementare navigazione ai dettagli pacchetto
        console.log('Visualizza dettagli pacchetto:', packageId);
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