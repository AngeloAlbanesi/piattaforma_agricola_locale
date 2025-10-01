import { ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-distributore-stats-overview',
  templateUrl: './distributore-stats-overview.component.html',
  styleUrls: ['./distributore-stats-overview.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DistributoreStatsOverviewComponent implements OnChanges {
  @Input() totalPackages: number = 0;
  @Input() activePackages: number = 0;
  @Input() soldPackages: number = 0;
  @Input() totalRevenue: number = 0;
  @Input() averagePackagePrice: number = 0;

  // Statistiche calcolate
  packageCompletionRate: number = 0;
  revenueGrowth: number = 0;

  ngOnChanges(changes: SimpleChanges): void {
    this.calculateDerivedStats();
  }

  private calculateDerivedStats(): void {
    // Calcola tasso di completamento pacchetti
    if (this.totalPackages > 0) {
      this.packageCompletionRate = (this.activePackages / this.totalPackages) * 100;
    }

    // Calcola crescita ricavi (simulazione)
    this.revenueGrowth = this.totalRevenue > 0 ? Math.random() * 20 : 0;
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('it-IT', {
      style: 'currency',
      currency: 'EUR'
    }).format(value);
  }

  formatPercentage(value: number): string {
    return `${value.toFixed(1)}%`;
  }

  getProgressColor(value: number): string {
    if (value >= 80) return '#27ae60'; // Verde
    if (value >= 50) return '#f39c12'; // Arancione
    return '#e74c3c'; // Rosso
  }
}