import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProdottiManagementComponent } from '../../components/prodotti-management/prodotti-management.component';

@Component({
    selector: 'app-prodotti-page',
    standalone: true,
    imports: [CommonModule, ProdottiManagementComponent],
    template: '<app-prodotti-management></app-prodotti-management>'
})
export class ProdottiPageComponent { }
