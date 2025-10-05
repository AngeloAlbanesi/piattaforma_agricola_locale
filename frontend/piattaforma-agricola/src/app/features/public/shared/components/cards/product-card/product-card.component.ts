import { Component, Input, Output, EventEmitter, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatCommonModule } from '@angular/material/core';
import { MatRippleModule } from '@angular/material/core';
import { PublicProdottoSummaryDTO } from '../../../../../../core/models/public.models';

@Component({
    selector: 'app-product-card',
    templateUrl: './product-card.component.html',
    styleUrls: ['./product-card.component.scss'],
    standalone: true,
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule,
        MatCardModule,
        MatButtonModule,
        MatIconModule,
        MatChipsModule,
        MatCommonModule,
        MatRippleModule
    ]
})
export class ProductCardComponent {
    @Input() prodotto!: PublicProdottoSummaryDTO;
    @Input() showActions = true;
    @Input() compact = false;

    @Output() cardClick = new EventEmitter<number>();
    @Output() quickView = new EventEmitter<number>();
    @Output() addToCart = new EventEmitter<number>();

    onCardClick(): void {
        this.cardClick.emit(this.prodotto.id);
    }

    onQuickView(event: Event): void {
        event.stopPropagation();
        this.quickView.emit(this.prodotto.id);
    }

    onAddToCart(event: Event): void {
        event.stopPropagation();
        this.addToCart.emit(this.prodotto.id);
    }

    getProdottoImageUrl(): string {
        if (this.prodotto.immagineUrl) {
            return this.prodotto.immagineUrl;
        }

        // Fallback basato sulla categoria
        const categoryImages: Record<string, string> = {
            'FRUTTA': '/assets/images/placeholders/frutta.jpg',
            'VERDURA': '/assets/images/placeholders/verdura.jpg',
            'LATTE_DERIVATI': '/assets/images/placeholders/latte.jpg',
            'CARNE': '/assets/images/placeholders/carne.jpg',
            'PESCE': '/assets/images/placeholders/pesce.jpg',
            'CEREALI': '/assets/images/placeholders/cereali.jpg',
            'FORMAGGI': '/assets/images/placeholders/formaggio.jpg',
            'VINO': '/assets/images/placeholders/vino.jpg',
            'OLIO': '/assets/images/placeholders/olio.jpg'
        };

        return categoryImages[this.prodotto.categoria || ''] || '/assets/images/placeholders/prodotto-generico.jpg';
    }

    isDisponibile(): boolean {
        return this.prodotto.quantitaDisponibile > 0;
    }

    hasCertificazioni(): boolean {
        return !!(this.prodotto.certificazioni && this.prodotto.certificazioni.length > 0);
    }

    getCertificazioniVisibili(): string[] {
        if (!this.prodotto.certificazioni) return [];
        return this.prodotto.certificazioni.slice(0, this.compact ? 1 : 2);
    }

    formatPrezzo(): string {
        return new Intl.NumberFormat('it-IT', {
            style: 'currency',
            currency: 'EUR',
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        }).format(this.prodotto.prezzo);
    }

    onImageError(event: Event): void {
        const img = event.target as HTMLImageElement | null;
        if (img) {
            img.src = '/assets/images/placeholders/prodotto-generico.jpg';
        }
    }
}