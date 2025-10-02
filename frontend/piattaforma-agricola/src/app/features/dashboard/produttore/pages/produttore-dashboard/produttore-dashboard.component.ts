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

import { AuthService } from '@core/services/auth.service';
import { ProduttoreService } from '@core/services/produttore.service';
import { ProduttoreStatsDTO } from '@core/models/produttore.models';

// Import dei componenti figli
import { ProduttoreStatsOverviewComponent } from '../../components/produttore-stats-overview/produttore-stats-overview.component';
import { ProduttoreQuickActionsComponent } from '../../components/produttore-quick-actions/produttore-quick-actions.component';
import { ProdottiManagementComponent } from '../../components/prodotti-management/prodotti-management.component';
import { OrdiniManagementComponent } from '../../components/ordini-management/ordini-management.component';
import { CertificazioniManagementComponent } from '../../components/certificazioni-management/certificazioni-management.component';
import { MetodiColtivazioneComponent } from '../../components/metodi-coltivazione/metodi-coltivazione.component';

@Component({
    selector: 'app-produttore-dashboard',
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
        ProduttoreStatsOverviewComponent,
        ProduttoreQuickActionsComponent,
        ProdottiManagementComponent,
        OrdiniManagementComponent,
        CertificazioniManagementComponent,
        MetodiColtivazioneComponent
    ],
    templateUrl: './produttore-dashboard.component.html',
    styleUrls: ['./produttore-dashboard.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProduttoreDashboardComponent implements OnInit, OnDestroy {
    private destroy$ = new Subject<void>();

    // Dati utente
    userName: string = '';
    userId: number | null = null;

    // Statistiche dashboard
    stats: ProduttoreStatsDTO | null = null;
    isLoading = false;

    // Tab selezionata
    selectedTab = 0;

    constructor(
        private authService: AuthService,
        private produttoreService: ProduttoreService,
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
        this.userName = authState.username || 'Produttore';
        this.userId = authState.userId || null;
    }

    private loadDashboardStats(): void {
        this.isLoading = true;

        this.produttoreService.getProduttoreStats()
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
                next: (stats: ProduttoreStatsDTO) => {
                    this.stats = stats;
                    this.isLoading = false;
                },
                error: () => {
                    this.isLoading = false;
                }
            });
    }

    // === NAVIGAZIONE ===

    navigateToProductsManagement(): void {
        this.selectedTab = 0; // Tab Prodotti
    }

    navigateToOrdersManagement(): void {
        this.selectedTab = 1; // Tab Ordini
    }

    navigateToCertifications(): void {
        this.selectedTab = 2; // Tab Certificazioni
    }

    navigateToCultivationMethods(): void {
        this.selectedTab = 3; // Tab Metodi di Coltivazione
    }

    // === GESTIONE TAB ===

    onTabChange(index: number): void {
        this.selectedTab = index;
    }

    // === AZIONI RAPIDE ===

    onQuickAction(action: string): void {
        switch (action) {
            case 'add-product':
                this.navigateToAddProduct();
                break;
            case 'view-orders':
                this.navigateToOrdersManagement();
                break;
            case 'manage-certifications':
                this.navigateToCertifications();
                break;
            case 'view-analytics':
                this.viewAnalytics();
                break;
            case 'edit-profile':
                this.router.navigate(['/profilo']);
                break;
            default:
                console.log('Azione non gestita:', action);
        }
    }

    private navigateToAddProduct(): void {
        // TODO: Implementare navigazione a form aggiunta prodotto
        console.log('Navigazione a form aggiunta prodotto');
    }

    private viewAnalytics(): void {
        // TODO: Implementare navigazione a pagina analytics dettagliata
        console.log('Visualizzazione analytics');
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

    get totalProducts(): number {
        return this.stats?.prodottiTotali || 0;
    }

    get approvedProducts(): number {
        return this.stats?.prodottiApprovati || 0;
    }

    get pendingProducts(): number {
        return this.stats?.prodottiInAttesa || 0;
    }

    get rejectedProducts(): number {
        return this.stats?.prodottiRespinti || 0;
    }

    get totalOrders(): number {
        return this.stats?.ordiniTotali || 0;
    }

    get completedOrders(): number {
        return this.stats?.ordiniCompletati || 0;
    }

    get totalRevenue(): number {
        return this.stats?.fatturatoTotale || 0;
    }

    get productsSold(): number {
        return this.stats?.prodottiVenduti || 0;
    }

    get totalViews(): number {
        return this.stats?.visualizzazioniTotali || 0;
    }

    get totalCertifications(): number {
        return this.stats?.certificazioniTotali || 0;
    }

    get recentOrdersCount(): number {
        return this.stats?.ordiniRecenti?.length || 0;
    }

    get popularProductsCount(): number {
        return this.stats?.prodottiPopolari?.length || 0;
    }

    // Formatta valori per display
    formatCurrency(value: number): string {
        return this.produttoreService.formatCurrency(value);
    }

    formatNumber(value: number): string {
        if (value >= 1000000) {
            return (value / 1000000).toFixed(1) + 'M';
        } else if (value >= 1000) {
            return (value / 1000).toFixed(0) + 'K';
        }
        return value.toString();
    }

    // Calcola percentuali per statistiche
    getApprovalRate(): number {
        if (this.totalProducts === 0) return 0;
        return Math.round((this.approvedProducts / this.totalProducts) * 100);
    }

    getCompletionRate(): number {
        if (this.totalOrders === 0) return 0;
        return Math.round((this.completedOrders / this.totalOrders) * 100);
    }
}