import { ChangeDetectionStrategy, Component, EventEmitter, Output } from '@angular/core';
import { AzioneRapidaDistributore } from '../../../../../core/models/distributore.models';

@Component({
  selector: 'app-distributore-quick-actions',
  templateUrl: './distributore-quick-actions.component.html',
  styleUrls: ['./distributore-quick-actions.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DistributoreQuickActionsComponent {
  @Output() action = new EventEmitter<string>();

  quickActions: AzioneRapidaDistributore[] = [
    {
      id: 'create-package',
      label: 'Crea Pacchetto',
      icon: 'add_box',
      color: 'primary',
      description: 'Crea un nuovo pacchetto di tipicità',
      route: '/pacchetti/nuovo'
    },
    {
      id: 'manage-packages',
      label: 'Gestisci Pacchetti',
      icon: 'inventory_2',
      color: 'accent',
      description: 'Modifica o elimina pacchetti esistenti',
      route: '/pacchetti'
    },
    {
      id: 'view-products',
      label: 'Prodotti Disponibili',
      icon: 'category',
      color: 'primary',
      description: 'Esplora prodotti da aggiungere ai pacchetti',
      route: '/prodotti'
    },
    {
      id: 'view-orders',
      label: 'Ordini',
      icon: 'receipt_long',
      color: 'warn',
      description: 'Visualizza ordini e vendite',
      route: '/ordini'
    },
    {
      id: 'edit-profile',
      label: 'Profilo Azienda',
      icon: 'business',
      color: 'primary',
      description: 'Gestisci informazioni aziendali',
      route: '/profilo'
    }
  ];

  onActionClick(actionId: string): void {
    this.action.emit(actionId);
  }

  getActionIconColor(color: 'primary' | 'accent' | 'warn'): string {
    switch (color) {
      case 'primary':
        return '#9b59b6';
      case 'accent':
        return '#8e44ad';
      case 'warn':
        return '#e67e22';
      default:
        return '#9b59b6';
    }
  }

  getActionBgColor(color: 'primary' | 'accent' | 'warn'): string {
    switch (color) {
      case 'primary':
        return 'rgba(155, 89, 182, 0.1)';
      case 'accent':
        return 'rgba(142, 68, 173, 0.1)';
      case 'warn':
        return 'rgba(230, 126, 34, 0.1)';
      default:
        return 'rgba(155, 89, 182, 0.1)';
    }
  }
}