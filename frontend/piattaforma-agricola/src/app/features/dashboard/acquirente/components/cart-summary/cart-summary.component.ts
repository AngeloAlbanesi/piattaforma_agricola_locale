import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-cart-summary',
  templateUrl: './cart-summary.component.html',
  styleUrls: ['./cart-summary.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true
})
export class CartSummaryComponent {
  // TODO: Implementare riepilogo carrello
}