import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTabsModule } from '@angular/material/tabs';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { TrasformatoreStatsDTO } from '../../../../../core/models/trasformatore.models';
import { TrasformatoreService } from '../../../../../core/services/trasformatore.service';
import { TrasformatoreStatsOverviewComponent } from '../../components/trasformatore-stats-overview/trasformatore-stats-overview.component';
import { TrasformatoreQuickActionsComponent } from '../../components/trasformatore-quick-actions/trasformatore-quick-actions.component';
import { ProcessiManagementComponent } from '../../components/processi-management/processi-management.component';
import { FasiLavorazioneComponent } from '../../components/fasi-lavorazione/fasi-lavorazione.component';
import { TracciabilitaComponent } from '../../components/tracciabilita/tracciabilita.component';
import { CertificazioniTrasformatoreComponent } from '../../components/certificazioni-trasformatore/certificazioni-trasformatore.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-trasformatore-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    MatIconModule,
    MatButtonModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatTabsModule,
    MatCardModule,
    MatChipsModule,
    TrasformatoreStatsOverviewComponent,
    TrasformatoreQuickActionsComponent,
    ProcessiManagementComponent,
    FasiLavorazioneComponent,
    TracciabilitaComponent,
    CertificazioniTrasformatoreComponent
  ],
  templateUrl: './trasformatore-dashboard.component.html',
  styleUrls: ['./trasformatore-dashboard.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TrasformatoreDashboardComponent implements OnInit {
  isLoading = true;
  welcomeMessage: string = 'Benvenuto, Trasformatore!';
  stats: TrasformatoreStatsDTO | null = null;
  hasStats: boolean = false;
  selectedTab: number = 0;

  constructor(
    private trasformatoreService: TrasformatoreService,
    private snackBar: MatSnackBar,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    this.isLoading = true;
    this.trasformatoreService.getTrasformatoreStats().subscribe(
      (data: TrasformatoreStatsDTO) => {
        this.stats = data;
        this.hasStats = true;
        this.isLoading = false;
      },
      (error) => {
        console.error('Errore durante il caricamento delle statistiche del trasformatore:', error);
        this.snackBar.open('Impossibile caricare le statistiche del dashboard.', 'Chiudi', { duration: 3000 });
        this.hasStats = false;
        this.isLoading = false;
      }
    );
  }

  refreshData(): void {
    this.loadDashboardData();
    this.snackBar.open('Dati aggiornati!', 'Chiudi', { duration: 2000 });
  }

  onQuickAction(actionId: string): void {
    switch (actionId) {
      case 'manage-processes':
        this.router.navigate(['/dashboard/trasformatore/processi']);
        break;
      case 'manage-certifications':
        this.router.navigate(['/dashboard/trasformatore/certificazioni']);
        break;
      case 'view-traceability':
        this.router.navigate(['/dashboard/trasformatore/tracciabilita']);
        break;
      case 'create-process':
        this.router.navigate(['/dashboard/trasformatore/processi/nuovo']);
        break;
      default:
        this.snackBar.open(`Azione non riconosciuta: ${actionId}`, 'Chiudi', { duration: 2000 });
        break;
    }
  }

  onTabChange(index: number): void {
    this.selectedTab = index;
    // Potenziale logica per caricare dati specifici della tab
  }

  logout(): void {
    // Implementare la logica di logout
    this.snackBar.open('Logout effettuato con successo!', 'Chiudi', { duration: 2000 });
    this.router.navigate(['/auth/login']);
  }
}