import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrdersManagementComponent } from '../../../dashboard/acquirente/components/orders-management/orders-management.component';

@Component({
    selector: 'app-ordini-list',
    standalone: true,
    imports: [
        CommonModule,
        OrdersManagementComponent
    ],
    template: `
        <div class="ordini-list-container">
            <app-orders-management></app-orders-management>
        </div>
    `,
    styles: [`
        .ordini-list-container {
            padding: 24px;
            max-width: 1400px;
            margin: 0 auto;
        }

        @media (max-width: 768px) {
            .ordini-list-container {
                padding: 16px;
            }
        }
    `]
})
export class OrdiniListComponent { }

