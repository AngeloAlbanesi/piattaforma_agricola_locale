import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
    selector: 'app-cart-item',
    standalone: true,
    imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule],
    template: `
    <div class="cart-item">
      <p>Cart Item - In sviluppo</p>
    </div>
  `,
    styles: [``]
})
export class CartItemComponent {
    @Input() item: any;
}
