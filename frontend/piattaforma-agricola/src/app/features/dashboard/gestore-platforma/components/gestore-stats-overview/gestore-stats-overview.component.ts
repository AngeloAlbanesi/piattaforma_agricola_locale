import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { GestorePlatformaStatsDTO } from '../../../../../core/models/gestore-platforma.models';

@Component({
  selector: 'app-gestore-stats-overview',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatDividerModule
  ],
  templateUrl: './gestore-stats-overview.component.html',
  styleUrls: ['./gestore-stats-overview.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GestoreStatsOverviewComponent {
  @Input() totalUsers: number | null = 0;
  @Input() activeUsers: number | null = 0;
  @Input() newUsersThisMonth: number | null = 0;
  @Input() totalProducts: number | null = 0;
  @Input() approvedProducts: number | null = 0;
  @Input() totalCompanies: number | null = 0;
  @Input() approvedCompanies: number | null = 0;
  @Input() totalEvents: number | null = 0;
  @Input() publishedEvents: number | null = 0;
  @Input() totalTransactions: number | null = 0;
  @Input() totalRevenue: number | null = 0;
  @Input() userGrowthRate: number | null = 0;
  @Input() productGrowthRate: number | null = 0;
  @Input() eventGrowthRate: number | null = 0;

  getStats(): GestorePlatformaStatsDTO {
    return {
      utentiTotali: this.totalUsers || 0,
      utentiAttivi: this.activeUsers || 0,
      utentiNuoviMese: this.newUsersThisMonth || 0,
      prodottiTotali: this.totalProducts || 0,
      prodottiApprovati: this.approvedProducts || 0,
      aziendeTotali: this.totalCompanies || 0,
      aziendeApprovate: this.approvedCompanies || 0,
      eventiTotali: this.totalEvents || 0,
      eventiPubblicati: this.publishedEvents || 0,
      transazioniTotali: this.totalTransactions || 0,
      ricavoTotale: this.totalRevenue || 0,
      andamentoUtenti: [], // Placeholder, non gestito in questo componente
      andamentoProdotti: [], // Placeholder, non gestito in questo componente
      andamentoEventi: [] // Placeholder, non gestito in questo componente
    };
  }
}