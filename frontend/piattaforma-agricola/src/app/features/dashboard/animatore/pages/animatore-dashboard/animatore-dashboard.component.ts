import { ChangeDetectionStrategy, Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Subject, takeUntil, catchError } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';

import { AuthService } from '../../../../../core/services/auth.service';
import { AnimatoreService } from '../../../../../core/services/animatore.service';
import { AnimatoreStatsDTO } from '../../../../../core/models/animatore.models';
import { AnimatoreStatsOverviewComponent } from '../../components/animatore-stats-overview/animatore-stats-overview.component';
import { AnimatoreQuickActionsComponent } from '../../components/animatore-quick-actions/animatore-quick-actions.component';
import { EventiManagementComponent } from '../../components/eventi-management/eventi-management.component';

@Component({
    selector: 'app-animatore-dashboard',
    standalone: true,
    imports: [
        CommonModule,
        MatCardModule,
        MatIconModule,
        MatButtonModule,
        MatProgressSpinnerModule,
        MatTabsModule,
        MatTooltipModule,
        AnimatoreStatsOverviewComponent,
        AnimatoreQuickActionsComponent,
        EventiManagementComponent
    ],
    templateUrl: './animatore-dashboard.component.html',
    styleUrls: ['./animatore-dashboard.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class AnimatoreDashboardComponent implements OnInit, OnDestroy {
    private destroy$ = new Subject<void>();

    // Dati utente
    userName: string = '';
    userId: number | null = null;

    // Statistiche dashboard
    stats: AnimatoreStatsDTO | null = null;
    isLoading = false;

    // Tab selezionata
    selectedTab = 0;

    constructor(
        private authService: AuthService,
        private animatoreService: AnimatoreService,
        private router: Router,
        private snackBar: MatSnackBar
    ) { }

    ngOnInit(): void {
        this.initializeUserData();
        // Statistiche rimosse temporaneamente
        this.stats = null;
        this.isLoading = false;
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    // === INIZIALIZZAZIONE ===

    private initializeUserData(): void {
        const authState = this.authService.authState();
        this.userName = authState.username || 'Animatore';
        this.userId = authState.userId || null;
    }

    private loadDashboardStats(): void {
        // Metodo vuoto - statistiche rimosse temporaneamente
        // Potrebbe essere implementato in futuro quando le API saranno disponibili
    }

    // === NAVIGAZIONE ===

    navigateToEvents(): void {
        this.router.navigate(['/']);
    }

    navigateToCreateEvent(): void {
        this.router.navigate(['/eventi/nuovo']);
    }

    navigateToParticipants(): void {
        this.router.navigate(['/partecipanti']);
    }

    navigateToFeedback(): void {
        this.router.navigate(['/feedback']);
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
            case 'create-event':
                this.navigateToCreateEvent();
                break;
            case 'view-events':
                this.navigateToEvents();
                break;
            case 'view-participants':
                this.navigateToParticipants();
                break;
            case 'view-feedback':
                this.navigateToFeedback();
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
        // Nessun dato da refresh - statistiche rimosse temporaneamente
    }

    logout(): void {
        this.authService.logout();
    }

    // === METODI PUBBLICI PER TEMPLATE ===

    formatCurrency(value: number): string {
        return this.animatoreService.formatCurrency(value);
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

    get eventsCreated(): number {
        return this.stats?.eventiCreati || 0;
    }

    get eventsPublished(): number {
        return this.stats?.eventiPubblicati || 0;
    }

    get eventsInProgress(): number {
        return this.stats?.eventiInCorso || 0;
    }

    get eventsPast(): number {
        return this.stats?.eventiPassati || 0;
    }

    get totalParticipants(): number {
        return this.stats?.partecipantiTotali || 0;
    }

    get avgParticipantsPerEvent(): number {
        return this.stats?.mediaPartecipantiPerEvento || 0;
    }

    get upcomingEvents(): any[] {
        return this.stats?.prossimiEventi || [];
    }

    get popularEvents(): any[] {
        return this.stats?.eventiPopolari || [];
    }

    get participationTrend(): number {
        if (!this.stats?.andamentoPartecipazioni || this.stats.andamentoPartecipazioni.length < 2) {
            return 0;
        }

        const data = this.stats.andamentoPartecipazioni;
        const lastMonth = data[data.length - 1];
        const previousMonth = data[data.length - 2];

        return previousMonth.partecipanti > 0
            ? ((lastMonth.partecipanti - previousMonth.partecipanti) / previousMonth.partecipanti) * 100
            : 0;
    }
}