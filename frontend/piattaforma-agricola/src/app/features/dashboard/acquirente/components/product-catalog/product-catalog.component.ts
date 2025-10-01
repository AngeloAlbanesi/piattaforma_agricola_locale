import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-product-catalog',
  templateUrl: './product-catalog.component.html',
  styleUrls: ['./product-catalog.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true
})
export class ProductCatalogComponent {
  // TODO: Implementare catalogo prodotti con filtri, ricerca e paginazione
}