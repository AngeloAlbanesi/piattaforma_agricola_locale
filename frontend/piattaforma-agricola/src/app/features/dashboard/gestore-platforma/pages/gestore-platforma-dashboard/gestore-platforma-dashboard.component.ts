/*
 *   Copyright (c) 2025 Angelo Albanesi
 *   All rights reserved.
 */
import { ChangeDetectionStrategy, Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Subject, takeUntil, catchError } from 'rxjs';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';

import { AuthService } from '../../../../../core/services/auth.service';
import { GestorePlatformaService } from '../../../../../core/services/gestore-platforma.service';
import { GestorePlatformaStatsDTO } from '../../../../../core/models/gestore-platforma.models';

// Import child components
import { GestoreStatsOverviewComponent } from '../../components/gestore-stats-overview/gestore-stats-overview.component';
import { GestoreQuickActionsComponent } from '../../components/gestore-quick-actions/gestore-quick-actions.component';
import { UtentiManagementComponent } from '../../components/utenti-management/utenti-management.component';
import { PersonalDataCardComponent } from '../../../shared/components';

@Component({
    selector: 'app-gestore-platforma-dashboard',
    standalone: true,
    imports: [
        CommonModule,
        MatCardModule,
        MatButtonModule,
        MatIconModule,
        MatProgressSpinnerModule,
        MatTabsModule,
        MatTooltipModule,
        MatSnackBarModule,
        GestoreStatsOverviewComponent,
        GestoreQuickActionsComponent,
        UtentiManagementComponent,
        PersonalDataCardComponent
    ],
    templateUrl: './gestore-platforma-dashboard.component.html',
    styleUrls: ['./gestore-platforma-dashboard.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class GestorePlatformaDashboardComponent implements OnInit, OnDestroy {
    private destroy$ = new Subject<void>();

    // Dati utente
    userName: string = '';
    userId: number | null = null;

    // Statistiche dashboard
    stats: GestorePlatformaStatsDTO | null = null;
    isLoading = false;

    // Tab selezionata
    selectedTab = 0;

    constructor(
        private authService: AuthService,
        private gestoreService: GestorePlatformaService,
        private router: Router,
        private snackBar: MatSnackBar
    ) { }

    ngOnInit(): void {
        this.initializeUserData();
        // Statistiche disabilitate: backend non implementato
        this.isLoading = false;
        this.stats = null;
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    // === INIZIALIZZAZIONE ===

    private initializeUserData(): void {
        const authState = this.authService.authState();
        this.userName = authState.nome || 'Gestore Piattaforma';
        this.userId = authState.userId || null;
    }

    private loadDashboardStats(): void {
        this.isLoading = true;

        this.gestoreService.getGestoreStats()
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

    navigateToUsers(): void {
        this.router.navigate(['/dashboard/admin/utenti']);
    }

    navigateToProducts(): void {
        this.router.navigate(['/dashboard/admin/prodotti']);
    }

    navigateToCompanies(): void {
        this.router.navigate(['/dashboard/admin/aziende']);
    }

    navigateToEvents(): void {
        this.router.navigate(['/dashboard/admin/eventi']);
    }

    navigateToReports(): void {
        this.router.navigate(['/dashboard/admin/report']);
    }

    navigateToSettings(): void {
        this.router.navigate(['/dashboard/admin/impostazioni']);
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
            case 'view-users':
                this.navigateToUsers();
                break;
            case 'view-products':
                this.navigateToProducts();
                break;
            case 'view-companies':
                this.navigateToCompanies();
                break;
            case 'view-events':
                this.navigateToEvents();
                break;
            case 'view-reports':
                this.navigateToReports();
                break;
            case 'view-settings':
                this.navigateToSettings();
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
        // Statistiche disabilitate: evitiamo chiamate fallimentari
        this.snackBar.open('Statistiche non disponibili al momento', 'Chiudi', { duration: 2500 });
    }

    logout(): void {
        this.authService.logout();
    }

    // === METODI PUBBLICI PER TEMPLATE ===

    formatCurrency(value: number): string {
        return this.gestoreService.formatCurrency(value);
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

    get totalUsers(): number {
        return this.stats?.utentiTotali || 0;
    }

    get activeUsers(): number {
        return this.stats?.utentiAttivi || 0;
    }

    get newUsersThisMonth(): number {
        return this.stats?.utentiNuoviMese || 0;
    }

    get totalProducts(): number {
        return this.stats?.prodottiTotali || 0;
    }

    get approvedProducts(): number {
        return this.stats?.prodottiApprovati || 0;
    }

    get totalCompanies(): number {
        return this.stats?.aziendeTotali || 0;
    }

    get approvedCompanies(): number {
        return this.stats?.aziendeApprovate || 0;
    }

    get totalEvents(): number {
        return this.stats?.eventiTotali || 0;
    }

    get publishedEvents(): number {
        return this.stats?.eventiPubblicati || 0;
    }

    get totalTransactions(): number {
        return this.stats?.transazioniTotali || 0;
    }

    get totalRevenue(): number {
        return this.stats?.ricavoTotale || 0;
    }

    get userGrowthRate(): number {
        if (!this.stats?.andamentoUtenti || this.stats.andamentoUtenti.length < 2) {
            return 0;
        }

        const data = this.stats.andamentoUtenti;
        const lastMonth = data[data.length - 1];
        const previousMonth = data[data.length - 2];

        if (previousMonth.totali === 0) return 0;

        return ((lastMonth.totali - previousMonth.totali) / previousMonth.totali) * 100;
    }

    get productGrowthRate(): number {
        if (!this.stats?.andamentoProdotti || this.stats.andamentoProdotti.length < 2) {
            return 0;
        }

        const data = this.stats.andamentoProdotti;
        const lastMonth = data[data.length - 1];
        const previousMonth = data[data.length - 2];

        if (previousMonth.creati === 0) return 0;

        return ((lastMonth.creati - previousMonth.creati) / previousMonth.creati) * 100;
    }

    get eventGrowthRate(): number {
        if (!this.stats?.andamentoEventi || this.stats.andamentoEventi.length < 2) {
            return 0;
        }

        const data = this.stats.andamentoEventi;
        const lastMonth = data[data.length - 1];
        const previousMonth = data[data.length - 2];

        if (previousMonth.creati === 0) return 0;

        return ((lastMonth.creati - previousMonth.creati) / previousMonth.creati) * 100;
    }
}