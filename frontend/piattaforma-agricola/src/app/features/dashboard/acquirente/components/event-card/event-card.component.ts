import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
    selector: 'app-event-card',
    standalone: true,
    imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule],
    template: `
    <mat-card>
      <mat-card-content>
        <p>Event Card - In sviluppo</p>
      </mat-card-content>
    </mat-card>
  `,
    styles: [``]
})
export class EventCardComponent {
    @Input() event: any;
}
