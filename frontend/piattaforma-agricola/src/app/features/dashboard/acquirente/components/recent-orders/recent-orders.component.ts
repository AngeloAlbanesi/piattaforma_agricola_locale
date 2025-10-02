import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';

@Component({
    selector: 'app-recent-orders',
    standalone: true,
    imports: [CommonModule, MatCardModule, MatIconModule],
    template: `
    <mat-card>
      <mat-card-content>
        <p><mat-icon>shopping_bag</mat-icon> Ordini recenti - In sviluppo</p>
      </mat-card-content>
    </mat-card>
  `,
    styles: [``]
})
export class RecentOrdersComponent { }
