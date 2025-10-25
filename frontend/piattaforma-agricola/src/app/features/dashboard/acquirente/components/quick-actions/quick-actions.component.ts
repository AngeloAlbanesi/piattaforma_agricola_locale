import { ChangeDetectionStrategy, Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';

export interface QuickAction {
  id: string;
  label: string;
  icon: string;
  color: 'primary' | 'accent' | 'warn';
  description?: string;
}

@Component({
  selector: 'app-quick-actions',
  templateUrl: './quick-actions.component.html',
  styleUrls: ['./quick-actions.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatCardModule, MatIconModule, MatTooltipModule]
})
export class QuickActionsComponent {
  @Output() action = new EventEmitter<string>();

  quickActions: QuickAction[] = [
    {
      id: 'browse-products',
      label: 'Sfoglia Prodotti',
      icon: 'shopping_basket',
      color: 'primary',
      description: 'Scopri i prodotti locali disponibili'
    },
    {
      id: 'view-cart',
      label: 'Carrello',
      icon: 'shopping_cart',
      color: 'accent',
      description: 'Gestisci il tuo carrello'
    },
    {
      id: 'view-orders',
      label: 'I Miei Ordini',
      icon: 'receipt_long',
      color: 'primary',
      description: 'Visualizza storico ordini'
    },
    {
      id: 'browse-events',
      label: 'Eventi',
      icon: 'event',
      color: 'warn',
      description: 'Partecipa agli eventi della filiera'
    },
    {
      id: 'edit-profile',
      label: 'Profilo',
      icon: 'person',
      color: 'primary',
      description: 'Gestisci il tuo profilo'
    }
  ];

  onActionClick(actionId: string): void {
    this.action.emit(actionId);
  }

  trackByAction(index: number, action: QuickAction): string {
    return action.id;
  }
}