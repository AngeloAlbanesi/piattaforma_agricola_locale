import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTabsModule } from '@angular/material/tabs';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { CuratoreService } from '../../../../../core/services/curatore.service';
import { ApprovazionePendingDTO, ApprovazioneFilters } from '../../../../../core/models/curatore.models';
import { ApprovazioneCardComponent } from '../approvazione-card/approvazione-card.component';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ApprovalFiltersComponent } from '../approval-filters/approval-filters.component';
import { ProductDetailDialogComponent } from '../product-detail-dialog/product-detail-dialog.component';
import { CompanyDetailDialogComponent } from '../company-detail-dialog/company-detail-dialog.component';

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
        MatSnackBarModule,
        ApprovalFiltersComponent
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

    // Track if user has explicitly set filters (don't override with tab)
    private userFilteredTipo = false;

    constructor(
        private curatoreService: CuratoreService,
        private snackBar: MatSnackBar,
        private dialog: MatDialog,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit(): void {
        this.loadApprovazioni();
    }

    private loadApprovazioni(): void {
        this.isLoading = true;
        this.errorMessage = null;

        // Only update filters by tab if tipo is not already set by user filters
        // This prevents overriding user's explicit filter selections
        if (!this.userFilteredTipo) {
            this.updateFiltersByTab();
        }

        this.curatoreService.getPendingApprovals(this.filters)
            .subscribe({
                next: (approvazioni) => {
                    console.log('Approvazioni caricate:', approvazioni);
                    console.log('Stati delle approvazioni:', approvazioni.map(a => ({ id: a.elementoId, nome: a.elementoNome, stato: a.stato })));
                    this.approvazioni = approvazioni;
                    this.isLoading = false;
                    this.cdr.markForCheck();
                },
                error: (error) => {
                    console.error('Errore nel caricamento approvazioni:', error);
                    this.errorMessage = 'Impossibile caricare le approvazioni. Riprova più tardi.';
                    this.snackBar.open(this.errorMessage, 'Chiudi', { duration: 3000, panelClass: 'error-snackbar' });
                    this.isLoading = false;
                    this.cdr.markForCheck();
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
        // When tab changes, clear user filter flag so tab filter applies
        this.userFilteredTipo = false;
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
                    this.cdr.markForCheck();
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
                    this.cdr.markForCheck();
                }
            });
    }

    viewElementDetails(elementId: number, tipo: string): void {
        if (tipo === 'PRODOTTO') {
            this.openProductDetailDialog(elementId);
        } else if (tipo === 'AZIENDA') {
            this.openCompanyDetailDialog(elementId);
        } else {
            console.log('Dettagli contenuto non ancora implementati:', elementId);
        }
    }

    private openProductDetailDialog(productId: number): void {
        const approvazione = this.approvazioni.find(a => a.elementoId === productId && a.tipo === 'PRODOTTO');

        const dialogRef = this.dialog.open(ProductDetailDialogComponent, {
            width: '800px',
            maxWidth: '95vw',
            maxHeight: '90vh',
            data: {
                productId: productId,
                productName: approvazione?.elementoNome || 'Prodotto'
            },
            disableClose: false,
            panelClass: 'product-detail-dialog-container'
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result && (result.action === 'approved' || result.action === 'rejected')) {
                // Refresh the list after approval/rejection
                this.loadApprovazioni();
            }
        });
    }

    private openCompanyDetailDialog(companyId: number): void {
        const approvazione = this.approvazioni.find(a => a.elementoId === companyId && a.tipo === 'AZIENDA');

        const dialogRef = this.dialog.open(CompanyDetailDialogComponent, {
            width: '700px',
            maxWidth: '95vw',
            maxHeight: '90vh',
            data: {
                companyId: companyId,
                companyName: approvazione?.elementoNome || 'Azienda'
            },
            disableClose: false,
            panelClass: 'company-detail-dialog-container'
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result && (result.action === 'approved' || result.action === 'rejected')) {
                // Refresh the list after approval/rejection
                this.loadApprovazioni();
            }
        });
    }

    onFiltersChanged(newFilters: ApprovazioneFilters): void {
        this.filters = { ...this.filters, ...newFilters };
        // Mark that user has explicitly set tipo filter
        this.userFilteredTipo = !!newFilters.tipo;
        this.loadApprovazioni();
    }

    onFiltersReset(): void {
        this.filters = {
            stato: 'IN_ATTESA',
            pagina: 1,
            elementiPerPagina: 10
        };
        // Clear user filter flag on reset
        this.userFilteredTipo = false;
        this.loadApprovazioni();
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