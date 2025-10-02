import { ChangeDetectionStrategy, Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';

@Component({
    selector: 'app-product-catalog',
    templateUrl: './product-catalog.component.html',
    styleUrls: ['./product-catalog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: true,
    imports: [MatCardModule, MatIconModule]
})
export class ProductCatalogComponent {
    // TODO: Implementare catalogo prodotti con filtri, ricerca e paginazione
}