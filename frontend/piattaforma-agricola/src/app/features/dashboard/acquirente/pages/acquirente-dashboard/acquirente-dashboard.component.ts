import { ChangeDetectionStrategy, Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Subject, takeUntil, catchError, of } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';

import { AuthService } from '../../../../../core/services/auth.service';
import { AcquirenteService } from '../../../../../core/services/acquirente.service';
import { AcquirenteStatsDTO } from '../../../../../core/models/acquirente.models';
import { StatsOverviewComponent } from '../../components/stats-overview/stats-overview.component';
import { QuickActionsComponent } from '../../components/quick-actions/quick-actions.component';
import { ProductCatalogComponent } from '../../components/product-catalog/product-catalog.component';
import { CartSummaryComponent } from '../../components/cart-summary/cart-summary.component';
import { RecentOrdersComponent } from '../../components/recent-orders/recent-orders.component';
import { UpcomingEventsComponent } from '../../components/upcoming-events/upcoming-events.component';
import { PersonalDataCardComponent } from '../../../shared/components';

@Component({
    selector: 'app-acquirente-dashboard',
    standalone: true,
    imports: [
        CommonModule,
        MatCardModule,
        MatIconModule,
        MatButtonModule,
        MatProgressSpinnerModule,
        MatTabsModule,
        MatTooltipModule,
        StatsOverviewComponent,
        QuickActionsComponent,
        ProductCatalogComponent,
        CartSummaryComponent,
        RecentOrdersComponent,
        UpcomingEventsComponent,
        PersonalDataCardComponent
    ],
    templateUrl: './acquirente-dashboard.component.html',
    styleUrls: ['./acquirente-dashboard.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class AcquirenteDashboardComponent implements OnInit, OnDestroy {
    private destroy$ = new Subject<void>();

    // Dati utente
    userName: string = '';
    userId: number | null = null;

    // Statistiche dashboard
    stats: AcquirenteStatsDTO | null = null;
    isLoading = false;

    // Tab selezionata
    selectedTab = 0;

    constructor(
        private authService: AuthService,
        private acquirenteService: AcquirenteService,
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
        this.userName = authState.nome || 'Acquirente';
        this.userId = authState.userId || null;
    }

    private loadDashboardStats(): void {
        this.isLoading = true;
        const defaultStats: AcquirenteStatsDTO = {
            totaleOrdini: 0,
            spesaTotale: 0,
            prodottiAcquistati: 0,
            eventiPartecipati: 0,
            ordiniRecenti: [],
            prodottiPreferiti: [],
            eventiProssimi: []
        };

        this.acquirenteService.getAcquirenteStats()
            .pipe(
                takeUntil(this.destroy$),
                catchError(error => {
                    console.warn('Acquirente stats non disponibili, uso default:', error);
                    // Mostra un messaggio non intrusivo all'utente
                    this.snackBar.open('Statistiche non disponibili al momento. Verranno mostrate informazioni di base.', 'Chiudi', {
                        duration: 4000,
                        panelClass: 'warning-snackbar'
                    });
                    // Ritornare valori di default in modo che il template possa renderizzare comunque le sezioni
                    return of(defaultStats as AcquirenteStatsDTO);
                })
            )
            .subscribe({
                next: (stats) => {
                    this.stats = stats;
                    this.isLoading = false;
                },
                error: () => {
                    // In caso di errore imprevisto, impostiamo comunque valori di default per evitare pagina vuota
                    this.stats = defaultStats;
                    this.isLoading = false;
                }
            });
    }

    // === NAVIGAZIONE ===

    navigateToCatalog(): void {
        this.router.navigate(['/']);
    }

    navigateToCart(): void {
        this.router.navigate(['/']);
    }

    navigateToOrders(): void {
        this.router.navigate(['/ordini']);
    }

    navigateToEvents(): void {
        this.router.navigate(['/']);
    }

    // === GESTIONE TAB ===

    onTabChange(index: number): void {
        this.selectedTab = index;
    }

    // === AZIONI RAPIDE ===

    onQuickAction(action: string): void {
        switch (action) {
            case 'browse-products':
                this.navigateToCatalog();
                break;
            case 'view-cart':
                this.navigateToCart();
                break;
            case 'view-orders':
                this.navigateToOrders();
                break;
            case 'browse-events':
                this.navigateToEvents();
                break;
            case 'edit-profile':
                this.router.navigate(['/profilo']);
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

    get totalOrders(): number {
        return this.stats?.totaleOrdini || 0;
    }

    get totalSpent(): number {
        return this.stats?.spesaTotale || 0;
    }

    get productsPurchased(): number {
        return this.stats?.prodottiAcquistati || 0;
    }

    get eventsAttended(): number {
        return this.stats?.eventiPartecipati || 0;
    }

    get recentOrdersCount(): number {
        return this.stats?.ordiniRecenti?.length || 0;
    }

    get upcomingEventsCount(): number {
        return this.stats?.eventiProssimi?.length || 0;
    }
}