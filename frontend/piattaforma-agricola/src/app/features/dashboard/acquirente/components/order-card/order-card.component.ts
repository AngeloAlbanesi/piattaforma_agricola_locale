import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
    selector: 'app-order-card',
    standalone: true,
    imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule],
    template: `
    <mat-card>
      <mat-card-content>
        <p>Order Card - In sviluppo</p>
      </mat-card-content>
    </mat-card>
  `,
    styles: [``]
})
export class OrderCardComponent {
    @Input() order: any;
}
