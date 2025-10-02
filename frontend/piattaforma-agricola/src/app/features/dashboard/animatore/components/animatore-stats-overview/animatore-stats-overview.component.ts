import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { AnimatoreStatsDTO } from '../../../../../core/models/animatore.models';

@Component({
  selector: 'app-animatore-stats-overview',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatDividerModule,
    DecimalPipe
  ],
  templateUrl: './animatore-stats-overview.component.html',
  styleUrl: './animatore-stats-overview.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AnimatoreStatsOverviewComponent {
  @Input() eventiCreati: number | null = 0;
  @Input() eventiPubblicati: number | null = 0;
  @Input() eventiInCorso: number | null = 0;
  @Input() eventiPassati: number | null = 0;
  @Input() partecipantiTotali: number | null = 0;
  @Input() mediaPartecipantiPerEvento: number | null = 0;

  getStats(): AnimatoreStatsDTO {
    return {
      eventiCreati: this.eventiCreati || 0,
      eventiPubblicati: this.eventiPubblicati || 0,
      eventiInCorso: this.eventiInCorso || 0,
      eventiPassati: this.eventiPassati || 0,
      partecipantiTotali: this.partecipantiTotali || 0,
      mediaPartecipantiPerEvento: this.mediaPartecipantiPerEvento || 0,
      prossimiEventi: [], // Placeholder
      eventiPopolari: [], // Placeholder
      andamentoPartecipazioni: [] // Placeholder
    };
  }
}