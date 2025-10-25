import { ChangeDetectionStrategy, Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatButtonModule } from '@angular/material/button';
import { AzioneRapidaProduttore } from '../../../../../core/models/produttore.models';

@Component({
  selector: 'app-produttore-quick-actions',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatTooltipModule,
    MatButtonModule
  ],
  templateUrl: './produttore-quick-actions.component.html',
  styleUrl: './produttore-quick-actions.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProduttoreQuickActionsComponent {
  @Output() action = new EventEmitter<string>();

  quickActions: AzioneRapidaProduttore[] = [
    {
      id: 'manage-products',
      label: 'Gestisci Prodotti',
      icon: 'inventory_2',
      color: 'primary',
      description: 'Visualizza e gestisci i tuoi prodotti nel catalogo',
      route: '/produttore/prodotti'
    },
    {
      id: 'manage-orders',
      label: 'Gestisci Ordini',
      icon: 'shopping_cart_checkout',
      color: 'accent',
      description: 'Visualizza e gestisci gli ordini ricevuti',
      route: '/produttore/ordini'
    },
    {
      id: 'manage-certifications',
      label: 'Gestisci Certificazioni',
      icon: 'verified_user',
      color: 'warn',
      description: 'Gestisci le certificazioni dei tuoi prodotti',
      route: '/produttore/certificazioni'
    },
    {
      id: 'manage-methods',
      label: 'Metodi di Coltivazione',
      icon: 'agriculture',
      color: 'primary',
      description: 'Gestisci i metodi di coltivazione utilizzati',
      route: '/produttore/metodi-coltivazione'
    }
  ];

  onActionClick(actionId: string | undefined): void {
    if (actionId) {
      this.action.emit(actionId);
    }
  }

  getDescription(action: AzioneRapidaProduttore): string {
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