import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';

@Component({
    selector: 'app-product-filters',
    standalone: true,
    imports: [CommonModule, MatCardModule, MatFormFieldModule, MatSelectModule],
    template: `
    <div class="filters">
      <p>Filtri prodotti - In sviluppo</p>
    </div>
  `,
    styles: [``]
})
export class ProductFiltersComponent { }
