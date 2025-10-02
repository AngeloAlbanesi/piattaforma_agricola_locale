import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { CommonModule, CurrencyPipe, DecimalPipe, PercentPipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { TrasformatoreStatsDTO } from '../../../../../core/models/trasformatore.models';

@Component({
  selector: 'app-trasformatore-stats-overview',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    CurrencyPipe,
    DecimalPipe,
    PercentPipe
  ],
  templateUrl: './trasformatore-stats-overview.component.html',
  styleUrls: ['./trasformatore-stats-overview.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TrasformatoreStatsOverviewComponent {
  @Input() processiTotali: number | null = 0;
  @Input() processiAttivi: number | null = 0;
  @Input() processiCompletati: number | null = 0;
  @Input() prodottiTrasformati: number | null = 0;
  @Input() valoreProduzione: number | null = 0;
  @Input() costiTotali: number | null = 0;
  @Input() ricavoTotale: number | null = 0;
  @Input() margineProfitto: number | null = 0;
  @Input() certificazioniTotali: number | null = 0;
  @Input() tracciabilitaAttive: number | null = 0;

  getStats(): TrasformatoreStatsDTO {
    return {
      processiTotali: this.processiTotali || 0,
      processiAttivi: this.processiAttivi || 0,
      processiCompletati: this.processiCompletati || 0,
      prodottiTrasformati: this.prodottiTrasformati || 0,
      valoreProduzione: this.valoreProduzione || 0,
      costiTotali: this.costiTotali || 0,
      ricavoTotale: this.ricavoTotale || 0,
      margineProfitto: this.margineProfitto || 0,
      certificazioniTotali: this.certificazioniTotali || 0,
      tracciabilitaAttive: this.tracciabilitaAttive || 0,
      processiRecenti: [], // Placeholder, non gestito in questo componente
      prodottiPopolari: [], // Placeholder, non gestito in questo componente
      andamentoProduzione: [] // Placeholder, non gestito in questo componente
    };
  }
}