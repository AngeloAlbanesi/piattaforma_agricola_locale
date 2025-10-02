import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';

@Component({
    selector: 'app-upcoming-events',
    standalone: true,
    imports: [CommonModule, MatCardModule, MatIconModule],
    template: `
    <mat-card>
      <mat-card-content>
        <p><mat-icon>event</mat-icon> Eventi futuri - In sviluppo</p>
      </mat-card-content>
    </mat-card>
  `,
    styles: [``]
})
export class UpcomingEventsComponent { }
