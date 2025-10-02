import { ChangeDetectionStrategy, Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AzioneRapidaTrasformatore } from '../../../../../core/models/trasformatore.models';

@Component({
  selector: 'app-trasformatore-quick-actions',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatTooltipModule
  ],
  templateUrl: './trasformatore-quick-actions.component.html',
  styleUrls: ['./trasformatore-quick-actions.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TrasformatoreQuickActionsComponent {
  @Output() action = new EventEmitter<string>();

  quickActions: AzioneRapidaTrasformatore[] = [
    {
      id: 'manage-processes',
      label: 'Gestisci Processi',
      icon: 'precision_manufacturing',
      color: 'primary',
      description: 'Visualizza e gestisci i processi di trasformazione',
      route: '/trasformatore/processi'
    },
    {
      id: 'manage-certifications',
      label: 'Gestisci Certificazioni',
      icon: 'verified',
      color: 'accent',
      description: 'Visualizza e gestisci le certificazioni dei processi',
      route: '/trasformatore/certificazioni'
    },
    {
      id: 'view-traceability',
      label: 'Tracciabilità',
      icon: 'track_changes',
      color: 'warn',
      description: 'Visualizza i dati di tracciabilità dei prodotti',
      route: '/trasformatore/tracciabilita'
    },
    {
      id: 'create-process',
      label: 'Crea Nuovo Processo',
      icon: 'add_circle',
      color: 'primary',
      description: 'Avvia un nuovo processo di trasformazione',
      route: '/trasformatore/processi/nuovo'
    }
  ];

  onActionClick(actionId: string | undefined): void {
    if (actionId) {
      this.action.emit(actionId);
    }
  }

  getDescription(action: AzioneRapidaTrasformatore): string {
    return action?.description || '';
  }

  getActionIconColor(color: 'primary' | 'accent' | 'warn' | undefined): string {
    switch (color) {
      case 'primary':
        return '#3498db';
      case 'accent':
        return '#2980b9';
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
        return 'rgba(41, 128, 185, 0.1)';
      case 'warn':
        return 'rgba(230, 126, 34, 0.1)';
      default:
        return 'rgba(52, 152, 219, 0.1)';
    }
  }
}