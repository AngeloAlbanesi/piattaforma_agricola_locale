import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { OrdersManagementComponent } from '../../../dashboard/acquirente/components/orders-management/orders-management.component';

@Component({
    selector: 'app-ordini-list',
    standalone: true,
    imports: [
        CommonModule,
        MatButtonModule,
        MatIconModule,
        OrdersManagementComponent
    ],
    template: `
        <div class="ordini-list-container">
            <div class="header-actions">
                <button mat-raised-button color="primary" (click)="tornaAllaDashboard()">
                    <mat-icon>arrow_back</mat-icon>
                    Torna alla Dashboard
                </button>
            </div>
            <app-orders-management></app-orders-management>
        </div>
    `,
    styles: [`
        .ordini-list-container {
            padding: 24px;
            max-width: 1400px;
            margin: 0 auto;
        }

        .header-actions {
            margin-bottom: 24px;
            display: flex;
            gap: 12px;
            align-items: center;
        }

        @media (max-width: 768px) {
            .ordini-list-container {
                padding: 16px;
            }

            .header-actions {
                margin-bottom: 16px;
            }
        }
    `]
})
export class OrdiniListComponent {
    constructor(private router: Router) { }

    tornaAllaDashboard(): void {
        this.router.navigate(['/dashboard/acquirente']);
    }
}

