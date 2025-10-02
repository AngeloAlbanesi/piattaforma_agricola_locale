import { ChangeDetectionStrategy, Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule, DatePipe, CurrencyPipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { EventoDTO, StatoEvento } from '../../../../../core/models/animatore.models';

@Component({
  selector: 'app-evento-card',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatChipsModule,
    MatTooltipModule,
    DatePipe,
    CurrencyPipe
  ],
  templateUrl: './evento-card.component.html',
  styleUrl: './evento-card.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventoCardComponent {
  @Input() evento!: EventoDTO;
  @Output() viewDetails = new EventEmitter<EventoDTO>();
  @Output() editEvent = new EventEmitter<EventoDTO>();
  @Output() deleteEvent = new EventEmitter<EventoDTO>();

  onViewDetails(): void {
    this.viewDetails.emit(this.evento);
  }

  onEditEvent(): void {
    this.editEvent.emit(this.evento);
  }

  onDeleteEvent(): void {
    this.deleteEvent.emit(this.evento);
  }

  getStatoClass(stato: string): string {
    switch (stato) {
      case StatoEvento.PUBBLICATO:
        return 'primary';
      case StatoEvento.DA_PUBBLICARE:
        return 'accent';
      case StatoEvento.ANNULLATO:
        return 'warn';
      default:
        return 'basic';
    }
  }
}