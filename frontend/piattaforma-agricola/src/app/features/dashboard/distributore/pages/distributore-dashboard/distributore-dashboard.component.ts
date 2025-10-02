import { ChangeDetectionStrategy, Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Subject, takeUntil, catchError } from 'rxjs';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';

import { AuthService } from '../../../../../core/services/auth.service';
import { DistributoreService } from '../../../../../core/services/distributore.service';
import { DistributoreStatsDTO } from '../../../../../core/models/distributore.models';
import { DistributoreStatsOverviewComponent } from '../../components/distributore-stats-overview/distributore-stats-overview.component';
import { DistributoreQuickActionsComponent } from '../../components/distributore-quick-actions/distributore-quick-actions.component';
import { PacchettiManagementComponent } from '../../components/pacchetti-management/pacchetti-management.component';

@Component({
    selector: 'app-distributore-dashboard',
    standalone: true,
    imports: [
        CommonModule,
        MatCardModule,
        MatIconModule,
        MatButtonModule,
        MatProgressSpinnerModule,
        MatTabsModule,
        MatTooltipModule,
        MatSnackBarModule,
        DistributoreStatsOverviewComponent,
        DistributoreQuickActionsComponent,
        PacchettiManagementComponent
    ],
    templateUrl: './distributore-dashboard.component.html',
    styleUrls: ['./distributore-dashboard.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class DistributoreDashboardComponent implements OnInit, OnDestroy {
    private destroy$ = new Subject<void>();

    // Dati utente
    userName: string = '';
    userId: number | null = null;

    // Statistiche dashboard
    stats: DistributoreStatsDTO | null = null;
    isLoading = false;

    // Tab selezionata
    selectedTab = 0;

    constructor(
        private authService: AuthService,
        private distributoreService: DistributoreService,
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
        this.userName = authState.username || 'Distributore';
        this.userId = authState.userId || null;
    }

    private loadDashboardStats(): void {
        this.isLoading = true;

        this.distributoreService.getDistributoreStats()
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

    navigateToPackages(): void {
        this.router.navigate(['/pacchetti']);
    }

    navigateToProducts(): void {
        this.router.navigate(['/prodotti']);
    }

    navigateToOrders(): void {
        this.router.navigate(['/ordini']);
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
            case 'create-package':
                this.router.navigate(['/pacchetti/nuovo']);
                break;
            case 'manage-packages':
                this.navigateToPackages();
                break;
            case 'view-products':
                this.navigateToProducts();
                break;
            case 'view-orders':
                this.navigateToOrders();
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

    // === METODI PUBBLICI PER TEMPLATE ===

    formatCurrency(value: number): string {
        return this.distributoreService.formatCurrency(value);
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

    get totalPackages(): number {
        return this.stats?.pacchettiTotali || 0;
    }

    get activePackages(): number {
        return this.stats?.pacchettiAttivi || 0;
    }

    get soldPackages(): number {
        return this.stats?.pacchettiVenduti || 0;
    }

    get totalRevenue(): number {
        return this.stats?.ricavoTotale || 0;
    }

    get averagePackagePrice(): number {
        return this.stats?.mediaPrezzoPacchetto || 0;
    }
}