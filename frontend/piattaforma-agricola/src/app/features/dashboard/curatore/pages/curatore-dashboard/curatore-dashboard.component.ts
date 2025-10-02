import { ChangeDetectionStrategy, Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subject, takeUntil, catchError } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSnackBarModule } from '@angular/material/snack-bar';

import { AuthService } from '../../../../../core/services/auth.service';
import { CuratoreService } from '../../../../../core/services/curatore.service';
import { CuratoreStatsDTO } from '../../../../../core/models/curatore.models';
import { CuratoreStatsOverviewComponent } from '../../components/curatore-stats-overview/curatore-stats-overview.component';
import { CuratoreQuickActionsComponent } from '../../components/curatore-quick-actions/curatore-quick-actions.component';
import { ApprovazioniManagementComponent } from '../../components/approvazioni-management/approvazioni-management.component';

@Component({
    selector: 'app-curatore-dashboard',
    standalone: true,
    imports: [
        CommonModule,
        // Material modules needed for the template
        MatCardModule,
        MatButtonModule,
        MatIconModule,
        MatProgressSpinnerModule,
        MatTabsModule,
        MatTooltipModule,
        MatSnackBarModule,
        // Child components
        CuratoreStatsOverviewComponent,
        CuratoreQuickActionsComponent,
        ApprovazioniManagementComponent
    ],
    templateUrl: './curatore-dashboard.component.html',
    styleUrls: ['./curatore-dashboard.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CuratoreDashboardComponent implements OnInit, OnDestroy {
    private destroy$ = new Subject<void>();

    // Dati utente
    userName: string = '';
    userId: number | null = null;

    // Statistiche dashboard
    stats: CuratoreStatsDTO | null = null;
    isLoading = false;

    // Tab selezionata
    selectedTab = 0;

    constructor(
        private authService: AuthService,
        private curatoreService: CuratoreService,
        private router: Router,
        private snackBar: MatSnackBar
    ) { }

    ngOnInit(): void {
        this.initializeUserData();
        this.loadDashboardStats();
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    // === INIZIALIZZAZIONE ===

    private initializeUserData(): void {
        const authState = this.authService.authState();
        this.userName = authState.username || 'Curatore';
        this.userId = authState.userId || null;
    }

    private loadDashboardStats(): void {
        this.isLoading = true;

        this.curatoreService.getCuratoreStats()
            .pipe(
                takeUntil(this.destroy$),
                catchError(error => {
                    console.error('Errore nel caricamento statistiche:', error);
                    this.snackBar.open('Impossibile caricare le statistiche', 'Chiudi', {
                        duration: 3000,
                        panelClass: 'error-snackbar'
                    });
                    return [];
                })
            )
            .subscribe({
                next: (stats) => {
                    this.stats = stats;
                    this.isLoading = false;
                },
                error: () => {
                    this.isLoading = false;
                }
            });
    }

    // === NAVIGAZIONE ===

    navigateToApprovals(): void {
        this.router.navigate(['/approvazioni']);
    }

    navigateToHistory(): void {
        this.router.navigate(['/approvazioni/storico']);
    }

    navigateToProducts(): void {
        this.router.navigate(['/prodotti']);
    }

    navigateToCompanies(): void {
        this.router.navigate(['/aziende']);
    }

    navigateToContent(): void {
        this.router.navigate(['/contenuti']);
    }

    navigateToProfile(): void {
        this.router.navigate(['/profilo']);
    }

    // === GESTIONE TAB ===

    onTabChange(index: number): void {
        this.selectedTab = index;
    }

    // === AZIONI RAPIDE ===

    onQuickAction(action: string): void {
        switch (action) {
            case 'view-approvals':
                this.navigateToApprovals();
                break;
            case 'view-history':
                this.navigateToHistory();
                break;
            case 'view-products':
                this.navigateToProducts();
                break;
            case 'view-companies':
                this.navigateToCompanies();
                break;
            case 'view-content':
                this.navigateToContent();
                break;
            case 'edit-profile':
                this.navigateToProfile();
                break;
            default:
                console.log('Azione non gestita:', action);
        }
    }

    // === UTILITIES ===

    refreshData(): void {
        this.loadDashboardStats();
    }

    logout(): void {
        this.authService.logout();
    }

    // === GETTERS PER TEMPLATE ===

    get welcomeMessage(): string {
        const hour = new Date().getHours();
        let greeting = 'Buongiorno';

        if (hour >= 12 && hour < 18) {
            greeting = 'Buon pomeriggio';
        } else if (hour >= 18) {
            greeting = 'Buonasera';
        }

        return `${greeting}, ${this.userName}!`;
    }

    get hasStats(): boolean {
        return this.stats !== null;
    }

    get productsToApprove(): number {
        return this.stats?.prodottiDaApprovare || 0;
    }

    get companiesToApprove(): number {
        return this.stats?.aziendeDaApprovare || 0;
    }

    get contentToModerate(): number {
        return this.stats?.contenutiDaModerare || 0;
    }

    get productsApproved(): number {
        return this.stats?.prodottiApprovati || 0;
    }

    get productsRejected(): number {
        return this.stats?.prodottiRifiutati || 0;
    }

    get companiesApproved(): number {
        return this.stats?.aziendeApprovate || 0;
    }

    get companiesRejected(): number {
        return this.stats?.aziendeRifiutate || 0;
    }

    get contentModerated(): number {
        return this.stats?.contenutiModerati || 0;
    }

    get totalApprovals(): number {
        return this.productsToApprove + this.companiesToApprove + this.contentToModerate;
    }

    get approvalTrend(): number {
        if (!this.stats?.andamentoApprovazioni || this.stats.andamentoApprovazioni.length < 2) {
            return 0;
        }

        const data = this.stats.andamentoApprovazioni;
        const lastMonth = data[data.length - 1];
        const previousMonth = data[data.length - 2];

        const totalLastMonth = lastMonth.prodotti + lastMonth.aziende + lastMonth.contenuti;
        const totalPreviousMonth = previousMonth.prodotti + previousMonth.aziende + previousMonth.contenuti;

        if (totalPreviousMonth === 0) return 0;

        return ((totalLastMonth - totalPreviousMonth) / totalPreviousMonth) * 100;
    }
}