import { ChangeDetectionStrategy, Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatButtonModule } from '@angular/material/button';
import { AzioneRapidaAnimatore } from '../../../../../core/models/animatore.models';

@Component({
  selector: 'app-animatore-quick-actions',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatTooltipModule,
    MatButtonModule
  ],
  templateUrl: './animatore-quick-actions.component.html',
  styleUrl: './animatore-quick-actions.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AnimatoreQuickActionsComponent {
  @Output() action = new EventEmitter<string>();

  quickActions: AzioneRapidaAnimatore[] = [
    {
      id: 'manage-events',
      label: 'Gestisci Eventi',
      icon: 'event',
      color: 'primary',
      description: 'Visualizza e gestisci tutti gli eventi creati',
      route: '/animatore/eventi'
    },
    {
      id: 'create-event',
      label: 'Crea Nuovo Evento',
      icon: 'add_circle',
      color: 'accent',
      description: 'Crea e pubblica un nuovo evento',
      route: '/animatore/eventi/nuovo'
    },
    {
      id: 'view-registrations',
      label: 'Visualizza Registrazioni',
      icon: 'how_to_reg',
      color: 'warn',
      description: 'Controlla le registrazioni agli eventi',
      route: '/animatore/registrazioni'
    },
    {
      id: 'view-feedback',
      label: 'Visualizza Feedback',
      icon: 'feedback',
      color: 'primary',
      description: 'Analizza i feedback ricevuti dagli eventi',
      route: '/animatore/feedback'
    }
  ];

  onActionClick(actionId: string | undefined): void {
    if (actionId) {
      this.action.emit(actionId);
    }
  }

  getDescription(action: AzioneRapidaAnimatore): string {
    return action?.description || '';
  }

  getActionIconColor(color: 'primary' | 'accent' | 'warn' | undefined): string {
    switch (color) {
      case 'primary':
        return '#3498db';
      case 'accent':
        return '#2ecc71';
      case 'warn':
        return '#e67e22';
      default:
        return '#3498db';
    }
  }

  getActionBgColor(color: 'primary' | 'accent' | 'warn' | undefined): string {
    switch (color) {
      case 'primary':
        return 'rgba(52, 152, 219, 0.1)';
      case 'accent':
        return 'rgba(46, 204, 113, 0.1)';
      case 'warn':
        return 'rgba(230, 126, 34, 0.1)';
      default:
        return 'rgba(52, 152, 219, 0.1)';
    }
  }
}