import { ChangeDetectionStrategy, Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule, DatePipe, CurrencyPipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { OrdineRiepilogoDTO, StatoOrdineProduttore } from '../../../../../core/models/produttore.models';

@Component({
  selector: 'app-produttore-order-card',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatChipsModule,
    MatTooltipModule,
    MatMenuModule,
    MatDividerModule,
    DatePipe,
    CurrencyPipe
  ],
  templateUrl: './produttore-order-card.component.html',
  styleUrl: './produttore-order-card.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProduttoreOrderCardComponent {
  @Input() ordine!: OrdineRiepilogoDTO;
  @Output() viewDetails = new EventEmitter<OrdineRiepilogoDTO>();
  @Output() updateStatus = new EventEmitter<{ ordine: OrdineRiepilogoDTO, nuovoStato: StatoOrdineProduttore }>();

  statiOrdine = [
    { value: StatoOrdineProduttore.PAGATO, viewValue: 'Pronto per Lavorazione' },
    { value: StatoOrdineProduttore.IN_LAVORAZIONE, viewValue: 'In Lavorazione' },
    { value: StatoOrdineProduttore.SPEDITO, viewValue: 'Spedito' },
    { value: StatoOrdineProduttore.CONSEGNATO, viewValue: 'Consegnato' },
    { value: StatoOrdineProduttore.ANNULLATO, viewValue: 'Annullato' }
  ];

  onViewDetails(): void {
    this.viewDetails.emit(this.ordine);
  }

  onUpdateStatus(nuovoStato: StatoOrdineProduttore): void {
    this.updateStatus.emit({ ordine: this.ordine, nuovoStato });
  }

  getStatoClass(stato: string): 'primary' | 'accent' | 'warn' | 'basic' {
    switch (stato) {
      case StatoOrdineProduttore.CONSEGNATO:
        return 'primary';
      case StatoOrdineProduttore.SPEDITO:
        return 'accent';
      case StatoOrdineProduttore.IN_LAVORAZIONE:
        return 'warn';
      case StatoOrdineProduttore.PAGATO:
        return 'basic'; // Usiamo basic per pagato/pronto
      default:
        return 'basic';
    }
  }
}