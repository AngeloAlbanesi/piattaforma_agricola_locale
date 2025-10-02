import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AcquirenteService } from '../../../../../core/services/acquirente.service';
import { ProductSummaryDTO } from '../../../../../core/models/acquirente.models';

@Component({
    selector: 'app-product-catalog',
    templateUrl: './product-catalog.component.html',
    styleUrls: ['./product-catalog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: true,
    imports: [CommonModule, MatCardModule, MatIconModule, MatProgressSpinnerModule]
})
export class ProductCatalogComponent implements OnInit {
    products: ProductSummaryDTO[] = [];
    isLoading = false;

    constructor(private acquirenteService: AcquirenteService) { }

    ngOnInit(): void {
        this.loadProducts();
    }

    private loadProducts(): void {
        this.isLoading = true;
        this.acquirenteService.getProducts(undefined, { page: 0, size: 6 }).subscribe({
            next: res => {
                this.products = res.content || [];
                this.isLoading = false;
            },
            error: err => {
                console.warn('Impossibile caricare prodotti demo:', err);
                this.products = [];
                this.isLoading = false;
            }
        });
    }
}