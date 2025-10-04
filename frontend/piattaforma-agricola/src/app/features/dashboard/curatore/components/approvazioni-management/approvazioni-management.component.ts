import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTabsModule } from '@angular/material/tabs';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { CuratoreService } from '../../../../../core/services/curatore.service';
import { ApprovazionePendingDTO, ApprovazioneFilters } from '../../../../../core/models/curatore.models';
import { ApprovazioneCardComponent } from '../approvazione-card/approvazione-card.component';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
    selector: 'app-approvazioni-management',
    standalone: true,
    imports: [
        CommonModule,
        MatTabsModule,
        MatCardModule,
        MatProgressSpinnerModule,
        MatIconModule,
        MatButtonModule,
        ApprovazioneCardComponent,
        MatSnackBarModule
    ],
    templateUrl: './approvazioni-management.component.html',
    styleUrls: ['./approvazioni-management.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ApprovazioniManagementComponent implements OnInit {
    approvazioni: ApprovazionePendingDTO[] = [];
    isLoading = false;
    errorMessage: string | null = null;
    selectedTab = 0; // 0: Prodotti, 1: Aziende, 2: Contenuti

    // Filtri
    filters: ApprovazioneFilters = {
        stato: 'IN_ATTESA',
        pagina: 1,
        elementiPerPagina: 10
    };

    constructor(
        private curatoreService: CuratoreService,
        private snackBar: MatSnackBar
    ) { }

    ngOnInit(): void {
        this.loadApprovazioni();
    }

    private loadApprovazioni(): void {
        this.isLoading = true;
        this.errorMessage = null;

        // Aggiorna i filtri in base alla tab selezionata
        this.updateFiltersByTab();

        this.curatoreService.getPendingApprovals(this.filters)
            .subscribe({
                next: (approvazioni) => {
                    this.approvazioni = approvazioni;
                    this.isLoading = false;
                },
                error: (error) => {
                    console.error('Errore nel caricamento approvazioni:', error);
                    this.errorMessage = 'Impossibile caricare le approvazioni. Riprova più tardi.';
                    this.snackBar.open(this.errorMessage, 'Chiudi', { duration: 3000, panelClass: 'error-snackbar' });
                    this.isLoading = false;
                }
            });
    }

    private updateFiltersByTab(): void {
        switch (this.selectedTab) {
            case 0:
                this.filters.tipo = 'PRODOTTO';
                break;
            case 1:
                this.filters.tipo = 'AZIENDA';
                break;
            case 2:
                this.filters.tipo = 'CONTENUTO';
                break;
            default:
                this.filters.tipo = 'TUTTI';
        }
    }

    refreshApprovazioni(): void {
        this.loadApprovazioni();
    }

    onTabChange(index: number): void {
        this.selectedTab = index;
        this.loadApprovazioni();
    }

    approveElement(elementId: number, tipo: string): void {
        this.isLoading = true;
        this.curatoreService.approveElement(elementId, tipo, { motivazione: 'Approvato' })
            .subscribe({
                next: () => {
                    this.snackBar.open('Elemento approvato con successo', 'Chiudi', { duration: 2500, panelClass: 'success-snackbar' });
                    this.loadApprovazioni();
                },
                error: (error) => {
                    console.error('Errore nell\'approvazione:', error);
                    this.snackBar.open('Errore durante l\'approvazione', 'Chiudi', { duration: 3000, panelClass: 'error-snackbar' });
                    this.isLoading = false;
                }
            });
    }

    rejectElement(elementId: number, tipo: string, event: { id: number, motivo: string }): void {
        if (!event.motivo || !event.motivo.trim()) {
            this.snackBar.open('Inserisci una motivazione per il rifiuto', 'Chiudi', { duration: 3000, panelClass: 'warning-snackbar' });
            return;
        }
        this.isLoading = true;
        this.curatoreService.rejectElement(elementId, tipo, { motivazione: event.motivo.trim() })
            .subscribe({
                next: () => {
                    this.snackBar.open('Elemento rifiutato', 'Chiudi', { duration: 2500, panelClass: 'success-snackbar' });
                    this.loadApprovazioni();
                },
                error: (error) => {
                    console.error('Errore nel rifiuto:', error);
                    this.snackBar.open('Errore durante il rifiuto', 'Chiudi', { duration: 3000, panelClass: 'error-snackbar' });
                    this.isLoading = false;
                }
            });
    }

    viewElementDetails(elementId: number, tipo: string): void {
        // TODO: Implementare navigazione ai dettagli dell'elemento
        console.log('Visualizza dettagli elemento:', elementId, tipo);
    }

    formatDate(date: string): string {
        return this.curatoreService.formatDate(date);
    }

    formatDateTime(date: string): string {
        return this.curatoreService.formatDateTime(date);
    }

    getStatoColor(stato: string): string {
        switch (stato) {
            case 'IN_ATTESA':
                return '#f39c12';
            case 'APPROVATO':
                return '#27ae60';
            case 'RIFIUTATO':
                return '#e74c3c';
            default:
                return '#95a5a6';
        }
    }

    getStatoLabel(stato: string): string {
        switch (stato) {
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

    getTipoLabel(tipo: string): string {
        switch (tipo) {
            case 'PRODOTTO':
                return 'Prodotto';
            case 'AZIENDA':
                return 'Azienda';
            case 'CONTENUTO':
                return 'Contenuto';
            default:
                return tipo;
        }
    }

    getTipoIcon(tipo: string): string {
        switch (tipo) {
            case 'PRODOTTO':
                return 'inventory_2';
            case 'AZIENDA':
                return 'business';
            case 'CONTENUTO':
                return 'article';
            default:
                return 'category';
        }
    }
}