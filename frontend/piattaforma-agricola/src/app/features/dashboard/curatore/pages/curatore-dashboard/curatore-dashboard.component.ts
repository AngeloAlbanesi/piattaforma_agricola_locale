import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
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
import { CuratoreStatsDTO } from '../../../../../core/models/curatore.models';
import { CuratoreStatsOverviewComponent } from '../../components/curatore-stats-overview/curatore-stats-overview.component';
import { CuratoreQuickActionsComponent } from '../../components/curatore-quick-actions/curatore-quick-actions.component';
import { ApprovazioniManagementComponent } from '../../components/approvazioni-management/approvazioni-management.component';
import { PersonalDataCardComponent } from '../../../shared/components';

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
        ApprovazioniManagementComponent,
        PersonalDataCardComponent
    ],
    templateUrl: './curatore-dashboard.component.html',
    styleUrls: ['./curatore-dashboard.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CuratoreDashboardComponent implements OnInit {

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
        private router: Router,
        private snackBar: MatSnackBar
    ) { }

    ngOnInit(): void {
        this.initializeUserData();
    }

    // === INIZIALIZZAZIONE ===

    private initializeUserData(): void {
        const authState = this.authService.authState();
        this.userName = authState.nome || 'Curatore';
        this.userId = authState.userId || null;
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
        // Nessuna statistica da ricaricare: rinfreschiamo solo i dati utente e notifichiamo l'utente.
        this.initializeUserData();
        this.snackBar.open('Dati aggiornati', 'OK', { duration: 2000 });
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