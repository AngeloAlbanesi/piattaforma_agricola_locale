import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrdiniManagementComponent } from '../../components/ordini-management/ordini-management.component';

@Component({
    selector: 'app-ordini-page',
    standalone: true,
    imports: [CommonModule, OrdiniManagementComponent],
    template: '<app-ordini-management></app-ordini-management>'
})
export class OrdiniPageComponent { }
