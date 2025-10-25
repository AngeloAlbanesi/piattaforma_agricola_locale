import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AcquirenteService } from '../../../../../core/services/acquirente.service';
import { CarrelloDTO } from '../../../../../core/models/acquirente.models';

@Component({
    selector: 'app-cart-summary',
    templateUrl: './cart-summary.component.html',
    styleUrls: ['./cart-summary.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: true,
    imports: [CommonModule, MatCardModule, MatIconModule, MatProgressSpinnerModule]
})
export class CartSummaryComponent implements OnInit {
    cart: CarrelloDTO | null = null;
    isLoading = false;

    constructor(
        private acquirenteService: AcquirenteService,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit(): void {
        this.loadCart();
    }

    private loadCart(): void {
        this.isLoading = true;
        this.cdr.markForCheck();
        
        this.acquirenteService.getCart().subscribe({
            next: c => {
                this.cart = c;
                this.isLoading = false;
                this.cdr.markForCheck();
            },
            error: err => {
                console.warn('Impossibile caricare carrello demo:', err);
                this.cart = null;
                this.isLoading = false;
                this.cdr.markForCheck();
            }
        });
    }

    getCartTotal(): number {
        if (!this.cart || !this.cart.elementiCarrello) return 0;
        return this.cart.elementiCarrello.reduce((sum, item) => sum + (item.prezzoUnitario * item.quantita), 0);
    }
}