import { ChangeDetectionStrategy, Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subject, takeUntil, catchError } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';

import { AuthService } from '../../../../core/services/auth.service';
import { AcquirenteService } from '../../../../core/services/acquirente.service';
import { AcquirenteStatsDTO } from '../../../../core/models/acquirente.models';

@Component({
  selector: 'app-acquirente-dashboard',
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
  ) {}
  
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
    this.userName = authState.username || 'Acquirente';
    this.userId = authState.userId || null;
  }
  
  private loadDashboardStats(): void {
    this.isLoading = true;
    
    this.acquirenteService.getAcquirenteStats()
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
  
  navigateToCatalog(): void {
    this.router.navigate(['/catalogo']);
  }
  
  navigateToCart(): void {
    this.router.navigate(['/carrello']);
  }
  
  navigateToOrders(): void {
    this.router.navigate(['/ordini']);
  }
  
  navigateToEvents(): void {
    this.router.navigate(['/eventi']);
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