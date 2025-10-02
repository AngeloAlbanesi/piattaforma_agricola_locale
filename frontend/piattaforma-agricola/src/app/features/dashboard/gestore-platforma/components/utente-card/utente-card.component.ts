import { ChangeDetectionStrategy, Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { UtenteDTO, StatoUtente } from '../../../../../core/models/gestore-platforma.models';

@Component({
  selector: 'app-utente-card',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatChipsModule,
    DatePipe
  ],
  templateUrl: './utente-card.component.html',
  styleUrls: ['./utente-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UtenteCardComponent {
  @Input() user!: UtenteDTO;
  @Output() viewDetails = new EventEmitter<UtenteDTO>();
  @Output() blockUser = new EventEmitter<UtenteDTO>();
  @Output() suspendUser = new EventEmitter<UtenteDTO>();
  @Output() activateUser = new EventEmitter<UtenteDTO>();

  // Esponi l'enum StatoUtente al template
  readonly StatoUtente = StatoUtente;

  onViewDetails(): void {
    this.viewDetails.emit(this.user);
  }

  onBlockUser(): void {
    this.blockUser.emit(this.user);
  }

  onSuspendUser(): void {
    this.suspendUser.emit(this.user);
  }

  onActivateUser(): void {
    this.activateUser.emit(this.user);
  }

  getUserStatusColor(status: StatoUtente | 'ATTIVO' | 'INATTIVO' | 'SOSPESO'): string {
    switch (status) {
      case StatoUtente.ATTIVO:
      case 'ATTIVO':
        return 'primary';
      case StatoUtente.INATTIVO:
      case 'INATTIVO':
        return 'accent';
      case StatoUtente.SOSPESO:
      case 'SOSPESO':
        return 'warn';
      default:
        return 'basic';
    }
  }
}